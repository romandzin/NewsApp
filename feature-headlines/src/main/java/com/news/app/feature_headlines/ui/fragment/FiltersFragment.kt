package com.news.app.feature_headlines.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.news.app.feature_headlines.R
import com.news.app.feature_headlines.databinding.FragmentFiltersBinding
import com.news.app.feature_headlines.ui.mvi.FiltersSideEffect
import com.news.app.feature_headlines.ui.mvi.FiltersState
import com.news.app.feature_headlines.ui.view_models.FiltersViewModel
import com.news.core.App
import com.news.data.data_api.model.Filters
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.viewmodel.observe

const val APPLY_FILTERS_KEY = "applyFiltersKey"
const val SEND_FILTERS_TO_ACTIVITY_KEY = "sendFiltersToActivityKey"
const val SEND_FILTERS_KEY = "sendFiltersKey"
const val DISABLE_FILTERS_KEY = "disableFilters"
const val FILTERS_KEY = "filtersKey"

class FiltersFragment : Fragment() {
    private lateinit var binding: FragmentFiltersBinding
    private val filtersViewModel: FiltersViewModel by lazy {
        ViewModelProvider(this)[FiltersViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setReadyButtonListener()
    }

    private fun initView() {
        filtersViewModel.init((requireActivity().application as App).provideAppDependenciesProvider())
        filtersViewModel.observe(
            state = ::render,
            lifecycleOwner = viewLifecycleOwner,
        )
        lifecycleScope.launch {
            filtersViewModel.container.sideEffectFlow.collect { sideEffect ->
                handleSideEffect(sideEffect)
            }
        }
        binding.calendarIcon.setOnClickListener {
            filtersViewModel.calendarIconClicked()
        }
    }

    private fun render(state: FiltersState) {
        if (!state.isInternetEnabled) setNoInternetMode()
        else {
            setToggleButtonListeners()
            setLanguageButtonListeners()
        }

        if (state.isCalendarShowed) prepareDialog()
        else binding.dialogBackground.isVisible = false

        if (state.dateTo != "") {
            binding.calendarText.text = "${state.dateFrom}-${state.dateTo}"
            binding.calendarText.setTextColor(
                resources.getColor(
                    R.color.main_blue,
                    context?.theme
                )
            )
            binding.calendarIcon.setImageDrawable(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.ic_selected_calendar
                )
            )
        }

        setNoDrawable(binding.sortByCategoryButton1)
        setNoDrawable(binding.sortByCategoryButton2)
        setNoDrawable(binding.sortByCategoryButton3)

        when (state.sortCategory) {
            "popularity" -> {
                setCheckDrawable(binding.sortByCategoryButton1)
            }

            "publishedAt" -> {
                setCheckDrawable(binding.sortByCategoryButton2)
            }

            "relevancy" -> {
                setCheckDrawable(binding.sortByCategoryButton3)
            }
        }

        setBackgroundToButton(
            binding.deutschLanguageButton,
            R.drawable.rounded_corner_button_bg
        )
        setBackgroundToButton(
            binding.englishLanguageButton,
            R.drawable.rounded_corner_button_bg
        )
        setBackgroundToButton(
            binding.russianLanguageButton,
            R.drawable.rounded_corner_button_bg
        )
        when (state.language) {
            "ru" -> {
                setBackgroundToButton(
                    binding.russianLanguageButton,
                    R.drawable.rounded_corner_selected_button_bg
                )
            }

            "en" -> {
                setBackgroundToButton(
                    binding.englishLanguageButton,
                    R.drawable.rounded_corner_selected_button_bg
                )
            }

            "de" -> {
                setBackgroundToButton(
                    binding.deutschLanguageButton,
                    R.drawable.rounded_corner_selected_button_bg
                )
            }
        }
    }

    private fun handleSideEffect(sideEffect: FiltersSideEffect) {
        Log.d("tag", "handleSide")
        when (sideEffect) {
            is FiltersSideEffect.ApplyFilters -> applyFilters(sideEffect.filters)
        }
    }

    private fun applyFilters(filters: Filters) {
        setFragmentResult(
            SEND_FILTERS_TO_ACTIVITY_KEY,
            bundleOf(
                FILTERS_KEY to filters
            )
        )
    }

    private fun setNoInternetMode() {
        binding.toggleButton.isVisible = false
        binding.languageText.isVisible = false
        binding.russianLanguageButton.isVisible = false
        binding.englishLanguageButton.isVisible = false
        binding.deutschLanguageButton.isVisible = false
    }

    private fun prepareDialog() {
        binding.dialogBackground.isVisible = true
        val dateRangePicker =
            MaterialDatePicker.Builder.dateRangePicker()
                .setTitleText("Select date")
                .setSelection(
                    Pair(
                        MaterialDatePicker.thisMonthInUtcMilliseconds(),
                        MaterialDatePicker.todayInUtcMilliseconds()
                    )
                )

                .setTheme(R.style.ThemeOverlay_Material3_MaterialCalendar_New)
                .build()

        dateRangePicker.show(parentFragmentManager, "datePicker")
        dateRangePicker.addOnDismissListener {
            filtersViewModel.calendarDismissed()
        }
        dateRangePicker.addOnPositiveButtonClickListener {
            binding.dialogBackground.isVisible = false
            filtersViewModel.calendarPositiveButtonClicked(
                dateRangePicker.selection!!.first,
                dateRangePicker.selection!!.second
            )
        }
    }


    private fun setToggleButtonListeners() {
        binding.sortByCategoryButton1.setOnClickListener {
            filtersViewModel.sortByCategoryButtonClicked("popularity")
        }
        binding.sortByCategoryButton2.setOnClickListener {
            filtersViewModel.sortByCategoryButtonClicked("publishedAt")
        }
        binding.sortByCategoryButton3.setOnClickListener {
            filtersViewModel.sortByCategoryButtonClicked("relevancy")
        }
    }

    private fun setLanguageButtonListeners() {
        binding.russianLanguageButton.setOnClickListener {
            filtersViewModel.languageButtonClicked("ru")
        }
        binding.englishLanguageButton.setOnClickListener {
            filtersViewModel.languageButtonClicked("en")
        }
        binding.deutschLanguageButton.setOnClickListener {
            filtersViewModel.languageButtonClicked("de")
        }
    }

    private fun setNoDrawable(view: MaterialButton) {
        view.setCompoundDrawablesWithIntrinsicBounds(
            0, 0, 0, 0
        )
    }

    private fun setCheckDrawable(view: MaterialButton) {
        view.setCompoundDrawablesWithIntrinsicBounds(
            R.drawable.ic_check, 0, 0, 0
        )
    }

    private fun setBackgroundToButton(view: AppCompatButton, drawable: Int) {
        view.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                requireContext(),
                drawable
            )
        )
    }

    private fun setReadyButtonListener() {
        setFragmentResultListener(APPLY_FILTERS_KEY) { _, _ ->
            filtersViewModel.applyFilters()
        }
    }

    companion object {
        fun newInstance(): FiltersFragment {
            return FiltersFragment()
        }
    }
}