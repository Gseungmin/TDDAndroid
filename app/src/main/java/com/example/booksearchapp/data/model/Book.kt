package com.example.booksearchapp.data.model


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

@Parcelize
@JsonClass(generateAdapter = true)
//데이터를 받아올 클래스
//Room에서 사용하기 위해 Entity 클래스로 만든다
@Entity(tableName = "books")
data class Book(
    @field:Json(name = "authors")
    val authors: List<String>,
    @field:Json(name = "contents")
    val contents: String,
    @field:Json(name = "datetime")
    val datetime: String,
    @PrimaryKey(autoGenerate = false)
    @field:Json(name = "isbn")
    val isbn: String,
    @field:Json(name = "price")
    val price: Int,
    @field:Json(name = "publisher")
    val publisher: String,
    @ColumnInfo(name = "sale_price")
    @field:Json(name = "sale_price")
    val salePrice: Int,
    @field:Json(name = "status")
    val status: String,
    @field:Json(name = "thumbnail")
    val thumbnail: String,
    @field:Json(name = "title")
    val title: String,
    @field:Json(name = "translators")
    val translators: List<String>,
    @field:Json(name = "url")
    val url: String
) : Parcelable