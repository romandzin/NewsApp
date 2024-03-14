package com.news.app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.news.app.databinding.FragmentHeadLinesBinding
import com.news.app.model.data_classes.News
import com.news.app.moxy.MvpAppCompatFragment
import com.news.app.moxy.views.HeadLinesView
import com.news.app.presenters.HeadlinesPresenter
import com.news.app.ui.adapters.HeadLinesAdapter
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import moxy.presenter.InjectPresenter

class HeadLinesFragment : MvpAppCompatFragment(), HeadLinesView {

    @InjectPresenter lateinit var headlinesPresenter: HeadlinesPresenter
    private lateinit var binding: FragmentHeadLinesBinding
    private lateinit var headLinesAdapter: HeadLinesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeadLinesBinding.inflate(layoutInflater)
        headLinesAdapter = HeadLinesAdapter(arrayListOf())
        binding.newsRecyclerView.adapter = headLinesAdapter
        viewShowed()
        return binding.root
    }

    override fun viewShowed() {
        MainScope().launch {
            headlinesPresenter.viewShowed()
        }
    }

    override fun displayNewsList(newsList: ArrayList<News>) {
        requireActivity().runOnUiThread {headLinesAdapter.setData(headLinesAdapter.arrayList, newsList)}
    }

    override fun showError() {
        Toast.makeText(requireContext(), "text", Toast.LENGTH_LONG).show()
    }

}