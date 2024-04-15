package com.news.app.data.mappers

import com.news.app.data.model.Article
import com.news.app.data.model.ArticleCacheDbEntity
import com.news.app.data.model.ArticleSavedDbEntity
import com.news.app.data.model.Source
import javax.inject.Inject

class ArticlesMapper @Inject constructor() {

    fun transform(dbElement: ArticleSavedDbEntity): Article {
        return Article(
            Source("someId", dbElement.sourceName),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }

    fun transform(dbElement: ArticleCacheDbEntity): Article {
        return Article(
            Source("someId", dbElement.sourceName),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }

    fun transform(article: Article, savedDate: String): ArticleSavedDbEntity {
        return ArticleSavedDbEntity(
            newsTitle = article.newsTitle!!,
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            savedDate = savedDate,
            sourceName = article.source.name
        )
    }

    fun transformToCache(article: Article, category: String): ArticleCacheDbEntity {
        return ArticleCacheDbEntity(
            newsTitle = article.newsTitle!!,
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            sourceName = article.source.name,
            category = category
        )
    }
}