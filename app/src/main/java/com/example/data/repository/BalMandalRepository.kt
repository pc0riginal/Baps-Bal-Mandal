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
        // Pre-fill starter demo dataset so UI is immediately rich and functional
        initializeDefaultData()

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
            else -> "Mahesh Patel"
        }
        return UserProfile(
            uid = user.uid,
            name = name,
            email = user.email ?: "karyakar@baps.org",
            phone = user.phoneNumber ?: "",
            role = "karyakar",
            mandalId = "mandal-001",
            mandalName = "BAPS Bal Mandal - Ahmedabad West",
            active = true
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
                    }
                } else {
                    fs.collection("users").document(user.uid).set(defaultProfile)
                        .addOnFailureListener { e -> Log.w("BalMandalRepo", "Failed to save initial user profile", e) }
                }
                startListeners()
            }
            .addOnFailureListener { e ->
                Log.w("BalMandalRepo", "Firestore user profile read failed, using default profile", e)
                startListeners()
            }
    }

    private fun startListeners() {
        stopListeners()
        val fs = firestore ?: return
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"

        try {
            // 1. Balaks: Listen to collection and load all balaks from Firestore DB
            val balakReg = fs.collection("balaks")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Balaks DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val remoteList = snapshot.toObjects(Balak::class.java)
                        val matching = remoteList.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        if (matching.isNotEmpty()) {
                            _balaks.value = matching
                        } else if (remoteList.isNotEmpty()) {
                            _balaks.value = remoteList
                        }
                    }
                }
            activeListeners.add(balakReg)

            // 2. Attendance: Listen to attendance records from Firestore DB
            val attReg = fs.collection("attendance")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Attendance DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(AttendanceRecord::class.java)
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        if (matching.isNotEmpty()) {
                            _attendanceRecords.value = matching
                        } else if (list.isNotEmpty()) {
                            _attendanceRecords.value = list
                        }
                    }
                }
            activeListeners.add(attReg)

            // 3. Sabhas: Listen to sabha sessions from Firestore DB
            val sabhaReg = fs.collection("sabhas")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Sabhas DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(SabhaSession::class.java).sortedByDescending { it.date }
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        if (matching.isNotEmpty()) {
                            _sabhas.value = matching
                        } else if (list.isNotEmpty()) {
                            _sabhas.value = list
                        }
                    }
                }
            activeListeners.add(sabhaReg)

            // 4. Activities: Listen to activities from Firestore DB
            val actReg = fs.collection("activities")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Activities DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(MandalActivity::class.java).sortedByDescending { it.date }
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        if (matching.isNotEmpty()) {
                            _activities.value = matching
                        } else if (list.isNotEmpty()) {
                            _activities.value = list
                        }
                    }
                }
            activeListeners.add(actReg)

            // 5. Karyakars: Listen to karyakars from Firestore DB
            val karyakarReg = fs.collection("karyakars")
                .addSnapshotListener { snapshot, e ->
                    if (e != null) {
                        Log.e("BalMandalRepo", "Karyakars DB read error", e)
                        return@addSnapshotListener
                    }
                    if (snapshot != null) {
                        val list = snapshot.toObjects(Karyakar::class.java)
                        val matching = list.filter { it.mandalId.isBlank() || it.mandalId == mandalId }
                        if (matching.isNotEmpty()) {
                            _karyakars.value = matching
                        } else if (list.isNotEmpty()) {
                            _karyakars.value = list
                        }
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

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
        val authInstance = auth
        if (authInstance == null) {
            _currentUser.value = UserProfile(
                uid = "karyakar-demo",
                name = "Mahesh Patel",
                email = email,
                role = "karyakar",
                mandalId = "mandal-001",
                mandalName = "BAPS Bal Mandal - Ahmedabad West"
            )
            onComplete(true, null)
            return
        }

        authInstance.signInWithEmailAndPassword(email, password)
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
                    val exception = task.exception
                    // Auto-register karyakar on first login attempt if user does not exist in Firebase Auth yet
                    if (exception is com.google.firebase.auth.FirebaseAuthInvalidUserException) {
                        authInstance.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { createParam ->
                                if (createParam.isSuccessful) {
                                    val newUser = authInstance.currentUser
                                    if (newUser != null) {
                                        val profile = createDefaultUserProfile(newUser)
                                        _currentUser.value = profile
                                        fetchUserProfileAndStartListeners(newUser)
                                    }
                                    onComplete(true, null)
                                } else {
                                    onComplete(false, createParam.exception?.localizedMessage ?: "User not found")
                                }
                            }
                    } else {
                        onComplete(false, exception?.localizedMessage ?: "Login failed")
                    }
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
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        val newBalak = balak.copy(
            id = newId,
            createdAt = if (balak.createdAt > 0) balak.createdAt else System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            mandalId = mandalId
        )

        // 1. Immediate local StateFlow update so UI updates instantly
        val current = _balaks.value.filter { it.id != newId }
        _balaks.value = current + newBalak

        // 2. Persist directly to Firestore DB
        firestore?.collection("balaks")?.document(newId)?.set(newBalak)
            ?.addOnSuccessListener {
                Log.d("BalMandalRepo", "Balak $newId successfully added to Firestore DB")
            }
            ?.addOnFailureListener { e ->
                Log.e("BalMandalRepo", "Failed to add balak $newId to Firestore DB", e)
            }

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
            ?.addOnSuccessListener {
                Log.d("BalMandalRepo", "Balak ${withTimestamp.id} updated in Firestore DB")
            }
            ?.addOnFailureListener { e ->
                Log.e("BalMandalRepo", "Failed to update balak ${withTimestamp.id} in Firestore DB", e)
            }
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
            ?.addOnSuccessListener {
                Log.d("BalMandalRepo", "Balak $balakId deleted from Firestore DB")
            }
            ?.addOnFailureListener { e ->
                Log.e("BalMandalRepo", "Failed to delete balak $balakId from Firestore DB", e)
            }
    }

    fun getBalakById(balakId: String): Balak? {
        return _balaks.value.firstOrNull { it.id == balakId }
    }

    // --- Attendance Management (Write to DB & Update Local State) ---

    fun getAttendanceForSabha(date: String): List<AttendanceRecord> {
        return _attendanceRecords.value.filter { it.date == date }
    }

    fun saveSabhaAttendance(
        date: String,
        sabhaId: String,
        statusMap: Map<String, AttendanceStatus>,
        notesMap: Map<String, String> = emptyMap(),
        markedByName: String = _currentUser.value?.name ?: "Mahesh Patel"
    ) {
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
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

        // 1. Optimistic local update
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
                .addOnSuccessListener { Log.d("BalMandalRepo", "Attendance batch committed to Firestore DB") }
                .addOnFailureListener { e -> Log.e("BalMandalRepo", "Attendance batch DB commit failed", e) }
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

    // --- Activities (Write to DB & Update Local State) ---

    fun addActivity(activity: MandalActivity) {
        val newId = if (activity.id.isNotBlank()) activity.id else "act-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        val item = activity.copy(id = newId, mandalId = mandalId)

        // Local update
        _activities.value = listOf(item) + _activities.value.filter { it.id != newId }

        // Persist to Firestore DB
        firestore?.collection("activities")?.document(newId)?.set(item)
            ?.addOnSuccessListener { Log.d("BalMandalRepo", "Activity $newId stored in Firestore DB") }
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to add activity in Firestore DB", e) }
    }

    // --- Karyakars (Write to DB & Update Local State) ---

    fun addKaryakar(karyakar: Karyakar) {
        val newId = if (karyakar.id.isNotBlank()) karyakar.id else "k-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        val item = karyakar.copy(id = newId, mandalId = mandalId)

        // Local update
        _karyakars.value = listOf(item) + _karyakars.value.filter { it.id != newId }

        // Persist to Firestore DB
        firestore?.collection("karyakars")?.document(newId)?.set(item)
            ?.addOnSuccessListener { Log.d("BalMandalRepo", "Karyakar $newId stored in Firestore DB") }
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to add karyakar in Firestore DB", e) }
    }

    fun toggleKaryakarActive(karyakarId: String) {
        val k = _karyakars.value.find { it.id == karyakarId } ?: return
        val updated = k.copy(active = !k.active)
        val current = _karyakars.value.toMutableList()
        val idx = current.indexOfFirst { it.id == karyakarId }
        if (idx >= 0) current[idx] = updated
        _karyakars.value = current

        firestore?.collection("karyakars")?.document(karyakarId)?.update("active", updated.active)
            ?.addOnSuccessListener { Log.d("BalMandalRepo", "Karyakar $karyakarId status updated in Firestore DB") }
            ?.addOnFailureListener { e -> Log.e("BalMandalRepo", "Failed to update karyakar status in DB", e) }
    }

    // --- Default Initial Dataset ---

    private fun initializeDefaultData() {
        if (_balaks.value.isNotEmpty()) return

        val sampleBalaks = listOf(
            Balak(
                id = "bal-001",
                firstName = "Nilkanth",
                lastName = "Patel",
                dateOfBirth = "2015-04-12",
                age = 11,
                gender = "Male",
                standard = 6,
                parentName = "Rameshbhai Patel",
                parentMobile = "+91 98250 12345",
                address = "12, Pramukh Swami Society, Memnagar, Ahmedabad",
                school = "Swaminarayan Gurukul Vidyalaya",
                bloodGroup = "O+",
                skills = "Mukhpath, Speech, Kirtan",
                assignedKaryakar = "Mahesh Patel",
                active = true
            ),
            Balak(
                id = "bal-002",
                firstName = "Pramukh",
                lastName = "Shah",
                dateOfBirth = "2016-08-20",
                age = 10,
                gender = "Male",
                standard = 5,
                parentName = "Bipinbhai Shah",
                parentMobile = "+91 98790 54321",
                address = "45, Akshardham Flats, Naranpura, Ahmedabad",
                school = "Zydus School for Excellence",
                bloodGroup = "B+",
                skills = "Tabla, Satsang Quiz",
                assignedKaryakar = "Mahesh Patel",
                active = true
            ),
            Balak(
                id = "bal-003",
                firstName = "Sahaj",
                lastName = "Dave",
                dateOfBirth = "2017-01-15",
                age = 9,
                gender = "Male",
                standard = 4,
                parentName = "Pankajbhai Dave",
                parentMobile = "+91 94260 98765",
                address = "7, Yogi Krupa Bunglows, Bodakdev, Ahmedabad",
                school = "DPS Bopal",
                bloodGroup = "A+",
                skills = "Acting, Drama, Chesta",
                assignedKaryakar = "Kishan Vora",
                active = true
            ),
            Balak(
                id = "bal-004",
                firstName = "Shlok",
                lastName = "Panchal",
                dateOfBirth = "2014-11-05",
                age = 12,
                gender = "Male",
                standard = 7,
                parentName = "Dineshbhai Panchal",
                parentMobile = "+91 97120 45678",
                address = "22, Gunatit Tenement, Chandlodiya, Ahmedabad",
                school = "Mahatma Gandhi International School",
                bloodGroup = "AB+",
                skills = "Art, Decoration, Singing",
                assignedKaryakar = "Mahesh Patel",
                active = true
            ),
            Balak(
                id = "bal-005",
                firstName = "Darshan",
                lastName = "Suthar",
                dateOfBirth = "2015-09-28",
                age = 11,
                gender = "Male",
                standard = 6,
                parentName = "Jayeshbhai Suthar",
                parentMobile = "+91 99040 11223",
                address = "88, Harikrushna Residency, Gota, Ahmedabad",
                school = "Nirma Vidyavihar",
                bloodGroup = "B+",
                skills = "Sports, Khel, Mukhpath",
                assignedKaryakar = "Kishan Vora",
                active = true
            )
        )

        val sampleSabhas = listOf(
            SabhaSession(
                id = "sabha-2026-08-16",
                title = "Weekly Bal Sabha",
                date = "2026-08-16",
                displayDate = "Sunday, 16 Aug 2026",
                time = "5:00 PM - 6:30 PM",
                location = "BAPS Swaminarayan Mandir, Bal Hall",
                topic = "Mahant Swami Maharaj Vicharan & Niyama",
                totalBalaks = 5,
                presentCount = 4,
                absentCount = 1,
                isCompleted = true
            ),
            SabhaSession(
                id = "sabha-2026-08-09",
                title = "Weekly Bal Sabha",
                date = "2026-08-09",
                displayDate = "Sunday, 9 Aug 2026",
                time = "5:00 PM - 6:30 PM",
                location = "BAPS Swaminarayan Mandir, Bal Hall",
                topic = "Bhagwan Swaminarayan Childhood Stories (Ghanshyam Charitra)",
                totalBalaks = 5,
                presentCount = 5,
                absentCount = 0,
                isCompleted = true
            )
        )

        val sampleAttendance = listOf(
            AttendanceRecord(id = "att-1", balakId = "bal-001", balakName = "Nilkanth Patel", sabhaId = "sabha-2026-08-16", date = "2026-08-16", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-2", balakId = "bal-002", balakName = "Pramukh Shah", sabhaId = "sabha-2026-08-16", date = "2026-08-16", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-3", balakId = "bal-003", balakName = "Sahaj Dave", sabhaId = "sabha-2026-08-16", date = "2026-08-16", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-4", balakId = "bal-004", balakName = "Shlok Panchal", sabhaId = "sabha-2026-08-16", date = "2026-08-16", status = AttendanceStatus.LATE),
            AttendanceRecord(id = "att-5", balakId = "bal-005", balakName = "Darshan Suthar", sabhaId = "sabha-2026-08-16", date = "2026-08-16", status = AttendanceStatus.ABSENT),

            AttendanceRecord(id = "att-6", balakId = "bal-001", balakName = "Nilkanth Patel", sabhaId = "sabha-2026-08-09", date = "2026-08-09", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-7", balakId = "bal-002", balakName = "Pramukh Shah", sabhaId = "sabha-2026-08-09", date = "2026-08-09", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-8", balakId = "bal-003", balakName = "Sahaj Dave", sabhaId = "sabha-2026-08-09", date = "2026-08-09", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-9", balakId = "bal-004", balakName = "Shlok Panchal", sabhaId = "sabha-2026-08-09", date = "2026-08-09", status = AttendanceStatus.PRESENT),
            AttendanceRecord(id = "att-10", balakId = "bal-005", balakName = "Darshan Suthar", sabhaId = "sabha-2026-08-09", date = "2026-08-09", status = AttendanceStatus.PRESENT)
        )

        val sampleActivities = listOf(
            MandalActivity(
                id = "act-001",
                name = "Vachanamrut & Satsang Diksha Mukhpath Quiz",
                category = "Quiz",
                date = "2026-08-23",
                description = "Annual inter-mandal Satsang Diksha mukhpath competition with audio-visual rounds."
            ),
            MandalActivity(
                id = "act-002",
                name = "Bal Mandal Sports Day & Khel Mahotsav",
                category = "Khel",
                date = "2026-08-30",
                description = "Cricket tournament, Kho-Kho, and traditional games for balaks."
            )
        )

        val sampleKaryakars = listOf(
            Karyakar(
                id = "k-001",
                name = "Mahesh Patel",
                email = "mahesh.patel@baps.org",
                phone = "+91 98250 99887",
                role = "admin",
                responsibilities = "Bal Mandal Sanchalak & Attendance Lead"
            ),
            Karyakar(
                id = "k-002",
                name = "Kishan Vora",
                email = "kishan.vora@baps.org",
                phone = "+91 98790 11223",
                role = "karyakar",
                responsibilities = "Std 4-5 Activity Coordinator & Mukhpath Incharge"
            )
        )

        _balaks.value = sampleBalaks
        _sabhas.value = sampleSabhas
        _attendanceRecords.value = sampleAttendance
        _activities.value = sampleActivities
        _karyakars.value = sampleKaryakars
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
