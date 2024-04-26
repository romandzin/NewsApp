package com.news.app.ui.headlines_tests

import android.content.Context
import com.news.app.feature_headlines.ui.moxy.views.HeadLinesView
import com.news.app.feature_headlines.ui.presenters.HeadlinesPresenter
import com.news.dat.data_impl.RepositoryImpl
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Filters
import com.news.data.data_api.model.Source
import io.kotest.core.spec.style.BehaviorSpec
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.verify
import io.mockk.verifyOrder
import io.mockk.verifySequence
import io.reactivex.rxjava3.android.plugins.RxAndroidPlugins
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.Callable


class HeadlinesPresenterTest : BehaviorSpec({

    val appDependencies = mockk<com.news.core.AppDependenciesProvider>()
    val repository = mockk<RepositoryImpl>()
    val context = mockk<Context>()
    val headlinesPresenter = spyk<HeadlinesPresenter>(HeadlinesPresenter())
    val viewState = spyk<HeadLinesView>()
    val emptyArticle =
        Article(Source("", ""), "", "", "", "")

    beforeSpec {
        headlinesPresenter.attachView(viewState)
        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler: Callable<Scheduler?>? -> Schedulers.trampoline() }
    }

    afterSpec {
        RxAndroidPlugins.reset()
    }

    Given("test init function") {
        every { appDependencies.provideRepository() } returns repository
        every { appDependencies.provideApplicationContext() } answers { context }
        When("call init function") {
            headlinesPresenter.init(appDependencies)
            Then("check all need objects is providing") {
                verifySequence {
                    appDependencies.provideRepository()
                }
            }
        }
    }

    Given("test refreshView with needToRefresh is false") {
        headlinesPresenter.anotherModeEnabled()
        every { viewState.setSelectedTab(0) } returns Unit
        every { viewState.setSelectedTab(1) } returns Unit
        every { viewState.setSelectedTab(2) } returns Unit
        When("call refreshView") {
            headlinesPresenter.refreshView()
            Then("check sequence of methods") {
                verify(exactly = 0) { viewState.setSelectedTab(0) }
                verify(exactly = 0) { viewState.setSelectedTab(1) }
                verify(exactly = 0) { viewState.setSelectedTab(2) }
            }
        }
    }

    Given("test refreshView with needToRefresh is true") {
        headlinesPresenter.defaultModeIsSet()
        every { viewState.setSelectedTab(0) } returns Unit
        When("call refreshView") {
            headlinesPresenter.refreshView()
            Then("check sequence of methods") {
                verify(atLeast = 1) { viewState.setSelectedTab(0) }
            }
        }
    }

    Given("test searchInArrayByText when internet is disabled") {
        every { headlinesPresenter["observerInternetConnection"]() } returns false
        every { repository.getHeadlinesNewsByQuery("general", "") } returns Flowable.fromArray(
            arrayListOf(emptyArticle)
        )
        When("call refreshView") {
            headlinesPresenter.searchInArrayByText("")
            Then("check sequence of methods") {
                verify(exactly = 0) {
                    viewState.displayNewsList(
                        arrayListOf(emptyArticle)
                    )
                }
            }
        }
    }

    Given("test searchInArrayByText when internet is enabled") {
        every { headlinesPresenter["observerInternetConnection"]() } returns true
        every { repository.getHeadlinesNewsByQuery("general", "") } returns Flowable.fromArray(
            arrayListOf(emptyArticle)
        )
        When("call refreshView") {
            headlinesPresenter.searchInArrayByText("")
            Then("check sequence of methods") {
                verify(atLeast = 1) {
                    viewState.displayNewsList(
                        arrayListOf(emptyArticle)
                    )
                }
            }
        }
    }

    Given("test fuction enable filters") {
        every { repository.getFilteredNews(Filters(), true) } returns Observable.fromArray(
            arrayListOf(emptyArticle)
        )
        When("calling enableFilters") {
            headlinesPresenter.enableFilters(Filters())
            Then("check all methods are called") {
                verify {
                    viewState.showLoading()
                    repository.getFilteredNews(
                        Filters(),
                        true
                    )
                    viewState.displayNewsList(arrayListOf())
                    viewState.hideLoading()
                }
            }
        }
    }

    Given("searchEnabledResultGet") {
        every { viewState.setSearchModeToFragment() } returns Unit
        When("searchEnabled is called") {
            headlinesPresenter.searchEnabledResultGet()
            Then("verify") {
                verify {
                    viewState.setSearchModeToFragment()
                }
            }
        }
    }

    Given("searchDisabledResultGet") {
        every { viewState.disableSearchModeInFragment() } returns Unit
        When("searchDisabled is called") {
            headlinesPresenter.searchDisabledResultGet()
            Then("verify") {
                verify {
                    viewState.disableSearchModeInFragment()
                }
            }
        }
    }
})
