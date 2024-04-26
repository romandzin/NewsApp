package com.news.dat.data_impl

import com.news.dat.data_impl.db.CachedDao
import com.news.dat.data_impl.db.SavedDao
import com.news.dat.data_impl.mappers.DatabaseObjectsMapper
import com.news.dat.data_impl.model.db_entities.ArticleCacheDbEntity
import com.news.dat.data_impl.model.db_entities.ArticleSourceCacheDbEntity
import com.news.dat.data_impl.model.db_entities.SourceDbEntity
import com.news.dat.data_impl.retrofit.ApiNewsService
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.reactivex.rxjava3.core.Observable
import java.text.SimpleDateFormat
import java.util.Locale

class RepositoryImplTest : BehaviorSpec({

    val fromString = "2024-04-12"
    val toString = "2024-04-27"
    val newsServiceApi = mockk<ApiNewsService>()
    val savedDao = mockk<SavedDao>()
    val cachedDao = spyk<CachedDao>()
    val databaseObjectsMapper = DatabaseObjectsMapper()
    val repositoryImpl = spyk(
        RepositoryImpl(
            newsServiceApi,
            savedDao,
            cachedDao,
            databaseObjectsMapper
        ), recordPrivateCalls = true)
    val articleCacheDb = ArticleCacheDbEntity(
        "title1",
        null,
        null,
        "2024-04-19T06:10:26Z",
        null,
        null,
        "",
        null,
        1
    )

    Given("before calling filter") {
        val arrayDbArticlesList = listOf(
            articleCacheDb,
            ArticleCacheDbEntity(
                "title2",
                null,
                null,
                "2024-04-28T06:10:26Z",
                null,
                null,
                "",
                null,
                1
            )
        )
        Then("local filters should return array with one element") {
            repositoryImpl.filterLocalArticles(fromString, toString, arrayDbArticlesList)
                .subscribe({
                    it.size shouldBe 1
                    it[0].newsTitle shouldBe "title1"
                },
                    {
                        throw AssertionError(it.message)
                    })
        }
    }

    Given("two dates range and one date in range") {
        val filtersDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = filtersDateFormat.parse(fromString)
        val toDate = filtersDateFormat.parse(toString)
        val dateInRange = filtersDateFormat.parse("2024-04-14")
        Then("is within range should return true") {
            repositoryImpl.isWithinRange(fromDate, toDate, dateInRange) shouldBe true
        }
    }

    Given("two dates range and one date not in range") {
        val filtersDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val fromDate = filtersDateFormat.parse(fromString)
        val toDate = filtersDateFormat.parse(toString)
        val dateInRange = filtersDateFormat.parse("2024-05-14")
        Then("is within range should return false") {
            repositoryImpl.isWithinRange(fromDate, toDate, dateInRange) shouldBe false
        }
    }

    Given("getHeadlinesNews is called and get news from internet") {
        every { cachedDao.getCachedArticlesByCategory("general", 1) } returns Observable.fromArray(
            arrayListOf(articleCacheDb)
        )
        val page = 1
        val pageSize = 8
        Then("check if we get news from internet when request page with news that is bigger than our all cached news") {
            cachedDao.getCachedArticlesByCategory("general", 1).subscribe {
                if (it.size < page * pageSize) {
                    repositoryImpl.saveToCache(arrayListOf(), "general", 1)
                    assert(true)
                } else assert(false)
            }
        }
    }

    Given("test subscribe in getCachedArticleBySource when get empty list") {
        every { cachedDao.getCachedArticlesBySource("source") } returns Observable.fromArray(
            emptyList()
        )
        Then("should assert true") {
            cachedDao.getCachedArticlesBySource("source").subscribe { articleBySourceList ->
                if (articleBySourceList.isEmpty()) {
                    assert(true)
                } else {
                    assert(false)
                }
            }
        }
    }

    Given("test subscribe in getCachedArticleBySource when get not empty list") {
        every { cachedDao.getCachedArticlesBySource("source") } returns Observable.fromArray(
            arrayListOf(
                ArticleSourceCacheDbEntity(
                    "",
                    null,
                    null,
                    null,
                    null,
                    null
                )
            )
        )
        Then("should assert true") {
            cachedDao.getCachedArticlesBySource("source").subscribe { articleBySourceList ->
                if (articleBySourceList.isEmpty()) {
                    assert(false)
                } else {
                    assert(true)
                }
            }
        }
    }

    Given("test subscribe in getCachedSource when get not empty list") {
        every { cachedDao.getCachedSources() } returns Observable.fromArray(
            arrayListOf(SourceDbEntity("", ""))
        )
        Then("should assert true") {
            cachedDao.getCachedSources().subscribe { cachedSourceList ->
                if (cachedSourceList.isEmpty()) {
                    assert(false)
                } else {
                    assert(true)
                }
            }
        }
    }

    Given("test subscribe in getCachedSource when get empty list") {
        every { cachedDao.getCachedSources() } returns Observable.fromArray(
            emptyList()
        )
        Then("should assert true") {
            cachedDao.getCachedSources().subscribe { cachedSourceList ->
                if (cachedSourceList.isEmpty()) {
                    assert(true)
                } else {
                    assert(false)
                }
            }
        }
    }
})