package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.models.AttendanceRecord
import com.example.data.models.AttendanceStatus
import com.example.data.models.Balak
import com.example.data.models.BalakAttendanceSummary
import com.example.data.models.DashboardStats
import com.example.data.models.Karyakar
import com.example.data.models.MandalActivity
import com.example.data.models.SabhaSession
import com.example.data.models.UserProfile
import com.example.data.repository.BalMandalRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed class Screen {
    data object Login : Screen()
    data object Dashboard : Screen()
    data object BalaksList : Screen()
    data class BalakDetail(val balakId: String) : Screen()
    data class AddEditBalak(val balakId: String? = null) : Screen()
    data class Attendance(val sabhaDate: String = "2026-08-16") : Screen()
    data object Reports : Screen()
    data object More : Screen()
    data object KaryakarManagement : Screen()
    data object Activities : Screen()
}

class BalMandalViewModel(
    private val repository: BalMandalRepository = BalMandalRepository.getInstance()
) : ViewModel() {

    // Current Screen
    private val _currentScreen = MutableStateFlow<Screen>(Screen.Dashboard)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    // Navigation History for Back Button
    private val backStack = mutableListOf<Screen>()

    // Current User
    val currentUser: StateFlow<UserProfile?> = repository.currentUser

    // Language Toggle: true = Gujarati / English Bilingual, false = English
    private val _isGujaratiMode = MutableStateFlow(false)
    val isGujaratiMode: StateFlow<Boolean> = _isGujaratiMode.asStateFlow()

    // Balak Filtering & Search
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedStandardFilter = MutableStateFlow<Int?>(null) // null = All
    val selectedStandardFilter: StateFlow<Int?> = _selectedStandardFilter.asStateFlow()

    private val _showOnlyLowAttendance = MutableStateFlow(false)
    val showOnlyLowAttendance: StateFlow<Boolean> = _showOnlyLowAttendance.asStateFlow()

    private val _showOnlyActive = MutableStateFlow(true)
    val showOnlyActive: StateFlow<Boolean> = _showOnlyActive.asStateFlow()

    // Raw Balaks & Summaries
    val allBalaks: StateFlow<List<Balak>> = repository.balaks
    val sabhas: StateFlow<List<SabhaSession>> = repository.sabhas
    val activities: StateFlow<List<MandalActivity>> = repository.activities
    val karyakars: StateFlow<List<Karyakar>> = repository.karyakars

    // Filtered Balak Summaries
    val filteredBalakSummaries: StateFlow<List<BalakAttendanceSummary>> = combine(
        combine(repository.balaks, repository.attendanceRecords) { b, a -> Pair(b, a) },
        _searchQuery,
        _selectedStandardFilter,
        _showOnlyLowAttendance,
        _showOnlyActive
    ) { (balaks, _), query, stdFilter, lowAttOnly, activeOnly ->
        val summaries = repository.getAllBalakAttendanceSummaries()
        summaries.filter { summary ->
            val b = summary.balak
            val matchesQuery = query.isBlank() ||
                    b.fullName.contains(query, ignoreCase = true) ||
                    b.id.contains(query, ignoreCase = true) ||
                    b.parentName.contains(query, ignoreCase = true) ||
                    b.parentMobile.contains(query, ignoreCase = true) ||
                    b.school.contains(query, ignoreCase = true)

            val matchesStd = stdFilter == null || b.standard == stdFilter
            val matchesLow = !lowAttOnly || summary.percentage < 70f
            val matchesActive = !activeOnly || b.active

            matchesQuery && matchesStd && matchesLow && matchesActive
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendance marking screen state
    private val _selectedSabhaDate = MutableStateFlow("2026-08-16")
    val selectedSabhaDate: StateFlow<String> = _selectedSabhaDate.asStateFlow()

    private val _attendanceDraftMap = MutableStateFlow<Map<String, AttendanceStatus>>(emptyMap())
    val attendanceDraftMap: StateFlow<Map<String, AttendanceStatus>> = _attendanceDraftMap.asStateFlow()

    private val _attendanceNotesMap = MutableStateFlow<Map<String, String>>(emptyMap())
    val attendanceNotesMap: StateFlow<Map<String, String>> = _attendanceNotesMap.asStateFlow()

    private val _verificationId = MutableStateFlow<String?>(null)
    val verificationId: StateFlow<String?> = _verificationId.asStateFlow()

    private val _userMessage = MutableStateFlow<String?>(null)
    val userMessage: StateFlow<String?> = _userMessage.asStateFlow()

    init {
        loadAttendanceDraftForDate("2026-08-16")
    }

    // --- Navigation Actions ---
    fun navigateTo(screen: Screen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        return false
    }

    fun toggleLanguage() {
        _isGujaratiMode.value = !_isGujaratiMode.value
    }

    fun clearUserMessage() {
        _userMessage.value = null
    }

    // --- Auth Actions ---
    fun signInWithGoogle(context: android.content.Context) {
        viewModelScope.launch {
            try {
                val credentialManager = androidx.credentials.CredentialManager.create(context)
                val request = androidx.credentials.GetCredentialRequest.Builder()
                    .addCredentialOption(
                        com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption.Builder(com.example.BuildConfig.WEB_CLIENT_ID)
                            .build()
                    )
                    .build()

                val result = credentialManager.getCredential(context, request)
                val credential = result.credential

                if (credential is androidx.credentials.CustomCredential && credential.type == com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    val googleIdTokenCredential = com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.createFrom(credential.data)
                    repository.signInWithGoogle(googleIdTokenCredential.idToken) { success, error ->
                        if (success) {
                            _currentScreen.value = Screen.Dashboard
                            _userMessage.value = "Jai Swaminarayan! Logged in successfully"
                        } else {
                            _userMessage.value = "Login failed: $error"
                        }
                    }
                } else {
                    _userMessage.value = "Unexpected credential type."
                }
            } catch (e: Exception) {
                _userMessage.value = "Google Sign-In failed: ${e.localizedMessage}"
            }
        }
    }

    fun sendPhoneVerificationCode(activity: android.app.Activity, phoneNumber: String) {
        val auth = com.google.firebase.auth.FirebaseAuth.getInstance()
        val options = com.google.firebase.auth.PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)
            .setTimeout(60L, java.util.concurrent.TimeUnit.SECONDS)
            .setActivity(activity)
            .setCallbacks(object : com.google.firebase.auth.PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                override fun onVerificationCompleted(credential: com.google.firebase.auth.PhoneAuthCredential) {
                    repository.signInWithCredential(credential) { success, error ->
                        if (success) {
                            _currentScreen.value = Screen.Dashboard
                            _userMessage.value = "Jai Swaminarayan! Logged in successfully"
                        } else {
                            _userMessage.value = "Login failed: $error"
                        }
                    }
                }

                override fun onVerificationFailed(e: com.google.firebase.FirebaseException) {
                    _userMessage.value = "Phone verification failed: ${e.localizedMessage}"
                }

                override fun onCodeSent(verificationId: String, token: com.google.firebase.auth.PhoneAuthProvider.ForceResendingToken) {
                    _verificationId.value = verificationId
                    _userMessage.value = "Code sent to $phoneNumber"
                }
            })
            .build()
        com.google.firebase.auth.PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun verifyPhoneCode(code: String) {
        val vid = _verificationId.value
        if (vid == null) {
            _userMessage.value = "Session expired or code not sent."
            return
        }
        val credential = com.google.firebase.auth.PhoneAuthProvider.getCredential(vid, code)
        repository.signInWithCredential(credential) { success, error ->
            if (success) {
                _currentScreen.value = Screen.Dashboard
                _userMessage.value = "Jai Swaminarayan! Logged in successfully"
            } else {
                _userMessage.value = "Login failed: $error"
            }
        }
    }

    fun login(email: String, password: String) {
        repository.login(email, password) { success, error ->
            if (success) {
                _currentScreen.value = Screen.Dashboard
                _userMessage.value = "Jai Swaminarayan! Logged in successfully"
            } else {
                _userMessage.value = "Login failed: $error"
            }
        }
    }

    fun logout() {
        repository.logout()
        backStack.clear()
        _currentScreen.value = Screen.Login
        _userMessage.value = "Logged out successfully"
    }

    // --- Balak Filter Actions ---
    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setStandardFilter(standard: Int?) {
        _selectedStandardFilter.value = standard
    }

    fun toggleLowAttendanceFilter() {
        _showOnlyLowAttendance.value = !_showOnlyLowAttendance.value
    }

    fun toggleActiveFilter() {
        _showOnlyActive.value = !_showOnlyActive.value
    }

    fun resetFilters() {
        _searchQuery.value = ""
        _selectedStandardFilter.value = null
        _showOnlyLowAttendance.value = false
        _showOnlyActive.value = true
    }

    // --- Balak CRUD ---
    fun saveBalak(balak: Balak) {
        if (balak.id.isBlank()) {
            val newId = repository.addBalak(balak)
            _userMessage.value = "Balak ${balak.fullName} added successfully!"
            navigateTo(Screen.BalakDetail(newId))
        } else {
            repository.updateBalak(balak)
            _userMessage.value = "Balak ${balak.fullName} updated!"
            navigateTo(Screen.BalakDetail(balak.id))
        }
    }

    fun toggleBalakActiveStatus(balakId: String) {
        repository.toggleBalakActive(balakId)
        val balak = repository.getBalakById(balakId)
        _userMessage.value = "Status updated for ${balak?.fullName}"
    }

    fun deleteBalak(balakId: String) {
        val balak = repository.getBalakById(balakId)
        repository.deleteBalak(balakId)
        _userMessage.value = "Balak ${balak?.fullName} removed"
        navigateTo(Screen.BalaksList)
    }

    fun getBalakSummary(balakId: String): BalakAttendanceSummary {
        return repository.getBalakAttendanceSummary(balakId)
    }

    fun getBalakAttendanceHistory(balakId: String): List<AttendanceRecord> {
        return repository.attendanceRecords.value
            .filter { it.balakId == balakId }
            .sortedByDescending { it.date }
    }

    // --- Attendance Operations ---
    fun selectSabhaDate(date: String) {
        _selectedSabhaDate.value = date
        loadAttendanceDraftForDate(date)
    }

    fun loadAttendanceDraftForDate(date: String) {
        val existing = repository.getAttendanceForSabha(date)
        val map = mutableMapOf<String, AttendanceStatus>()
        val notes = mutableMapOf<String, String>()

        if (existing.isNotEmpty()) {
            for (rec in existing) {
                map[rec.balakId] = rec.status
                if (rec.note.isNotBlank()) notes[rec.balakId] = rec.note
            }
        } else {
            // Default active balaks to PRESENT for rapid 1-tap confirmation
            for (balak in repository.balaks.value.filter { it.active }) {
                map[balak.id] = AttendanceStatus.PRESENT
            }
        }
        _attendanceDraftMap.value = map
        _attendanceNotesMap.value = notes
    }

    fun setBalakAttendanceStatus(balakId: String, status: AttendanceStatus) {
        _attendanceDraftMap.value = _attendanceDraftMap.value + (balakId to status)
    }

    fun setBalakAttendanceNote(balakId: String, note: String) {
        _attendanceNotesMap.value = _attendanceNotesMap.value + (balakId to note)
    }

    fun markAllAs(status: AttendanceStatus) {
        val updated = _attendanceDraftMap.value.toMutableMap()
        for (balak in repository.balaks.value.filter { it.active }) {
            updated[balak.id] = status
        }
        _attendanceDraftMap.value = updated
    }

    fun saveAttendance() {
        val date = _selectedSabhaDate.value
        val sabhaId = "sabha-$date"
        repository.saveSabhaAttendance(
            date = date,
            sabhaId = sabhaId,
            statusMap = _attendanceDraftMap.value,
            notesMap = _attendanceNotesMap.value
        )
        _userMessage.value = "Attendance for $date saved successfully! ✓"
    }

    // --- Dashboard Stats ---
    fun getDashboardStats(): DashboardStats {
        return repository.getDashboardStats()
    }

    // --- Activities ---
    fun addActivity(activity: MandalActivity) {
        repository.addActivity(activity)
        _userMessage.value = "Activity '${activity.name}' added!"
    }

    // --- Karyakar Admin ---
    fun addKaryakar(karyakar: Karyakar) {
        repository.addKaryakar(karyakar)
        _userMessage.value = "Karyakar '${karyakar.name}' registered!"
    }

    fun toggleKaryakarActive(karyakarId: String) {
        repository.toggleKaryakarActive(karyakarId)
        _userMessage.value = "Karyakar status updated"
    }
}
