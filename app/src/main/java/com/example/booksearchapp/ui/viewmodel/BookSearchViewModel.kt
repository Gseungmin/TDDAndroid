package com.example.booksearchapp.ui.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.data.repository.BookSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//Repository로 부터 데이터를 받아와서 처리, 따라서 Factory 필요
//ViewModel은 그 자체로는 생성시 초기 값을 전달 받을 수 없음
class BookSearchViewModel(
    private val bookSearchRepository: BookSearchRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _searchResult = MutableLiveData<SearchResponse>()
    val searchResult: LiveData<SearchResponse> get() = _searchResult

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
    val favoriteBooks: StateFlow<List<Book>> = bookSearchRepository.getFavoriteBooks()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), listOf())

    // SavedState
    var query = String()
        set(value) {
            field = value
            //query값이 변경되면 값을 바로 반영
            savedStateHandle.set(SAVE_STATE_KEY, value)
        }

    init {
        //viewModel을 초기화 할때 query 초기 값을 savedStateHandle에서 가져온다
        query = savedStateHandle.get<String>(SAVE_STATE_KEY) ?: ""
    }

    //저장 및 로드에 사용할 SAVE_STATE_KEY정의
    companion object {
        private const val SAVE_STATE_KEY = "query"
    }

    // DataStore
    //값을 저장
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
    //Repository의 getFavoritePagingBooks 응답에 cachedIn을 붙여 코루틴이 데이터 스트림을 캐시하고 공유 가능하게 만들어줌
    //그리고 UI에서 감시해야 하는 데이터이므로 stateIn사용
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


}