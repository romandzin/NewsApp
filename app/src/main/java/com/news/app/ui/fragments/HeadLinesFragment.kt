package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.tabs.TabLayout
import com.news.app.data.model.Article
import com.news.app.databinding.FragmentHeadLinesBinding
import com.news.app.ui.activity.MainActivity
import com.news.app.ui.adapters.HeadLinesAdapter
import com.news.app.ui.moxy.MvpAppCompatFragment
import com.news.app.ui.moxy.views.HeadLinesView
import com.news.app.ui.presenters.HeadlinesPresenter
import moxy.presenter.InjectPresenter

class HeadLinesFragment : MvpAppCompatFragment(), HeadLinesView {

    @InjectPresenter
    lateinit var headlinesPresenter: HeadlinesPresenter
    private lateinit var binding: FragmentHeadLinesBinding
    private lateinit var headLinesAdapter: HeadLinesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHeadLinesBinding.inflate(layoutInflater)
        setAdapter()
        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                setTabByCategory()
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabReselected(tab: TabLayout.Tab?) {
                setTabByCategory()
            }

        })
        initView()
        return binding.root
    }

    private fun setTabByCategory() {
        when (binding.tabLayout.selectedTabPosition) {
            0 -> tabSelected("general")
            1 -> tabSelected("business")
            2 -> tabSelected("technology")
        }
    }

    private fun setAdapter() {
        headLinesAdapter = HeadLinesAdapter(arrayListOf(), requireActivity() as MainActivity)
        binding.newsRecyclerView.adapter = headLinesAdapter
    }

    override fun tabSelected(category: String) {
        headlinesPresenter.tabSelected(category)
    }

    override fun initView() {
        headlinesPresenter.initView()
        //headlinesPresenter.getList()
    }

    override fun setSelectedTab(index: Int) {
        val tab = binding.tabLayout.getTabAt(index)
        tab!!.select()
    }

    override fun displayNewsList(newsList: ArrayList<Article>) {
        headLinesAdapter.setData(headLinesAdapter.arrayList, newsList)
    }

    override fun showError(errorText: String) {
        Toast.makeText(requireContext(), errorText, Toast.LENGTH_LONG).show()
    }

}