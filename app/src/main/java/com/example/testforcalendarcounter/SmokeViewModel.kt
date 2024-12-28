package com.example.testforcalendarcounter

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SmokeViewModel @Inject constructor(
    private val repository: CigaretteRepository
): ViewModel(){

    private val _dayCigaretteCount = MutableLiveData<Int>()
    val dayCigaretteCount: LiveData<Int> = _dayCigaretteCount

    private val _weekCigaretteCount = MutableLiveData<Int>()
    val weekCigaretteCount: LiveData<Int> = _weekCigaretteCount

    private val _monthCigaretteCount = MutableLiveData<Int>()
    val monthCigaretteCount: LiveData<Int> = _monthCigaretteCount

    fun refreshCounts() {
        viewModelScope.launch {
            _dayCigaretteCount.value = repository.getDailyCount()
            _weekCigaretteCount.value = repository.getWeeklyCount()
            _monthCigaretteCount.value = repository.getMonthlyCount()
        }
    }

    fun addCigarette() {
        viewModelScope.launch {
            repository.addCigarette()
            refreshCounts()
        }

    }

}