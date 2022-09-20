package com.example.booksearchapp.ui.viewmodel

import androidx.lifecycle.*
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.data.repository.BookSearchRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
        val response = bookSearchRepository.searchBooks(query, "accuracy", 1, 15)
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
    val favoriteBooks: LiveData<List<Book>> = bookSearchRepository.getFavoriteBooks()

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
}