package com.ottoscents.smartshelf

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ottoscents.smartshelf.data.AlertItem
import com.ottoscents.smartshelf.data.AuthRepository
import com.ottoscents.smartshelf.data.FirestoreRepository
import com.ottoscents.smartshelf.data.InventoryItem
import com.ottoscents.smartshelf.data.MovementLog
import com.ottoscents.smartshelf.data.RestockItem
import com.ottoscents.smartshelf.data.SystemActivity
import com.ottoscents.smartshelf.data.FanActivity
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    private val firestoreRepo = FirestoreRepository()
    private val authRepo = AuthRepository()

    init {
        authRepo.currentUser?.uid?.let { uid ->
            viewModelScope.launch {
                val details = firestoreRepo.getUserDetails(uid) { error ->
                    _debugMessage.value = error
                }
                if (details != null) {
                    _userRole.value = details.role
                    _userBranch.value = details.branch
                } else {
                    if (_debugMessage.value.isEmpty()) {
                        _debugMessage.value = "Init: Details null for $uid"
                    }
                }
            }
        }
    }

    private val _isLoggedIn = MutableStateFlow(authRepo.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow<String?>("staff")
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _userBranch = MutableStateFlow<String?>("San Pablo")
    val userBranch: StateFlow<String?> = _userBranch.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _debugMessage = MutableStateFlow<String>("")
    val debugMessage: StateFlow<String> = _debugMessage.asStateFlow()

    private val _handshakeStatus = MutableStateFlow<String>("IDLE")
    val handshakeStatus: StateFlow<String> = _handshakeStatus.asStateFlow()

    val inventoryList: StateFlow<List<InventoryItem>> = firestoreRepo.getInventoryStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val alertsList: StateFlow<List<AlertItem>> = firestoreRepo.getAlertsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val movementLogs: StateFlow<List<MovementLog>> = firestoreRepo.getMovementLogsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val restockRequests: StateFlow<List<RestockItem>> = firestoreRepo.getRestockRequestsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val systemLogs: StateFlow<List<SystemActivity>> = firestoreRepo.getSystemLogsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val fanLogs: StateFlow<List<FanActivity>> = firestoreRepo.getFanLogsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginError.value = null
            val success = authRepo.login(email, pass)
            if (success) {
                authRepo.currentUser?.uid?.let { uid ->
                    val details = firestoreRepo.getUserDetails(uid) { error ->
                        _debugMessage.value = error
                    }
                    if (details != null) {
                        _userRole.value = details.role
                        _userBranch.value = details.branch
                    } else {
                        if (_debugMessage.value.isEmpty()) {
                            _debugMessage.value = "Login: Details null for $uid"
                        }
                    }
                }
                _isLoggedIn.value = true
            } else {
                _loginError.value = "Login failed. Please check credentials."
            }
        }
    }

    fun logout() {
        authRepo.logout()
        _isLoggedIn.value = false
    }

    fun saveInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            firestoreRepo.saveInventoryItem(item)
        }
    }

    fun deleteInventoryItem(itemId: String) {
        viewModelScope.launch {
            firestoreRepo.deleteInventoryItem(itemId)
        }
    }

    fun saveRestockRequest(item: RestockItem) {
        viewModelScope.launch {
            firestoreRepo.saveRestockRequest(item)
        }
    }

    /**
     * MOCKUP HANDSHAKE: Demonstrates Architectural Readiness for Camera Hardware Integration
     * Following 4-step Universal Implementation:
     */
    fun triggerShelfCameraHandshake() {
        viewModelScope.launch {
            // 1. Isolate Logic Path: Start connection attempt
            _handshakeStatus.value = "CONNECTING..."
            kotlinx.coroutines.delay(1500) // Simulate network/hardware latency

            // 3. Simulate Response: Mock data from an external camera sensor
            val mockExternalSignal = "CAMERA_DATA_v1.0" 

            // 2. Gatekeeper Validation: Verify the "external" signal
            if (mockExternalSignal.startsWith("CAMERA_DATA")) {
                // 4. Feedback Loop: Send success back to UI
                _handshakeStatus.value = "CONNECTION_VERIFIED_200_OK"
            } else {
                _handshakeStatus.value = "ERROR_UNAUTHORIZED_DEVICE"
            }
        }
    }
}
