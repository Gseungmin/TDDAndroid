package com.example.booksearchapp.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksearchapp.databinding.FragmentFavoriteBinding
import com.example.booksearchapp.ui.adapter.BookSearchPagingAdapter
import com.example.booksearchapp.util.collectLatestStateFlow
import com.google.android.material.snackbar.Snackbar
import com.qualitybitz.booksearchapp.ui.viewmodel.FavoriteViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FavoriteFragment : Fragment() {

    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    //    private lateinit var bookSearchViewModel: BookSearchViewModel
    private val bookSearchViewModel by viewModels<FavoriteViewModel>()

    //    private lateinit var bookSearchAdapter: BookSearchAdapter
    private lateinit var bookSearchAdapter: BookSearchPagingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        bookSearchViewModel = (activity as MainActivity).viewModel

        setupRecyclerView()
        setupTouchHelper(view)

        /**
         * livedata 사용
         * */
        //observing을 통해 자동으로 view 갱신
//        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner) {
//            bookSearchAdapter.submitList(it)
//        }

        /**
         * flow 사용
         * */
        //observer를 사용하는 LiveData와 다르게 Flow는 코루틴 안에서 collectLatest를 써서 데이터 구독
        //favoriteBooks를 stateFlow로 변환해서 favoriteFragment 생명주기와 동기화
//        lifecycleScope.launch {
//            bookSearchViewModel.favoriteBooks.collectLatest {
//                bookSearchAdapter.submitList(it)
//            }
//        }

        /**
         * stateflow 사용
         * */
        //StateFlow의 구독이 되면서 favoriteFragment 생명주기와 동기화
//        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
//                bookSearchViewModel.favoriteBooks.collectLatest {
//                    bookSearchAdapter.submitList(it)
//                }
//            }
//        }

        /**
         * stateflow 확장함수로 사용
         * */
//        //확장 함수로 처리
//        collectLatestStateFlow(bookSearchViewModel.favoriteBooks) {
//            bookSearchAdapter.submitList(it)
//        }

        /**
         * favoriteBooks를 처리하던 코루틴에서 favoritePagingBooks로 전환
         * */
        //확장 함수로 처리
        //페이징 데이터는 시간에 따라 변화하는 특성을 가지고 있으므로 collectLatest로 값을 가져와야 한다
        //따라서 기존의 Paging 값을 캔슬하고 새 값을 가지도록 해야 함
        collectLatestStateFlow(bookSearchViewModel.favoritePagingBooks) {
            bookSearchAdapter.submitData(it) //submitList가 아닌 submitData
        }
    }

    private fun setupRecyclerView() {
//        bookSearchAdapter = BookSearchAdapter()
        bookSearchAdapter = BookSearchPagingAdapter()
        binding.rvFavoriteBooks.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
            adapter = bookSearchAdapter
        }
        //search한 아이템의 url 속성을 bookFragment에 전달
        bookSearchAdapter.setOnItemClickListener {
            val action = FavoriteFragmentDirections.actionFragmentFavoriteToFragmentBook(it)
            findNavController().navigate(action)
        }
    }

    //아이템을 왼쪽으로 스와이프하면 데이터 삭제
    private fun setupTouchHelper(view: View) {
        //아이템 swipe를 위해 SimpleCallback를 만듬
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT //드래그는 사용하지 않을 것이기때문에 0, swipe 방향은 왼쪽만 인식
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder,
            ): Boolean {
                return true
            }

            //swipe 동작이 발생했을때 발생하는 작업
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //터치한 viewHolder 위치
                val position = viewHolder.bindingAdapterPosition
                //터치한 viewHolder 위치를 Adapter에 전달하여 현재 아이템을 획득
//                val book = bookSearchAdapter.currentList[position]
//                bookSearchViewModel.deleteBook(book)
//                //undo를 누르면 다시 아이템 저장
//                Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
//                    setAction("Undo") {
//                        bookSearchViewModel.saveBook(book)
//                    }
//                }.show()

                val pagedBook = bookSearchAdapter.peek(position)
                //Paging 응답이 null이 될 수 있으므로 따로 null 처리
                pagedBook?.let { book ->
                    bookSearchViewModel.deleteBook(book)
                    Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
                        setAction("Undo") {
                            bookSearchViewModel.saveBook(book)
                        }
                    }.show()
                }
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            //attachToRecyclerView 드래그 동작 인식
            //recyclerView와 연결해 swipe나 drag동작 인식
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }

    //viewBinding이 더이상 필요 없을 경우 null 처리 필요
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}