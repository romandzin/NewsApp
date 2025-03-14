package com.news.app.ui.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.news.app.R
import com.news.app.common.Extensions.getParcelableCompat
import com.news.core.App
import com.news.app.databinding.FragmentNewsDetailsBinding
import com.news.app.ui.di.details.DaggerDetailsComponent
import com.news.app.ui.viewmodels.DetailsViewModel
import com.news.core.MainAppNavigator
import com.news.data.data_api.model.Article
import com.squareup.picasso.Picasso
import kotlinx.coroutines.launch
import javax.inject.Inject

const val NEWS_KEY = "news_key"

class NewsDetailsFragment : Fragment() {

    @Inject
    lateinit var detailsViewModel: DetailsViewModel
    private lateinit var binding: FragmentNewsDetailsBinding
    private val mainAppNavigator by lazy {
        requireActivity() as MainAppNavigator
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsDetailsBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerDetailsComponent
            .builder()
            .build()
            .inject(this)
        initButtons()
        detailsViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        activity?.window?.statusBarColor = Color.TRANSPARENT
        updateIfBundleExists()
    }

    private fun updateIfBundleExists() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                detailsViewModel.saved.collect { saved ->
                    if (saved) binding.bookmarkButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_saved_bookmark,
                            resources.newTheme()
                        )
                    )
                    else binding.bookmarkButton.setImageDrawable(
                        ResourcesCompat.getDrawable(
                            resources,
                            R.drawable.ic_saved,
                            resources.newTheme()
                        )
                    )
                }
            }
        }
        lifecycleScope.launch {
            detailsViewModel.url.collect { url ->
                if (url != "") {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(browserIntent)
                }
            }
        }
        lifecycleScope.launch {
            detailsViewModel.clickableText.collect { text ->
                setContent(text)
            }
        }
        lifecycleScope.launch {
            detailsViewModel.currentArticleFlow.collect { article ->
                if (article != null) {
                    setDataToUI(article)
                }
            }
        }
        val bundleData = getBundleData()
        if (bundleData != null) {
            detailsViewModel.onBundleDataIsGet(bundleData)
        }
    }

    private fun initButtons() {
        binding.bookmarkButton.setOnClickListener {
            detailsViewModel.onBookmarkButtonClicked()
        }
        binding.backButton.setOnClickListener {
            mainAppNavigator.goBack()
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
        binding.toolbar.title = news.newsTitle
        binding.collapsing.title = news.newsTitle
        if (news.newsIcon == null) loadPhoto("https://placebear.com/640/360")
        else loadPhoto(news.newsIcon!!)
    }

    private fun setContent(text: SpannableString) {
        if (text == SpannableString("")) showNoContentView()
        else {
            hideNoContentView()
            binding.articleContent.text = text
            binding.articleContent.movementMethod = LinkMovementMethod.getInstance()
        }
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

    private fun hideNoContentView() {
        binding.noContentView.root.isVisible = false
        binding.articleContent.isVisible = true
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