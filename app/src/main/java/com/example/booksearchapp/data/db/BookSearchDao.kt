package com.example.booksearchapp.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.example.booksearchapp.data.model.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookSearchDao {

    //동일한 키가 있는 경우 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books")
    fun getBooks(): Flow<List<Book>>

    //Repository로 부터 PagingSource를 받아와야하는데 Room은 PagingSource로 반환할 수 있다
    //Flow로 전체를 받아오던 기존의 getBooks()대신 페이징소스로 받아옴
    @Query("SELECT * FROM books")
    fun getFavoritePagingBooks(): PagingSource<Int, Book>
}