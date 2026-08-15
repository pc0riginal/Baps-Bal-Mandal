package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.SaffronLight
import com.example.ui.theme.SaffronPrimary
import com.example.viewmodel.Screen

@Composable
fun MoreScreen(
    currentUser: UserProfile?,
    isGujarati: Boolean,
    onToggleLanguage: () -> Unit,
    onLogout: () -> Unit,
    onNavigate: (Screen) -> Unit,
    onUpdateProfile: ((name: String, phone: String, mandalName: String, mandalCity: String, role: String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // User Profile Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("more_profile_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(SaffronLight, SaffronPrimary)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = currentUser?.name?.take(2)?.uppercase() ?: "KM",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = currentUser?.name ?: "Karyakar",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = RoundedCornerShape(100.dp),
                                    color = if (currentUser?.isAdmin == true) SaffronPrimary.copy(alpha = 0.12f) else NavySecondary.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = if (currentUser?.isAdmin == true) "ADMIN" else "KARYAKAR",
                                        color = if (currentUser?.isAdmin == true) SaffronPrimary else NavySecondary,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                            }

                            if (!currentUser?.email.isNullOrBlank()) {
                                Text(
                                    text = currentUser?.email ?: "",
                                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                            Text(
                                text = currentUser?.mandalName?.takeIf { it.isNotBlank() } ?: "BAPS Bal Mandal",
                                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
                            )
                        }

                        IconButton(
                            onClick = { showEditProfileDialog = true },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Mandal",
                                tint = SaffronPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }
        }

        // Section: Mandal Management Actions
        item {
            Text(
                text = "MANDAL & ACTIVITIES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary,
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    // Edit Mandal Info
                    MoreMenuItem(
                        icon = Icons.Default.TempleHindu,
                        title = "Mandal & Center Information",
                        subtitle = currentUser?.mandalName?.takeIf { it.isNotBlank() } ?: "Update Mandal Details",
                        onClick = { showEditProfileDialog = true },
                        testTag = "menu_edit_mandal"
                    )

                    HorizontalDivider(color = BorderSubtleLight.copy(alpha = 0.3f))

                    // Activities & Events Tracker
                    MoreMenuItem(
                        icon = Icons.Default.Celebration,
                        title = "Bal Mandal Activities & Events",
                        subtitle = "Track Quiz, Khel, Shibir, Seva participations",
                        onClick = { onNavigate(Screen.Activities) },
                        testTag = "menu_activities"
                    )

                    HorizontalDivider(color = BorderSubtleLight.copy(alpha = 0.3f))

                    // Karyakar Management (Admin only)
                    MoreMenuItem(
                        icon = Icons.Default.AdminPanelSettings,
                        title = "Manage Karyakars",
                        subtitle = if (currentUser?.isAdmin == true) "Add, edit, or assign karyakars" else "Admin privileges required",
                        badge = if (currentUser?.isAdmin == true) null else "Admin Only",
                        onClick = { onNavigate(Screen.KaryakarManagement) },
                        testTag = "menu_karyakar_management"
                    )
                }
            }
        }

        // Section: App Settings & Preferences
        item {
            Text(
                text = "SETTINGS & PREFERENCES",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = SaffronPrimary,
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
            Spacer(modifier = Modifier.height(4.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    // Language Switch Row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(SaffronPrimary.copy(alpha = 0.1f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Language, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isGujarati) "ભાષા (ગુજરાતી)" else "Language (English)",
                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                                )
                                Text(
                                    text = if (isGujarati) "ગુજરાતી મોડ સક્રિય છે" else "English mode active",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Switch(
                            checked = isGujarati,
                            onCheckedChange = { onToggleLanguage() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = SaffronPrimary,
                                uncheckedThumbColor = Color.White,
                                uncheckedTrackColor = BorderSubtleLight
                            ),
                            modifier = Modifier.testTag("switch_language_more")
                        )
                    }

                    HorizontalDivider(color = BorderSubtleLight.copy(alpha = 0.3f))

                    // BAPS Official Portal link
                    MoreMenuItem(
                        icon = Icons.Default.Info,
                        title = "BAPS Official Portal",
                        subtitle = "Visit baps.org for Pravrutti and Satsang materials",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baps.org"))
                            context.startActivity(intent)
                        },
                        testTag = "menu_baps_org"
                    )

                    HorizontalDivider(color = BorderSubtleLight.copy(alpha = 0.3f))

                    // Contact Support
                    MoreMenuItem(
                        icon = Icons.Default.ContactSupport,
                        title = "Karyakar Help & Support",
                        subtitle = "Help, Guidelines & Bug Reporting",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.baps.org/Contact-Us.aspx"))
                            context.startActivity(intent)
                        },
                        testTag = "menu_support"
                    )
                }
            }
        }

        // Section: Session & Security
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column {
                    MoreMenuItem(
                        icon = Icons.Default.Security,
                        title = "Security & Privacy",
                        subtitle = "Role-based access & Balak data safety",
                        onClick = {},
                        testTag = "menu_privacy"
                    )

                    HorizontalDivider(color = BorderSubtleLight.copy(alpha = 0.3f))

                    // Sign Out
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showLogoutDialog = true }
                            .padding(14.dp)
                            .testTag("button_logout"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AbsentRed.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Logout, contentDescription = null, tint = AbsentRed, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sign Out",
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold, color = AbsentRed)
                            )
                            Text(
                                text = "Sign out from this karyakar account",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }
            }
        }

        // Footer Branding
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "BAPS Bal Mandal App • Version 1.0.0",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
                Text(
                    text = "BAPS Swaminarayan Sanstha",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SaffronPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp
                    )
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Edit Profile & Mandal Dialog
    if (showEditProfileDialog) {
        var editName by remember { mutableStateOf(currentUser?.name ?: "") }
        var editPhone by remember { mutableStateOf(currentUser?.phone ?: "") }
        var editMandalName by remember { mutableStateOf(currentUser?.mandalName ?: "") }
        var editCity by remember { mutableStateOf(currentUser?.mandalCity ?: "") }
        var editRole by remember { mutableStateOf(currentUser?.role ?: "karyakar") }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Update Mandal & Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Full Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editPhone,
                        onValueChange = { editPhone = it },
                        label = { Text("Mobile Number") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editMandalName,
                        onValueChange = { editMandalName = it },
                        label = { Text("Mandal / Mandir Name") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editCity,
                        onValueChange = { editCity = it },
                        label = { Text("City / Region") },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (editName.isNotBlank() && editMandalName.isNotBlank()) {
                            onUpdateProfile?.invoke(
                                editName.trim(),
                                editPhone.trim(),
                                editMandalName.trim(),
                                editCity.trim(),
                                editRole
                            )
                            showEditProfileDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                ) {
                    Text("Save Changes")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Logout Confirmation Dialog
    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Sign Out", fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to sign out from the BAPS Bal Mandal portal?") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed)
                ) {
                    Text("Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun MoreMenuItem(
    icon: ImageVector,
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    badge: String? = null,
    testTag: String = ""
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(14.dp)
            .testTag(testTag),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(SaffronPrimary.copy(alpha = 0.1f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = SaffronPrimary, modifier = Modifier.size(20.dp))
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                if (badge != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = NavySecondary.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = badge,
                            color = NavySecondary,
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 9.sp),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
            )
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = BorderSubtleLight,
            modifier = Modifier.size(20.dp)
        )
    }
}
