package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.RaceRepository
import com.example.data.RaceUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RaceViewModel(private val repository: RaceRepository) : ViewModel() {
    val uiState: StateFlow<RaceUiState> = repository.uiStateFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RaceUiState.Loading
    )
    
    val ownedItems = repository.ownedItemsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )
    
    val recentEvents = repository.recentEventsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    fun createProfile(name: String) {
        viewModelScope.launch {
            repository.createProfile(name)
        }
    }

    fun addPoints(points: Int, note: String) {
        viewModelScope.launch {
            repository.addPoints(points, note)
        }
    }
    
    fun buyItem(itemId: String, price: Int) {
        viewModelScope.launch {
            repository.buyItem(itemId, price)
        }
    }
    
    fun equipCar(carId: String) {
        viewModelScope.launch {
            repository.equipCar(carId)
        }
    }
}
