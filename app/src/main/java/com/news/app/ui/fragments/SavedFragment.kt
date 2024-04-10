package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.news.app.core.App
import com.news.app.data.model.Article
import com.news.app.databinding.FragmentSavedBinding
import com.news.app.ui.activity.MainActivity
import com.news.app.ui.adapters.ArticlesAdapter
import com.news.app.ui.viewmodels.SavedViewModel


class SavedFragment : Fragment() {

    private lateinit var binding: FragmentSavedBinding
    private val savedViewModel: SavedViewModel by lazy {
        ViewModelProvider(this)[SavedViewModel::class.java]
    }

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
        }
        savedViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        showLoading()
    }

    private fun setAdapter(articlesList: ArrayList<Article>) {
        val articlesAdapter =
            ArticlesAdapter(articlesList, requireActivity() as MainActivity, requireContext())
        binding.newsRecyclerView.adapter = articlesAdapter
    }

    private fun showLoading() {
        binding.newsRecyclerView.isVisible = false
        binding.loadingProgressBar.isVisible = true
    }

    private fun hideLoading() {
        binding.newsRecyclerView.isVisible = true
        binding.loadingProgressBar.isVisible = false
    }
}