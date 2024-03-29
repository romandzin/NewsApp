package com.news.app.data.mappers

import com.news.app.data.model.Article
import com.news.app.data.model.ArticleDbEntity
import com.news.app.data.model.Source
import javax.inject.Inject

class ArticleDbToArticleMapper @Inject constructor() {

    fun transform(dbElement: ArticleDbEntity): Article {
        return Article(
            Source("123", "Google"),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }
}