package com.news.feature_source.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.news.core.App
import com.news.core.MainAppNavigator
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.InAppError
import com.news.data.data_api.model.Source
import com.news.feature_source.databinding.FragmentSourcesBinding
import com.news.feature_source.ui.adapters.ArticlesAdapter
import com.news.feature_source.ui.adapters.SourcesAdapter
import com.news.feature_source.ui.viewModel.SourcesViewModel

const val SEARCH_ENABLED_KEY = "searchKey"
const val SEARCH_ENABLED = "searchEnable"
const val SEARCH_TEXT_ENTERED_KEY = "searchTextEntered"
const val SEARCH_TEXT = "searchText"
class SourcesFragment : Fragment() {

    private lateinit var binding: FragmentSourcesBinding
    private val sourcesViewModel: SourcesViewModel by lazy {
        ViewModelProvider(this)[SourcesViewModel::class.java]
    }
    private val navigator by lazy {
        requireActivity() as MainAppNavigator
    }
    private var articlesAdapter: ArticlesAdapter? = null
    private lateinit var sourcesAdapter: SourcesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSourcesBinding.inflate(layoutInflater)
        initFragment()
        return binding.root
    }

    private fun observeError() {
        val errorObserver = Observer<InAppError?> { error ->
            if (error != null) {
                navigator.removeError()
                navigator.showError(error.errorType, error.errorFunction)
            }
        }
        sourcesViewModel.errorState.observe(viewLifecycleOwner, errorObserver)
        val unShowErrorObserver = Observer<Boolean?> {
            if (it != null) navigator.removeError()
        }
        sourcesViewModel.unshowError.observe(viewLifecycleOwner, unShowErrorObserver)
    }

    private fun initFragment() {
        setAdapter(arrayListOf())
        sourcesViewModel.sourcesList.observe(viewLifecycleOwner) { sourcesList ->
            showLoadedList(sourcesList)
        }
        binding.refreshLayout.setOnRefreshListener {
            sourcesViewModel.pulledToRefresh()
        }
        observeError()
        setListenerForSearchMode()
        sourcesViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        showLoading()
    }

    private fun setListenerForSearchMode() {
        setFragmentResultListener(SEARCH_ENABLED_KEY) { _, bundle ->
            if (bundle.getBoolean(SEARCH_ENABLED)) {
                setSearchModeToFragment()
            } else disableSearchMode()
        }
    }

    private fun <T> showLoadedList(arrayList: ArrayList<T>) {
        if (arrayList[0] is Source) displaySourcesList(arrayList as ArrayList<Source>)
        else displayArticlesList(arrayList as ArrayList<Article>)
        hideLoading()
        binding.refreshLayout.isRefreshing = false
    }

    private fun displaySourcesList(newsList: ArrayList<Source>) {
        sourcesAdapter.setData(sourcesAdapter.arrayList, newsList)
    }

    private fun displayArticlesList(newsList: ArrayList<Article>) {
        articlesAdapter!!.setData(articlesAdapter!!.arrayList, newsList)
    }
    fun showArticles(source: String, name: String) {
        showLoading()
        setAdapterArticles(arrayListOf())
        navigator.sourcesShowingArticles(name)
        sourcesViewModel.articlesList.observe(viewLifecycleOwner) { articlesList ->
            if (articlesList != null) showLoadedList(articlesList)
        }
        sourcesViewModel.sourceClicked(source)
    }

    private fun setAdapter(sourcesList: ArrayList<Source>) {
        sourcesAdapter =
            SourcesAdapter(sourcesList, this)
        binding.sourcesRecyclerView.adapter = sourcesAdapter
    }

    private fun setSearchModeToFragment() {
        if (this.isResumed) {
            sourcesAdapter.setSearchMode()
            articlesAdapter?.setSearchMode()
            displayEmptyList()
            setFragmentResultListener(SEARCH_TEXT_ENTERED_KEY) { _, bundle ->
                sourcesViewModel.filter(bundle.getString(SEARCH_TEXT) ?: "")
            }
        }
    }

    private fun disableSearchMode() {
        sourcesAdapter.disableSearchMode()
        articlesAdapter?.disableSearchMode()
        displayEmptyList()
        initFragment()
    }

    private fun displayEmptyList() {
        val adapter = binding.sourcesRecyclerView.adapter
        if (adapter is ArticlesAdapter)
        adapter.setData(adapter.arrayList, arrayListOf())
        else (adapter as SourcesAdapter).setData(adapter.arrayList, arrayListOf())
    }

    private fun setAdapterArticles(sourcesList: ArrayList<Article>) {
        articlesAdapter =
            ArticlesAdapter(
                sourcesList,
                requireActivity() as MainAppNavigator,
                requireContext()
            )
        binding.sourcesRecyclerView.adapter = articlesAdapter
    }

    private fun showLoading() {
        binding.sourcesRecyclerView.isVisible = false
        binding.loadingProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.sourcesRecyclerView.isVisible = true
        binding.loadingProgressBar.isVisible = false
    }

    fun goBack() {
        initFragment()
    }
}