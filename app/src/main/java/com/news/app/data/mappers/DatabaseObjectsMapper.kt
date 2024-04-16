package com.news.app.data.mappers

import com.news.app.domain.model.Article
import com.news.app.data.model.db_entities.ArticleCacheDbEntity
import com.news.app.data.model.db_entities.ArticleSavedDbEntity
import com.news.app.data.model.db_entities.ArticleSourceCacheDbEntity
import com.news.app.domain.model.Source
import com.news.app.data.model.db_entities.SourceDbEntity
import javax.inject.Inject

class DatabaseObjectsMapper @Inject constructor() {

    fun transform(dbElement: ArticleSavedDbEntity): Article {
        return Article(
            Source(dbElement.sourceId, dbElement.sourceName),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }

    fun transform(dbElement: ArticleCacheDbEntity): Article {
        return Article(
            Source(dbElement.sourceId, dbElement.sourceName),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }

    fun transform(dbElement: ArticleSourceCacheDbEntity): Article {
        return Article(
            Source(dbElement.sourceId, dbElement.sourceName),
            dbElement.newsTitle,
            dbElement.newsIcon,
            dbElement.publishedAt,
            dbElement.content
        )
    }

    fun transformToArticleSourceCache(article: Article): ArticleSourceCacheDbEntity {
        return ArticleSourceCacheDbEntity(
            newsTitle = article.newsTitle!!,
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            sourceName = article.source.name,
            sourceId = article.source.id
        )
    }

    fun transform(article: Article, savedDate: String): ArticleSavedDbEntity {
        return ArticleSavedDbEntity(
            newsTitle = article.newsTitle!!,
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            savedDate = savedDate,
            sourceName = article.source.name,
            sourceId = article.source.id
        )
    }

    fun transformToCache(article: Article, category: String): ArticleCacheDbEntity {
        return ArticleCacheDbEntity(
            newsTitle = article.newsTitle!!,
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            sourceName = article.source.name,
            category = category,
            sourceId = article.source.id
        )
    }

    fun transformSource(sourceDbEntity: SourceDbEntity): Source {
        return Source(
            id = sourceDbEntity.id,
            name = sourceDbEntity.name,
            country = sourceDbEntity.country,
            url = sourceDbEntity.url,
            category = sourceDbEntity.category,
            language = sourceDbEntity.language,
        )
    }

    fun transformSource(source: Source): SourceDbEntity {
        return SourceDbEntity(
            id = source.id.toString(),
            name = source.name,
            country = source.country,
            url = source.url,
            category = source.category,
            language = source.language,
        )
    }
}