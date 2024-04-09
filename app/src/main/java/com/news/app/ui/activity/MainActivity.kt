package com.news.app.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
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
import com.news.app.common.Navigator
import com.news.app.common.NetworkConnectivityObserver
import com.news.app.common.ToolbarState
import com.news.app.databinding.ActivityMainBinding
import com.news.app.ui.fragments.APPLY_FILTERS_KEY
import com.news.app.ui.fragments.ErrorFragment
import com.news.app.ui.fragments.FiltersFragment
import com.news.app.ui.fragments.HeadLinesFragment
import com.news.app.ui.fragments.NO_INTERNET_ERROR
import com.news.app.ui.fragments.SavedFragment
import com.news.app.ui.fragments.SourcesFragment
import com.news.app.ui.viewmodels.MainViewModel
import kotlinx.coroutines.flow.onEach

const val SEARCH_ENABLED_KEY = "searchKey"
const val SEARCH_ENABLED = "searchEnable"
const val SEARCH_TEXT_ENTERED_KEY = "searchTextEntered"
const val SEARCH_TEXT = "searchText"

class MainActivity : AppCompatActivity(), Navigator {
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    private var isInternetEnabled: Boolean = true
    var searching = false
    var isBackPressed = false
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            isBackPressed = true
            val size = supportFragmentManager.backStackEntryCount
            if (searching) {
                binding.bottomNavView.selectedItemId =
                    R.id.headlines_page
                searching = false
                supportFragmentManager.setFragmentResult(
                    SEARCH_ENABLED_KEY,
                    bundleOf(
                        SEARCH_ENABLED to false
                    )
                )
            } else {
                if (size == 1) finish()
                else {
                    val previousCurrent =
                        supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 2)
                    val tagNew = previousCurrent.name
                    val fragmentNew = supportFragmentManager.findFragmentByTag(tagNew)
                    when (fragmentNew!!::class.java) {
                        HeadLinesFragment::class.java -> {
                            binding.bottomNavView.selectedItemId =
                                R.id.headlines_page
                        }

                        SourcesFragment::class.java -> {
                            binding.bottomNavView.selectedItemId = R.id.sources_page
                        }

                        SavedFragment::class.java -> {
                            binding.bottomNavView.selectedItemId = R.id.saved_page
                        }
                    }
                    supportFragmentManager.popBackStack()
                }
            }
            isBackPressed = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
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
            viewModel.isAnimationContinue = true
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
        observeInternetConnection()
        binding.bottomNavView.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
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
        }
        binding.toolbar.toolbarDefault.filterButton.setOnClickListener {
            moveToFragment(FiltersFragment.newInstance(), "filterFragment")
            setNewToolbarState(ToolbarState.Filter)
        }
        binding.toolbar.toolbarDefault.searchButton.setOnClickListener {
            searching = true
            setNewToolbarState(ToolbarState.Search)
            supportFragmentManager.setFragmentResult(
                SEARCH_ENABLED_KEY,
                bundleOf(
                    SEARCH_ENABLED to true
                )
            )
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
        }
        binding.toolbar.toolbarFilter.completeButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
            supportFragmentManager.setFragmentResult(
                APPLY_FILTERS_KEY,
                bundleOf()
            )
        }
        if (!viewModel.isReady && isInternetEnabled) binding.bottomNavView.selectedItemId =
            R.id.headlines_page
        else {
            moveToErrorFragment(ErrorFragment.newInstance(NO_INTERNET_ERROR), "errorFragment")
        }
    }

    private fun observeInternetConnection() {
        val connectivityObserver = NetworkConnectivityObserver(applicationContext)
        connectivityObserver.observe().onEach {
            isInternetEnabled = it
            if (!it) moveToErrorFragment(ErrorFragment.newInstance(NO_INTERNET_ERROR), "errorFragment")
        }
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

    override fun moveToDetailsFragment(fragment: Fragment, nameTag: String) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment, nameTag)
            .addToBackStack(nameTag)
            .commit()
        setNewToolbarState(ToolbarState.Gone)
    }

    private fun moveToErrorFragment(fragment: Fragment, nameTag: String) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment, nameTag)
            .commit()
    }

    override fun showError(errorType: Int) {
        moveToErrorFragment(ErrorFragment.newInstance(errorType), "errorFragment")
    }

    private fun setNewToolbarState(currentToolbarState: ToolbarState, toolbarText: String = "") {
        when (currentToolbarState) {
            ToolbarState.Filter -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.VISIBLE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
            }

            ToolbarState.Search -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.VISIBLE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
            }

            ToolbarState.Gone -> {
                binding.toolbar.root.visibility = View.GONE
            }

            else -> {
                binding.toolbar.root.isVisible = true
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.VISIBLE
                binding.toolbar.toolbarDefault.toolbarBaseText.text = toolbarText
            }
        }
    }
}