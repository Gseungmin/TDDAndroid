package com.example.booksearchapp.ui.view

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.booksearchapp.R
import com.example.booksearchapp.databinding.FragmentFavoriteBinding
import com.example.booksearchapp.ui.adapter.BookSearchAdapter
import com.example.booksearchapp.ui.viewmodel.BookSearchViewModel
import com.google.android.material.snackbar.Snackbar

class FavoriteFragment : Fragment() {

    private var _binding : FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookSearchViewModel: BookSearchViewModel
    private lateinit var bookSearchAdapter: BookSearchAdapter

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
        bookSearchViewModel = (activity as MainActivity).viewModel

        setupRecyclerView()
        setupTouchHelper(view)

        //observing을 통해 자동으로 view 갱신
        bookSearchViewModel.favoriteBooks.observe(viewLifecycleOwner) {
            bookSearchAdapter.submitList(it)
        }
    }

    private fun setupRecyclerView() {
        bookSearchAdapter = BookSearchAdapter()
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
        bookSearchAdapter.setOnItemClick {
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
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            //swipe 동작이 발생했을때 발생하는 작업
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                //터치한 viewHolder 위치
                val position = viewHolder.bindingAdapterPosition
                //터치한 viewHolder 위치를 Adapter에 전달하여 현재 아이템을 획득
                val book = bookSearchAdapter.currentList[position]
                bookSearchViewModel.deleteBook(book)
                //undo를 누르면 다시 아이템 저장
                Snackbar.make(view, "Book has deleted", Snackbar.LENGTH_SHORT).apply {
                    setAction("Undo") {
                        bookSearchViewModel.saveBook(book)
                    }
                }.show()
            }
        }
        ItemTouchHelper(itemTouchHelperCallback).apply {
            //attachToRecyclerView 드래그 동작 인식
            attachToRecyclerView(binding.rvFavoriteBooks)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}