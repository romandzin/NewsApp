package com.news.app.ui.fragments

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.DisposableEffect
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.news.app.R
import com.news.app.common.Extensions.getParcelableCompat
import com.news.app.core.App
import com.news.app.data.model.Article
import com.news.app.databinding.FragmentNewsDetailsBinding
import com.news.app.ui.di.details.DaggerDetailsComponent
import com.news.app.ui.viewmodels.DetailsViewModel
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

const val NEWS_KEY = "news_key"

class NewsDetailsFragment : Fragment() {

    @Inject
    lateinit var detailsViewModel: DetailsViewModel
    private lateinit var binding: FragmentNewsDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsDetailsBinding.inflate(layoutInflater)
        DaggerDetailsComponent
            .builder()
            .build()
            .inject(this)
        initButtons()
        detailsViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        activity?.window?.statusBarColor = Color.TRANSPARENT
        updateIfBundleExists()
        return binding.root
    }

    private fun updateIfBundleExists() {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                detailsViewModel.saved.collect { saved ->
                    if (saved) binding.bookmarkButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_saved_bookmark, resources.newTheme()))
                    else binding.bookmarkButton.setImageDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_saved, resources.newTheme()))
                }
            }
        }
        val bundleData = getBundleData()
        if (bundleData != null) {
            detailsViewModel.uiUpdated(bundleData)
            setDataToUI(bundleData)
        }
    }

    private fun initButtons() {
        binding.bookmarkButton.setOnClickListener {
            detailsViewModel.bookmarkButtonClicked()
        }
    }

    private fun getBundleData(): Article? {
        val bundle = this.arguments
        if (bundle != null) {
            return bundle.getParcelableCompat(NEWS_KEY, Article::class.java)
        }
        return null
    }

    private fun setDataToUI(news: Article) {
        binding.titleText.text = news.newsTitle
        binding.articleSource.text = news.source.name
        binding.articleTime.text = news.publishedAt
        if (news.newsIcon == null) loadPhoto("https://placebear.com/640/360")
        else loadPhoto(news.newsIcon)
        if (news.content == null) showNoContentView()
        else binding.articleContent.text = news.content
    }

    private fun loadPhoto(icon: String) {
        Picasso.with(context)
            .load(icon)
            .fit().centerInside()
            .into(binding.articleImage)
    }

    private fun showNoContentView() {
        binding.noContentView.root.isVisible = true
        binding.articleContent.isVisible = false
    }

    companion object {

        fun newInstance(news: Article) =
            NewsDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(NEWS_KEY, news)
                }
            }
    }
}