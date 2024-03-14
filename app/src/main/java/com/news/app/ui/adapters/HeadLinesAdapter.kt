package com.news.app.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.news.app.R
import com.news.app.model.data_classes.News

class HeadLinesAdapter(var arrayList: ArrayList<News>): RecyclerView.Adapter<HeadLinesAdapter.HeadlinesViewHolder>() {

    private var moveList = arrayList.toMutableList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HeadlinesViewHolder {
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return HeadlinesViewHolder(view)
    }

    override fun onBindViewHolder(holder: HeadlinesViewHolder, position: Int) {
        holder.bindData(arrayList[position])
    }

    private fun clickItem(holder: RecyclerView.ViewHolder) {
        val news: News = arrayList[holder.adapterPosition]
        val currentHolder = holder as HeadlinesViewHolder
       /* if (!currentHolder.checkBox.isChecked) {
            currentHolder.checkBox.isVisible = true
            currentHolder.checkBox.isChecked = true
            (context as MainActivity).mainViewModel.deleteList.add(news)
        } else {
            currentHolder.checkBox.isVisible = false
            currentHolder.checkBox.isChecked = false
            (context as MainActivity).mainViewModel.deleteList.remove(news)
        } */
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    fun setData(oldList: ArrayList<News>, newList: ArrayList<News>) {
        val diffCallback = DiffCallback(oldList, newList)
        val diffCourses = DiffUtil.calculateDiff(diffCallback)
        diffCourses.dispatchUpdatesTo(this)
        arrayList.clear()
        arrayList.addAll(newList)
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

        fun bindData(news: News) {
            channelNameTextView.text = news.source.name
            newsTextView.text = news.newsTitle
        }
    }

    inner class DiffCallback(private val oldList: ArrayList<News>, private val newList: ArrayList<News>): DiffUtil.Callback() {

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition].newsTitle == newList[newItemPosition].newsTitle

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition] == newList[newItemPosition]
        }
    }
}