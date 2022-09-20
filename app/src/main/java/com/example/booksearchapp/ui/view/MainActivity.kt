package com.example.booksearchapp.ui.view

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booksearchapp.R
import com.example.booksearchapp.data.db.BookSearchDatabase
import com.example.booksearchapp.data.repository.BookSearchRepositoryImpl
import com.example.booksearchapp.databinding.ActivityMainBinding
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModelProviderFactory
import com.example.booksearchapp.util.Constants.DATASTORE_NAME

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    lateinit var viewModel: BookSearchViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    //dataStore의 싱글톤 객체 생성
    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setupJetpackNavigation()

        //앱이 처음 실행되었을 경우에만 화면에 SearchFragment 표현
        //액티비티가 재생성 되었을 경우에는 굳이 첫번째 화면을 표시할 필요가 없기 때문
        //savedInstanceState가 앱이 처음 실행되었는지 여부 판단
//        if (savedInstanceState == null) {
//            binding.navBottom.selectedItemId = R.id.fragment_search
//        }

        val database = BookSearchDatabase.getInstance(this)
        val bookSearchRepository = BookSearchRepositoryImpl(database, dataStore)
        val factory = BookSearchViewModelProviderFactory(bookSearchRepository, this)
        //viewModel 초기화
        viewModel = ViewModelProvider(this, factory).get(BookSearchViewModel::class.java)
    }

    private fun setupJetpackNavigation() {
        //NavController 인스턴스 생성
        val host = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment? ?: return
        navController = host.navController
        //bottomNav와 NavController를 연결, 이러면 Navgation이 Fragment전환을 수행 해준다
        binding.navBottom.setupWithNavController(navController)

        //appBarConfiguration에 NavGraph를 넘겨줌
        appBarConfiguration = AppBarConfiguration(
            //navController.graph라고만 하면 뒤로가기에 hostFragment로 이동
            setOf(
                R.id.fragment_search, R.id.fragment_favorite, R.id.fragment_setting
            )
        )
        //setupActionBarWithNavController로 navController와 appBarConfiguration 연결
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}