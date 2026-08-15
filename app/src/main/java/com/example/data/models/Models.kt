package com.example.data.models

data class UserProfile(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "karyakar", // "admin" or "karyakar"
    val mandalId: String = "mandal-001",
    val mandalName: String = "BAPS Bal Mandal - Ahmedabad West",
    val active: Boolean = true
) {
    val isAdmin: Boolean get() = role.equals("admin", ignoreCase = true)
}

data class Balak(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val dateOfBirth: String = "",
    val age: Int = 10,
    val gender: String = "Male",
    val standard: Int = 5,
    val parentName: String = "",
    val parentMobile: String = "",
    val address: String = "",
    val mandalId: String = "mandal-001",
    val mandalName: String = "BAPS Bal Mandal",
    val joiningDate: String = "",
    val photoUrl: String = "",
    val school: String = "",
    val bloodGroup: String = "B+",
    val notes: String = "",
    val interests: String = "Kirtan, Satsang Quiz, Cricket",
    val skills: String = "Singing, Speech",
    val assignedKaryakar: String = "Mahesh Patel",
    val active: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val fullName: String get() = if (firstName.isNotBlank()) "$firstName $lastName".trim() else "Balak"
    val balakIdFormatted: String get() = "BAL-${id.takeLast(4).padStart(4, '0')}"
}

enum class AttendanceStatus(val value: String, val label: String) {
    PRESENT("present", "Present"),
    ABSENT("absent", "Absent"),
    LATE("late", "Late"),
    EXCUSED("excused", "Excused");

    companion object {
        fun fromString(str: String): AttendanceStatus {
            return entries.firstOrNull { it.value.equals(str, ignoreCase = true) } ?: ABSENT
        }
    }
}

data class AttendanceRecord(
    val id: String = "",
    val balakId: String = "",
    val balakName: String = "",
    val sabhaId: String = "",
    val date: String = "", // YYYY-MM-DD
    val status: AttendanceStatus = AttendanceStatus.PRESENT,
    val markedBy: String = "Mahesh Patel",
    val note: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val mandalId: String = "mandal-001"
)

data class SabhaSession(
    val id: String = "",
    val title: String = "Weekly Bal Sabha",
    val date: String = "", // YYYY-MM-DD
    val displayDate: String = "", // e.g. "Sunday, 16 Aug 2026"
    val time: String = "5:00 PM - 6:30 PM",
    val location: String = "BAPS Swaminarayan Mandir, Bal Hall",
    val topic: String = "Mahant Swami Maharaj Vicharan & Niyama",
    val mandalId: String = "mandal-001",
    val totalBalaks: Int = 0,
    val presentCount: Int = 0,
    val absentCount: Int = 0,
    val isCompleted: Boolean = false
)

data class MandalActivity(
    val id: String = "",
    val name: String = "",
    val category: String = "Quiz", // "Quiz", "Khel", "Shibir", "Seva", "Special Event", "Satsang Exam"
    val date: String = "",
    val description: String = "",
    val participantIds: List<String> = emptyList(),
    val notes: String = "",
    val mandalId: String = "mandal-001"
)

data class Karyakar(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "karyakar",
    val mandalId: String = "mandal-001",
    val mandalName: String = "BAPS Bal Mandal - Ahmedabad West",
    val active: Boolean = true,
    val responsibilities: String = "Std 5-7 Balaks, Attendance Incharge"
)

data class BalakAttendanceSummary(
    val balak: Balak,
    val totalSabhas: Int = 0,
    val attendedSabhas: Int = 0,
    val lateCount: Int = 0,
    val excusedCount: Int = 0,
    val percentage: Float = 0f,
    val lastAttendedDate: String = "N/A"
)

data class DashboardStats(
    val totalBalaks: Int = 0,
    val presentToday: Int = 0,
    val absentToday: Int = 0,
    val attendancePercentage: Int = 0,
    val newBalaksCount: Int = 0,
    val lowAttendanceCount: Int = 0,
    val upcomingSabha: SabhaSession? = null
)
