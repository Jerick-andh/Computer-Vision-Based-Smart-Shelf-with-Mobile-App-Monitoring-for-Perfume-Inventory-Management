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

    private val _userRole = MutableStateFlow<String?>("staff")
    val userRole: StateFlow<String?> = _userRole.asStateFlow()

    private val _userBranch = MutableStateFlow<String?>("San Pablo")
    val userBranch: StateFlow<String?> = _userBranch.asStateFlow()

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError.asStateFlow()

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

    fun seedDatabase() {
        viewModelScope.launch {
            val perfumes = listOf(
                "A" to "BVLGARI EXTREME",
                "B" to "BVLGARI AQUA",
                "C" to "CLINIQUE HAPPY MEN",
                "D" to "CHRISTIAN DIOR SAUVAGE",
                "E" to "RALPH LAUREN POLO BLACK",
                "F" to "DAVIDOFF COOL WATER",
                "G" to "RALPH LAUREN POLO RED",
                "H" to "LACOSTE BLACK",
                "I" to "HUGO BOSS",
                "J" to "LACOSTE ESSENTIAL",
                "K" to "LACOSTE RED",
                "L" to "LACOSTE WHITE",
                "M" to "PACO RABANNE INVICTUS",
                "N" to "RALPH LAUREN POLO SPORT",
                "O" to "TOMMY BOY",
                "P" to "CHANEL CHANCE",
                "Q" to "CHANEL NO.5",
                "R" to "CLINIQUE HAPPY WOMEN",
                "S" to "ESCADA MOON SPARKLE",
                "T" to "DKNY BE DELICIOUS",
                "U" to "D&G LIGHT BLUE WOMEN",
                "V" to "ELIZABETH ARDEN GREEN TEA",
                "W" to "CHANEL COCO",
                "X" to "ESTEE LAUDER PLEASURES",
                "Y" to "FERRAGAMO INCANTO",
                "Z" to "JO MALONE MIMOSA & CARDAMOM",
                "AA" to "RALPH LAUREN ROMANCE",
                "AB" to "LANCOME LA VIE EST BELLE",
                "AC" to "LANVIN ECLAT D'ARPEGE",
                "AD" to "TOMMY GIRL"
            )

            perfumes.forEach { (code, name) ->
                val isMen = code.length == 1 && code[0] <= 'O'
                val category = if (isMen) "Men's Fragrance" else "Women's Fragrance"
                val item = InventoryItem(
                    id = code,
                    perfumeCode = code,
                    name = name,
                    category = category,
                    shelf = "Unassigned",
                    recorded = 0,
                    detected = 0,
                    status = "normal",
                    lastUpdated = "Just now"
                )
                firestoreRepo.saveInventoryItem(item)
            }
        }
    }
}
