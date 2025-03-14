package com.news.app.feature_headlines.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.news.app.feature_headlines.R
import com.news.core.MainAppNavigator
import com.news.data.data_api.model.Article
import com.squareup.picasso.Picasso

class ArticlesAdapter(
    var arrayList: ArrayList<Article>,
    private val navigator: MainAppNavigator,
    val context: Context
) : RecyclerView.Adapter<ArticlesAdapter.HeadlinesViewHolder>() {

    val iconsArray = arrayListOf(
        R.drawable.ic_bbc,
        R.drawable.ic_bloomberg,
        R.drawable.ic_cnn,
        R.drawable.ic_new_york_times,
        R.drawable.ic_daily_mail
    )
    private var searchMode = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinesViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return HeadlinesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeadlinesViewHolder, position: Int) {
        holder.bindData(arrayList[position])
        holder.itemView.setOnClickListener {
            clickItem(arrayList[holder.adapterPosition])
        }
        if (searchMode) holder.itemView.rootView.setBackgroundColor(
            context.resources.getColor(
                R.color.main_blue,
                context.resources.newTheme()
            )
        )
        else holder.itemView.rootView.setBackgroundColor(
            context.resources.getColor(
                R.color.white,
                context.resources.newTheme()
            )
        )
    }

    private fun clickItem(article: Article) {
        navigator.moveToDetailsFragment(article, "detailsFragment")
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

    inner class HeadlinesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
            Log.d("tag", news.newsIcon.toString())
            if (news.newsIcon == null || news.newsIcon == "") loadPhoto("https://placebear.com/640/360")
            else loadPhoto(news.newsIcon!!)
            setSourceIcon(news)
        }

        private fun setSourceIcon(news: Article) {
            when (news.source.name) {
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

    inner class DiffCallback(
        private val oldList: ArrayList<Article>,
        private val newList: ArrayList<Article>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].newsTitle == newList[newItemPosition].newsTitle

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}