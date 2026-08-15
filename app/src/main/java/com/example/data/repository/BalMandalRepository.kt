package com.example.data.repository

import android.util.Log
import com.example.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

class BalMandalRepository private constructor() {

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _balaks = MutableStateFlow<List<Balak>>(emptyList())
    val balaks: StateFlow<List<Balak>> = _balaks.asStateFlow()

    private val _attendanceRecords = MutableStateFlow<List<AttendanceRecord>>(emptyList())
    val attendanceRecords: StateFlow<List<AttendanceRecord>> = _attendanceRecords.asStateFlow()

    private val _sabhas = MutableStateFlow<List<SabhaSession>>(emptyList())
    val sabhas: StateFlow<List<SabhaSession>> = _sabhas.asStateFlow()

    private val _activities = MutableStateFlow<List<MandalActivity>>(emptyList())
    val activities: StateFlow<List<MandalActivity>> = _activities.asStateFlow()

    private val _karyakars = MutableStateFlow<List<Karyakar>>(emptyList())
    val karyakars: StateFlow<List<Karyakar>> = _karyakars.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val auth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }
    private val firestore: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }

    private val activeListeners = mutableListOf<ListenerRegistration>()

    init {
        try {
            val existingUser = auth?.currentUser
            if (existingUser != null) {
                val initialProfile = createDefaultUserProfile(existingUser)
                _currentUser.value = initialProfile
                fetchUserProfileAndStartListeners(existingUser)
            }

            auth?.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val initialProfile = _currentUser.value ?: createDefaultUserProfile(user)
                    _currentUser.value = initialProfile
                    fetchUserProfileAndStartListeners(user)
                } else {
                    _currentUser.value = null
                    stopListeners()
                }
            }
        } catch (e: Exception) {
            Log.e("BalMandalRepo", "Firebase initialization error", e)
        }
    }

    private fun createDefaultUserProfile(user: FirebaseUser): UserProfile {
        val name = when {
            !user.displayName.isNullOrBlank() -> user.displayName!!
            !user.email.isNullOrBlank() -> user.email!!.substringBefore("@").replace(".", " ").capitalizeWords()
            !user.phoneNumber.isNullOrBlank() -> "Karyakar (${user.phoneNumber})"
            else -> "Karyakar"
        }
        val generatedMandalId = "mandal-${user.uid.take(8)}"
        return UserProfile(
            uid = user.uid,
            name = name,
            email = user.email ?: "",
            phone = user.phoneNumber ?: "",
            role = "karyakar",
            mandalId = generatedMandalId,
            mandalName = "",
            mandalCity = "",
            active = true,
            isProfileComplete = false
        )
    }

    private fun fetchUserProfileAndStartListeners(user: FirebaseUser) {
        val defaultProfile = _currentUser.value ?: createDefaultUserProfile(user)
        val fs = firestore
        if (fs == null) {
            startListeners()
            return
        }

        fs.collection("users").document(user.uid).get()
            .addOnSuccessListener { doc ->
                if (doc != null && doc.exists()) {
                    val profile = doc.toObject(UserProfile::class.java)
                    if (profile != null) {
                        _currentUser.value = profile
                        if (profile.isProfileComplete) {
                            startListeners()
                        }
                    }
                } else {
                    _currentUser.value = defaultProfile
                }
            }
            .addOnFailureListener { e ->
                Log.w("BalMandalRepo", "Firestore user profile read failed", e)
                _currentUser.value = defaultProfile
            }
    }

    fun updateUserProfile(profile: UserProfile, onComplete: ((Boolean, String?) -> Unit)? = null) {
        val updated = profile.copy(isProfileComplete = true)
        _currentUser.value = updated

        val fs = firestore
        if (fs != null) {
            fs.collection("users").document(updated.uid).set(updated)
                .addOnSuccessListener {
                    Log.d("BalMandalRepo", "User profile updated successfully")
                    startListeners()
                    onComplete?.invoke(true, null)
                }
                .addOnFailureListener { e ->
                    Log.e("BalMandalRepo", "Failed to update user profile in Firestore", e)
                    startListeners()
                    onComplete?.invoke(false, e.localizedMessage)
                }
        } else {
            startListeners()
            onComplete?.invoke(true, null)
        }
    }

    private fun startListeners() {
        stopListeners()
        val fs = firestore ?: return
        val mandalId = _currentUser.value?.mandalId ?: return

        try {
            // 1. Balaks: Listen to collection for user's mandal
            val balakReg = fs.collection("balaks")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Balaks DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val remoteList = snapshot.toObjects(Balak::class.java)
                        val matching = remoteList.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        _balaks.value = if (matching.isNotEmpty()) matching else remoteList
                    }
                }
            activeListeners.add(balakReg)

            // 2. Attendance: Listen to attendance records
            val attReg = fs.collection("attendance")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Attendance DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(AttendanceRecord::class.java)
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        _attendanceRecords.value = if (matching.isNotEmpty()) matching else list
                    }
                }
            activeListeners.add(attReg)

            // 3. Sabhas: Listen to sabha sessions
            val sabhaReg = fs.collection("sabhas")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Sabhas DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(SabhaSession::class.java).sortedByDescending { it.date }
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        _sabhas.value = if (matching.isNotEmpty()) matching else list
                    }
                }
            activeListeners.add(sabhaReg)

            // 4. Activities: Listen to activities
            val actReg = fs.collection("activities")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Activities DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(MandalActivity::class.java).sortedByDescending { it.date }
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        _activities.value = if (matching.isNotEmpty()) matching else list
                    }
                }
            activeListeners.add(actReg)

            // 5. Karyakars: Listen to karyakars
            val karyakarReg = fs.collection("karyakars")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Karyakars DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(Karyakar::class.java)
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        _karyakars.value = if (matching.isNotEmpty()) matching else list
                    }
                }
            activeListeners.add(karyakarReg)
        } catch (e: Exception) {
            Log.e("BalMandalRepo", "Failed to register Firestore snapshot listeners", e)
        }
    }

    private fun stopListeners() {
        for (reg in activeListeners) {
            try { reg.remove() } catch (ignored: Exception) {}
        }
        activeListeners.clear()
        _balaks.value = emptyList()
        _attendanceRecords.value = emptyList()
        _sabhas.value = emptyList()
        _activities.value = emptyList()
        _karyakars.value = emptyList()
    }

    // --- Authentication ---

    fun signInWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        signInWithCredential(credential, onComplete)
    }

    fun signInWithCredential(credential: com.google.firebase.auth.AuthCredential, onComplete: (Boolean, String?) -> Unit) {
        val authInstance = auth
        if (authInstance == null) {
            onComplete(false, "Firebase Auth is not available. Please check google-services.json.")
            return
        }

        authInstance.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = authInstance.currentUser
                    if (user != null) {
                        val profile = createDefaultUserProfile(user)
                        _currentUser.value = profile
                        fetchUserProfileAndStartListeners(user)
                    }
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.localizedMessage ?: "Login failed")
                }
            }
    }

    fun logout() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("BalMandalRepo", "Error during sign out", e)
        }
        _currentUser.value = null
        stopListeners()
    }

    // --- Balak Management (Write to DB & Update Local State) ---

    fun addBalak(balak: Balak): String {
        val newId = if (balak.id.isNotBlank()) balak.id else "bal-${UUID.randomUUID().toString().take(8)}"
        val user = _currentUser.value
        val mandalId = if (balak.mandalId.isNotBlank()) balak.mandalId else (user?.mandalId ?: "")
        val mandalName = if (balak.mandalName.isNotBlank()) balak.mandalName else (user?.mandalName ?: "")
        val karyakar = if (balak.assignedKaryakar.isNotBlank()) balak.assignedKaryakar else (user?.name ?: "")

        val newBalak = balak.copy(
            id = newId,
            createdAt = if (balak.createdAt > 0) balak.createdAt else System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            mandalId = mandalId,
            mandalName = mandalName,
            assignedKaryakar = karyakar
        )

        // 1. Immediate local update
        val current = _balaks.value.filter { it.id != newId }
        _balaks.value = current + newBalak

        // 2. Persist to Firestore DB
        firestore?.collection("balaks")?.document(newId)?.set(newBalak)
            ?.addOnSuccessListener { Log.d("BalMandalRepo", "Balak $newId added to Firestore") }
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to add balak $newId", e) }

        return newId
    }

    fun updateBalak(updatedBalak: Balak) {
        val withTimestamp = updatedBalak.copy(updatedAt = System.currentTimeMillis())

        // 1. Immediate local update
        val current = _balaks.value.toMutableList()
        val idx = current.indexOfFirst { it.id == withTimestamp.id }
        if (idx >= 0) {
            current[idx] = withTimestamp
        } else {
            current.add(withTimestamp)
        }
        _balaks.value = current

        // 2. Persist update to Firestore DB
        firestore?.collection("balaks")?.document(withTimestamp.id)?.set(withTimestamp)
            ?.addOnSuccessListener { Log.d("BalMandalRepo", "Balak ${withTimestamp.id} updated") }
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to update balak ${withTimestamp.id}", e) }
    }

    fun toggleBalakActive(balakId: String) {
        val balak = getBalakById(balakId) ?: return
        val updated = balak.copy(active = !balak.active, updatedAt = System.currentTimeMillis())
        updateBalak(updated)
    }

    fun deleteBalak(balakId: String) {
        // 1. Immediate local removal
        _balaks.value = _balaks.value.filter { it.id != balakId }

        // 2. Delete from Firestore DB
        firestore?.collection("balaks")?.document(balakId)?.delete()
            ?.addOnSuccessListener { Log.d("BalMandalRepo", "Balak $balakId deleted") }
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to delete balak $balakId", e) }
    }

    fun getBalakById(balakId: String): Balak? {
        return _balaks.value.firstOrNull { it.id == balakId }
    }

    // --- Attendance Management ---

    fun getAttendanceForSabha(date: String): List<AttendanceRecord> {
        return _attendanceRecords.value.filter { it.date == date }
    }

    fun saveSabhaAttendance(
        date: String,
        sabhaId: String,
        statusMap: Map<String, AttendanceStatus>,
        notesMap: Map<String, String> = emptyMap(),
        markedByName: String = _currentUser.value?.name ?: "Karyakar"
    ) {
        val mandalId = _currentUser.value?.mandalId ?: ""
        var presentCount = 0
        var absentCount = 0
        val newRecords = mutableListOf<AttendanceRecord>()

        for ((balakId, status) in statusMap) {
            val balak = getBalakById(balakId)
            val note = notesMap[balakId] ?: ""
            val id = "att-${date}-${balakId}"
            val record = AttendanceRecord(
                id = id,
                balakId = balakId,
                balakName = balak?.fullName ?: "Balak",
                sabhaId = sabhaId,
                date = date,
                status = status,
                markedBy = markedByName,
                note = note,
                timestamp = System.currentTimeMillis(),
                mandalId = mandalId
            )
            newRecords.add(record)

            if (status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE) presentCount++
            if (status == AttendanceStatus.ABSENT) absentCount++
        }

        // 1. Local update
        val existingFiltered = _attendanceRecords.value.filter { it.date != date }
        _attendanceRecords.value = existingFiltered + newRecords

        val existingSabha = _sabhas.value.find { it.id == sabhaId || it.date == date }
            ?: SabhaSession(id = sabhaId, date = date, mandalId = mandalId)
        val updatedSabha = existingSabha.copy(
            totalBalaks = _balaks.value.count { it.active },
            presentCount = presentCount,
            absentCount = absentCount,
            isCompleted = true
        )
        val sabhasList = _sabhas.value.filter { it.id != updatedSabha.id }.toMutableList()
        sabhasList.add(0, updatedSabha)
        _sabhas.value = sabhasList

        // 2. Persist batch to Firestore DB
        val fs = firestore ?: return
        try {
            val batch = fs.batch()
            for (rec in newRecords) {
                val docRef = fs.collection("attendance").document(rec.id)
                batch.set(docRef, rec)
            }
            val sabhaRef = fs.collection("sabhas").document(updatedSabha.id)
            batch.set(sabhaRef, updatedSabha)
            batch.commit()
                .addOnSuccessListener { Log.d("BalMandalRepo", "Attendance batch committed to DB") }
                .addOnFailureListener { e -> Log.e("BalMandalRepo", "Attendance batch commit failed", e) }
        } catch (e: Exception) {
            Log.e("BalMandalRepo", "Failed to write attendance batch to DB", e)
        }
    }

    fun getBalakAttendanceSummary(balakId: String): BalakAttendanceSummary {
        val balak = getBalakById(balakId) ?: return BalakAttendanceSummary(Balak(id = balakId))
        val records = _attendanceRecords.value.filter { it.balakId == balakId }
        val total = records.size
        val attended = records.count { it.status == AttendanceStatus.PRESENT || it.status == AttendanceStatus.LATE }
        val late = records.count { it.status == AttendanceStatus.LATE }
        val excused = records.count { it.status == AttendanceStatus.EXCUSED }
        val percentage = if (total > 0) (attended.toFloat() / total.toFloat()) * 100f else 0f
        val lastRecord = records.sortedByDescending { it.date }.firstOrNull()

        return BalakAttendanceSummary(
            balak = balak,
            totalSabhas = total,
            attendedSabhas = attended,
            lateCount = late,
            excusedCount = excused,
            percentage = percentage,
            lastAttendedDate = lastRecord?.date ?: "N/A"
        )
    }

    fun getAllBalakAttendanceSummaries(): List<BalakAttendanceSummary> {
        return _balaks.value.map { getBalakAttendanceSummary(it.id) }
    }

    // --- Dashboard Stats ---

    fun getDashboardStats(): DashboardStats {
        val activeBalaks = _balaks.value.filter { it.active }
        val totalCount = activeBalaks.size
        val latestSabha = _sabhas.value.firstOrNull()
        val latestRecords = latestSabha?.let { sabha -> _attendanceRecords.value.filter { it.date == sabha.date } } ?: emptyList()

        val presentCount = if (latestRecords.isNotEmpty()) {
            latestRecords.count { it.status == AttendanceStatus.PRESENT || it.status == AttendanceStatus.LATE }
        } else { 0 }
        val absentCount = if (latestRecords.isNotEmpty()) {
            latestRecords.count { it.status == AttendanceStatus.ABSENT }
        } else { 0 }
        val percentage = if (totalCount > 0 && latestRecords.isNotEmpty()) ((presentCount.toFloat() / totalCount) * 100).toInt() else 0

        val summaries = getAllBalakAttendanceSummaries()
        val lowCount = summaries.count { it.totalSabhas > 0 && it.percentage < 70f }
        val newBalaks = _balaks.value.count { System.currentTimeMillis() - it.createdAt < 30L * 24 * 60 * 60 * 1000 }

        return DashboardStats(
            totalBalaks = totalCount,
            presentToday = presentCount,
            absentToday = absentCount,
            attendancePercentage = percentage,
            newBalaksCount = if (newBalaks > 0) newBalaks else 0,
            lowAttendanceCount = lowCount,
            upcomingSabha = latestSabha
        )
    }

    // --- Activities ---

    fun addActivity(activity: MandalActivity) {
        val newId = if (activity.id.isNotBlank()) activity.id else "act-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: ""
        val item = activity.copy(id = newId, mandalId = mandalId)

        _activities.value = listOf(item) + _activities.value.filter { it.id != newId }

        firestore?.collection("activities")?.document(newId)?.set(item)
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to add activity in DB", e) }
    }

    // --- Karyakars ---

    fun addKaryakar(karyakar: Karyakar) {
        val newId = if (karyakar.id.isNotBlank()) karyakar.id else "k-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: ""
        val mandalName = _currentUser.value?.mandalName ?: ""
        val item = karyakar.copy(id = newId, mandalId = mandalId, mandalName = mandalName)

        _karyakars.value = listOf(item) + _karyakars.value.filter { it.id != newId }

        firestore?.collection("karyakars")?.document(newId)?.set(item)
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to add karyakar in DB", e) }
    }

    fun toggleKaryakarActive(karyakarId: String) {
        val k = _karyakars.value.find { it.id == karyakarId } ?: return
        val updated = k.copy(active = !k.active)
        val current = _karyakars.value.toMutableList()
        val idx = current.indexOfFirst { it.id == karyakarId }
        if (idx >= 0) current[idx] = updated
        _karyakars.value = current

        firestore?.collection("karyakars")?.document(karyakarId)?.update("active", updated.active)
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to update karyakar in DB", e) }
    }

    companion object {
        @Volatile
        private var instance: BalMandalRepository? = null

        fun getInstance(): BalMandalRepository {
            return instance ?: synchronized(this) {
                instance ?: BalMandalRepository().also { instance = it }
            }
        }
    }
}

private fun String.capitalizeWords(): String =
    split(" ").joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }
