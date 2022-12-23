package com.example.booksearchapp.data.db

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.booksearchapp.data.model.Book
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Named

//@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@SmallTest
@ExperimentalCoroutinesApi
class BookSearchDaoTest {

//    private lateinit var database: BookSearchDatabase
    @Inject
    @Named("test_db")
    lateinit var database: BookSearchDatabase
    private lateinit var dao: BookSearchDao

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

//    /**
//     * db는 inMemoryDatabaseBuilder를 통해 메모리 안에서만 사용 후 테스트가 끝나면 파괴
//     * Room은 ANR(Appliaction Not Responding)을 방지하기 위해 MainThread에서의 쿼리를 금지하는데
//     * DB에 대한 쿼리를 멀티 쓰레드에서 사용하면 테스트 결과를 예측할 수 없으므로 allowMainThreadQueries를 통해
//     * MainThread 쿼리 수행 가능하도록 설정
//     * */
    @Before
    fun setUp() {
//        database = Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            BookSearchDatabase::class.java
//        ).allowMainThreadQueries().build()
        hiltRule.inject()
        dao = database.bookSearchDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    /**
     * 코루틴 테스트는 runTest 블록 안에서 실행, 아직 실험중인 API이므로 ExperimentalCoroutinesApi 어노테이션 필요
     * */
    @Test
    fun insert_book_to_db() = runTest {
        val book = Book(
            listOf("a"), "b", "c", "d", 0, "e",
            0, "f", "g", "h", listOf("i"), "j"
        )
        dao.insertBook(book)

        val favoriteBooks = dao.getBooks().first()
        assertThat(favoriteBooks).contains(book)
    }

    @Test
    fun delete_book_in_db() = runTest {
        val book = Book(
            listOf("a"), "b", "c", "d", 0, "e",
            0, "f", "g", "h", listOf("i"), "j"
        )
        dao.insertBook(book)
        dao.deleteBook(book)

        val favoriteBooks = dao.getBooks().first()
        assertThat(favoriteBooks).doesNotContain(book)
    }
}