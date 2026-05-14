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
import com.ottoscents.smartshelf.data.UserRole
import com.ottoscents.smartshelf.data.FanActivity
import com.ottoscents.smartshelf.data.SystemSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.flatMapLatest
import android.content.Context
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
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
                
                // Load global settings
                val settings = firestoreRepo.getSystemSettings()
                if (settings != null) {
                    _captureSchedule.value = settings.captureSchedule
                    _lowStockThreshold.value = settings.lowStockThreshold
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

    private val _captureSchedule = MutableStateFlow("Manual Only")
    val captureSchedule: StateFlow<String> = _captureSchedule.asStateFlow()

    private val _lowStockThreshold = MutableStateFlow(5)
    val lowStockThreshold: StateFlow<Int> = _lowStockThreshold.asStateFlow()

    private val _selectedInventoryBranch = MutableStateFlow("All Branches")
    val selectedInventoryBranch: StateFlow<String> = _selectedInventoryBranch.asStateFlow()

    init {
        viewModelScope.launch {
            firestoreRepo.getSystemSettingsStream().collect { settings ->
                if (settings != null) {
                    _currentTemperature.value = settings.currentTemperature.toFloat()
                    _isFanActive.value = settings.isFanActive
                    _captureSchedule.value = settings.captureSchedule
                    _lowStockThreshold.value = settings.lowStockThreshold
                }
            }
        }
    }

    fun setInventoryBranch(branch: String) {
        _selectedInventoryBranch.value = branch
    }

    fun setCaptureSchedule(schedule: String) {
        _captureSchedule.value = schedule
        logActivity("schedule_change", "Capture schedule updated to: $schedule")
        
        // Persist to database
        viewModelScope.launch {
            firestoreRepo.updateSystemSettingsField("captureSchedule", schedule)
        }
    }

    fun setLowStockThreshold(threshold: Int) {
        _lowStockThreshold.value = threshold
        logActivity("system_update", "Low stock threshold updated to: $threshold bottles")
        
        // Persist to database
        viewModelScope.launch {
            firestoreRepo.updateSystemSettingsField("lowStockThreshold", threshold)
        }
    }

    val inventoryList: StateFlow<List<InventoryItem>> = firestoreRepo.getInventoryStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val alertsList: StateFlow<List<AlertItem>> = firestoreRepo.getAlertsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val movementLogs: StateFlow<List<MovementLog>> = firestoreRepo.getMovementLogsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val restockRequests: StateFlow<List<RestockItem>> = firestoreRepo.getRestockRequestsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val systemLogs: StateFlow<List<SystemActivity>> = firestoreRepo.getSystemLogsStream()
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyIn(emptyList()))

    private fun <T> emptyIn(value: T): T = value

    val fanLogs: StateFlow<List<FanActivity>> = _userBranch
        .flatMapLatest { branch ->
            firestoreRepo.getFanLogsStream(branch ?: "Lipa")
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _loginError.value = null
            val success = authRepo.login(email, pass)
            if (success) {
                authRepo.currentUser?.let { user ->
                    val details = firestoreRepo.getUserDetails(user.uid)
                    if (details != null) {
                        _userRole.value = details.role
                        _userBranch.value = details.branch
                    } else {
                        // If no details exist, create a deterministic user entry
                        val isFirstAdmin = user.email?.equals("admin@ottoscents.com", ignoreCase = true) == true
                        val newUser = UserRole(
                            id = user.uid,
                            email = user.email ?: "",
                            role = if (isFirstAdmin) "admin" else "staff",
                            // Admin is always San Pablo, Staff always defaults to Lipa
                            branch = if (isFirstAdmin) "San Pablo" else "Lipa"
                        )
                        firestoreRepo.saveUserDetails(newUser)
                        _userRole.value = newUser.role
                        _userBranch.value = newUser.branch
                    }
                }
                _isLoggedIn.value = true
                logActivity("user_login", "User logged in successfully.")
            } else {
                _loginError.value = "Login failed. Please check credentials."
            }
        }
    }

    fun register(email: String, pass: String) {
        viewModelScope.launch {
            _loginError.value = null
            val success = authRepo.register(email, pass)
            if (success) {
                authRepo.currentUser?.let { user ->
                    // Automatically provision deterministic profile on registration
                    val isFirstAdmin = user.email?.equals("admin@ottoscents.com", ignoreCase = true) == true
                    val newUser = UserRole(
                        id = user.uid,
                        email = user.email ?: "",
                        role = if (isFirstAdmin) "admin" else "staff",
                        // Admin is always San Pablo, Staff defaults to Lipa
                        branch = if (isFirstAdmin) "San Pablo" else "Lipa"
                    )
                    firestoreRepo.saveUserDetails(newUser)
                    _userRole.value = newUser.role
                    _userBranch.value = newUser.branch
                }
                _isLoggedIn.value = true
                logActivity("user_register", "New user registered.")
            } else {
                _loginError.value = "Registration failed. Email might be in use or password too weak."
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
            if (item.branch == "Both Branches") {
                // Sync across both branches by using perfumeCode as deterministic ID
                firestoreRepo.saveInventoryItem(item.copy(branch = "Lipa"))
                firestoreRepo.saveInventoryItem(item.copy(branch = "San Pablo"))
                logActivity("product_update", "Product '${item.name}' synced across BOTH branches.")
            } else {
                firestoreRepo.saveInventoryItem(item)
                logActivity("product_update", "Inventory item '${item.name}' saved/updated for ${item.branch}.")
            }
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            // Remove the product from all branches since they share perfumes
            val matches = inventoryList.value.filter { it.name == item.name || (it.perfumeCode == item.perfumeCode && item.perfumeCode != "#") }
            matches.forEach { firestoreRepo.deleteInventoryItem(it) }
            logActivity("product_update", "Product '${item.name}' removed from all branches.")
        }
    }

    fun triggerShelfCameraHandshake() {
        viewModelScope.launch {
            _handshakeStatus.value = "CONNECTING..."
            kotlinx.coroutines.delay(1500)
            val mockExternalSignal = "CAMERA_DATA_v1.0" 
            if (mockExternalSignal.startsWith("CAMERA_DATA")) {
                _handshakeStatus.value = "CONNECTION_VERIFIED_200_OK"
            } else {
                _handshakeStatus.value = "ERROR_UNAUTHORIZED_DEVICE"
            }
        }
    }

    fun simulateTemperatureSpike() {
        viewModelScope.launch {
            val branch = _userBranch.value ?: "Lipa"
            val spikeTemp = 26.5f
            
            // Push to Firestore so the simulator can react
            firestoreRepo.updateSystemSettingsField("currentTemperature", spikeTemp.toDouble())
            
            val now = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date())

            firestoreRepo.saveFanActivity(
                FanActivity(
                    triggerTemperature = spikeTemp.toDouble(),
                    startTime = now,
                    status = "active",
                    branch = branch,
                    shelfArea = "Area A1"
                )
            )

            logActivity("temperature_alert", "High temperature detected: ${spikeTemp}°C. Fan activated.")

            firestoreRepo.saveAlert(
                AlertItem(
                    title = "Critical Temperature",
                    desc = "Shelf temperature reached ${spikeTemp}°C. Fan auto-activated.",
                    branch = branch,
                    time = now,
                    type = "critical"
                )
            )
        }
    }

    fun resetCoolingSystem() {
        viewModelScope.launch {
            firestoreRepo.updateSystemSettings(mapOf(
                "isFanActive" to false,
                "currentTemperature" to 22.4
            ))
            logActivity("fan_activation", "Cooling system reset. Fan stopped.")
        }
    }

    fun triggerManualInventory(target: String) {
        viewModelScope.launch {
            _handshakeStatus.value = "TRIGGERING_REMOTE_CHECK..."
            try {
                // Atomic update: Set both the trigger and the branch in ONE step to avoid race conditions
                val updates = mapOf(
                    "manualTriggerPending" to true,
                    "triggerBranch" to target
                )
                firestoreRepo.updateSystemSettings(updates)
                logActivity("manual_trigger", "Manual inventory run requested for $target.")
                
                // Keep status for a bit then revert to idle if no response (simplified)
                delay(5000)
                if (_handshakeStatus.value == "TRIGGERING_REMOTE_CHECK...") {
                    _handshakeStatus.value = "IDLE"
                }
            } catch (e: Exception) {
                _handshakeStatus.value = "TRIGGER_FAILED"
            }
        }
    }

    fun runInventoryCheck() {
        // Now using the remote trigger for manual inventory runs
        // This is now called after branch selection in the UI
    }

    private fun simulateInventoryCheck() {
        viewModelScope.launch {
            val now = java.text.SimpleDateFormat("MMM d, yyyy • h:mm a", java.util.Locale.getDefault()).format(java.util.Date())
            val dateOnly = java.text.SimpleDateFormat("MMM d, yyyy", java.util.Locale.getDefault()).format(java.util.Date())
            
            val pendingRequests = restockRequests.value.filter { it.status == "pending" || it.status == "in_transit" || it.status == "received" }
            val currentBranch = _userBranch.value ?: "Lipa"

            val updates = inventoryList.value.filter { it.branch == currentBranch }.map { item ->
                // Simulate detection (for prototype purposes)
                val simulatedDetected = if (Math.random() > 0.95) (item.recorded - 1).coerceAtLeast(0) else item.recorded
                
                var finalRecorded = item.recorded
                var finalStatus = "normal"
                
                // 1. AUTOMATIC RESTOCK VERIFICATION:
                val restock = pendingRequests.find { it.productName == item.name && it.toBranch == item.branch }
                if (restock != null) {
                    if (simulatedDetected > item.recorded) {
                        finalRecorded = simulatedDetected
                        firestoreRepo.saveRestockRequest(restock.copy(status = "completed", completedDate = now))
                        logActivity("restock_auto_verify", "Restock for '${item.name}' at ${item.branch} verified.")
                    }
                }

                // 2. LOW STOCK TRIGGER:
                if (simulatedDetected <= _lowStockThreshold.value) {
                    finalStatus = "low"
                    val alreadyRequested = pendingRequests.any { it.productName == item.name && it.toBranch == item.branch }
                    
                    val alertTitle = "Low Stock: ${item.name}"
                    val alreadyAlerted = alertsList.value.any { it.title == alertTitle && it.time.contains(dateOnly) }

                    if (!alreadyAlerted) {
                        firestoreRepo.saveAlert(AlertItem(
                            title = alertTitle,
                            desc = "${item.branch} branch: ${item.name} is running low ($simulatedDetected bottles remaining). Please restock soon.",
                            branch = item.branch,
                            time = now,
                            type = "warning"
                        ))
                    }

                    if (!alreadyRequested && item.branch != "San Pablo") {
                        val quantityToRequest = 10 - simulatedDetected
                        firestoreRepo.saveRestockRequest(RestockItem(
                            productName = item.name,
                            productId = item.id,
                            quantity = quantityToRequest,
                            fromBranch = "San Pablo",
                            toBranch = item.branch,
                            requestedBy = "System Auto-Trigger",
                            requestedDate = dateOnly,
                            status = "pending"
                        ))
                    }
                } else if (simulatedDetected < finalRecorded) {
                    finalStatus = "needs_review"
                }

                item.copy(
                    recorded = finalRecorded,
                    detected = simulatedDetected,
                    status = finalStatus,
                    lastUpdated = now
                )
            }
            
            firestoreRepo.processBulkInventoryUpdate(
                updates = updates,
                activity = SystemActivity(
                    type = "shelf_check",
                    description = "Shelf inventory check completed. Automated verification and restock logic processed.",
                    user = authRepo.currentUser?.email ?: "System",
                    branch = currentBranch,
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
                    user = authRepo.currentUser?.email ?: "System",
                    branch = _userBranch.value ?: "Unknown",
                    timestamp = now,
                    createdAt = System.currentTimeMillis()
                )
            )
        }
    }
}
