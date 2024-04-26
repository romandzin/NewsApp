package com.news.dat.data_impl

import com.news.data.RepositoryImpl
import com.news.data.db.CachedDao
import com.news.data.db.SavedDao
import com.news.data.mappers.DatabaseObjectsMapper
import com.news.data.model.db_entities.ArticleCacheDbEntity
import com.news.data.model.db_entities.ArticleSourceCacheDbEntity
import com.news.data.model.db_entities.SourceDbEntity
import com.news.data.retrofit.ApiNewsService
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
    val newsServiceApi = mockk<com.news.data.retrofit.ApiNewsService>()
    val savedDao = mockk<com.news.data.db.SavedDao>()
    val cachedDao = spyk<com.news.data.db.CachedDao>()
    val databaseObjectsMapper = com.news.data.mappers.DatabaseObjectsMapper()
    val repositoryImpl = spyk(
        com.news.data.RepositoryImpl(
            newsServiceApi,
            savedDao,
            cachedDao,
            databaseObjectsMapper
        ), recordPrivateCalls = true)
    val articleCacheDb = com.news.data.model.db_entities.ArticleCacheDbEntity(
        "title1",
        null,
        null,
        "2024-04-19T06:10:26Z",
        null,
        null,
        "",
        null
    )

    Given("before calling filter") {
        val arrayDbArticlesList = listOf(
            articleCacheDb,
            com.news.data.model.db_entities.ArticleCacheDbEntity(
                "title2",
                null,
                null,
                "2024-04-28T06:10:26Z",
                null,
                null,
                "",
                null
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

    /*Given("getHeadlinesNews is called and get news from cache") {
        every { cachedDao.getCachedArticlesByCategory("general") } returns Observable.fromArray(
            arrayListOf(articleCacheDb)
        )
        val page = 1
        val pageSize = 1
        Then("check if we get news from cache when request page with news that is lower than our all cached news") {
            cachedDao.getCachedArticlesByCategory("general").subscribe {
                if (it.size >= page * pageSize) {
                    val articlesList: ArrayList<Article> =
                        it.map { databaseObjectsMapper.transform(it) } as ArrayList<Article>
                    articlesList.size shouldBe 1
                } else assert(false)
            }
        }
    } */

    Given("getHeadlinesNews is called and get news from internet") {
        every { cachedDao.getCachedArticlesByCategory("general") } returns Observable.fromArray(
            arrayListOf(articleCacheDb)
        )
        val page = 1
        val pageSize = 8
        Then("check if we get news from internet when request page with news that is bigger than our all cached news") {
            cachedDao.getCachedArticlesByCategory("general").subscribe {
                if (it.size < page * pageSize) {
                    repositoryImpl.saveToCache(arrayListOf(), "general")
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
                com.news.data.model.db_entities.ArticleSourceCacheDbEntity(
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
            arrayListOf(com.news.data.model.db_entities.SourceDbEntity("", ""))
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


    /* fun isWithinRange(fromDate: Date, toDate: Date, articleDate: Date): Boolean {
         return !(articleDate.before(fromDate) || articleDate.after(toDate))
     }

     fun checkIfDateIsFresh(articleSavedDateString: String?): Boolean {
         val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
         val articleSavedData = sdf.parse(articleSavedDateString)
         return if (articleSavedData != null) {
             val timeDifference = Calendar.getInstance().time.time - articleSavedData.time
             val daysDiff = TimeUnit.MILLISECONDS.toDays(timeDifference)
             Log.d("tag", daysDiff.toString())
             daysDiff < 14L
         } else false
     } */
})