package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.models.BalakAttendanceSummary
import com.example.ui.components.BalMandalBottomNavigation
import com.example.ui.components.BalMandalTopAppBar
import com.example.ui.screens.ActivitiesScreen
import com.example.ui.screens.AddEditBalakScreen
import com.example.ui.screens.AttendanceScreen
import com.example.ui.screens.BalakDetailScreen
import com.example.ui.screens.BalaksScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.KaryakarManagementScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BalMandalViewModel
import com.example.viewmodel.Screen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BalMandalApp()
            }
        }
    }
}

@Composable
fun BalMandalApp(
    viewModel: BalMandalViewModel = viewModel()
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isGujarati by viewModel.isGujaratiMode.collectAsState()
    val userMessage by viewModel.userMessage.collectAsState()

    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedStandard by viewModel.selectedStandardFilter.collectAsState()
    val showLowAttendanceOnly by viewModel.showOnlyLowAttendance.collectAsState()
    val showActiveOnly by viewModel.showOnlyActive.collectAsState()
    val balakSummaries by viewModel.filteredBalakSummaries.collectAsState()

    val sabhas by viewModel.sabhas.collectAsState()
    val activities by viewModel.activities.collectAsState()
    val karyakars by viewModel.karyakars.collectAsState()
    val rawBalaks by viewModel.allBalaks.collectAsState()

    val selectedSabhaDate by viewModel.selectedSabhaDate.collectAsState()
    val attendanceDraftMap by viewModel.attendanceDraftMap.collectAsState()
    val attendanceNotesMap by viewModel.attendanceNotesMap.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(userMessage) {
        userMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearUserMessage()
        }
    }

    // Handle System Back button
    BackHandler(enabled = currentScreen !is Screen.Dashboard && currentScreen !is Screen.Login) {
        viewModel.navigateBack()
    }

    val verificationId by viewModel.verificationId.collectAsState()

    if (currentUser == null || currentScreen is Screen.Login) {
        LoginScreen(
            onGoogleSignIn = { context ->
                viewModel.signInWithGoogle(context)
            },
            onSendPhoneCode = { activity, phone ->
                viewModel.sendPhoneVerificationCode(activity, phone)
            },
            onVerifyPhoneCode = { code ->
                viewModel.verifyPhoneCode(code)
            },
            verificationId = verificationId,
            userMessage = userMessage
        )
    } else {
        val title = when (currentScreen) {
            is Screen.Dashboard -> if (isGujarati) "બાળ મંડળ" else "Bal Mandal"
            is Screen.BalaksList -> if (isGujarati) "બાલકો યાદી" else "Balaks Directory"
            is Screen.BalakDetail -> if (isGujarati) "બાલક પ્રોફાઇલ" else "Balak Profile"
            is Screen.AddEditBalak -> if (isGujarati) "બાલક ફોર્મ" else "Balak Form"
            is Screen.Attendance -> if (isGujarati) "સાપ્તાહિક હાજરી" else "Sabha Attendance"
            is Screen.Reports -> if (isGujarati) "હાજરી અહેવાલ" else "Attendance Reports"
            is Screen.More -> if (isGujarati) "વધુ સેટિંગ્સ" else "Settings & Profile"
            is Screen.Activities -> if (isGujarati) "મંડળ પ્રવૃત્તિઓ" else "Activities & Events"
            is Screen.KaryakarManagement -> if (isGujarati) "કાર્યકર ટીમ" else "Karyakar Management"
            else -> "BAPS Bal Mandal"
        }

        val canGoBack = currentScreen !is Screen.Dashboard

        Scaffold(
            topBar = {
                BalMandalTopAppBar(
                    title = title,
                    currentUser = currentUser,
                    canNavigateBack = canGoBack,
                    onNavigateBack = { viewModel.navigateBack() },
                    isGujarati = isGujarati,
                    onToggleLanguage = { viewModel.toggleLanguage() },
                    onRoleBadgeClick = { viewModel.navigateTo(Screen.More) }
                )
            },
            bottomBar = {
                BalMandalBottomNavigation(
                    currentScreen = currentScreen,
                    onNavigate = { screen -> viewModel.navigateTo(screen) }
                )
            },
            snackbarHost = { SnackbarHost(snackbarHostState) },
            contentWindowInsets = WindowInsets(0, 0, 0, 0)
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (val screen = currentScreen) {
                    is Screen.Dashboard -> {
                        DashboardScreen(
                            stats = viewModel.getDashboardStats(),
                            currentUser = currentUser,
                            isGujarati = isGujarati,
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }

                    is Screen.BalaksList -> {
                        BalaksScreen(
                            balakSummaries = balakSummaries,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            selectedStandard = selectedStandard,
                            onSelectStandard = { viewModel.setStandardFilter(it) },
                            showLowAttendanceOnly = showLowAttendanceOnly,
                            onToggleLowAttendance = { viewModel.toggleLowAttendanceFilter() },
                            showActiveOnly = showActiveOnly,
                            onToggleActiveOnly = { viewModel.toggleActiveFilter() },
                            isGujarati = isGujarati,
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }

                    is Screen.BalakDetail -> {
                        val summary = viewModel.getBalakSummary(screen.balakId)
                        val history = viewModel.getBalakAttendanceHistory(screen.balakId)
                        BalakDetailScreen(
                            summary = summary,
                            attendanceHistory = history,
                            allActivities = activities,
                            currentUser = currentUser,
                            isGujarati = isGujarati,
                            onNavigate = { dest -> viewModel.navigateTo(dest) },
                            onToggleActive = { viewModel.toggleBalakActiveStatus(it) },
                            onDeleteBalak = { viewModel.deleteBalak(it) }
                        )
                    }

                    is Screen.AddEditBalak -> {
                        val initialBalak = screen.balakId?.let { id -> rawBalaks.firstOrNull { it.id == id } }
                        AddEditBalakScreen(
                            initialBalak = initialBalak,
                            isGujarati = isGujarati,
                            onSave = { savedBalak -> viewModel.saveBalak(savedBalak) },
                            onDelete = if (initialBalak != null) { { id -> viewModel.deleteBalak(id) } } else null
                        )
                    }

                    is Screen.Attendance -> {
                        AttendanceScreen(
                            balaks = rawBalaks,
                            sabhas = sabhas,
                            selectedDate = selectedSabhaDate,
                            onSelectDate = { viewModel.selectSabhaDate(it) },
                            statusMap = attendanceDraftMap,
                            notesMap = attendanceNotesMap,
                            onStatusChange = { id, status -> viewModel.setBalakAttendanceStatus(id, status) },
                            onNoteChange = { id, note -> viewModel.setBalakAttendanceNote(id, note) },
                            onMarkAll = { status -> viewModel.markAllAs(status) },
                            onSaveAttendance = { viewModel.saveAttendance() },
                            isGujarati = isGujarati
                        )
                    }

                    is Screen.Reports -> {
                        ReportsScreen(
                            summaries = viewModel.filteredBalakSummaries.value.ifEmpty { viewModel.repositoryAllSummaries() },
                            sabhas = sabhas,
                            isGujarati = isGujarati,
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }

                    is Screen.More -> {
                        MoreScreen(
                            currentUser = currentUser,
                            isGujarati = isGujarati,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onLogout = { viewModel.logout() },
                            onNavigate = { dest -> viewModel.navigateTo(dest) }
                        )
                    }

                    is Screen.Activities -> {
                        ActivitiesScreen(
                            activities = activities,
                            onAddActivity = { viewModel.addActivity(it) },
                            isGujarati = isGujarati
                        )
                    }

                    is Screen.KaryakarManagement -> {
                        KaryakarManagementScreen(
                            karyakars = karyakars,
                            onAddKaryakar = { viewModel.addKaryakar(it) },
                            onToggleKaryakarActive = { viewModel.toggleKaryakarActive(it) },
                            isGujarati = isGujarati
                        )
                    }

                    else -> {}
                }
            }
        }
    }
}

// Extension helper for reports fallback
private fun BalMandalViewModel.repositoryAllSummaries(): List<BalakAttendanceSummary> {
    return this.allBalaks.value.map { getBalakSummary(it.id) }
}
