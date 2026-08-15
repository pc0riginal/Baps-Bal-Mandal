package com.example.data.repository

import android.util.Log
import com.example.data.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
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

    init {
        try {
            auth?.addAuthStateListener { firebaseAuth ->
                val user = firebaseAuth.currentUser
                if (user != null) {
                    val fallbackProfile = UserProfile(
                        uid = user.uid,
                        name = user.displayName ?: user.email?.substringBefore("@")?.replace(".", " ")?.capitalizeWords() ?: "Karyakar",
                        email = user.email ?: "",
                        role = "karyakar",
                        mandalId = "mandal-001"
                    )
                    val task = firestore?.collection("users")?.document(user.uid)?.get()
                    if (task != null) {
                        task.addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                _currentUser.value = doc.toObject(UserProfile::class.java)
                            } else {
                                firestore?.collection("users")?.document(user.uid)?.set(fallbackProfile)
                                _currentUser.value = fallbackProfile
                            }
                            startListeners()
                        }.addOnFailureListener {
                            _currentUser.value = fallbackProfile
                            startListeners()
                        }
                    } else {
                        _currentUser.value = fallbackProfile
                        startListeners()
                    }
                } else {
                    _currentUser.value = null
                    stopListeners()
                }
            }
        } catch (e: Exception) {
            Log.e("Repo", "Firebase not initialized", e)
        }
    }

    private fun startListeners() {
        val mandalId = "mandal-001" // Simplified for now

        firestore?.collection("balaks")?.whereEqualTo("mandalId", mandalId)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) { Log.e("Repo", "Balaks listen failed.", e); return@addSnapshotListener }
                _balaks.value = snapshot?.toObjects(Balak::class.java) ?: emptyList()
            }

        firestore?.collection("attendance")?.whereEqualTo("mandalId", mandalId)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) { Log.e("Repo", "Attendance listen failed.", e); return@addSnapshotListener }
                _attendanceRecords.value = snapshot?.toObjects(AttendanceRecord::class.java) ?: emptyList()
            }

        firestore?.collection("sabhas")?.whereEqualTo("mandalId", mandalId)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) { Log.e("Repo", "Sabhas listen failed.", e); return@addSnapshotListener }
                _sabhas.value = snapshot?.toObjects(SabhaSession::class.java)?.sortedByDescending { it.date } ?: emptyList()
            }

        firestore?.collection("activities")?.whereEqualTo("mandalId", mandalId)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) { Log.e("Repo", "Activities listen failed.", e); return@addSnapshotListener }
                _activities.value = snapshot?.toObjects(MandalActivity::class.java)?.sortedByDescending { it.date } ?: emptyList()
            }

        firestore?.collection("karyakars")?.whereEqualTo("mandalId", mandalId)
            ?.addSnapshotListener { snapshot, e ->
                if (e != null) { Log.e("Repo", "Karyakars listen failed.", e); return@addSnapshotListener }
                _karyakars.value = snapshot?.toObjects(Karyakar::class.java) ?: emptyList()
            }
    }

    private fun stopListeners() {
        _balaks.value = emptyList()
        _attendanceRecords.value = emptyList()
        _sabhas.value = emptyList()
        _activities.value = emptyList()
        _karyakars.value = emptyList()
    }

    fun signInWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        signInWithCredential(credential, onComplete)
    }

    fun signInWithCredential(credential: com.google.firebase.auth.AuthCredential, onComplete: (Boolean, String?) -> Unit) {
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message ?: "Login failed")
                }
            } ?: onComplete(false, "Firebase not initialized. Add google-services.json via Secrets.")
    }

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        auth?.signInWithEmailAndPassword(email, password)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message ?: "Login failed")
                }
            } ?: onComplete(false, "Firebase not initialized. Add google-services.json via Secrets.")
    }

    fun logout() {
        auth?.signOut()
    }

    // --- Balak Management ---
    fun addBalak(balak: Balak): String {
        val newId = if (balak.id.isNotBlank()) balak.id else "bal-${UUID.randomUUID().toString().take(8)}"
        val newBalak = balak.copy(
            id = newId,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        )
        firestore?.collection("balaks")?.document(newId)?.set(newBalak)?.addOnFailureListener { Log.e("Repo", "Failed to add Balak", it) }
        _balaks.value = _balaks.value + newBalak
        return newId
    }

    fun updateBalak(updatedBalak: Balak) {
        val withTimestamp = updatedBalak.copy(updatedAt = System.currentTimeMillis())
        firestore?.collection("balaks")?.document(withTimestamp.id)?.set(withTimestamp)?.addOnFailureListener { Log.e("Repo", "Failed to update Balak", it) }
        _balaks.value = _balaks.value.map { if (it.id == withTimestamp.id) withTimestamp else it }
    }

    fun toggleBalakActive(balakId: String) {
        val balak = getBalakById(balakId) ?: return
        firestore?.collection("balaks")?.document(balakId)?.update("active", !balak.active)
    }

    fun deleteBalak(balakId: String) {
        firestore?.collection("balaks")?.document(balakId)?.delete()
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
        val batch = firestore?.batch() ?: return
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        var presentCount = 0
        var absentCount = 0

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
            val docRef = firestore?.collection("attendance")?.document(id)
            if (docRef != null) batch.set(docRef, record)
            
            if (status == AttendanceStatus.PRESENT || status == AttendanceStatus.LATE) presentCount++
            if (status == AttendanceStatus.ABSENT) absentCount++
        }

        // Update or create sabha session
        val sabhaRef = firestore?.collection("sabhas")?.document(sabhaId)
        val sabha = _sabhas.value.find { it.id == sabhaId } ?: SabhaSession(id = sabhaId, date = date, mandalId = mandalId)
        val updatedSabha = sabha.copy(
            totalBalaks = _balaks.value.count { it.active },
            presentCount = presentCount,
            absentCount = absentCount,
            isCompleted = true
        )
        if (sabhaRef != null) batch.set(sabhaRef, updatedSabha)

        batch.commit()
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
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        val newAct = activity.copy(id = newId, mandalId = mandalId)
        firestore?.collection("activities")?.document(newId)?.set(newAct)?.addOnFailureListener { Log.e("Repo", "Failed to add activity", it) }
        _activities.value = _activities.value + newAct
    }

    // --- Karyakars ---
    fun addKaryakar(karyakar: Karyakar) {
        val newId = if (karyakar.id.isNotBlank()) karyakar.id else "k-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        firestore?.collection("karyakars")?.document(newId)?.set(karyakar.copy(id = newId, mandalId = mandalId))
    }

    fun toggleKaryakarActive(karyakarId: String) {
        val k = _karyakars.value.find { it.id == karyakarId } ?: return
        firestore?.collection("karyakars")?.document(karyakarId)?.update("active", !k.active)
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
