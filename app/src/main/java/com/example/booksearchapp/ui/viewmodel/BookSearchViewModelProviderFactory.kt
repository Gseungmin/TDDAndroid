package com.example.booksearchapp.ui.viewmodel

/**
 * ViewModel에 초기 값을 전달하기 위한 Factory
 * */
//@Suppress("UNCHECKED_CAST")
////class BookSearchViewModelProviderFactory(
////    private val bookSearchRepository: BookSearchRepository,
////    private val workManager: WorkManager,
////    owner: SavedStateRegistryOwner,
////    defaultArgs: Bundle? = null,
////) : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
////    override fun <T : ViewModel> create(
////        key: String,
////        modelClass: Class<T>,
////        handle: SavedStateHandle
////    ): T {
////        //bookSearchRepository를 받아서 ViewModel 반환하는 Factory
////        if (modelClass.isAssignableFrom(BookSearchViewModel::class.java)) {
////            return BookSearchViewModel(bookSearchRepository, workManager, handle) as T
////        }
////        throw IllegalArgumentException("ViewModel class not found")
////    }
////}