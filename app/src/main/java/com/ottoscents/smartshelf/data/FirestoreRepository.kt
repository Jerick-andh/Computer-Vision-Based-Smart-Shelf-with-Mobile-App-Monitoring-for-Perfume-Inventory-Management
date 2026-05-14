package com.ottoscents.smartshelf.data

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.tasks.await

class FirestoreRepository {
    private val db = FirebaseFirestore.getInstance()

    // --- Stream Handlers (Queries) ---

    fun getInventoryStream(): Flow<List<InventoryItem>> = combine(
        collectionStream("inventory_lipa", InventoryItem::class.java),
        collectionStream("inventory_san_pablo", InventoryItem::class.java)
    ) { lipa, sanPablo -> lipa + sanPablo }

    fun getAlertsStream(): Flow<List<AlertItem>> = combine(
        collectionStream("alerts_lipa", AlertItem::class.java, "time", Query.Direction.DESCENDING),
        collectionStream("alerts_san_pablo", AlertItem::class.java, "time", Query.Direction.DESCENDING)
    ) { lipa, sanPablo -> (lipa + sanPablo).sortedByDescending { it.time } }

    fun getMovementLogsStream(): Flow<List<MovementLog>> = combine(
        collectionStream("movement_logs_lipa", MovementLog::class.java, "timestamp", Query.Direction.DESCENDING),
        collectionStream("movement_logs_san_pablo", MovementLog::class.java, "timestamp", Query.Direction.DESCENDING)
    ) { lipa, sanPablo -> (lipa + sanPablo).sortedByDescending { it.timestamp } }

    fun getRestockRequestsStream(): Flow<List<RestockItem>> = 
        collectionStream<RestockItem>("restock_requests", RestockItem::class.java)

    fun getSystemLogsStream(): Flow<List<SystemActivity>> = 
        collectionStream("system_logs", SystemActivity::class.java, "createdAt", Query.Direction.DESCENDING)

    fun getFanLogsStream(branch: String): Flow<List<FanActivity>> {
        val collection = if (branch == "Lipa") "fan_logs_lipa" else "fan_logs_san_pablo"
        return collectionStream(collection, FanActivity::class.java, "startTime", Query.Direction.DESCENDING)
    }

    // --- Data Modifiers (Actions) ---

    suspend fun saveInventoryItem(item: InventoryItem) {
        val collection = if (item.branch == "Lipa") "inventory_lipa" else "inventory_san_pablo"
        val docId = item.perfumeCode
        db.collection(collection).document(docId).set(item).await()
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        val collection = if (item.branch == "Lipa") "inventory_lipa" else "inventory_san_pablo"
        db.collection(collection).document(item.perfumeCode).delete().await()
    }

    suspend fun saveRestockRequest(item: RestockItem) {
        // Restock requests are cross-branch, using unique ID based on product and date
        val docId = "${item.toBranch}_${item.productName}_${item.requestedDate}".replace(" ", "_").replace("#", "")
        db.collection("restock_requests").document(docId).set(item).await()
    }

    suspend fun saveFanActivity(activity: FanActivity) {
        val collection = if (activity.branch == "Lipa") "fan_logs_lipa" else "fan_logs_san_pablo"
        val docId = activity.startTime.replace(" ", "_").replace("•", "_").replace(",", "").replace(":", "")
        db.collection(collection).document(docId).set(activity).await()
    }

    suspend fun saveSystemActivity(activity: SystemActivity) {
        // System logs are global but with readable IDs
        val docId = "${activity.type}_${activity.createdAt}"
        db.collection("system_logs").document(docId).set(activity).await()
    }

    suspend fun saveAlert(alert: AlertItem) {
        val collection = if (alert.branch == "Lipa") "alerts_lipa" else "alerts_san_pablo"
        val docId = alert.time.replace(" ", "_").replace("•", "_").replace(",", "").replace(":", "")
        db.collection(collection).document(docId).set(alert).await()
    }

    suspend fun saveMovementLog(log: MovementLog) {
        val collection = if (log.branch == "Lipa") "movement_logs_lipa" else "movement_logs_san_pablo"
        val docId = log.timestamp.replace(" ", "_").replace("•", "_").replace(",", "").replace(":", "")
        db.collection(collection).document(docId).set(log).await()
    }

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
                val collection = if (item.branch == "Lipa") "inventory_lipa" else "inventory_san_pablo"
                val ref = db.collection(collection).document(item.perfumeCode)
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
                doc.toObject(UserRole::class.java)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveUserDetails(user: UserRole) {
        // Use the Firestore Auth UID as the document ID for users
        if (user.id.isNotEmpty()) {
            db.collection("users").document(user.id).set(user).await()
        }
    }

    fun getSystemSettingsStream(): Flow<SystemSettings?> = callbackFlow {
        val subscription = db.collection("settings").document("global_config")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(SystemSettings::class.java))
                } else {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    suspend fun getSystemSettings(): SystemSettings? {
        return try {
            val doc = db.collection("settings").document("global_config").get().await()
            if (doc.exists()) {
                doc.toObject(SystemSettings::class.java)
            } else null
        } catch (_: Exception) {
            null
        }
    }

    suspend fun saveSystemSettings(settings: SystemSettings) {
        db.collection("settings").document("global_config").set(settings).await()
    }

    suspend fun updateSystemSettingsField(field: String, value: Any) {
        db.collection("settings").document("global_config").update(field, value).await()
    }

    suspend fun updateSystemSettings(fields: Map<String, Any>) {
        db.collection("settings").document("global_config").update(fields).await()
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
