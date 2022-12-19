package com.example.booksearchapp.data.api

import com.example.booksearchapp.util.Constants.BASE_URL
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/**
 * Api 객체를 만들고 요청을 보내주는 인스턴스
 * object와 lazyz키워드로 구현함으로 실제 사용되는 순간 단 하나의 Retrofit 인스턴스가 만들어지도록 싱글톤으로 구현
 * */
object RetrofitInstance {

    //LAZY를 사용하는 이유: 여러개의 Retrofit 객체가 만들어지면 자원도 낭비되고 통신에 혼선이 발생하므로
    //스프링의 LazyLoading과 비슷
    private val okHttpClient: OkHttpClient by lazy {
        val httpLoggingInterceptor = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder().addInterceptor(httpLoggingInterceptor).build()
    }

    //Retrofit 객체 생성
    private val retrofit: Retrofit by lazy {
        //DTO 변환에 사용할 MoshiConverterFactory를 Json Converter로 지정
        //OkHttp Core는 서버와 애플리케이션 사이에서 데이터를 인터셉터하는 기능이 있어 okHttpClient 인터셉터를 넘겨주어 로그 캣에서 패킷 내용 모니터링
        Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient).baseUrl(BASE_URL).build() //build로 객체 생성
    }

    val api: BookSearchApi by lazy {
        //BookSearchApi 객체 생성
        retrofit.create(BookSearchApi::class.java)
    }

    fun getInstance() : Retrofit {
        return retrofit
    }
}