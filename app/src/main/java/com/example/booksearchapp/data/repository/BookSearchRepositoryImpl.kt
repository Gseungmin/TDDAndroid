package com.example.booksearchapp.data.repository

import com.example.booksearchapp.data.api.BookSearchApi
import com.example.booksearchapp.data.api.RetrofitInstance
import com.example.booksearchapp.data.api.RetrofitInstance.api
import com.example.booksearchapp.data.model.SearchResponse
import retrofit2.Response

class BookSearchRepositoryImpl : BookSearchRepository {
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
}