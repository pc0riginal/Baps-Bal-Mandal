package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Karyakar
import com.example.data.models.UserProfile
import com.example.ui.components.BalakAvatar
import com.example.ui.components.mandalTextFieldColors
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KaryakarManagementScreen(
    karyakars: List<Karyakar>,
    currentUser: UserProfile?,
    onAddKaryakar: (Karyakar) -> Unit,
    onToggleKaryakarActive: (String) -> Unit,
    onDeleteKaryakar: ((String) -> Unit)? = null,
    isGujarati: Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var karyakarToDelete by remember { mutableStateOf<Karyakar?>(null) }
    val isAdmin = currentUser?.isAdmin == true

    Scaffold(
        floatingActionButton = {
            if (isAdmin) {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = SaffronPrimary,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.testTag("fab_add_karyakar")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Karyakar")
                }
            }
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
            Column {
                Text(
                    text = "TEAM ADMINISTRATION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SaffronPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = if (isGujarati) "કાર્યકર વ્યવસ્થાપન" else "Karyakar Team Management",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                )
            }
            Text(
                text = "Manage coordinators, sabha leaders, standard in-charges & mentors",
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )

            if (!isAdmin) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = SaffronPrimary.copy(alpha = 0.08f),
                    border = BorderStroke(1.dp, SaffronPrimary.copy(alpha = 0.2f))
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Only the Mandal Admin (Database Owner) has permission to add, activate, or delete karyakars.",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            if (karyakars.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = BorderSubtleLight,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Karyakars Registered Yet",
                            style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isAdmin) "Tap '+' below to add karyakar members to your team." else "Karyakars will appear once added by Admin.",
                            style = MaterialTheme.typography.bodySmall.copy(color = Color.Gray)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(karyakars) { karyakar ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("karyakar_card_${karyakar.id}"),
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BalakAvatar(name = karyakar.name, size = 44)
                                    Spacer(modifier = Modifier.width(12.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = karyakar.name,
                                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(
                                                shape = RoundedCornerShape(100.dp),
                                                color = if (karyakar.role.contains("Admin", ignoreCase = true) || karyakar.role.contains("Sanchalak", ignoreCase = true)) {
                                                    SaffronPrimary.copy(alpha = 0.12f)
                                                } else {
                                                    NavySecondary.copy(alpha = 0.1f)
                                                }
                                            ) {
                                                Text(
                                                    text = karyakar.role,
                                                    color = if (karyakar.role.contains("Admin", ignoreCase = true) || karyakar.role.contains("Sanchalak", ignoreCase = true)) {
                                                        SaffronPrimary
                                                    } else {
                                                        NavySecondary
                                                    },
                                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        val details = listOfNotNull(
                                            karyakar.responsibilities.takeIf { it.isNotBlank() },
                                            karyakar.phone.takeIf { it.isNotBlank() }
                                        ).joinToString(" • ")

                                        if (details.isNotBlank()) {
                                            Text(
                                                text = details,
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    }

                                    if (karyakar.phone.isNotBlank()) {
                                        IconButton(
                                            onClick = {
                                                val intent = Intent(Intent.ACTION_DIAL).apply {
                                                    data = Uri.parse("tel:${karyakar.phone}")
                                                }
                                                context.startActivity(intent)
                                            },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.Phone, contentDescription = "Call", tint = Color(0xFF2563EB), modifier = Modifier.size(18.dp))
                                        }
                                    }

                                    if (isAdmin) {
                                        IconButton(
                                            onClick = { karyakarToDelete = karyakar },
                                            modifier = Modifier.size(36.dp)
                                        ) {
                                            Icon(Icons.Default.DeleteOutline, contentDescription = "Delete Karyakar", tint = AbsentRed, modifier = Modifier.size(20.dp))
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (karyakar.active) "Status: Active Karyakar" else "Status: Inactive / Disabled",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = if (karyakar.active) PresentGreen else Color.Gray,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    )

                                    if (isAdmin) {
                                        Switch(
                                            checked = karyakar.active,
                                            onCheckedChange = { onToggleKaryakarActive(karyakar.id) },
                                            colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary)
                                        )
                                    } else {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = if (karyakar.active) PresentGreen.copy(alpha = 0.1f) else Color.Gray.copy(alpha = 0.1f)
                                        ) {
                                            Text(
                                                text = if (karyakar.active) "ACTIVE" else "INACTIVE",
                                                color = if (karyakar.active) PresentGreen else Color.Gray,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    if (karyakarToDelete != null) {
        val target = karyakarToDelete!!
        AlertDialog(
            onDismissRequest = { karyakarToDelete = null },
            title = { Text("Delete Karyakar", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to remove ${target.name} from the Mandal team? This will delete their karyakar entry.") },
            confirmButton = {
                Button(
                    onClick = {
                        val idToDelete = target.id
                        karyakarToDelete = null
                        onDeleteKaryakar?.invoke(idToDelete)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { karyakarToDelete = null }) {
                    Text("Cancel")
                }
            },
            shape = RoundedCornerShape(20.dp)
        )
    }

    // Add Karyakar Dialog
    if (showAddDialog) {
        var name by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var mobile by remember { mutableStateOf("") }
        var role by remember { mutableStateOf("Bal Mandal Karyakar") }
        var assignedStandard by remember { mutableStateOf("Std 5 - 7") }
        var roleDropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Register New Karyakar", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Full Name *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
                    )
                    OutlinedTextField(
                        value = mobile,
                        onValueChange = { mobile = it },
                        label = { Text("Mobile Number *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
                    )

                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = it }
                    ) {
                        OutlinedTextField(
                            value = role,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable).fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = mandalTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false }
                        ) {
                            listOf("Bal Mandal Karyakar", "Sanchalak (Admin)", "Sah-Karyakar", "Attendance Incharge", "Activity Coordinator").forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r) },
                                    onClick = {
                                        role = r
                                        roleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    OutlinedTextField(
                        value = assignedStandard,
                        onValueChange = { assignedStandard = it },
                        label = { Text("Responsibilities / Group (e.g. Std 1-4)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (name.isNotBlank()) {
                            onAddKaryakar(
                                Karyakar(
                                    name = name.trim(),
                                    email = email.trim(),
                                    phone = mobile.trim(),
                                    role = role,
                                    responsibilities = assignedStandard.trim(),
                                    active = true
                                )
                            )
                            showAddDialog = false
                        }
                    },
                    modifier = Modifier.heightIn(min = 44.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Add Karyakar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}
