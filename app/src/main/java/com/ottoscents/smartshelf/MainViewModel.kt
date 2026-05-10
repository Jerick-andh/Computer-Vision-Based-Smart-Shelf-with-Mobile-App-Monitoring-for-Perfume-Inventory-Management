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
                val details = firestoreRepo.getUserDetails(uid)
                if (details != null) {
                    _userRole.value = details.role
                    _userBranch.value = details.branch
                }
            }
        }
    }

    private val _isLoggedIn = MutableStateFlow(authRepo.currentUser != null)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _userBranch = MutableStateFlow<String?>(null)
    val userBranch: StateFlow<String?> = _userBranch.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

    private val _handshakeStatus = MutableStateFlow<String>("IDLE")
    val handshakeStatus: StateFlow<String> = _handshakeStatus.asStateFlow()

    private val _currentTemperature = MutableStateFlow(22.4f)
    val currentTemperature: StateFlow<Float> = _currentTemperature.asStateFlow()

    private val _isFanActive = MutableStateFlow(false)
    val isFanActive: StateFlow<Boolean> = _isFanActive.asStateFlow()

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
                    val details = firestoreRepo.getUserDetails(uid)
                    if (details != null) {
                        _userRole.value = details.role
                        _userBranch.value = details.branch
                    }
                }
                _isLoggedIn.value = true
                logActivity("user_login", "User logged in successfully.")
            } else {
                _loginError.value = "Login failed. Please check credentials."
            }
        }
    }

    fun logout() {
        logActivity("user_logout", "User logged out.")
        authRepo.logout()
        _isLoggedIn.value = false
    }

    fun saveInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            firestoreRepo.saveInventoryItem(item)
            logActivity("product_update", "Inventory item '${item.name}' saved/updated.")
        }
    }

    fun deleteInventoryItem(itemId: String) {
        viewModelScope.launch {
            firestoreRepo.deleteInventoryItem(itemId)
            logActivity("product_update", "Inventory item deleted (ID: $itemId).")
        }
    }

    fun saveRestockRequest(item: RestockItem) {
        viewModelScope.launch {
            firestoreRepo.saveRestockRequest(item)
            logActivity("restock_event", "Restock request for '${item.productName}' created.")
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

    fun simulateTemperatureSpike() {
        viewModelScope.launch {
            val spikeTemp = 26.5f
            _currentTemperature.value = spikeTemp
            _isFanActive.value = true
            val now = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date())

            // 1. Log Fan Activity in DB
            firestoreRepo.saveFanActivity(
                FanActivity(
                    triggerTemperature = spikeTemp.toDouble(),
                    startTime = now,
                    status = "active",
                    branch = _userBranch.value ?: "Unknown",
                    shelfArea = "Area A1"
                )
            )

            // 2. Log System Activity in DB
            logActivity("temperature_alert", "High temperature detected: ${spikeTemp}°C. Fan activated.")

            // 3. Create active alert in DB
            firestoreRepo.saveAlert(
                AlertItem(
                    title = "Critical Temperature",
                    desc = "Shelf temperature reached ${spikeTemp}°C. Fan auto-activated.",
                    branch = _userBranch.value ?: "Unknown",
                    time = now,
                    type = "critical"
                )
            )
        }
    }

    fun resetCoolingSystem() {
        viewModelScope.launch {
            _isFanActive.value = false
            _currentTemperature.value = 22.4f
            logActivity("fan_activation", "Cooling system reset. Fan stopped.")
        }
    }

    fun runInventoryCheck() {
        viewModelScope.launch {
            val now = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            val updates = inventoryList.value.map { item ->
                // Simulate detection: most of the time it's accurate, sometimes 1 missing
                val simulatedDetected = if (Math.random() > 0.9) (item.recorded - 1).coerceAtLeast(0) else item.recorded
                val newStatus = if (simulatedDetected < item.recorded) "needs_review" else "normal"
                
                item.copy(
                    detected = simulatedDetected,
                    status = newStatus,
                    lastUpdated = now
                )
            }
            
            // Execute "Stored Procedure" logic in Repository
            firestoreRepo.processBulkInventoryUpdate(
                updates = updates,
                activity = SystemActivity(
                    type = "shelf_check",
                    description = "Manual shelf inventory check completed via schedule window.",
                    user = authRepo.currentUser?.email,
                    branch = _userBranch.value ?: "Unknown",
                    timestamp = now,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }

    private fun logActivity(type: String, description: String) {
        viewModelScope.launch {
            val now = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            firestoreRepo.saveSystemActivity(
                SystemActivity(
                    type = type,
                    description = description,
                    user = authRepo.currentUser?.email,
                    branch = _userBranch.value ?: "Unknown",
                    timestamp = now,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}
