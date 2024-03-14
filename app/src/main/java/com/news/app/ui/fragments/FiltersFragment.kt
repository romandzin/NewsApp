package com.news.app.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.appcompat.widget.AppCompatButton
import androidx.core.util.Pair
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.news.app.R
import com.news.app.databinding.FragmentFiltersBinding
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale


class FiltersFragment : Fragment() {
    private lateinit var binding: FragmentFiltersBinding
    var currentLanguageButton = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFiltersBinding.inflate(layoutInflater)
        initView()
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
                val secondDate = getDate(dateRangePicker.selection!!.second, "MMM dd, YYYY")!!
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
        binding.button1.setOnClickListener {
            if (binding.button1.isChecked) setCheckDrawable(binding.button1)
            else setNoDrawable(binding.button1)
            setNoDrawable(binding.button2)
            setNoDrawable(binding.button3)
        }
        binding.button2.setOnClickListener {
            if (binding.button2.isChecked) setCheckDrawable(binding.button2)
            else setNoDrawable(binding.button2)
            setNoDrawable(binding.button1)
            setNoDrawable(binding.button3)
        }
        binding.button3.setOnClickListener {
            if (binding.button3.isChecked) setCheckDrawable(binding.button3)
            else setNoDrawable(binding.button3)
            setNoDrawable(binding.button2)
            setNoDrawable(binding.button1)
        }
    }

    private fun setLanguageButtonListeners() {
        binding.russianLanguageButton.setOnClickListener {
            if (currentLanguageButton == 1) setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_button_bg)
            else setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_selected_button_bg)
            setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_button_bg)
            setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_button_bg)
            currentLanguageButton = 1
        }
        binding.englishLanguageButton.setOnClickListener {
            if (currentLanguageButton == 2) setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_button_bg)
            else setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_selected_button_bg)
            setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_button_bg)
            setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_button_bg)
            currentLanguageButton = 2
        }
        binding.deutschLanguageButton.setOnClickListener {
            if (currentLanguageButton == 3) setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_button_bg)
            else setBackgroundToButton(binding.deutschLanguageButton, R.drawable.rounded_corner_selected_button_bg)
            setBackgroundToButton(binding.englishLanguageButton, R.drawable.rounded_corner_button_bg)
            setBackgroundToButton(binding.russianLanguageButton, R.drawable.rounded_corner_button_bg)
            currentLanguageButton = 3
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

    companion object {
        fun newInstance(): FiltersFragment {
            return FiltersFragment()
        }
    }
}