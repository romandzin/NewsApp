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
import com.news.app.ui.fragments.NewsDetailsFragment
import com.squareup.picasso.Picasso

class HeadLinesAdapter(var arrayList: ArrayList<Article>, val navigator: Navigator, val context: Context): RecyclerView.Adapter<HeadLinesAdapter.HeadlinesViewHolder>() {

    val iconsArray = arrayListOf(R.drawable.ic_bbc, R.drawable.ic_bloomberg, R.drawable.ic_cnn, R.drawable.ic_new_york_times, R.drawable.ic_daily_mail)
    private var searchMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinesViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return HeadlinesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeadlinesViewHolder, position: Int) {
        holder.bindData(arrayList[position])
        holder.itemView.setOnClickListener {
            clickItem(arrayList[position])
        }
        if (searchMode) holder.itemView.rootView.setBackgroundColor(context.resources.getColor(R.color.main_blue, context.resources.newTheme()))
        else holder.itemView.rootView.setBackgroundColor(context.resources.getColor(R.color.white, context.resources.newTheme()))
    }

    private fun clickItem(article: Article) {
        navigator.moveToDetailsFragment(NewsDetailsFragment.newInstance(article), "detailsFragment")
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setData(oldList: ArrayList<Article>, newList: ArrayList<Article>) {
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

    inner class HeadlinesViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val newsImageView: ImageView
        private val channelImageView: ImageView
        private val channelNameTextView: TextView
        private val newsTextView: TextView

        init {
            newsImageView = itemView.findViewById(R.id.news_image)
            channelImageView = itemView.findViewById(R.id.chanel_icon)
            channelNameTextView = itemView.findViewById(R.id.channel_name)
            newsTextView = itemView.findViewById(R.id.news_text)
        }

        fun bindData(news: Article) {
            channelNameTextView.text = news.source.name
            newsTextView.text = news.newsTitle
            if (news.newsIcon == null) loadPhoto("https://placebear.com/640/360")
            else loadPhoto(news.newsIcon)
            if (adapterPosition > 4) loadPhoto(R.drawable.ic_bbc)
            else {
                loadPhoto(iconsArray[adapterPosition])
            }

        }

        private fun loadPhoto(icon: Int) {
            Picasso.with(context)
                .load(icon)
                .into(channelImageView)
        }

        private fun loadPhoto(icon: String) {
            Picasso.with(context)
                .load(icon)
                .into(newsImageView)
        }
    }

    inner class DiffCallback(private val oldList: ArrayList<Article>, private val newList: ArrayList<Article>): DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].newsTitle == newList[newItemPosition].newsTitle

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}