package com.example.booksearchapp

import com.example.booksearchapp.ui.view.MainActivityTest
import com.example.booksearchapp.ui.viewmodel.BookViewModelTest
import com.example.booksearchapp.util.CalculatorTest
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

@RunWith(Suite::class)
@ExperimentalCoroutinesApi
@Suite.SuiteClasses(
    MainActivityTest::class,
    CalculatorTest::class
)
class InstrumentedTestSuite {
}