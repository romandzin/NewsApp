package com.news.app.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.news.app.R
import com.news.app.common.Extensions.getParcelableCompat
import com.news.app.common.ToolbarState
import com.news.app.databinding.ActivityMainBinding
import com.news.app.feature_headlines.ui.fragment.APPLY_FILTERS_KEY
import com.news.app.feature_headlines.ui.fragment.DISABLE_FILTERS_KEY
import com.news.app.feature_headlines.ui.fragment.FILTERS_KEY
import com.news.app.feature_headlines.ui.fragment.FiltersFragment
import com.news.app.feature_headlines.ui.fragment.HeadLinesFragment
import com.news.app.feature_headlines.ui.fragment.SEND_FILTERS_KEY
import com.news.app.feature_headlines.ui.fragment.SEND_FILTERS_TO_ACTIVITY_KEY
import com.news.app.feature_saved.ui.fragment.SavedFragment
import com.news.app.ui.fragments.ErrorFragment
import com.news.app.ui.fragments.NewsDetailsFragment
import com.news.app.ui.viewmodels.MainViewModel
import com.news.core.MainAppNavigator
import com.news.data.data_api.model.Article
import com.news.data.data_api.model.Filters
import com.news.feature_source.ui.fragment.SourcesFragment

const val SEARCH_ENABLED_KEY = "searchKey"
const val SEARCH_ENABLED = "searchEnable"
const val SEARCH_TEXT_ENTERED_KEY = "searchTextEntered"
const val SEARCH_TEXT = "searchText"

class MainActivity : AppCompatActivity(), MainAppNavigator {

    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    var searching = false
    var isBackPressed = false
    private var isSourcesWithArticle = false

    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isBackPressed = true
            binding.toolbar.toolbarDefault.appliedFiltersCount.isVisible = false
            val size = supportFragmentManager.backStackEntryCount
            if (searching) {
                disableSearchingAndGoBack()
            } else if (isCurrentShowingFragmentIsError()) {
                removeError()
                isBackPressed = false
                binding.bottomNavView.selectedItemId = binding.bottomNavView.selectedItemId
            } else if (isCurrentShowingFragmentIsSourceShowingArticles()) {
                val sourcesFragment =
                    supportFragmentManager.findFragmentByTag("sourcesFragment") as SourcesFragment
                sourcesFragment.goBack()
                isSourcesWithArticle = false
                setNewToolbarState(ToolbarState.Default, "Source")
            } else {
                if (size == 1) finish()
                else {
                    isBackPressed = false
                    val previousCurrent =
                        supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 2)
                    var tagNew = previousCurrent.name
                    if (tagNew == "filterFragment") {
                        val previousCurrent =
                            supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 3)
                        tagNew = previousCurrent.name
                    }
                    val fragmentNew = supportFragmentManager.findFragmentByTag(tagNew)
                    selectPreviousFragmentOnBottomNavBar(fragmentNew)
                }
            }
            isBackPressed = false
        }
    }

    private fun OnBackPressedCallback.disableSearchingAndGoBack() {
        when (supportFragmentManager.fragments.last()::class) {
            HeadLinesFragment::class -> {
                binding.bottomNavView.selectedItemId =
                    R.id.headlines_page
                disableSearching()
            }

            SavedFragment::class -> {
                binding.bottomNavView.selectedItemId =
                    R.id.saved_page
                disableSearching()
            }

            SourcesFragment::class -> {
                binding.bottomNavView.selectedItemId =
                    R.id.sources_page
                disableSearching()
            }

            NewsDetailsFragment::class -> {
                disableSearching()
                handleOnBackPressed()
            }
        }
    }

    private fun selectPreviousFragmentOnBottomNavBar(fragmentNew: Fragment?) {
        when (fragmentNew!!::class.java) {
            HeadLinesFragment::class.java -> {
                binding.bottomNavView.selectedItemId =
                    R.id.headlines_page
            }

            SourcesFragment::class.java -> {
                binding.bottomNavView.selectedItemId = R.id.sources_page
                isSourcesWithArticle = false
            }

            SavedFragment::class.java -> {
                binding.bottomNavView.selectedItemId = R.id.saved_page
            }
        }
    }

    private fun isCurrentShowingFragmentIsSourceShowingArticles() =
        supportFragmentManager.fragments.last()::class == SourcesFragment::class && isSourcesWithArticle

    private fun isCurrentShowingFragmentIsError() =
        supportFragmentManager.findFragmentByTag("errorFragment") != null && supportFragmentManager.fragments.last().tag == "errorFragment"

    private fun disableSearching() {
        searching = false
        supportFragmentManager.setFragmentResult(
            SEARCH_ENABLED_KEY,
            bundleOf(
                SEARCH_ENABLED to false
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onBackPressedDispatcher.addCallback(
            this,
            onBackPressedCallback
        )
        continueSplashScreenAnimationInActivity(splashScreen)
    }

    private fun continueSplashScreenAnimationInActivity(splashScreen: SplashScreen) {
        if (viewModel.isReady) {
            prepareMainActivity()
        }
        splashScreen.setOnExitAnimationListener { vp ->
            vp.view.alpha = 0f
            vp.iconView.alpha = 0f
            startLottieAnimationAsPartOfSplashScreen()
        }
    }

    private fun startLottieAnimationAsPartOfSplashScreen() {
        binding.animationView.enableMergePathsForKitKatAndAbove(true)
        binding.animationView.playAnimation()
        binding.animationView.addAnimatorListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                prepareMainActivity()
                initView()
                viewModel.isReady = true
            }
        })
    }

    private fun prepareMainActivity() {
        showMainScreen()
        setTheme(R.style.Base_Theme_NewsApp_NewTheme)
        binding.root.setBackgroundColor(resources.getColor(R.color.white, theme))
        setSupportActionBar(binding.toolbar.toolbarActionbar)
    }

    private fun showMainScreen() {
        binding.animationView.visibility = View.GONE
        binding.bottomNavView.isVisible = true
        binding.fragmentContainerView.isVisible = true
        binding.toolbar.toolbarActionbar.isVisible = true
    }

    private fun initView() {
        WindowCompat.setDecorFitsSystemWindows(
            window,
            false
        )
        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            selectMenuItem(menuItem)
        }
        binding.toolbar.toolbarDefault.filterButton.setOnClickListener {
            moveToFragment(FiltersFragment.newInstance(), "filterFragment")
            setNewToolbarState(ToolbarState.Filter)
        }
        binding.toolbar.toolbarSearch.searchEditText.addTextChangedListener { text ->
            supportFragmentManager.setFragmentResult(
                SEARCH_TEXT_ENTERED_KEY,
                bundleOf(
                    SEARCH_TEXT to text.toString()
                )
            )
        }
        binding.toolbar.toolbarSearch.removeTextButton.setOnClickListener {
            binding.toolbar.toolbarSearch.searchEditText.setText("")
        }
        binding.toolbar.toolbarFilter.completeButton.setOnClickListener {
            enableFilters()
        }
        binding.toolbar.toolbarDefault.searchButton.setOnClickListener {
            enableSearching()
        }
        binding.toolbar.toolbarSource.searchButton.setOnClickListener {
            enableSearching()
        }
        setClickListenersOnBackButtons()
        if (!viewModel.isReady) binding.bottomNavView.selectedItemId =
            R.id.headlines_page
    }

    private fun enableFilters() {
        goBack()
        supportFragmentManager.setFragmentResultListener(
            SEND_FILTERS_TO_ACTIVITY_KEY,
            this
        ) { _, bundle ->
            val filters = bundle.getParcelableCompat(FILTERS_KEY, Filters::class.java)
            setFiltersCount(filters)
            supportFragmentManager.setFragmentResult(
                SEND_FILTERS_KEY, bundleOf(
                    FILTERS_KEY to filters
                )
            )
        }
        if (binding.bottomNavView.selectedItemId != R.id.headlines_page) binding.bottomNavView.selectedItemId =
            R.id.headlines_page
        supportFragmentManager.setFragmentResult(
            APPLY_FILTERS_KEY,
            bundleOf()
        )
    }

    private fun selectMenuItem(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.headlines_page -> {
            if (!isBackPressed) moveToFragment(HeadLinesFragment(), "headlinesFragment")
            setNewToolbarState(
                ToolbarState.Default,
                getText(R.string.news_app_text).toString()
            )
            true
        }

        R.id.saved_page -> {
            if (!isBackPressed) moveToFragment(SavedFragment(), "savedFragment")
            setNewToolbarState(ToolbarState.Default, "Saved")
            true
        }

        R.id.sources_page -> {
            if (!isBackPressed) moveToFragment(SourcesFragment(), "sourcesFragment")
            setNewToolbarState(ToolbarState.Default, "Sources")
            true
        }

        else -> false
    }

    private fun setClickListenersOnBackButtons() {
        binding.toolbar.toolbarSearch.backButton.setOnClickListener {
            goBack()
        }
        binding.toolbar.toolbarFilter.backButton.setOnClickListener {
            supportFragmentManager.setFragmentResult(
                DISABLE_FILTERS_KEY,
                bundleOf()
            )
            goBack()
        }
        binding.toolbar.toolbarSource.backButton.setOnClickListener {
            goBack()
        }
    }

    private fun enableSearching() {
        searching = true
        setNewToolbarState(ToolbarState.Search)
        supportFragmentManager.setFragmentResult(
            SEARCH_ENABLED_KEY,
            bundleOf(
                SEARCH_ENABLED to true
            )
        )
    }

    private fun setFiltersCount(filters: Filters) {
        val filtersCount = countEnabledFilters(filters)
        if (filtersCount != 0) binding.toolbar.toolbarDefault.appliedFiltersCount.isVisible = true
        binding.toolbar.toolbarDefault.appliedFiltersCount.text = filtersCount.toString()
    }

    private fun countEnabledFilters(filters: Filters): Int {
        var counter = 0
        for (i in filters.properties) {
            if (i.get() != "") counter++
        }
        return counter
    }

    override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    private fun moveToFragment(fragment: Fragment, nameTag: String) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment, nameTag)
            .addToBackStack(nameTag)
            .commit()
    }

    override fun moveToDetailsFragment(article: Article, nameTag: String) {
        val fragment = NewsDetailsFragment.newInstance(article)
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment, nameTag)
            .addToBackStack(nameTag)
            .commit()
        setNewToolbarState(ToolbarState.Gone)
    }

    private fun moveToErrorFragment(fragment: Fragment, nameTag: String) {
        supportFragmentManager.beginTransaction()
            .add(binding.fragmentContainerView.id, fragment, nameTag)
            .commit()
    }

    override fun showError(errorType: Int, lastFunctionBeforeError: () -> Unit) {
        moveToErrorFragment(
            ErrorFragment.newInstance(errorType, lastFunctionBeforeError),
            "errorFragment"
        )
    }

    override fun goBack() {
        onBackPressedDispatcher.onBackPressed()
    }

    override fun sourcesShowingArticles(source: String) {
        isSourcesWithArticle = true
        setNewToolbarState(ToolbarState.Sources, source)
    }

    override fun removeError() {
        val errorFragment = supportFragmentManager.findFragmentByTag("errorFragment")
        if (errorFragment != null)
            supportFragmentManager.beginTransaction()
                .remove(errorFragment)
                .commit()
    }

    private fun setNewToolbarState(currentToolbarState: ToolbarState, toolbarText: String = "") {
        binding.toolbar.toolbarDefault.appliedFiltersCount.isVisible = false
        when (currentToolbarState) {
            ToolbarState.Filter -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.VISIBLE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
                binding.toolbar.toolbarSource.root.visibility = View.GONE
            }

            ToolbarState.Search -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.VISIBLE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
                binding.toolbar.toolbarSource.root.visibility = View.GONE
            }

            ToolbarState.Gone -> {
                binding.toolbar.root.visibility = View.GONE
            }

            ToolbarState.Sources -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
                binding.toolbar.toolbarSource.root.visibility = View.VISIBLE
                binding.toolbar.toolbarSource.toolbarBaseText.text = toolbarText
            }

            else -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.VISIBLE
                binding.toolbar.toolbarSource.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.toolbarBaseText.text = toolbarText
            }
        }
    }
}