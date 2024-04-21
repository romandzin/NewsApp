package com.news.app.ui.viewmodels

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.ViewModel
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.news.app.core.AppDependenciesProvider
import com.news.app.ui.model.Filters
import com.news.app.ui.mvi.FiltersSideEffect
import com.news.app.ui.mvi.FiltersState
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.syntax.simple.intent
import org.orbitmvi.orbit.syntax.simple.postSideEffect
import org.orbitmvi.orbit.syntax.simple.reduce
import org.orbitmvi.orbit.viewmodel.container
import java.text.SimpleDateFormat
import java.util.Calendar

class FiltersViewModel : ContainerHost<FiltersState, FiltersSideEffect>, ViewModel() {

    override val container = container<FiltersState, FiltersSideEffect>(FiltersState())
    private val filters = Filters()
    lateinit var context: Context

    fun init(appDependencies: AppDependenciesProvider) {
        context = appDependencies.provideApplicationContext()
        intent {
            reduce {
                state.copy(isInternetEnabled = observeInternetConnection())
            }
        }
    }

    private fun observeInternetConnection(): Boolean {
        val result: Boolean
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.activeNetwork ?: return false
        val actNw = connectivityManager.getNetworkCapabilities(networkCapabilities) ?: return false
        result = when {
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
        return result
    }

    fun calendarIconClicked() = intent {
        reduce {
            state.copy(isCalendarShowed = true)
        }
    }

    fun calendarDismissed() = intent {
        reduce {
            state.copy(isCalendarShowed = false)
        }
    }

    fun calendarPositiveButtonClicked(firstDate: Long, secondDate: Long) {
        val firstDateToRender = getDate(firstDate, "MMM dd")!!
        val secondDateToRender = getDate(secondDate, "MMM dd, YYYY")!!
        filters.dateFrom = getDate(firstDate, "yyyy-MM-dd")!!
        filters.dateTo = getDate(secondDate, "yyyy-MM-dd")!!
        intent {
            reduce {
                state.copy(dateFrom = firstDateToRender, dateTo = secondDateToRender)
            }
        }

    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun sortByCategoryButtonClicked(category: String) {
        intent {
            reduce {
                if (state.sortCategory != category) {
                    filters.sortByParam = category
                    state.copy(sortCategory = category)
                } else {
                    filters.sortByParam = ""
                    state.copy(sortCategory = "")
                }
            }
        }
    }

    fun applyFilters() = intent {
            postSideEffect(FiltersSideEffect.ApplyFilters(filters))
        }


    fun languageButtonClicked(language: String) {
        intent {
            reduce {
                if (state.language != language) {
                    filters.language = language
                    state.copy(language = language)
                } else {
                    filters.language = ""
                    state.copy(language = "")
                }
            }
        }
    }
}