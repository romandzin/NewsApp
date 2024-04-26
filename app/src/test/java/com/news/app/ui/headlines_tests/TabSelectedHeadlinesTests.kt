package com.news.app.ui.headlines_tests

import android.content.Context
import com.news.app.feature_headlines.ui.moxy.views.HeadLinesView
import com.news.app.feature_headlines.ui.presenters.HeadlinesPresenter
import com.news.core.AppDependenciesProvider
import com.news.dat.data_impl.RepositoryImpl
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Source
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Callable

class TabSelectedHeadlinesTests: BehaviorSpec({

    val appDependencies = mockk<AppDependenciesProvider>()
    val repository = mockk<RepositoryImpl>()
    val context = mockk<Context>()
    val headlinesPresenter = spyk<HeadlinesPresenter>(HeadlinesPresenter())
    val viewState = spyk<HeadLinesView>()
    Article(Source("", ""), "", "", "", "")

    beforeSpec {
        headlinesPresenter.attachView(viewState)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler: Callable<Scheduler?>? -> Schedulers.trampoline() }
        every { appDependencies.provideRepository() } returns repository
        every { appDependencies.provideApplicationContext() } answers { context }
        headlinesPresenter.init(appDependencies)
    }

    afterSpec {
        RxAndroidPlugins.reset()
    }

    Given("test function tab selected when getting from cache") {
        every {  repository.getAllCachedHeadlinesByPage("general", 2) } returns Observable.fromArray(
            arrayListOf()
        )
        every { repository.getHeadlinesNews("general", 8, 2) } returns Observable.fromArray(
            arrayListOf()
        )
        headlinesPresenter.defaultModeIsSet()
        headlinesPresenter.scrolledToEnd()
        When("tabSelected called with same category") {
            headlinesPresenter.tabSelected("general")
            Then("check if getAllCachedArticlesByPage") {
                verify {
                    repository.getAllCachedHeadlinesByPage("general", 2)
                    viewState.setDefaultMode()
                    viewState.displayNewsList(arrayListOf())
                    viewState.hideLoading()
                }
            }
        }
    }

    Given("test function tab selected when getting from internet") {
        every {  repository.getHeadlinesNews("business", 8, 1) } returns Observable.fromArray(
            arrayListOf()
        )
        When("tabSelected called with new category") {
            headlinesPresenter.tabSelected("business")
            Then("check if getHeadlinesNews") {
                verify {
                    repository.getHeadlinesNews("business", 8, 1)
                    viewState.setDefaultMode()
                    viewState.displayNewsList(arrayListOf())
                    viewState.hideLoading()
                }
            }
        }
    }



})