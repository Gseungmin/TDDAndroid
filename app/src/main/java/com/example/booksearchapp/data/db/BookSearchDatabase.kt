package com.example.booksearchapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.booksearchapp.data.model.Book

/**
 * Room DB
 * 데이터를 저장하는 공간
 * */
@Database(
    //Room에서 사용할 엔티티 선언
    entities = [Book::class],
    //DB 스키마를 바꿀 때 마다 버전을 바꾸어주어 구별
    version = 1,
    exportSchema = false
)
@TypeConverters(OrmConverter::class) //Converter 등록
abstract class BookSearchDatabase : RoomDatabase() {

    //Room에서 사용할 dao 지정
    abstract fun bookSearchDao(): BookSearchDao

//    //DB 객체도 생성하는데 비용이 많이 들기 때문에 중복으로 생성하지 않도록 싱글톤 설정
//    companion object {
//
//        //Java변수를 Main Memory에 저장하겠다는 것을 명시하는 것
//        //Multi-Thread 환경을 대비하기 위해 사용
//        @Volatile
//        private var INSTANCE: BookSearchDatabase? = null
//
//        private fun buildDatabase(context: Context): BookSearchDatabase =
//            Room.databaseBuilder(
//                context.applicationContext,
//                BookSearchDatabase::class.java,
//                "favorite-books"
//            ).build()
//
//        fun getInstance(context: Context): BookSearchDatabase =
//            INSTANCE ?: synchronized(this) {
//                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
//            }
//    }

    //ROOM은 orm 수행시 Primitive 타입과 Boxed 타입만 사용하도록 제한
    //Room에서 일반 객체로 orm을 수행하면 ui 쓰레드에서 lazy로딩을 해야하는데 그러면 처리 속도가 느려지고
    //그렇다고 필요하지 않은 데이터를 모두 로딩하면 메모리 낭비가 심해지므로 해당 구조 사용
}