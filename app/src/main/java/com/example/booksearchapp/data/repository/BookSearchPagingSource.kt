package com.example.booksearchapp.data.repository

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.booksearchapp.data.api.BookSearchApi
import com.example.booksearchapp.data.model.Book
import com.example.booksearchapp.util.Constants.PAGING_SIZE
import retrofit2.HttpException
import java.io.IOException

/**
 * retrofit 요청 결과를 load Result 객체로 반환하는 페이징 소스 정의
 * */
//결과값을 알아서 Paging소스로 반환해주던 Room과 달리 네트워크 응답은 우리가 직접 페이징 소스로 가공하는 과정이 추가
//Paging은 페이지 및 사이즈 값을 필요에 따라 변화시켜 결과를 PagingSource로 반환한다
//PagingSource는 크게 key를 만드는 부분과 페이징 소스를 만드는 부분으로 나누어짐
//key는 읽어올 페이지 번호, 키를 전달해서 받아온 데이터로 페이징 소스 작성
//다음 페이지 요청이 오면 페이지 번호를 지정해 페이지를 만드는 과정을 반복
class BookSearchPagingSource(
    private val api: BookSearchApi,
    private val query: String,
    private val sort: String,
    //PagingSource를 상속 받는데 안에는 페이지 타입과 데이터 타입이 들어감
) : PagingSource<Int, Book>() {

    //페이저가 데이터를 호출할때마다 호출되는 함수
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Book> {
        return try {
            //key를 받아와서 pageNumber에 대입
            val pageNumber = params.key ?: STARTING_PAGE_INDEX

            //pageNumber 값을 retrofit에 전달해서 해당하는 데이터를 받아옴
            val response = api.searchBooks(query, sort, pageNumber, params.loadSize)
            //값이 true이면 데이터의 끝이므로 next가 null
            val endOfPaginationReached = response.body()?.meta?.isEnd!! //카카오 api 속성 사용한 것

            val data = response.body()?.documents!!
            //만약 현재 키가 1이면 이전키 null
            val prevKey = if (pageNumber == STARTING_PAGE_INDEX) null else pageNumber - 1
            val nextKey = if (endOfPaginationReached) { //페이지의 끝 의미
                null
            } else {
                // initial load size = 3 * NETWORK_PAGE_SIZE
                // ensure we're not requesting duplicating items, at the 2nd request
                pageNumber + (params.loadSize / PAGING_SIZE)
            }
            LoadResult.Page(
                //이전 페이지와 다음 페이지의 key 값을 LoadResult 페이지에 담아 반환
                data = data,
                prevKey = prevKey,
                nextKey = nextKey,
            )
        } catch (exception: IOException) {
            LoadResult.Error(exception)
        } catch (exception: HttpException) {
            LoadResult.Error(exception)
        }
    }

    //여러가지 이유로 페이지를 갱신해야할때 수행하는 함수, 가장 최근에 접근한 페이지를 anchorPosition로 받고
    //그 주위의 페이지를 읽어오도록 키를 반환
    override fun getRefreshKey(state: PagingState<Int, Book>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    //key의 초기 값은 null이기 때문에 지정
    companion object {
        const val STARTING_PAGE_INDEX = 1
    }
}