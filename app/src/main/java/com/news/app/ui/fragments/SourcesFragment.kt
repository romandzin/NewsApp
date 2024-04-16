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
import com.news.app.common.Navigator
import com.news.app.core.App
import com.news.app.domain.model.Article
import com.news.app.domain.model.Source
import com.news.app.databinding.FragmentSourcesBinding
import com.news.app.ui.activity.SEARCH_ENABLED
import com.news.app.ui.activity.SEARCH_ENABLED_KEY
import com.news.app.ui.activity.SEARCH_TEXT
import com.news.app.ui.activity.SEARCH_TEXT_ENTERED_KEY
import com.news.app.ui.adapters.ArticlesAdapter
import com.news.app.ui.adapters.SourcesAdapter
import com.news.app.ui.model.InAppError
import com.news.app.ui.viewmodels.SourcesViewModel

class SourcesFragment : Fragment() {

    private lateinit var binding: FragmentSourcesBinding
    private val sourcesViewModel: SourcesViewModel by lazy {
        ViewModelProvider(this)[SourcesViewModel::class.java]
    }
    private val navigator by lazy {
        requireActivity() as Navigator
    }

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
        sourcesViewModel.sourcesList.observe(viewLifecycleOwner) { sourcesList ->
            setAdapter(sourcesList)
            hideLoading()
            binding.refreshLayout.isRefreshing = false
        }
        binding.refreshLayout.setOnRefreshListener {
            initFragment()
        }
        observeError()
        setFragmentResultListener(SEARCH_ENABLED_KEY) { _, bundle ->
            if (bundle.getBoolean(SEARCH_ENABLED)) {
                setSearchModeToFragment()
            }
            else disableSearchMode()
        }
        sourcesViewModel.init((requireActivity().application as App).provideAppDependenciesProvider(), requireContext())
        showLoading()
    }

    fun showArticles(source: String, name: String) {
        navigator.sourcesShowingArticles(name)
        sourcesViewModel.articlesList.observe(viewLifecycleOwner) { sourcesList ->
            setAdapterArticles(sourcesList)
            hideLoading()
            binding.refreshLayout.isRefreshing = false
        }
        sourcesViewModel.sourceClicked(source, requireContext())
    }

    private fun setAdapter(sourcesList: ArrayList<Source>) {
        val sourcesAdapter =
            SourcesAdapter(sourcesList, this)
        binding.sourcesRecyclerView.adapter = sourcesAdapter
    }

    private fun setSearchModeToFragment() {
        if (this.isResumed) {
            displayEmptyList()
            setFragmentResultListener(SEARCH_TEXT_ENTERED_KEY) { _, bundle ->
                sourcesViewModel.filter(bundle.getString(SEARCH_TEXT) ?: "")
            }
        }
    }

    private fun disableSearchMode() {
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
        val articlesAdapter =
            ArticlesAdapter(sourcesList, requireActivity() as Navigator, requireContext())
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