package com.news.app.feature_headlines.ui.moxy

import android.os.Bundle
import androidx.fragment.app.Fragment
import moxy.MvpDelegate
import moxy.MvpDelegateHolder


@Suppress("unused")
open class MvpAppCompatFragment : Fragment(), MvpDelegateHolder {
    private var isStateSaved = false
    private var mvpDelegate: MvpDelegate<out MvpAppCompatFragment>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getMvpDelegate().onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        isStateSaved = false
        getMvpDelegate().onAttach()
    }

    override fun onResume() {
        super.onResume()
        isStateSaved = false
        getMvpDelegate().onAttach()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        isStateSaved = true
        getMvpDelegate().onSaveInstanceState(outState)
        getMvpDelegate().onDetach()
    }

    override fun onStop() {
        super.onStop()
        getMvpDelegate().onDetach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        getMvpDelegate().onDetach()
        getMvpDelegate().onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (activity?.isFinishing == true) {
            getMvpDelegate().onDestroy()
            return
        }

        if (isStateSaved) {
            isStateSaved = false
            return
        }
        var anyParentIsRemoving = false
        var parent: Fragment? = parentFragment
        while (!anyParentIsRemoving && parent != null) {
            anyParentIsRemoving = parent.isRemoving
            parent = parent.parentFragment
        }
        if (isRemoving() || anyParentIsRemoving) {
            getMvpDelegate().onDestroy()
        }
    }

    override fun getMvpDelegate(): MvpDelegate<*> {
        if (mvpDelegate == null) {
            mvpDelegate = MvpDelegate(this)
        }
        return mvpDelegate as MvpDelegate<out MvpAppCompatFragment>
    }
}
