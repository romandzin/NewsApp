package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.news.app.common.Extensions.getParcelableCompat
import com.news.app.core.App
import com.news.app.data.model.Article
import com.news.app.databinding.FragmentSavedBinding
import com.news.app.ui.activity.MainActivity
import com.news.app.ui.activity.SEARCH_ENABLED
import com.news.app.ui.activity.SEARCH_ENABLED_KEY
import com.news.app.ui.activity.SEARCH_TEXT
import com.news.app.ui.activity.SEARCH_TEXT_ENTERED_KEY
import com.news.app.ui.adapters.ArticlesAdapter
import com.news.app.ui.model.Filters
import com.news.app.ui.model.InAppError
import com.news.app.ui.viewmodels.SavedViewModel


class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private val navigator by lazy {
        requireActivity() as MainActivity
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
        initFragment()
        return binding.root
    }

    private fun initFragment() {
        savedViewModel.savedList.observe(viewLifecycleOwner) { articlesList ->
            setAdapter(articlesList)
            hideLoading()
            binding.refreshLayout.isRefreshing = false
        }
        binding.refreshLayout.setOnRefreshListener {
            initFragment()
        }
        setFragmentResultListener(SEARCH_ENABLED_KEY) { _, bundle ->
            if (bundle.getBoolean(SEARCH_ENABLED)) {
                setSearchModeToFragment()
            }
            else disableSearchMode()
        }
        observeError()
        savedViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        showLoading()
    }

    private fun observeError() {
        val errorObserver = Observer<InAppError?> { error ->
            if (error != null) navigator.showError(error.errorType, error.errorFunction)
        }
        savedViewModel.errorState.observe(viewLifecycleOwner, errorObserver)
        val unShowErrorObserver = Observer<Boolean?> {
            if (it != null) navigator.removeError()
        }
        savedViewModel.unshowError.observe(viewLifecycleOwner, unShowErrorObserver)
    }

    private fun setSearchModeToFragment() {
        if (this.isResumed) {
            displaySavedList(arrayListOf())
            setFragmentResultListener(SEARCH_TEXT_ENTERED_KEY) { _, bundle ->
                savedViewModel.filter(bundle.getString(SEARCH_TEXT) ?: "")
            }
        }
    }

    private fun disableSearchMode() {
        displaySavedList(arrayListOf())
        savedViewModel.getList()
    }

    private fun setAdapter(articlesList: ArrayList<Article>) {
        articlesAdapter =
            ArticlesAdapter(articlesList, requireActivity() as MainActivity, requireContext())
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