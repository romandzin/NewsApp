package com.news.app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.news.app.R
import com.news.app.databinding.FragmentFiltersBinding
import com.news.app.ui.model.Filters
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


const val APPLY_FILTERS_KEY = "applyFiltersKey"
const val SEND_FILTERS_KEY = "sendFiltersKey"
const val DISABLE_FILTERS_KEY = "disableFilters"
const val FILTERS_KEY = "filtersKey"
class FiltersFragment : Fragment() {
    private lateinit var binding: FragmentFiltersBinding
    val filters = Filters()
    var currentLanguageButton = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(layoutInflater)
        initView()
        setReadyButtonListener()
        return binding.root
    }

    private fun initView() {
        setToggleButtonListeners()
        setLanguageButtonListeners()
        binding.calendarIcon.setOnClickListener {
            val dateRangePicker =
                MaterialDatePicker.Builder.dateRangePicker()
                    .setTitleText("Select dates")
                    .setSelection(
                        Pair(
                            MaterialDatePicker.thisMonthInUtcMilliseconds(),
                            MaterialDatePicker.todayInUtcMilliseconds()
                        )
                    )

                    .setTheme(R.style.ThemeOverlay_Material3_MaterialCalendar_New)
                    .build()

            dateRangePicker.show(parentFragmentManager, "datePicker")
            dateRangePicker.addOnPositiveButtonClickListener {
                val firstDate = getDate(dateRangePicker.selection!!.first, "MMM dd")!!
                filters.dateFrom = getDate(dateRangePicker.selection!!.first, "yyyy-MM-dd")!!
                val secondDate = getDate(dateRangePicker.selection!!.second, "MMM dd, YYYY")!!
                filters.dateTo = getDate(dateRangePicker.selection!!.second, "yyyy-MM-dd")!!
                binding.calendarText.text = "$firstDate-$secondDate"
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
        }
    }

    private fun getDate(milliSeconds: Long, dateFormat: String?): String? {
        val formatter = SimpleDateFormat(dateFormat)
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun setToggleButtonListeners() {
        binding.sortByCategoryButton1.setOnClickListener {
            if (binding.sortByCategoryButton1.isChecked) setCheckDrawable(binding.sortByCategoryButton1)
            else setNoDrawable(binding.sortByCategoryButton1)
            setNoDrawable(binding.sortByCategoryButton2)
            setNoDrawable(binding.sortByCategoryButton3)
            filters.sortByParam = "popularity"
        }
        binding.sortByCategoryButton2.setOnClickListener {
            if (binding.sortByCategoryButton2.isChecked) setCheckDrawable(binding.sortByCategoryButton2)
            else setNoDrawable(binding.sortByCategoryButton2)
            setNoDrawable(binding.sortByCategoryButton1)
            setNoDrawable(binding.sortByCategoryButton3)
            filters.sortByParam = "publishedAt"
        }
        binding.sortByCategoryButton3.setOnClickListener {
            if (binding.sortByCategoryButton3.isChecked) setCheckDrawable(binding.sortByCategoryButton3)
            else setNoDrawable(binding.sortByCategoryButton3)
            setNoDrawable(binding.sortByCategoryButton2)
            setNoDrawable(binding.sortByCategoryButton1)
            filters.sortByParam = "relevancy"
        }
    }

    private fun setLanguageButtonListeners() {
        binding.russianLanguageButton.setOnClickListener {
            if (currentLanguageButton == 1) setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_button_bg)
            else setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_selected_button_bg)
            setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_button_bg)
            setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_button_bg)
            currentLanguageButton = 1
            filters.language = "ru"
        }
        binding.englishLanguageButton.setOnClickListener {
            if (currentLanguageButton == 2) setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_button_bg)
            else setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_selected_button_bg)
            setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_button_bg)
            setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_button_bg)
            currentLanguageButton = 2
            filters.language = "en"
        }
        binding.deutschLanguageButton.setOnClickListener {
            if (currentLanguageButton == 3) setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_button_bg)
            else setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_selected_button_bg)
            setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_button_bg)
            setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_button_bg)
            currentLanguageButton = 3
            filters.language = "de"
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
            setFragmentResult(SEND_FILTERS_KEY,
                bundleOf(
                    FILTERS_KEY to filters
                )
            )
        }
    }

    override fun onStop() {
        super.onStop()
    }

    companion object {
        fun newInstance(): FiltersFragment {
            return FiltersFragment()
        }
    }
}