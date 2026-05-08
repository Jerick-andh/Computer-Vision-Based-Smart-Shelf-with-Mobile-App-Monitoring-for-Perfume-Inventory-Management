package com.ottoscents.smartshelf.data

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    fun getInventoryStream(): Flow<List<InventoryItem>> = callbackFlow {
        val subscription = db.collection("inventory")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(InventoryItem::class.java) }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun saveInventoryItem(item: InventoryItem) {
        if (item.id.isEmpty()) {
            db.collection("inventory").add(item).await()
        } else {
            db.collection("inventory").document(item.id).set(item).await()
        }
    }

    suspend fun deleteInventoryItem(itemId: String) {
        db.collection("inventory").document(itemId).delete().await()
    }

    fun getAlertsStream(): Flow<List<AlertItem>> = callbackFlow {
        val subscription = db.collection("alerts")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(AlertItem::class.java) }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getMovementLogsStream(): Flow<List<MovementLog>> = callbackFlow {
        val subscription = db.collection("movement_logs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(MovementLog::class.java) }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getRestockRequestsStream(): Flow<List<RestockItem>> = callbackFlow {
        val subscription = db.collection("restock_requests")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(RestockItem::class.java) }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun saveRestockRequest(item: RestockItem) {
        if (item.id.isEmpty()) {
            db.collection("restock_requests").add(item).await()
        } else {
            db.collection("restock_requests").document(item.id).set(item).await()
        }
    }

    suspend fun getUserDetails(uid: String): UserRole? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                val roleStr = doc.getString("role") ?: "staff"
                val branchStr = doc.getString("branch") ?: ""
                UserRole(id = doc.id, role = roleStr, branch = branchStr)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getSystemLogsStream(): Flow<List<SystemActivity>> = callbackFlow {
        val subscription = db.collection("system_logs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(SystemActivity::class.java) }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }

    fun getFanLogsStream(): Flow<List<FanActivity>> = callbackFlow {
        val subscription = db.collection("fan_logs")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val items = snapshot.documents.mapNotNull { it.toObject(FanActivity::class.java) }
                    trySend(items)
                }
            }
        awaitClose { subscription.remove() }
    }
}
