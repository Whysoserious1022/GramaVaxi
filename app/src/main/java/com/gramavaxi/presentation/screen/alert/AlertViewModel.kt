package com.gramavaxi.presentation.screen.alert

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gramavaxi.domain.repository.AlertRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlertViewModel @Inject constructor(
    private val alertRepository: AlertRepository
) : ViewModel() {
    val alerts = alertRepository.getAllAlerts()
    val unreadCount = alertRepository.getUnreadAlertCount()

    fun markAsRead(alertId: String) {
        viewModelScope.launch { alertRepository.markAlertAsRead(alertId) }
    }

    fun markAllAsRead() {
        viewModelScope.launch { alertRepository.markAllAlertsAsRead() }
    }
}
