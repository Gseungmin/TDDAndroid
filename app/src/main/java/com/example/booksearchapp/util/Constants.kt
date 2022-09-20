package com.example.booksearchapp.util

//request 요청할 도메인 및 API키
//전역변수로 만들어서 편하긴하지만 보안작업 필요
object Constants {
    const val BASE_URL = "https://dapi.kakao.com/"
    const val API_KEY = "937eecb78ab4f0780521a9f149517547"
    const val SEARCH_BOOKS_TIME_DELAY = 100L
}