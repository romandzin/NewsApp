package com.news.app.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.news.app.databinding.FragmentErrorBinding
import com.news.app.ui.di.error.DaggerErrorComponent
import com.news.app.ui.viewmodels.ErrorViewModel
import javax.inject.Inject

const val NO_INTERNET_ERROR = 0
const val ANOTHER_ERROR = 1
const val ERROR_TYPE = "error type"

class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    @Inject
    lateinit var errorViewModel: ErrorViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        DaggerErrorComponent
            .builder()
            .build()
            .inject(this)
        errorViewModel.errorTextLiveData.observe(viewLifecycleOwner) { errorText ->
            binding.errorText.text = errorText
        }
        binding.refreshIcon.setOnClickListener {
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