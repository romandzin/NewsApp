package com.news.app.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.news.app.R
import com.news.app.common.Navigator
import com.news.app.data.model.Article
import com.news.app.data.model.Source
import com.news.app.ui.fragments.NewsDetailsFragment
import com.news.app.ui.fragments.SourcesFragment
import com.squareup.picasso.Picasso

class SourcesAdapter(var arrayList: ArrayList<Source>, val parentFragment: SourcesFragment): RecyclerView.Adapter<SourcesAdapter.SourcesViewHolder>() {

    val iconsArray = arrayListOf(R.drawable.ic_bbc, R.drawable.ic_bloomberg, R.drawable.ic_cnn, R.drawable.ic_new_york_times, R.drawable.ic_daily_mail)
    private var searchMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourcesViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_source, parent, false)
        return SourcesViewHolder(view)
    }

    override fun onBindViewHolder(holder: SourcesViewHolder, position: Int) {
        holder.bindData(arrayList[position])
        holder.itemView.setOnClickListener {
            clickItem(arrayList[position].id!!, arrayList[position].name!!)
        }
        if (searchMode) holder.itemView.rootView.setBackgroundColor(parentFragment.requireContext().resources.getColor(R.color.main_blue, parentFragment.requireContext().resources.newTheme()))
        else holder.itemView.rootView.setBackgroundColor(parentFragment.requireContext().resources.getColor(R.color.white, parentFragment.requireContext().resources.newTheme()))
    }

    private fun clickItem(source: String, name: String) {
        parentFragment.showArticles(source, name)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setData(oldList: ArrayList<Source>, newList: ArrayList<Source>) {
        val diffCallback = DiffCallback(oldList, newList)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        diffCourses.dispatchUpdatesTo(this)
        arrayList.clear()
        arrayList.addAll(newList)
    }

    fun setSearchMode() {
        searchMode = true
    }

    fun disableSearchMode() {
        searchMode = false
    }

    inner class SourcesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val sourceImageView: ImageView
        private val sourceTextView: TextView
        private val sourceCategoryTextView: TextView

        init {
            sourceImageView = itemView.findViewById(R.id.source_image)
            sourceTextView = itemView.findViewById(R.id.source_name)
            sourceCategoryTextView = itemView.findViewById(R.id.source_category)
        }

        fun bindData(source: Source) {
            sourceTextView.text = source.name
            sourceCategoryTextView.text = "${source.category} | ${source.country}"
            setSourceIcon(source)
        }

        private fun setSourceIcon(source: Source) {
            when (source.name) {
                "BBC" -> {
                    loadPhoto(iconsArray[0])
                }

                "Bloomberg" -> {
                    loadPhoto(iconsArray[1])
                }

                "CNN" -> {
                    loadPhoto(iconsArray[2])
                }

                "The New York Times" -> {
                    loadPhoto(iconsArray[3])
                }

                "Daily Mail" -> {
                    loadPhoto(iconsArray[4])
                }

                else -> {
                    loadPhoto(R.drawable.ic_fox_news)
                }
            }
        }


        private fun loadPhoto(icon: Int) {
            Picasso.with(parentFragment.requireContext())
                .load(icon)
                .into(sourceImageView)
        }
    }

    inner class DiffCallback(private val oldList: ArrayList<Source>, private val newList: ArrayList<Source>): DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].id == newList[newItemPosition].id

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}