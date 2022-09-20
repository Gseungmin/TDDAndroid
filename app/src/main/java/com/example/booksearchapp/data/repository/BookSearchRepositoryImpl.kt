package com.example.booksearchapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.booksearchapp.data.api.RetrofitInstance.api
import com.example.booksearchapp.data.db.BookSearchDatabase
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.data.repository.BookSearchRepositoryImpl.PreferencesKeys.SORT_MODE
import com.example.booksearchapp.util.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException

class BookSearchRepositoryImpl(
    //생성자로 BookSearchDatabase를 받아서 Dao를 통해 각 메서드 구현
    //그 후 MainActivity에서 Repository 생성시 BookSearchDatabase 객체를 전달
    private val db: BookSearchDatabase,
    private val dataStore: DataStore<Preferences>
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

    // DataStore
    // 저장 및 불러오기에 사용할 키를 PreferencesKeys에 정의
    // 단순히 string을 사용하던 sharedPreference와 다르게 타입 안정을 위해 및 저장할 데이터가 String이기 때문에 stringPreferencesKey 사용
    private object PreferencesKeys {
        val SORT_MODE = stringPreferencesKey("sort_mode")
    }

    //저장 작업은 코루틴 안에서 이루어져야 하므로 suspend 사용 및 전달받을 Mode 값을 edit 블록 안에서 저장
    override suspend fun saveSortMode(mode: String) {
        dataStore.edit { prefs ->
            prefs[SORT_MODE] = mode
        }
    }

    //파일 접근을 위해서는 data 메소드 사용
    //캐치로 예외처리하고 웹 블록 안에서 키를 전달하여 플로우로 반환받으면 됨
    override suspend fun getSortMode(): Flow<String> {
        return dataStore.data
            .catch { exception ->
                if (exception is IOException) {
                    exception.printStackTrace()
                    emit(emptyPreferences())
                } else {
                    throw exception
                }
            }
            .map { prefs ->
                prefs[SORT_MODE] ?: Sort.ACCURACY.value
            }
    }
}