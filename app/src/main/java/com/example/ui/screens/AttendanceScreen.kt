package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AttendanceStatus
import com.example.data.models.Balak
import com.example.data.models.SabhaSession
import com.example.ui.components.BalakAvatar
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.ExcusedBlue
import com.example.ui.theme.ExcusedBlueBg
import com.example.ui.theme.LateOrange
import com.example.ui.theme.LateOrangeBg
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttendanceScreen(
    balaks: List<Balak>,
    sabhas: List<SabhaSession>,
    selectedDate: String,
    onSelectDate: (String) -> Unit,
    statusMap: Map<String, AttendanceStatus>,
    notesMap: Map<String, String>,
    onStatusChange: (balakId: String, status: AttendanceStatus) -> Unit,
    onNoteChange: (balakId: String, note: String) -> Unit,
    onMarkAll: (AttendanceStatus) -> Unit,
    onSaveAttendance: () -> Unit,
    isGujarati: Boolean,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var dateDropdownExpanded by remember { mutableStateOf(false) }
    var noteDialogBalakId by remember { mutableStateOf<String?>(null) }

    val activeBalaks = remember(balaks) { balaks.filter { it.active } }
    val filteredBalaks = remember(activeBalaks, searchQuery) {
        if (searchQuery.isBlank()) activeBalaks
        else activeBalaks.filter {
            it.fullName.contains(searchQuery, ignoreCase = true) ||
                    it.standard.toString() == searchQuery ||
                    it.balakIdFormatted.contains(searchQuery, ignoreCase = true)
        }
    }

    val presentCount = activeBalaks.count {
        val s = statusMap[it.id] ?: AttendanceStatus.PRESENT
        s == AttendanceStatus.PRESENT || s == AttendanceStatus.LATE
    }
    val absentCount = activeBalaks.count {
        val s = statusMap[it.id] ?: AttendanceStatus.PRESENT
        s == AttendanceStatus.ABSENT
    }
    val lateCount = activeBalaks.count { statusMap[it.id] == AttendanceStatus.LATE }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        // Date Selector Dropdown Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("attendance_date_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isGujarati) "બાળ મંડળ સભા તારીખ" else "BAL MANDAL SABHA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SaffronPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = selectedDate,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        )
                    }

                    ExposedDropdownMenuBox(
                        expanded = dateDropdownExpanded,
                        onExpandedChange = { dateDropdownExpanded = it }
                    ) {
                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = SaffronPrimary.copy(alpha = 0.12f),
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .clickable { dateDropdownExpanded = true }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CalendarMonth, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Change ▼",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = SaffronPrimary
                                )
                            }
                        }

                        ExposedDropdownMenu(
                            expanded = dateDropdownExpanded,
                            onDismissRequest = { dateDropdownExpanded = false }
                        ) {
                            sabhas.forEach { sabha ->
                                DropdownMenuItem(
                                    text = {
                                        Column {
                                            Text(sabha.displayDate, fontWeight = FontWeight.Bold)
                                            Text(sabha.title, fontSize = 11.sp, color = Color.Gray)
                                        }
                                    },
                                    onClick = {
                                        onSelectDate(sabha.date)
                                        dateDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Real-time Summary Counters Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(PresentGreen))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Present: $presentCount",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = PresentGreen)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AbsentRed))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Absent: $absentCount",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = AbsentRed)
                        )
                    }

                    if (lateCount > 0) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(LateOrange))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Late: $lateCount",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = LateOrange)
                            )
                        }
                    }

                    Text(
                        text = "Total: ${activeBalaks.size}",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Fast Actions & Search Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search balak...", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp), tint = SaffronPrimary) },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(100.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = SaffronPrimary,
                    unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .testTag("attendance_search_input")
            )

            OutlinedButton(
                onClick = { onMarkAll(AttendanceStatus.PRESENT) },
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.5f)),
                modifier = Modifier
                    .height(48.dp)
                    .testTag("button_mark_all_present")
            ) {
                Text("All Present", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = PresentGreen)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Balak Attendance Marking List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            items(filteredBalaks, key = { it.id }) { balak ->
                val currentStatus = statusMap[balak.id] ?: AttendanceStatus.PRESENT
                val note = notesMap[balak.id] ?: ""

                AttendanceBalakItem(
                    balak = balak,
                    status = currentStatus,
                    note = note,
                    onStatusChange = { newStatus -> onStatusChange(balak.id, newStatus) },
                    onEditNoteClick = { noteDialogBalakId = balak.id }
                )
            }
        }

        // Prominent Save Attendance Button Bar
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            color = MaterialTheme.colorScheme.background
        ) {
            Button(
                onClick = onSaveAttendance,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("button_save_attendance"),
                shape = RoundedCornerShape(24.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
            ) {
                Icon(Icons.Default.Check, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isGujarati) "હાજરી સાચવો ($presentCount હાજર)" else "Save Attendance ($presentCount Present)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        }
    }
}

@Composable
fun AttendanceBalakItem(
    balak: Balak,
    status: AttendanceStatus,
    note: String,
    onStatusChange: (AttendanceStatus) -> Unit,
    onEditNoteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("attendance_item_${balak.id}"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BalakAvatar(name = balak.fullName, gender = balak.gender, size = 40)
                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = balak.fullName,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Std. ${balak.standard} • ${balak.balakIdFormatted}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    )
                }

                // 4 Rapid Attendance Status Pill Buttons (P, A, L, E)
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    StatusButton(
                        label = "P",
                        isSelected = status == AttendanceStatus.PRESENT,
                        selectedBg = PresentGreen,
                        unselectedBg = Color(0xFFF3F0E9),
                        onClick = { onStatusChange(AttendanceStatus.PRESENT) }
                    )

                    StatusButton(
                        label = "A",
                        isSelected = status == AttendanceStatus.ABSENT,
                        selectedBg = AbsentRed,
                        unselectedBg = Color(0xFFF3F0E9),
                        onClick = { onStatusChange(AttendanceStatus.ABSENT) }
                    )

                    StatusButton(
                        label = "L",
                        isSelected = status == AttendanceStatus.LATE,
                        selectedBg = LateOrange,
                        unselectedBg = Color(0xFFF3F0E9),
                        onClick = { onStatusChange(AttendanceStatus.LATE) }
                    )

                    StatusButton(
                        label = "E",
                        isSelected = status == AttendanceStatus.EXCUSED,
                        selectedBg = ExcusedBlue,
                        unselectedBg = Color(0xFFF3F0E9),
                        onClick = { onStatusChange(AttendanceStatus.EXCUSED) }
                    )
                }
            }

            if (note.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Note: $note",
                        style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray, fontSize = 10.sp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusButton(
    label: String,
    isSelected: Boolean,
    selectedBg: Color,
    unselectedBg: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(if (isSelected) selectedBg else unselectedBg)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            color = if (isSelected) Color.White else Color(0xFF4B5563)
        )
    }
}

