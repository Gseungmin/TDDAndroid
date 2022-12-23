package com.example.booksearchapp.di

import android.content.Context
import androidx.room.Room
import com.example.booksearchapp.data.db.BookSearchDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
object TestAppModule {

    @Provides
    @Named("test_db") //Hilt가 객체를 구분해서 주입해주도록 구분하기 위한 어노테이션
    //테스트가 끝나면 사라지는 객체이므로 따로 싱글톤으로 만들 필요 없음
    fun provideInMemoryDb(@ApplicationContext context: Context): BookSearchDatabase =
        Room.inMemoryDatabaseBuilder(context, BookSearchDatabase::class.java)
            .allowMainThreadQueries()
            .build()
}