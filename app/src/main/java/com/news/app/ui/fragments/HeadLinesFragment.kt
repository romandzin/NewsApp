package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.tabs.TabLayout
import com.news.app.data.model.Article
import com.news.app.databinding.FragmentHeadLinesBinding
import com.news.app.ui.activity.MainActivity
import com.news.app.ui.activity.SEARCH_ENABLED
import com.news.app.ui.activity.SEARCH_ENABLED_KEY
import com.news.app.ui.activity.SEARCH_TEXT
import com.news.app.ui.activity.SEARCH_TEXT_ENTERED_KEY
import com.news.app.ui.adapters.HeadLinesAdapter
import com.news.app.ui.moxy.MvpAppCompatFragment
import com.news.app.ui.moxy.views.HeadLinesView
import com.news.app.ui.presenters.HeadlinesPresenter
import moxy.presenter.InjectPresenter
import java.util.Locale


class HeadLinesFragment : MvpAppCompatFragment(), HeadLinesView {

    @InjectPresenter
    lateinit var headlinesPresenter: HeadlinesPresenter
    private lateinit var binding: FragmentHeadLinesBinding
    private lateinit var headLinesAdapter: HeadLinesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeadLinesBinding.inflate(layoutInflater)
        setAdapter()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                setTabByCategory()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                setTabByCategory()
            }

        })
        binding.nestedScrollView.setOnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (!(v as NestedScrollView).canScrollVertically(1)) {
                headlinesPresenter.scrolledToEnd()
            }
        }
        setFragmentResultListener(SEARCH_ENABLED_KEY) { _, bundle ->
            if (bundle.getBoolean(SEARCH_ENABLED)) {
                setSearchModeToFragment()
            }
            else disableSearchMode()
        }
        refreshView()
        return binding.root
    }

    private fun disableSearchMode() {
        displayNewsList(arrayListOf())
        headlinesPresenter.searchModeDisabled()
        headLinesAdapter.disableSearchMode()
        binding.tabLayout.isVisible = true
        binding.paginationProgressBar.isVisible = true
        headlinesPresenter.refreshView()
    }

    private fun setSearchModeToFragment() {
        headlinesPresenter.searchModeEnabled()
        headLinesAdapter.setSearchMode()
        binding.tabLayout.isVisible = false
        binding.paginationProgressBar.isVisible = false
        displayNewsList(arrayListOf())
        setFragmentResultListener(SEARCH_TEXT_ENTERED_KEY) { _, bundle ->
            headlinesPresenter.filter(bundle.getString(SEARCH_TEXT) ?: "")
        }
    }

    private fun setTabByCategory() {
        when (binding.tabLayout.selectedTabPosition) {
            0 -> tabSelected("general")
            1 -> tabSelected("business")
            2 -> tabSelected("technology")
        }
    }

    private fun setAdapter() {
        headLinesAdapter =
            HeadLinesAdapter(arrayListOf(), requireActivity() as MainActivity, requireContext())
        binding.newsRecyclerView.adapter = headLinesAdapter
    }

    fun setFilteredText(text: String) { //TODO добавить setFragmentResult и получать текст
        headlinesPresenter.filter(text)
    }

    override fun tabSelected(category: String) {
        headlinesPresenter.tabSelected(category)
    }

    override fun refreshView() {
        headlinesPresenter.refreshView()
        //headlinesPresenter.getList()
    }

    override fun setSelectedTab(index: Int) {
        val tab = binding.tabLayout.getTabAt(index)
        tab!!.select()
    }

    override fun displayNewsList(newsList: ArrayList<Article>) {
        headLinesAdapter.setData(headLinesAdapter.arrayList, newsList)
    }

    override fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }

    override fun showLoading() {
        binding.paginationProgressBar.isVisible = false
        binding.loadingProgressBar.isVisible = true
        binding.newsRecyclerView.isVisible = false
    }

    override fun hideLoading() {
        binding.paginationProgressBar.isVisible = true
        binding.loadingProgressBar.isVisible = false
        binding.newsRecyclerView.isVisible = true
    }

}