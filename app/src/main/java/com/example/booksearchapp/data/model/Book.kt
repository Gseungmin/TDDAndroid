package com.example.booksearchapp.data.model


import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import kotlinx.parcelize.Parcelize

/**
 * 데이터 직렬화(serialization)
 * 메모리를 디스크에 저장하거나 네트워크 통신에 사용하기 위한 형식으로 변환하는 것을 말한다.
 * 1. 값 형식 데이터(Value Type) : 우리가 흔히 선언해서 사용하는 int, float, char 등, 값 형식 데이터들은 스택에 메모리가 쌓이고 직접 접근이 가능
 * 2. 참조 형식 데이터(Reference Type) : 해당 형식의 변수를 선언하면 힙에 메모리가 할당되고 스택에서는 이 힙 메모리를 참조하는 구조
 * 두가지 데이터 중에서 디스크에 저장하거나 통신에는 값 형식 데이터(Value Type)만 가능
 * 참조 형식 데이터(Reference Type)는 실제 데이터 값이 아닌 힙에 할당되어있는 메모리 번지 주소를 가지고 있기 때문에 저장, 통신에 사용할 수 없음
 * 프로그램을 종료하고 다시 실행하면 기존의 주소값을 가져오더라도 기존 A 객체의 데이터를 가져올 수 없기 때문이다
 * 네트워크 통신 또한 마찬가지로 각 PC마다 사용하고있는 메모리 공간 주소는 전혀 다르므로 내가 다른 PC로 전송한 A객체 데이터(0x00045523)은 무의미하다.
 * 직렬화를 하게 되면 각 주소값이 가지는 데이터들을 전부 끌어모아서 값 형식(Value Type)데이터로 변환
 * 이러한 이유 때문에 데이터를 저장, 통신전에 '데이터 직렬화(Serialization)' 작업이 필요한 것
 * 직렬화를 쓰는 이유는 사용하고 있는 데이터들을 파일 저장 혹은 데이터 통신에서 파싱 할 수 있는 유의미한 데이터를 만들기 위함이다.
*/

@Parcelize
@JsonClass(generateAdapter = true)
//데이터를 받아올 클래스
//safe args로 전달할 클래스는 직렬화가 가능해야 한다
//따라서 book class는 fragment간 safe args로 전달되므로 Parcelable 상속
@Entity(tableName = "books") //RoomDatabase에서 사용할 Entity로 만들어 줌
data class Book(

    //primitive 타입이 아니므로 변환 필요
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