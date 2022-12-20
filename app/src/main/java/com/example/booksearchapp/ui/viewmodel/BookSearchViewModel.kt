package com.example.booksearchapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.work.*
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.data.repository.BookSearchRepository
import com.example.booksearchapp.worker.CacheDeleteWorker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit
import javax.inject.Inject

/**
 * ViewModel 객체, Repository로 부터 데이터를 받아와서 처리, 따라서 Factory 필요
 * */
@HiltViewModel
class BookSearchViewModel @Inject constructor(
    private val bookSearchRepository: BookSearchRepository,
    private val workManager: WorkManager,
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val _searchResult = MutableLiveData<SearchResponse>()
    val searchResult: LiveData<SearchResponse> get() = _searchResult

    //repository의 searchBooks를 코루틴 내부에서 수행하는 함수
    fun searchBooks(query: String) = viewModelScope.launch(Dispatchers.IO) {
        //bookSearchRepository.searchBooks를 실행하되 파라미터는 query 이외에 모두 고정 값 사용
        val response = bookSearchRepository.searchBooks(query, getSortMode(), 1, 15)
        if (response.isSuccessful) {
            response.body()?.let { body ->
                //Retrofit 서비스의 반환값은 MutableLiveData에 저장
                _searchResult.postValue(body)
            }
        }
    }

    // CRUD를 수행하는 suspend 함수는 viewModelScope에서 실행하게 하여 viewModelScope의 기본 Dispatchers가 Main이므로 IO로 바꿔준다
    fun saveBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.insertBooks(book)
    }

    fun deleteBook(book: Book) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.deleteBooks(book)
    }

    // viewModelScope, WhileSubscribed(5000), listOf() 책의 초기값
    // stateflow로 변환해서 flow동작으로 favoriteFragment의 생명주기와 동기화
    val favoriteBooks: StateFlow<List<Book>> = bookSearchRepository.getFavoriteBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    //SavedState
    //쿼리 보존에 사용할 쿼리 변수
    var query = String()
        set(value) {
            field = value
            //field 를 사용해서 query값이 변경되면 값을 바로 반영
            savedStateHandle.set(SAVE_STATE_KEY, value)
        }

    init {
        //viewModel을 초기화 할때 query 초기 값을 savedStateHandle에서 가져온다
        query = savedStateHandle.get<String>(SAVE_STATE_KEY) ?: ""
    }

    //저장 및 로드에 사용할 SAVE_STATE_KEY정의
    companion object {
        private const val SAVE_STATE_KEY = "query"
        private val WORKER_KEY = "cache_worker"
    }

    //DataStore
    //값을 저장
    //repository의 saveSortMode를 viewModelScope에서 실행하되 IO 작업이므로 Dispatchers를 IO로 설정
    fun saveSortMode(value: String) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.saveSortMode(value)
    }

    //값을 불러옴
    suspend fun getSortMode() = withContext(Dispatchers.IO) {
        //설정 값 특성상 전체 데이터 스트림을 가져올 필요 없음
        //withContext는 반드시 값을 반환하고 종료됨
        bookSearchRepository.getSortMode().first()
    }

    //Paging
    //viewModel에서 paging된 데이터를 사용하기 위한 함수
    //Repository의 getFavoritePagingBooks 응답에 cachedIn을 붙여 코루틴이 데이터 스트림을 캐시하고 공유 가능하게 만들어줌
    //그리고 UI에서 감시해야 하는 데이터이므로 stateIn사용하여 StateFlow로 만들어줌
    val favoritePagingBooks: StateFlow<PagingData<Book>> =
        bookSearchRepository.getFavoritePagingBooks()
            .cachedIn(viewModelScope)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), PagingData.empty())

    private val _searchPagingResult = MutableStateFlow<PagingData<Book>>(PagingData.empty())
    val searchPagingResult: StateFlow<PagingData<Book>> = _searchPagingResult.asStateFlow()

    //searchBooksPaging의 결과가 _searchPagingResult를 갱신하고 UI에서는 변경 불가능한 searchPagingResult로 표현
    fun searchBooksPaging(query: String) {
        viewModelScope.launch {
            bookSearchRepository.searchBooksPaging(query, getSortMode())
                .cachedIn(viewModelScope)
                .collect {
                    _searchPagingResult.value = it
                }
        }
    }

    // WorkManager
    fun saveCacheDeleteMode(value: Boolean) = viewModelScope.launch(Dispatchers.IO) {
        bookSearchRepository.saveCacheDeleteMode(value)
    }

    suspend fun getCacheDeleteMode() = withContext(Dispatchers.IO) {
        bookSearchRepository.getCacheDeleteMode().first()
    }

    fun setWork() {
        //제약 상황 설정, 충전중이고 베터리 잔량이 낮지 않은 경우에만 작업 수행
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .setRequiresBatteryNotLow(true)
            .build()

        //workRequest를 만들면서 constraints를 설정
        //15분에 한번마다 수행
        val workRequest = PeriodicWorkRequestBuilder<CacheDeleteWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        //이름을 붙여 작업 큐에 전달
        //동일한 작업을 중복하지 않도록 enqueueUniquePeriodicWork사용 및 REPLACE
        workManager.enqueueUniquePeriodicWork(
            WORKER_KEY, ExistingPeriodicWorkPolicy.REPLACE, workRequest
        )
    }

    //WORKER_KEY라는 이름을 가진 작업 삭제
    fun deleteWork() = workManager.cancelUniqueWork(WORKER_KEY)

    //내부의 작업 중 WORKER_KEY라는 이름을 가진 작업의 현재 상태를 LiveData 형태로 반환
    fun getWorkStatus(): LiveData<MutableList<WorkInfo>> =
        workManager.getWorkInfosForUniqueWorkLiveData(WORKER_KEY)
}