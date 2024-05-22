package com.news.app.feature_saved.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import com.news.app.feature_saved.databinding.FragmentSavedBinding
import com.news.app.feature_saved.ui.adapters.ArticlesAdapter
import com.news.app.feature_saved.ui.viewModel.SavedViewModel
import com.news.core.MainAppNavigator
import com.news.data.data_api.model.Article

const val SEARCH_ENABLED_KEY = "searchKey"
const val SEARCH_ENABLED = "searchEnable"
const val SEARCH_TEXT_ENTERED_KEY = "searchTextEntered"
const val SEARCH_TEXT = "searchText"

class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private val navigator by lazy {
        requireActivity() as MainAppNavigator
    }
    private val savedViewModel: SavedViewModel by lazy {
        ViewModelProvider(this)[SavedViewModel::class.java]
    }
    private lateinit var articlesAdapter: ArticlesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSavedBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initFragment()
    }

    private fun initFragment() {
        setAdapter(arrayListOf())
        savedViewModel.savedList.observe(viewLifecycleOwner) { articlesList ->
            showLoadedList(articlesList)
        }
        binding.refreshLayout.setOnRefreshListener {
            initFragment()
        }
        setListenerForSearchMode()
        observeError()
        savedViewModel.init((requireActivity().application as com.news.core.App).provideAppDependenciesProvider())
        showLoading()
    }

    private fun setListenerForSearchMode() {
        setFragmentResultListener(SEARCH_ENABLED_KEY) { _, bundle ->
            if (bundle.getBoolean(SEARCH_ENABLED)) {
                setSearchModeToFragment()
            } else disableSearchMode()
        }
    }

    private fun showLoadedList(articlesList: ArrayList<Article>) {
        displaySavedList(articlesList)
        hideLoading()
        binding.refreshLayout.isRefreshing = false
    }

    private fun observeError() {
        savedViewModel.errorState.observe(viewLifecycleOwner) { error ->
            if (error != null) navigator.showError(error.errorType, error.errorFunction)
        }
        savedViewModel.unshowError.observe(viewLifecycleOwner) { error ->
            if (error != null) navigator.removeError()
        }
    }

    private fun setSearchModeToFragment() {
        if (this.isResumed) {
            articlesAdapter.setSearchMode()
            displaySavedList(arrayListOf())
            setFragmentResultListener(SEARCH_TEXT_ENTERED_KEY) { _, bundle ->
                savedViewModel.gotSearchText(bundle.getString(SEARCH_TEXT) ?: "")
            }
        }
    }

    private fun disableSearchMode() {
        articlesAdapter.disableSearchMode()
        displaySavedList(arrayListOf())
        savedViewModel.refreshView()
    }

    private fun setAdapter(articlesList: ArrayList<Article>) {
        articlesAdapter =
            ArticlesAdapter(articlesList, requireActivity() as MainAppNavigator, requireContext())
        binding.newsRecyclerView.adapter = articlesAdapter
    }

    private fun showLoading() {
        binding.newsRecyclerView.isVisible = false
        binding.loadingProgressBar.isVisible = true
    }

    private fun displaySavedList(newsList: ArrayList<Article>) {
        articlesAdapter.setData(articlesAdapter.arrayList, newsList)
    }

    private fun hideLoading() {
        binding.newsRecyclerView.isVisible = true
        binding.loadingProgressBar.isVisible = false
    }
}