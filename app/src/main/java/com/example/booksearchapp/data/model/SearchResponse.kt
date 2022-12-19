package com.example.booksearchapp.data.model


import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
//Json은 데이터를 중괄호로 감싸 표현하는데 이것을 DTO로 변환할때는 객체 하나를 데이터 클래스 하나로 변경시키므로 3개의 데이터 클래스가 만들어지는 것
//기본적으로 Document, meta 두개의 클래스가 만들어지며 그 클래스를 감싸는 클래스 총 3개의 클래스가 만들어짐
//Json 만으로는 코틀린에서 변환에 실패하므로 field:를 붙여주어야 함
data class SearchResponse(
    @field:Json(name = "documents")
    val documents: List<Book>,
    @field:Json(name = "meta")
    val meta: Meta
)