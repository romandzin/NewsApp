package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.news.app.databinding.FragmentErrorBinding
import com.news.app.ui.viewmodels.ErrorViewModel

const val NO_INTERNET_ERROR = 0
const val ANOTHER_ERROR = 1
const val ERROR_TYPE = "error type"

class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    private val errorViewModel by lazy {
        ViewModelProvider(this)[ErrorViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        errorViewModel.errorTextLiveData.observe(viewLifecycleOwner) { errorText ->
            binding.errorText.text = errorText
        }
        binding.refreshIcon.setOnClickListener {
            binding.refreshIcon.animate().rotationBy(1800F).setDuration(3000).start()
            errorViewModel.refreshButtonClicked(lastFunction)
        }
        errorViewModel.viewInit(getBundleData())
    }

    private fun getBundleData(): Int {
        val bundle = this.arguments
        return bundle?.getInt(ERROR_TYPE, 1) ?: 1
    }

    companion object {

        var lastFunction: (() -> Unit)? = null
        @JvmStatic
        fun newInstance(error: Int, lastFunctionBeforeError: () -> Unit) =
            ErrorFragment().apply {
                lastFunction = lastFunctionBeforeError
                arguments = Bundle().apply {
                    putInt(ERROR_TYPE, error)
                }
            }
    }
}