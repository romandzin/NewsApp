package com.news.dat.data_impl.mappers

import com.news.dat.data_impl.model.db_entities.ArticleCacheDbEntity
import com.news.dat.data_impl.model.db_entities.ArticleSavedDbEntity
import com.news.dat.data_impl.model.db_entities.ArticleSourceCacheDbEntity
import com.news.dat.data_impl.model.db_entities.SourceDbEntity
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Source
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
            dbElement.content,
            dbElement.url ?: ""
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
            newsTitle = article.newsTitle ?: "",
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            sourceName = article.source.name,
            sourceId = article.source.id
        )
    }

    fun transform(article: Article, savedDate: String): ArticleSavedDbEntity {
        return ArticleSavedDbEntity(
            newsTitle = article.newsTitle ?: "",
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            savedDate = savedDate,
            sourceName = article.source.name,
            sourceId = article.source.id
        )
    }

    fun transformToCache(article: Article, category: String, page: Int): ArticleCacheDbEntity {
        return ArticleCacheDbEntity(
            newsTitle = article.newsTitle ?: "",
            newsIcon = article.newsIcon,
            publishedAt = article.publishedAt,
            content = article.content,
            sourceName = article.source.name,
            category = category,
            sourceId = article.source.id,
            url = article.url,
            page = page
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

    fun transformSource(sourceDomainModel: Source): SourceDbEntity {
        return SourceDbEntity(
            id = sourceDomainModel.id.toString(),
            name = sourceDomainModel.name,
            country = sourceDomainModel.country,
            url = sourceDomainModel.url,
            category = sourceDomainModel.category,
            language = sourceDomainModel.language,
        )
    }
}