package com.ottoscents.smartshelf.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // --- Stream Handlers (Queries) ---

    fun getInventoryStream(): Flow<List<InventoryItem>> = 
        collectionStream("inventory", InventoryItem::class.java)

    fun getAlertsStream(): Flow<List<AlertItem>> = 
        collectionStream("alerts", AlertItem::class.java, "time", Query.Direction.DESCENDING)

    fun getMovementLogsStream(): Flow<List<MovementLog>> = 
        collectionStream("movement_logs", MovementLog::class.java, "timestamp", Query.Direction.DESCENDING)

    fun getRestockRequestsStream(): Flow<List<RestockItem>> = 
        collectionStream<RestockItem>("restock_requests", RestockItem::class.java)

    fun getSystemLogsStream(): Flow<List<SystemActivity>> = 
        collectionStream("system_logs", SystemActivity::class.java, "createdAt", Query.Direction.DESCENDING)

    fun getFanLogsStream(): Flow<List<FanActivity>> = 
        collectionStream("fan_logs", FanActivity::class.java, "startTime", Query.Direction.DESCENDING)

    // --- Data Modifiers (Actions) ---

    suspend fun saveInventoryItem(item: InventoryItem) = 
        saveDocument("inventory", item.id, item)

    suspend fun deleteInventoryItem(itemId: String) = 
        db.collection("inventory").document(itemId).delete().await()

    suspend fun saveRestockRequest(item: RestockItem) = 
        saveDocument("restock_requests", item.id, item)

    suspend fun saveFanActivity(activity: FanActivity) = 
        saveDocument("fan_logs", activity.id, activity)

    suspend fun saveSystemActivity(activity: SystemActivity) = 
        saveDocument("system_logs", activity.id, activity)

    suspend fun saveAlert(alert: AlertItem) = 
        saveDocument("alerts", alert.id, alert)

    // --- Stored Procedure Logic (Complex Operations) ---

    /**
     * Equivalent to a Stored Procedure: Processes a batch inventory update.
     * Updates multiple items and logs the system activity in a single logical block.
     */
    suspend fun processBulkInventoryUpdate(
        updates: List<InventoryItem>, 
        activity: SystemActivity
    ) {
        db.runBatch { batch ->
            // Update all inventory items
            updates.forEach { item ->
                val ref = db.collection("inventory").document(item.id)
                batch.set(ref, item)
            }
            // Log the activity
            val logRef = db.collection("system_logs").document()
            batch.set(logRef, activity)
        }.await()
    }

    suspend fun getUserDetails(uid: String): UserRole? {
        return try {
            val doc = db.collection("users").document(uid).get().await()
            if (doc.exists()) {
                val roleStr = doc.getString("role") ?: "staff"
                val branchStr = doc.getString("branch") ?: ""
                UserRole(id = doc.id, role = roleStr, branch = branchStr)
            } else null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // --- Private Generic Helpers to Maximize Code Reuse ---

    private fun <T> collectionStream(
        path: String, 
        clazz: Class<T>, 
        orderField: String? = null, 
        direction: Query.Direction = Query.Direction.ASCENDING
    ): Flow<List<T>> = callbackFlow {
        var query: Query = db.collection(path)
        if (orderField != null) {
            query = query.orderBy(orderField, direction)
        }
        
        val subscription = query.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val items = snapshot.documents.mapNotNull { it.toObject(clazz) }
                trySend(items)
            }
        }
        awaitClose { subscription.remove() }
    }

    private suspend fun saveDocument(collection: String, id: String, data: Any) {
        if (id.isEmpty()) {
            db.collection(collection).add(data).await()
        } else {
            db.collection(collection).document(id).set(data).await()
        }
    }
}
