package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.news.app.common.Navigator
import com.news.app.core.App
import com.news.app.data.model.Article
import com.news.app.data.model.Source
import com.news.app.databinding.FragmentSourcesBinding
import com.news.app.ui.activity.MainActivity
import com.news.app.ui.adapters.ArticlesAdapter
import com.news.app.ui.adapters.SourcesAdapter
import com.news.app.ui.viewmodels.SourcesViewModel

class SourcesFragment : Fragment() {

    private lateinit var binding: FragmentSourcesBinding
    private val sourcesViewModel: SourcesViewModel by lazy {
        ViewModelProvider(this)[SourcesViewModel::class.java]
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSourcesBinding.inflate(layoutInflater)
        initFragment()
        return binding.root
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
        sourcesViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        showLoading()
    }

    fun showArticles(source: String) {
        sourcesViewModel.articlesList.observe(viewLifecycleOwner) { sourcesList ->
            setAdapterArticles(sourcesList)
            hideLoading()
            binding.refreshLayout.isRefreshing = false
        }
        sourcesViewModel.sourceClicked(source)
    }

    private fun setAdapter(sourcesList: ArrayList<Source>) {
        val sourcesAdapter =
            SourcesAdapter(sourcesList, this)
        binding.sourcesRecyclerView.adapter = sourcesAdapter
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
}