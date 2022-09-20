package com.example.booksearchapp.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.PagingData
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import kotlinx.coroutines.flow.Flow
import retrofit2.Response

interface BookSearchRepository {

    suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ) : Response<SearchResponse>

    //Room Dao를 조작하기 위한 메소드
    suspend fun insertBooks(book: Book)

    suspend fun deleteBooks(book: Book)

    fun getFavoriteBooks(): Flow<List<Book>>

    // DataStore
    suspend fun saveSortMode(mode : String)
    suspend fun getSortMode(): Flow<String>

    // Paging
    fun getFavoritePagingBooks(): Flow<PagingData<Book>>
    fun searchBooksPaging(query: String, sort: String): Flow<PagingData<Book>>

    //workManger
    suspend fun saveCacheDeleteMode(mode: Boolean)
    suspend fun getCacheDeleteMode(): Flow<Boolean>
}