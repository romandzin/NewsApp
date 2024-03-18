package com.news.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.news.app.common.Extensions.getParcelableCompat
import com.news.app.databinding.FragmentNewsDetailsBinding
import com.news.app.model.data_classes.News

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

    private fun getBundleData(): News? {
        val bundle = this.arguments
        if (bundle != null) {
            return bundle.getParcelableCompat(NEWS_KEY, News::class.java)
        }
        return null
    }

    private fun setDataToUI(news: News) {
        binding.titleText.text = news.newsTitle
        binding.articleSource.text = news.source.name
        binding.articleTime.text = news.publishedAt
        binding.articleContent.text = news.content.toString()
    }

    companion object {

        fun newInstance(news: News) =
            NewsDetailsFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(NEWS_KEY, news)
                }
            }
    }
}