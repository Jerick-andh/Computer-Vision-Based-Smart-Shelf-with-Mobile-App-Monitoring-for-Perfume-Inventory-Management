package com.ottoscents.smartshelf.data

import com.google.firebase.firestore.DocumentId
import java.util.Date

data class InventoryItem(
    @DocumentId val id: String = "",
    val perfumeCode: String = "",
    val name: String = "",
    val category: String = "",
    val branch: String = "",
    val shelf: String = "",
    val recorded: Int = 0,
    val detected: Int = 0,
    val status: String = "",
    val lastUpdated: String = ""
)

data class AlertItem(
    @DocumentId val id: String = "",
    val title: String = "",
    val desc: String = "",
    val branch: String = "",
    val time: String = "",
    val type: String = ""
)

data class HistoryItem(
    @DocumentId val id: String = "",
    val date: String = "",
    val time: String = "",
    val branch: String = "",
    val status: String = ""
)

data class MovementLog(
    @DocumentId val id: String = "",
    val productName: String = "",
    val shelfArea: String = "",
    val branch: String = "",
    val timestamp: String = "",
    val status: String = "",
    val quantity: Int = 0
)

data class RestockItem(
    @DocumentId val id: String = "",
    val productName: String = "",
    val productId: String = "",
    val quantity: Int = 0,
    val fromBranch: String = "",
    val toBranch: String = "",
    val requestedBy: String = "",
    val requestedDate: String = "",
    val status: String = "",
    val estimatedArrival: String? = null,
    val completedDate: String? = null
)

data class FanActivity(
    @DocumentId val id: String = "",
    val triggerTemperature: Double = 0.0,
    val startTime: String = "",
    val stopTime: String? = null,
    val duration: String = "",
    val status: String = "",
    val branch: String = "",
    val shelfArea: String = ""
)

data class SystemActivity(
    @DocumentId val id: String = "",
    val type: String = "",
    val description: String = "",
    val user: String? = null,
    val branch: String = "",
    val timestamp: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class HelpTopic(
    @DocumentId val id: String = "",
    val title: String = "",
    val description: String = "",
    val paragraphs: List<String> = emptyList()
)

data class UserRole(
    @DocumentId val id: String = "",
    val email: String = "",
    val role: String = "staff", // "admin" or "staff"
    val branch: String = ""
)

data class SystemSettings(
    val captureSchedule: String = "Manual Only",
    val lowStockThreshold: Int = 5,
    val temperatureThreshold: Double = 25.0,
    val currentTemperature: Double = 22.4,
    val isFanActive: Boolean = false,
    val manualTriggerPending: Boolean = false,
    val triggerBranch: String = "Both",
    val lastHeartbeat: Long = 0,
    val calibrationFrame: String? = null
)
