package com.example.booksearchapp.util

/**
 * 전역 변수로 사용할 변수들 모음
 * Const val은 함수나 클래스의 상태에 관계없이 언제나 동일한 값
 * */
object Constants {
    //request 요청을 보낼 기본 ORIGIN
    const val BASE_URL = "https://dapi.kakao.com/"
    //request 요청에 필요한 API키
    const val API_KEY = ""
    //100L간의 타임동안 입력이 없다면 검색을 하기 위한 값
    const val SEARCH_BOOKS_TIME_DELAY = 100L
    const val DATASTORE_NAME = "preferences_datastore"
    const val PAGING_SIZE = 15
}