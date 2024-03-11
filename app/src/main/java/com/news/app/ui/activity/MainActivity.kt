package com.news.app.ui.activity

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.splashscreen.SplashScreen
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.isVisible
import com.airbnb.lottie.LottieAnimationView
import com.news.app.R
import com.news.app.databinding.ActivityMainBinding
import com.news.app.ui.fragment.HeadLinesFragment

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        continueSplashScreenAnimationInActivity(splashScreen)
        initView()
    }

    private fun continueSplashScreenAnimationInActivity(splashScreen: SplashScreen) {
        splashScreen.setOnExitAnimationListener { vp ->
            val lottieView = findViewById<LottieAnimationView>(R.id.animationView)
            lottieView.enableMergePathsForKitKatAndAbove(true)
            lottieView.postDelayed({
                vp.view.alpha = 0f
                vp.iconView.alpha = 0f
                lottieView!!.playAnimation()
            }, 1000)

            lottieView.addAnimatorListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
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
                    supportFragmentManager.beginTransaction()
                        .replace(binding.fragmentContainerView.id, HeadLinesFragment())
                        .addToBackStack("headLinesFragment")
                        .commit()
                    true
                }

                R.id.saved_page -> {
                    true
                }

                R.id.sources_page -> {
                    true
                }

                else -> false
            }
        }
        binding.bottomNavView.selectedItemId = R.id.headlines_page
    }

    override fun onResume() {
        super.onResume()
        WindowCompat.setDecorFitsSystemWindows(window, false)
    }

    override fun onStop() {
        super.onStop()
        finish()
    }
}