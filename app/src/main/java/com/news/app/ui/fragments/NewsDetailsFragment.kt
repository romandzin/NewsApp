package com.news.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import com.news.app.common.Extensions.getParcelableCompat
import com.news.app.data.model.Article
import com.news.app.databinding.FragmentNewsDetailsBinding

const val NEWS_KEY = "news_key"
class NewsDetailsFragment : Fragment() {

    private lateinit var binding: FragmentNewsDetailsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNewsDetailsBinding.inflate(layoutInflater)
        val bundleData = getBundleData()
        if (bundleData != null) setDataToUI(bundleData)
        return binding.root
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
        if (news.content == null) showNoContentView()
        else binding.articleContent.text = news.content
    }

    private fun showNoContentView() {
       // binding.noContentView.root.isVisible = true
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