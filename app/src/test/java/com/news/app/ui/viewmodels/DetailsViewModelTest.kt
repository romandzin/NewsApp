package com.news.app.ui.viewmodels

import com.news.dat.data_impl.RepositoryImpl
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Source
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import io.reactivex.rxjava3.core.Flowable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class DetailsViewModelTest : BehaviorSpec({

    val detailsViewModel = spyk(DetailsViewModel())
    val appDependencies = mockk<com.news.core.AppDependenciesProvider>()
    val repository = mockk<RepositoryImpl>()
    val scope = CoroutineScope(Dispatchers.Main + Job())
    val testArticle = Article(Source(null, null), null, null, null, null)
    val savedDate: String by lazy {
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        sdf.format(Calendar.getInstance().time)
    }

    beforeSpec {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        detailsViewModel.saved.value = true
        every { appDependencies.provideRepository() } returns repository
        coEvery { repository.deleteArticle(testArticle) } returns Unit
        coEvery { repository.saveArticle(testArticle, savedDate) } returns Unit
        detailsViewModel.init(appDependencies)
        every { detailsViewModel.checkIfElementSaved() } returns Unit
    }

    Given("saved value true") {
        When("bookmark button clicked") {
            detailsViewModel.onBundleDataIsGet(testArticle)
            detailsViewModel.onBookmarkButtonClicked()
            Then("check when value is true") {
                coVerify {
                    scope.launch {
                        repository.deleteArticle(testArticle)
                    }
                }
            }
        }
    }

    Given("get current date") {
        detailsViewModel.onBundleDataIsGet(testArticle)
        detailsViewModel.onBookmarkButtonClicked()
        Then("check if date is getting properly") {
            verify {
                scope.launch { repository.saveArticle(testArticle, savedDate) }
            }
        }
    }

    Given("calling init") {
        detailsViewModel.init(appDependencies)
        Then("check init calling properly") {
            coVerify {
                appDependencies.provideRepository()
            }
        }
    }

    Given("calling bundleDataIsGet") {
        When("bundle data is get") {
            detailsViewModel.onBundleDataIsGet(testArticle)
            Then("check saved methods in it") {
                detailsViewModel.currentArticleFlow.value shouldBe testArticle
                verify(atLeast = 1) {
                    detailsViewModel.checkIfElementSaved()
                }
            }
        }
    }


    Given("test getSavedList method") {
        every { repository.getSavedList() } returns Flowable.fromArray(
            arrayListOf(
                testArticle,
                null
            )
        )
        When("calling checkIfElementSaved") {
            detailsViewModel.checkIfElementSaved()
            Then("check if list is get properly") {
                repository.getSavedList().subscribe { savedList ->
                    savedList.removeIf { it == null }
                    savedList.forEach { article ->
                        if (article?.newsTitle == testArticle.newsTitle) detailsViewModel.saved.value =
                            true
                    }
                    savedList[0]!!.newsTitle shouldBe testArticle.newsTitle
                    savedList.size shouldBe 1
                }
            }
        }
    }

    afterSpec {
        Dispatchers.resetMain()
    }
})
