package com.example.booksearchapp.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.booksearchapp.data.model.Book

@Dao
interface BookSearchDao {

    //동일한 키가 있는 경우 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books")
    fun getBooks(): LiveData<List<Book>>
}