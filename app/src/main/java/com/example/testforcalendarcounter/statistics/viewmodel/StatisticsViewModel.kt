package com.example.testforcalendarcounter.statistics.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.testforcalendarcounter.repository.CigaretteRepository
import com.github.mikephil.charting.data.BarEntry
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val repository: CigaretteRepository
) : ViewModel() {

    private val _chartEntries = MutableLiveData<List<BarEntry>>()
    val chartEntries: LiveData<List<BarEntry>> = _chartEntries

    private val _timeRange = MutableLiveData<TimeRange>(TimeRange.WEEKLY)
    val timeRange: LiveData<TimeRange> = _timeRange

    private val _isCostMode = MutableLiveData<Boolean>(false)
    val isCostMode: LiveData<Boolean> = _isCostMode

    fun setTimeRange(range: TimeRange) {
        _timeRange.value = range
        refreshData()
    }

    fun toggleCostMode(isCost: Boolean) {
        _isCostMode.value = isCost
        refreshData()
    }

    fun refreshData() {
        viewModelScope.launch {
            val range = _timeRange.value ?: TimeRange.WEEKLY
            val costMode = _isCostMode.value ?: false

            val entries = when (range) {
                TimeRange.WEEKLY -> getWeeklyEntries(costMode)
                TimeRange.MONTHLY -> getMonthlyEntries(costMode)
                TimeRange.YEARLY -> getYearlyEntries(costMode)
            }
            _chartEntries.value = entries
        }
    }

    private suspend fun getWeeklyEntries(costMode: Boolean): List<BarEntry> {
        val weeklyData = repository.getWeeklyStatsForLast7Days() // Example method
        return weeklyData.mapIndexed { index, dayData ->
            val yValue = if (costMode) dayData.cost.toFloat() else dayData.count.toFloat()
            BarEntry(index.toFloat(), yValue)
        }
    }

    private suspend fun getMonthlyEntries(costMode: Boolean): List<BarEntry> {
        val monthlyData = repository.getMonthlyStatsForThisMonth()
        return monthlyData.map { dayData ->
            val x = dayData.day.toFloat()
            val y = if (costMode) dayData.cost.toFloat() else dayData.count.toFloat()
            BarEntry(x, y)
        }
    }

    private suspend fun getYearlyEntries(costMode: Boolean): List<BarEntry> {
        val yearlyData = repository.getYearlyStatsForThisYear() // Retrieve stats for the year
        val entries = mutableListOf<BarEntry>()

        // Create a map for quick lookup of stats by month
        val dataMap = yearlyData.associateBy { it.month }

        // Populate entries for all 12 months
        for (month in 1..12) {
            val yValue = dataMap[month]?.let {
                if (costMode) it.cost.toFloat() else it.count.toFloat()
            } ?: 0f // Default to 0 if no data for the month
            entries.add(BarEntry((month -1 ).toFloat(), yValue))
        }

        return entries
    }
}

enum class TimeRange {
    WEEKLY, MONTHLY, YEARLY
}

data class DayData(val day: Int, val count: Int, val cost: Double)
data class MonthData(val month: Int, val count: Int, val cost: Double)
