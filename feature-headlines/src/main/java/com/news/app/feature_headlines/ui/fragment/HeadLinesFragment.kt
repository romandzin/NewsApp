package com.news.app.feature_headlines.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.tabs.TabLayout
import com.news.app.feature_headlines.databinding.FragmentHeadLinesBinding
import com.news.app.feature_headlines.common.Extensions.getParcelableCompat
import com.news.app.feature_headlines.ui.adapters.ArticlesAdapter
import com.news.app.feature_headlines.ui.moxy.MvpAppCompatFragment
import com.news.app.feature_headlines.ui.moxy.views.HeadLinesView
import com.news.app.feature_headlines.ui.presenters.HeadlinesPresenter
import com.news.core.App
import com.news.core.MainAppNavigator
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Filters
import moxy.ktx.moxyPresenter
import moxy.presenter.InjectPresenter

const val SEARCH_ENABLED_KEY = "searchKey"
const val SEARCH_ENABLED = "searchEnable"
const val SEARCH_TEXT_ENTERED_KEY = "searchTextEntered"
const val SEARCH_TEXT = "searchText"

class HeadLinesFragment : MvpAppCompatFragment(), HeadLinesView {

    @InjectPresenter
    lateinit var headlinesPresenter: HeadlinesPresenter
    private lateinit var binding: FragmentHeadLinesBinding
    private lateinit var articlesAdapter: ArticlesAdapter
    private val navigator by lazy {
        requireActivity() as MainAppNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeadLinesBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        headlinesPresenter.init((requireActivity().application as App).provideAppDependenciesProvider())
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
        binding.refreshLayout.setOnRefreshListener {
            binding.refreshLayout.isRefreshing = false
            headlinesPresenter.refreshView()
        }
        setFragmentResultListeners()
    }

    private fun setFragmentResultListeners() {
        setFragmentResultListener(SEARCH_ENABLED_KEY) { _, bundle ->
            if (bundle.getBoolean(SEARCH_ENABLED)) {
                headlinesPresenter.searchEnabledResultGet()
            } else headlinesPresenter.searchDisabledResultGet()
        }
        setFragmentResultListener(SEND_FILTERS_KEY) { _, bundle ->
            headlinesPresenter.enableFilters(
                bundle.getParcelableCompat(
                    FILTERS_KEY,
                    Filters::class.java
                )
            )
            setAnotherMode()
        }
        setFragmentResultListener(DISABLE_FILTERS_KEY) { _, _ ->
            setDefaultMode()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshView()
    }


    override fun disableSearchModeInFragment() {
        displayNewsList(arrayListOf())
        setDefaultMode()
        articlesAdapter.disableSearchMode()
        headlinesPresenter.refreshView()
    }

    override fun setSearchModeToFragment() {
        if (this.isResumed) {
            setAnotherMode()
            articlesAdapter.setSearchMode()
            displayNewsList(arrayListOf())
            setFragmentResultListener(SEARCH_TEXT_ENTERED_KEY) { _, bundle ->
                headlinesPresenter.searchInArrayByText(bundle.getString(SEARCH_TEXT) ?: "")
            }
        }
    }

    private fun setAnotherMode() {
        headlinesPresenter.anotherModeEnabled()
        binding.tabLayout.isVisible = false
        binding.paginationProgressBar.isVisible = false
    }

    override fun setDefaultMode() {
        headlinesPresenter.defaultModeIsSet()
        binding.tabLayout.isVisible = true
        binding.paginationProgressBar.isVisible = true
    }

    override fun removeError() {
        navigator.removeError()
    }

    private fun setTabByCategory() {
        when (binding.tabLayout.selectedTabPosition) {
            0 -> tabSelected("general")
            1 -> tabSelected("business")
            2 -> tabSelected("technology")
        }
    }

    private fun setAdapter() {
        articlesAdapter =
            ArticlesAdapter(
                arrayListOf(),
                requireActivity() as MainAppNavigator,
                requireContext()
            )
        binding.newsRecyclerView.adapter = articlesAdapter
    }

    override fun tabSelected(category: String) {
        headlinesPresenter.tabSelected(category)
    }

    override fun refreshView() {
        headlinesPresenter.refreshView()
    }

    override fun setSelectedTab(index: Int) {
        val tab = binding.tabLayout.getTabAt(index)
        tab!!.select()
    }

    override fun displayNewsList(newsList: ArrayList<Article>) {
        articlesAdapter.setData(articlesAdapter.arrayList, newsList)
    }

    override fun showError(errorText: Int, lastAction: () -> Unit) {
        navigator.showError(errorText, lastAction)
    }

    override fun showLoading() {
        binding.paginationProgressBar.isVisible = false
        binding.loadingProgressBar.isVisible = true
        binding.newsRecyclerView.isVisible = false
    }

    override fun hideLoading() {
        binding.loadingProgressBar.isVisible = false
        binding.newsRecyclerView.isVisible = true
    }
}