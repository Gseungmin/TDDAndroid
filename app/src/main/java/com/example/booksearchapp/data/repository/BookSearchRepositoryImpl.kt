package com.example.booksearchapp.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.booksearchapp.data.api.RetrofitInstance.api
import com.example.booksearchapp.data.db.BookSearchDatabase
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.data.repository.BookSearchRepositoryImpl.PreferencesKeys.SORT_MODE
import com.example.booksearchapp.util.Constants.PAGING_SIZE
import com.example.booksearchapp.util.Sort
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.io.IOException

/**
 * data의 출처와 관계 없이 동일한 인터페이스로 데이터에 접근할 수 있도록 하는 패턴
 * Repository를 사용함으로 ViewModel과 Data Layer(ROOM, Retrofit)와의 결합이 낮춰짐
 * */
class BookSearchRepositoryImpl(
    //생성자로 BookSearchDatabase를 받아서 Dao를 통해 각 메서드 구현
    //그 후 MainActivity에서 Repository 생성시 BookSearchDatabase 객체를 전달
    private val db: BookSearchDatabase,
    private val dataStore: DataStore<Preferences>
) : BookSearchRepository {

    /**
     * Retrofit 인스턴스를 호출하는 메서드
     * */
    override suspend fun searchBooks(
        query: String,
        sort: String,
        page: Int,
        size: Int,
    ): Response<SearchResponse> {
        //api는 Retrofit Instance를 통해 만들어진 BookSearchApi 객체
        //api 객체의 searchBooks 요청을 실행해서 데이터를 받아와 ViewModel에 전달
        return api.searchBooks(query, sort, page, size)
    }

    //생성자로 db를 받아 dao를 통해 각 메서드 구현
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
        val CACHE_DELETE_MODE = booleanPreferencesKey("cache_delete_mode")
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
                //기본값은 ACCURACY
                prefs[SORT_MODE] ?: Sort.ACCURACY.value
            }
    }

    // Paging
    // Pager 구현
    // repository에 페이징 데이터를 반환하는 Pager를 정의
    override fun getFavoritePagingBooks(): Flow<PagingData<Book>> {
        val pagingSourceFactory = { db.bookSearchDao().getFavoritePagingBooks() }
        return Pager( //Pager를 구현하기 위해서는 PagingConfig를 통해 파라미터 전달
            //아래 3가지 속성을 가지는데 pageSize는 어떤 기기가 대상이 되더라도 viewHolder에 표시할 데이터가 모자라지 않을 값으로 설정
            //enablePlaceholders가 true면 데이터 전체 사이즈를 받아와서 RecyclerView에 Place Holder를 미리 만들어두고 화면에 표시되지 않은 항목 null
            //따라서 필요한 만큼만 로딩을 위해 false 처리
            //maxsize는 pager가 메모리에 최대로 가질수 있는 페이지 수
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            //getFavoritePagingBooks()의 결과를 pagingSourceFactory를 통해 전달하고 flow를 붙여 PagingData 결과를 Flow로 만들어줌
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    //Retrofit 응답에 페이징 적용
    //Room과 과정이 비슷하지만 결과 값을 알아서 페이징 소스로 반환해주던 룸과 달리 네트워크 응답은 우리가 직접 페이징 소스로 가공하는 과정이 추가 되어야 함
    //PagingSource는 크게 key를 만드는 부분과 pagingSource를 만드는 부분으로 나뉜다
    //key는 읽어올 페이지 번호, key를 전달해서 받아온 데이터로 PagingSource 작성
    //https://developer.android.com/topic/libraries/architecture/paging/v3-paged-data
    //PagingSource는 key의 초기 값으로 null을 넣어 페이지를 만들고 다음 페이지 요청이 있을 때까지 대기
    //그 후 다음 페이지 요청이 오면 key에 값을 지정해 페이지를 만드는 과정을 반복
    //과정은 room과 같음
    override fun searchBooksPaging(query: String, sort: String): Flow<PagingData<Book>> {
        val pagingSourceFactory = { BookSearchPagingSource(query, sort) }
        return Pager(
            config = PagingConfig(
                pageSize = PAGING_SIZE,
                enablePlaceholders = false,
                maxSize = PAGING_SIZE * 3
            ),
            pagingSourceFactory = pagingSourceFactory
        ).flow
    }

    //workManger
    override suspend fun saveCacheDeleteMode(mode: Boolean) {
        dataStore.edit { prefs ->
            prefs[PreferencesKeys.CACHE_DELETE_MODE] = mode
        }
    }

    override suspend fun getCacheDeleteMode(): Flow<Boolean> {
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
                prefs[PreferencesKeys.CACHE_DELETE_MODE] ?: false
            }
    }
}