package com.example.booksearchapp.ui.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.booksearchapp.R
import com.example.booksearchapp.databinding.ActivityMainBinding
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

//    //dataStore의 싱글톤 객체 생성
//    private val Context.dataStore by preferencesDataStore(DATASTORE_NAME)
//
//    private val workManager = WorkManager.getInstance(application)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = binding.root
        setContentView(view)

        setupJetpackNavigation()

//        val database = BookSearchDatabase.getInstance(this)
//        //datastore 의존성을 repository에 전달
//        val bookSearchRepository = BookSearchRepositoryImpl(database, dataStore)
//        val factory = BookSearchViewModelProviderFactory(bookSearchRepository, workManager, this)
//        //viewModel 초기화
//        viewModel = ViewModelProvider(this, factory).get(BookSearchViewModel::class.java)
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