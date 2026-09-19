package com.astute.ai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    
    private val _audioPermission = MutableStateFlow(false)
    val audioPermission: StateFlow<Boolean> = _audioPermission.asStateFlow()
    
    private val _overlayPermission = MutableStateFlow(false)
    val overlayPermission: StateFlow<Boolean> = _overlayPermission.asStateFlow()
    
    private val _accessibilityPermission = MutableStateFlow(false)
    val accessibilityPermission: StateFlow<Boolean> = _accessibilityPermission.asStateFlow()
    
    private val _assistantPermission = MutableStateFlow(false)
    val assistantPermission: StateFlow<Boolean> = _assistantPermission.asStateFlow()
    
    fun updatePermissions(hasAudio: Boolean, hasOverlay: Boolean) {
        viewModelScope.launch {
            _audioPermission.emit(hasAudio)
            _overlayPermission.emit(hasOverlay)
        }
    }
    
    fun updateAccessibilityPermission(hasPermission: Boolean) {
        viewModelScope.launch {
            _accessibilityPermission.emit(hasPermission)
        }
    }
    
    fun updateAssistantPermission(hasPermission: Boolean) {
        viewModelScope.launch {
            _assistantPermission.emit(hasPermission)
        }
    }
    
    val allPermissionsGranted: StateFlow<Boolean> = StateFlow(false)
    
    init {
        // Initial check would happen when activity resumes
    }
}
