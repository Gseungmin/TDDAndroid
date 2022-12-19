package com.example.booksearchapp.data.db

import androidx.paging.PagingSource
import androidx.room.*
import com.example.booksearchapp.data.model.Book
import kotlinx.coroutines.flow.Flow

/**
 * database를 조작할 쿼리를 날리는 역할
 * 데이터 접근 객체, Controller라고 생각하면 됨
 * */
@Dao
interface BookSearchDao {

    //쿼리를 제외한 CRUD 작업은 시간이 걸리는 작업이므로 suspend로 선언하여
    //코루틴 안에서 비동기적으로 사용
    //동일한 PK가 있는 경우 덮어쓰기
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: Book)

    @Delete
    suspend fun deleteBook(book: Book)

    @Query("SELECT * FROM books")
    fun getBooks(): Flow<List<Book>>

    /**
     * room에 페이징을 적용하면 전체 데이터를 받아오는 쿼리를 사용해도 한번에 모든 데이터를 가져오지 않고 페이지 단위로 반환하므로 페이징을 구현할 수 있음
     * Repository로 부터 PagingSource를 받아와야하는데 Room은 PagingSource로 반환할 수 있다
     * Flow로 전체를 받아오던 기존의 getBooks()대신 페이징소스로 받아옴
     */
    @Query("SELECT * FROM books")
    fun getFavoritePagingBooks(): PagingSource<Int, Book>
}