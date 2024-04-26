package com.news.dat.data_impl.mappers

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.shouldBe

class DatabaseObjectsMapperTest : FunSpec({

    val databaseObjectsMapper = com.news.data.mappers.DatabaseObjectsMapper()

    test("transformArticleSavedToArticle") {
        val articleSavedDbEntity =
            com.news.data.model.db_entities.ArticleSavedDbEntity(
                "title1",
                "iconUrl",
                "13 friday",
                "super context",
                "date",
                "Bbc",
                "12"
            )
        val baseArticle = databaseObjectsMapper.transform(articleSavedDbEntity)
        baseArticle.sourceDomainModel.id shouldBe articleSavedDbEntity.sourceId
        baseArticle.sourceDomainModel.name shouldBe articleSavedDbEntity.sourceName
        baseArticle.newsTitle shouldBe articleSavedDbEntity.newsTitle
        baseArticle.newsIcon shouldBe articleSavedDbEntity.newsIcon
        baseArticle.publishedAt shouldBe articleSavedDbEntity.publishedAt
        baseArticle.content shouldBe articleSavedDbEntity.content
    }

    test("transformArticleCacheToArticle") {
        val articleCacheDbEntity =
            com.news.data.model.db_entities.ArticleCacheDbEntity(
                "title1",
                "iconUrl",
                "url to article",
                "date",
                "super context",
                "Bbc",
                "new",
                "bbc"
            )
        val baseArticle = databaseObjectsMapper.transform(articleCacheDbEntity)
        baseArticle.sourceDomainModel.id shouldBe articleCacheDbEntity.sourceId
        baseArticle.sourceDomainModel.name shouldBe articleCacheDbEntity.sourceName
        baseArticle.newsTitle shouldBe articleCacheDbEntity.newsTitle
        baseArticle.newsIcon shouldBe articleCacheDbEntity.newsIcon
        baseArticle.publishedAt shouldBe articleCacheDbEntity.publishedAt
        baseArticle.content shouldBe articleCacheDbEntity.content
    }

    test("transformArticleSourceCacheToArticle") {
        val articleSourceCacheDbEntity =
            com.news.data.model.db_entities.ArticleSourceCacheDbEntity(
                "title1",
                "iconUrl",
                "13 friday",
                "super content",
                "Bbc",
                "bbc",
            )
        val baseArticle = databaseObjectsMapper.transform(articleSourceCacheDbEntity)
        baseArticle.sourceDomainModel.id shouldBe articleSourceCacheDbEntity.sourceId
        baseArticle.sourceDomainModel.name shouldBe articleSourceCacheDbEntity.sourceName
        baseArticle.newsTitle shouldBe articleSourceCacheDbEntity.newsTitle
        baseArticle.newsIcon shouldBe articleSourceCacheDbEntity.newsIcon
        baseArticle.publishedAt shouldBe articleSourceCacheDbEntity.publishedAt
        baseArticle.content shouldBe articleSourceCacheDbEntity.content
    }

    test("transformToArticleSourceCache") {
        val article = com.news.domain.model.ArticleDomainModel(
            com.news.domain.model.SourceDomainModel("bbc", "BBC"),
            "title",
            "icon url",
            "13 friday",
            "content",
            "article url",
        )
        val articleSourceCacheDbEntity = databaseObjectsMapper.transformToArticleSourceCache(article)
        articleSourceCacheDbEntity.sourceId shouldBe article.sourceDomainModel.id
        articleSourceCacheDbEntity.sourceName shouldBe article.sourceDomainModel.name
        articleSourceCacheDbEntity.newsTitle shouldBe article.newsTitle
        articleSourceCacheDbEntity.newsIcon shouldBe article.newsIcon
        articleSourceCacheDbEntity.publishedAt shouldBe article.publishedAt
        articleSourceCacheDbEntity.content shouldBe article.content
    }

    test("transformArticleWithDateToSavedArticle") {
        val article = com.news.domain.model.ArticleDomainModel(
            com.news.domain.model.SourceDomainModel("bbc", "BBC"),
            "title",
            "icon url",
            "13 friday",
            "content",
            "article url",
        )
        val savedDate = "19 April 2024"
        val articleSavedDbEntity = databaseObjectsMapper.transform(article, savedDate)
        articleSavedDbEntity.sourceId shouldBe article.sourceDomainModel.id
        articleSavedDbEntity.sourceName shouldBe article.sourceDomainModel.name
        articleSavedDbEntity.newsTitle shouldBe article.newsTitle
        articleSavedDbEntity.newsIcon shouldBe article.newsIcon
        articleSavedDbEntity.publishedAt shouldBe article.publishedAt
        articleSavedDbEntity.content shouldBe article.content
        articleSavedDbEntity.savedDate shouldBe savedDate
    }

    test("transformToCache") {
        val article = com.news.domain.model.ArticleDomainModel(
            com.news.domain.model.SourceDomainModel("bbc", "BBC"),
            "title",
            "icon url",
            "13 friday",
            "content",
            "article url",
        )
        val category = "popular"
        val articleCacheDbEntity = databaseObjectsMapper.transformToCache(article, category)
        articleCacheDbEntity.sourceId shouldBe article.sourceDomainModel.id
        articleCacheDbEntity.sourceName shouldBe article.sourceDomainModel.name
        articleCacheDbEntity.newsTitle shouldBe article.newsTitle
        articleCacheDbEntity.newsIcon shouldBe article.newsIcon
        articleCacheDbEntity.publishedAt shouldBe article.publishedAt
        articleCacheDbEntity.content shouldBe article.content
        articleCacheDbEntity.url shouldBe article.url
        articleCacheDbEntity.category shouldBe category
    }

    test("transformSourceFromDbEntityToSource") {
        val sourceDbEntity = com.news.data.model.db_entities.SourceDbEntity(
            "id",
            "name",
            "icon url",
            "popular",
            "ru",
            "russia",
        )
        val source = databaseObjectsMapper.transformSource(sourceDbEntity)
        source.id shouldBe sourceDbEntity.id
        source.name shouldBe sourceDbEntity.name
        source.country shouldBe sourceDbEntity.country
        source.url shouldBe sourceDbEntity.url
        source.category shouldBe sourceDbEntity.category
        source.language shouldBe sourceDbEntity.language
    }

    test("transformSourceFromSourceToDbEntity") {
        val sourceDomainModel = com.news.domain.model.SourceDomainModel(
            "id",
            "name",
            "icon url",
            "popular",
            "ru",
            "russia",
        )
        val sourceDbEntity = databaseObjectsMapper.transformSource(sourceDomainModel)
        sourceDbEntity.id shouldBe sourceDomainModel.id
        sourceDbEntity.name shouldBe sourceDomainModel.name
        sourceDbEntity.country shouldBe sourceDomainModel.country
        sourceDbEntity.url shouldBe sourceDomainModel.url
        sourceDbEntity.category shouldBe sourceDomainModel.category
        sourceDbEntity.language shouldBe sourceDomainModel.language
    }
})
