package com.example.booksearchapp.data.api

import com.example.booksearchapp.data.model.SearchResponse
import com.example.booksearchapp.util.Constants.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

//Get요청 수행
interface BookSearchApi {
    
    //인증에 필요한 Headers와 Get요청 주소 정의
    @Headers("Authorization: KakaoAK ${API_KEY}")
    @GET("v3/search/book")
    suspend fun searchBooks(
        @Query("query") query: String,
        @Query("sort") sort: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ) : Response<SearchResponse>
    //searchBooks는 SearchResponse타입을 가지는 Response 클래스 반환
}