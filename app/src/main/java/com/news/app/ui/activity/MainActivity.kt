package com.news.app.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.airbnb.lottie.LottieAnimationView
import com.news.app.R
import com.news.app.common.ToolbarState
import com.news.app.databinding.ActivityMainBinding
import com.news.app.ui.fragments.FiltersFragment
import com.news.app.ui.fragments.HeadLinesFragment
import com.news.app.ui.fragments.SavedFragment
import com.news.app.ui.fragments.SourcesFragment
import com.news.app.viewmodels.MainViewModel

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }
    var isBackPressed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    isBackPressed = true
                    val size = supportFragmentManager.backStackEntryCount
                    if (size == 1) finish()
                    else {
                        val previousCurrent = supportFragmentManager.getBackStackEntryAt(supportFragmentManager.backStackEntryCount - 2)
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
                    isBackPressed = false
                }
            })
        continueSplashScreenAnimationInActivity(splashScreen)
    }

    private fun continueSplashScreenAnimationInActivity(splashScreen: SplashScreen) {
        if (viewModel.isReady) {
            showMainScreen()
            setTheme(R.style.Base_Theme_NewsApp_NewTheme)
            binding.root.setBackgroundColor(resources.getColor(R.color.white, theme))
            setSupportActionBar(binding.toolbar.toolbarActionbar)
        } else {
            if (viewModel.isAnimationContinue) {
                val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
                lottieView.enableMergePathsForKitKatAndAbove(true)
                lottieView.postDelayed({
                    lottieView!!.playAnimation()
                }, 1000)

                lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator) {
                        showMainScreen()
                        setTheme(R.style.Base_Theme_NewsApp_NewTheme)
                        binding.root.setBackgroundColor(resources.getColor(R.color.white, theme))
                        setSupportActionBar(binding.toolbar.toolbarActionbar)
                        initView()
                        viewModel.isReady = true
                    }
                })
            }
        }
        splashScreen.setOnExitAnimationListener { vp ->
            viewModel.isAnimationContinue = true
            val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
            lottieView.enableMergePathsForKitKatAndAbove(true)
            lottieView.postDelayed({
                vp.view.alpha = 0f
                vp.iconView.alpha = 0f
                lottieView!!.playAnimation()
            }, 1000)

            lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    viewModel.isReady = true
                    showMainScreen()
                    setTheme(R.style.Base_Theme_NewsApp_NewTheme)
                    binding.root.setBackgroundColor(resources.getColor(R.color.white, theme))
                    setSupportActionBar(binding.toolbar.toolbarActionbar)
                }
            })
        }
    }

    private fun showMainScreen() {
        binding.animationView.visibility = View.GONE
        binding.bottomNavView.isVisible = true
        binding.fragmentContainerView.isVisible = true
        binding.toolbar.toolbarActionbar.isVisible = true
    }

    private fun initView() {
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
        if (!viewModel.isReady) binding.bottomNavView.selectedItemId = R.id.headlines_page
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

    private fun moveToFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(binding.fragmentContainerView.id, fragment)
            .commit()
    }

    private fun setNewToolbarState(currentToolbarState: ToolbarState, toolbarText: String = "") {
        when (currentToolbarState) {
            ToolbarState.Filter -> {
                binding.toolbar.toolbarFilter.root.visibility = View.VISIBLE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
            }

            ToolbarState.Search -> {
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.VISIBLE
                binding.toolbar.toolbarDefault.root.visibility = View.GONE
            }

            else -> {
                binding.toolbar.toolbarFilter.root.visibility = View.GONE
                binding.toolbar.toolbarSearch.root.visibility = View.GONE
                binding.toolbar.toolbarDefault.root.visibility = View.VISIBLE
                binding.toolbar.toolbarDefault.toolbarBaseText.text = toolbarText
            }
        }
    }
}