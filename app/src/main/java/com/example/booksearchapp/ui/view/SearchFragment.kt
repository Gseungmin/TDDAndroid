package com.example.booksearchapp.ui.view

import android.os.Bundle
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.booksearchapp.databinding.FragmentSearchBinding
import com.example.booksearchapp.ui.adapter.BookSearchLoadStateAdapter
import com.example.booksearchapp.ui.adapter.BookSearchPagingAdapter
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.example.booksearchapp.util.Constants.SEARCH_BOOKS_TIME_DELAY
import com.example.booksearchapp.util.collectLatestStateFlow
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

//    private lateinit var bookSearchViewModel: BookSearchViewModel

    private val bookSearchViewModel by activityViewModels<BookSearchViewModel>()

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
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        val view = binding.root
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //mainActivity에서 초기화한 viewModel을 가지고 온다.
//        bookSearchViewModel = (activity as MainActivity).viewModel

        setupRecyclerView()
        searchBooks()
        setupLoadState()
        //viewModel 값의 변화 감지
//        bookSearchViewModel.searchResult.observe(viewLifecycleOwner) { response ->
//            val books = response.documents
//            bookSearchAdapter.submitList(books)
//        }

        collectLatestStateFlow(bookSearchViewModel.searchPagingResult) {
            bookSearchAdapter.submitData(it)
        }
    }

    //RecyclerView 설정
    private fun setupRecyclerView() {
//        bookSearchAdapter = BookSearchAdapter()

        bookSearchAdapter = BookSearchPagingAdapter()
        binding.rvSearchResult.apply {
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
//            adapter = bookSearchAdapter
            //헤더와 푸터 둘다 설정 가능
            //페이징 데이터 어댑터에 로드 스테이트 어댑터를 연결하면 데이터의 로딩 상태를 리사이클러뷰의 내부의 헤더나 푸터로 표시할 수 있음
            adapter = bookSearchAdapter.withLoadStateFooter(
                footer = BookSearchLoadStateAdapter(bookSearchAdapter::retry)
            )
        }
        //클릭 리스너 설정
        bookSearchAdapter.setOnItemClickListener {
            val action = SearchFragmentDirections.actionFragmentSearchToBookFragment(it)
            findNavController().navigate(action)
        }
    }

    //Edit Text
    private fun searchBooks() {
        var startTime = System.currentTimeMillis()
        var endTime: Long

        //쿼리를 저장하고 불러오는 코드
        binding.etSearch.text =
            Editable.Factory.getInstance().newEditable(bookSearchViewModel.query)

        //addTextChangedListener는 TextInputLayout에 EditText속성을 가지는데 값이 변할때마다 viewModel로 결과가 전달
        //EditText의 값이 변할때마다 searchResult 값이 갱신되게 됨
        binding.etSearch.addTextChangedListener { text: Editable? ->
            endTime = System.currentTimeMillis()
            //처음 입력과 두번째 입력 사이의 차이가 100M초를 넘을때 실행
            if (endTime - startTime >= SEARCH_BOOKS_TIME_DELAY) {
                text?.let {
                    val query = it.toString().trim()
                    if (query.isNotEmpty()) {
//                        bookSearchViewModel.searchBooks(query)
                        bookSearchViewModel.searchBooksPaging(query)
                        bookSearchViewModel.query = query
                    }
                }
            }
            startTime = endTime
        }
    }

    private fun setupLoadState() {
        //addLoadStateListener를 달고 LoadStates 값을 받아옴
        //combinedLoadStates는 페이징 소스와 remoteImmediate 소스의 로딩 상태를 가지고 있음
        //여기서는 remoteImmediate는 다루지 않기 때문에 source에만 대응
        //loadState는 로딩 시작에 만들어지는 pretend, 종료시 만들어지는 append, 로딩 값을 갱신할때 만들어지는 refresh를 속성을고 가짐
        bookSearchAdapter.addLoadStateListener { combinedLoadStates ->
            val loadState = combinedLoadStates.source
            //item이 1개 미만이고 LoadState.NotLoading이면서 loadState.append.endOfPaginationReached이면 리스트가 비어있는지 판정
            val isListEmpty = bookSearchAdapter.itemCount < 1
                    && loadState.refresh is LoadState.NotLoading
                    && loadState.append.endOfPaginationReached

            //검색결과가 없으면 noResult 표시
            binding.tvEmptylist.isVisible = isListEmpty
            binding.rvSearchResult.isVisible = !isListEmpty
            //로딩중일때는 progressBar표시
            binding.progressBar.isVisible = loadState.refresh is LoadState.Loading

            //PagingDataAdapter에 loadStateAdapter를 연결하면 데이터의 로딩 상태를 recyclerView 내부의 헤더나 푸터로 표현할 수 있음
        }
    }

    //viewBinding이 더이상 필요 없을 경우 null 처리 필요
    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }
}