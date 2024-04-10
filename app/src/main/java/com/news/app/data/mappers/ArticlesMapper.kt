package com.news.app.data.mappers

import com.news.app.data.model.Article
import com.news.app.data.model.ArticleDbEntity
import com.news.app.data.model.Source
import javax.inject.Inject

class ArticlesMapper @Inject constructor() {

    fun transform(dbElement: ArticleDbEntity): Article {
        return Article(
            Source("someId", dbElement.sourceName),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }

    fun transform(article: Article, savedDate: String): ArticleDbEntity {
        return ArticleDbEntity(
            newsTitle = article.newsTitle!!,
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            savedDate = savedDate,
            sourceName = article.source.name
        )
    }
}