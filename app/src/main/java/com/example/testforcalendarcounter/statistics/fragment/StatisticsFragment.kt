package com.example.testforcalendarcounter.statistics.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.testforcalendarcounter.databinding.FragmentStatisticsBinding
import com.example.testforcalendarcounter.statistics.viewmodel.StatisticsViewModel
import com.example.testforcalendarcounter.statistics.viewmodel.TimeRange
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StatisticsFragment : Fragment() {

    private var _binding: FragmentStatisticsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: StatisticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStatisticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup spinner for Weekly/Monthly/Yearly
        val spinnerItems = listOf("Weekly", "Monthly", "Yearly")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        binding.timeRangeSpinner.adapter = adapter

        binding.timeRangeSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selected = spinnerItems[position]
                val range = when (selected) {
                    "Weekly" -> TimeRange.WEEKLY
                    "Monthly" -> TimeRange.MONTHLY
                    "Yearly" -> TimeRange.YEARLY
                    else -> TimeRange.WEEKLY
                }
                viewModel.setTimeRange(range)
            }
        }

        // Setup toggle for "Count" and "Cost"
        binding.costModeSwitch.setOnCheckedChangeListener { _, isChecked ->
            viewModel.toggleCostMode(isChecked)
            updateToggleLabels(isChecked)
        }

        // Observe chart entries
        viewModel.chartEntries.observe(viewLifecycleOwner) { entries ->
            setupBarChart(entries)
        }
    }

    private fun updateToggleLabels(isCost: Boolean) {
        binding.countTextView.isEnabled = !isCost
        binding.costTextView.isEnabled = isCost
    }

    private fun setupBarChart(entries: List<BarEntry>) {
        // 1) Create the dataset
        val dataSet = BarDataSet(entries, null).apply {
            valueTextSize = 14f
            // Use a custom formatter to hide zero-value labels
            valueFormatter = object : ValueFormatter() {
                override fun getFormattedValue(value: Float): String {
                    // If 0.0, don't show any label
                    if (value == 0f) return ""

                    // Otherwise, show cost with two decimals or integer count
                    return if (viewModel.isCostMode.value == true) {
                        String.format("%.2f", value)
                    } else {
                        value.toInt().toString()
                    }
                }
            }
        }

        // 2) Create and set BarData
        val barData = BarData(dataSet).apply {
            barWidth = 0.9f
        }
        binding.statsBarChart.data = barData

        // 3) Style the chart
        binding.statsBarChart.description.isEnabled = false
        binding.statsBarChart.legend.isEnabled = false
        binding.statsBarChart.axisRight.isEnabled = false

        // Adjust colors for Dark/Light mode
        val isDarkMode = resources.configuration.uiMode and
                android.content.res.Configuration.UI_MODE_NIGHT_MASK == android.content.res.Configuration.UI_MODE_NIGHT_YES

        val textColor = if (isDarkMode) android.R.color.white else android.R.color.black
        val barColor = if (isDarkMode) android.R.color.holo_blue_light else android.R.color.holo_orange_light

        dataSet.color = ContextCompat.getColor(requireContext(), barColor)
        dataSet.valueTextColor = ContextCompat.getColor(requireContext(), textColor)

        // X-Axis labels
        val xAxis = binding.statsBarChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f
        xAxis.labelRotationAngle = -45f
        xAxis.textColor = ContextCompat.getColor(requireContext(), textColor)

        // Assign the label set based on which TimeRange is active
        when (viewModel.timeRange.value) {
            TimeRange.WEEKLY -> {
                xAxis.valueFormatter = IndexAxisValueFormatter(listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"))
                xAxis.labelCount = 7
            }
            TimeRange.MONTHLY -> {
                xAxis.valueFormatter = IndexAxisValueFormatter(listOf("1", "6", "11", "16", "21", "26", "31"))
                xAxis.labelCount = 7
            }
            TimeRange.YEARLY -> {
                // Empty index at [0], so index 1 = "Jan", 2 = "Feb", ... 12 = "Dec"
                xAxis.valueFormatter = IndexAxisValueFormatter(
                    listOf("Jan", "Feb", "Mar", "Apr", "May", "Jun",
                        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
                )
                xAxis.labelCount = 12
            }
            else -> Unit
        }

        // Y-Axis styling
        val leftAxis = binding.statsBarChart.axisLeft
        leftAxis.axisMinimum = 0f
        leftAxis.textColor = ContextCompat.getColor(requireContext(), textColor)

        // Disable right axis
        binding.statsBarChart.axisRight.isEnabled = false

        // 4) Refresh chart
        binding.statsBarChart.invalidate()
    }

    override fun onResume() {
        super.onResume()
        viewModel.refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
