package com.example.booksearchapp.data.repository

import com.example.booksearchapp.data.api.RetrofitInstance.api
import com.example.booksearchapp.data.db.BookSearchDatabase
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

class BookSearchRepositoryImpl(
    //생성자로 BookSearchDatabase를 받아서 Dao를 통해 각 메서드 구현
    //그 후 MainActivity에서 Repository 생성시 BookSearchDatabase 객체를 전달
    private val db: BookSearchDatabase
) : BookSearchRepository {
    override suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ): Response<SearchResponse> {
        return api.searchBooks(query, sort, page, size)
    } //api는 Retrofit객체를 통해 만들어진 BookSearchApi 객체
    //searchBooks(Get) 요청을 실행해서 데이터를 받아옴
    //데이터를 viewModel에 전달한다

    override suspend fun insertBooks(book: Book) {
        db.bookSearchDao().insertBook(book)
    }

    override suspend fun deleteBooks(book: Book) {
        db.bookSearchDao().deleteBook(book)
    }

    override fun getFavoriteBooks(): Flow<List<Book>> {
        return db.bookSearchDao().getBooks()
    }
}