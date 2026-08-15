package com.example.ui.screens

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.DashboardStats
import com.example.data.models.UserProfile
import com.example.ui.components.BalakAvatar
import com.example.ui.components.LowAttendanceAlertBanner
import com.example.ui.components.StatCard
import com.example.ui.theme.AvatarContainer
import com.example.ui.theme.AvatarText
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.SaffronPrimary
import com.example.viewmodel.Screen

@Composable
fun DashboardScreen(
    stats: DashboardStats,
    currentUser: UserProfile?,
    isGujarati: Boolean,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Header
        item {
            Spacer(modifier = Modifier.height(2.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dashboard_welcome_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    modifier = Modifier.padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val initials = currentUser?.name?.split(" ")
                        ?.mapNotNull { it.firstOrNull()?.toString() }
                        ?.take(2)
                        ?.joinToString("") ?: "KM"

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(AvatarContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initials,
                            color = AvatarText,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isGujarati) "બાળ મંડળ" else "BAPS BAL MANDAL",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = SaffronPrimary,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = if (isGujarati) "જય સ્વામિનારાયણ, ${currentUser?.name?.substringBefore(" ") ?: "મહેશ"} 👋" 
                                   else "Jai Swaminarayan, ${currentUser?.name?.substringBefore(" ") ?: "Mahesh"} 👋",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 18.sp
                            )
                        )
                        Text(
                            text = currentUser?.mandalName ?: "BAPS Swaminarayan Mandir",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // Low Attendance Alert Banner if any balaks below 70%
        if (stats.lowAttendanceCount > 0) {
            item {
                LowAttendanceAlertBanner(
                    count = stats.lowAttendanceCount,
                    onClick = {
                        onNavigate(Screen.BalaksList)
                    }
                )
            }
        }

        // 4 Summary Metrics (2x2 Grid with Professional Polish Styling)
        item {
            Text(
                text = if (isGujarati) "મંડળ આંકડા" else "Overview",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = if (isGujarati) "કુલ બાલકો" else "Total Balaks",
                        value = "${stats.totalBalaks}",
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = BorderSubtleLight.copy(alpha = 0.35f),
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_total_balaks",
                        onClick = { onNavigate(Screen.BalaksList) }
                    )
                    StatCard(
                        title = if (isGujarati) "હાજર બાલકો" else "Present Today",
                        value = "${stats.presentToday}",
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = BorderSubtleLight.copy(alpha = 0.35f),
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_present_today",
                        onClick = { onNavigate(Screen.Attendance("2026-08-16")) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = if (isGujarati) "સરેરાશ હાજરી" else "Avg. Attendance",
                        value = "${stats.attendancePercentage}%",
                        containerColor = Color(0xFFFEF7FF),
                        borderColor = SaffronPrimary.copy(alpha = 0.25f),
                        valueColor = SaffronPrimary,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_attendance_percentage",
                        onClick = { onNavigate(Screen.Reports) }
                    )
                    StatCard(
                        title = if (isGujarati) "નવા બાલકો" else "New Balaks",
                        value = "${stats.newBalaksCount}",
                        badgeText = "+12%",
                        containerColor = MaterialTheme.colorScheme.surface,
                        borderColor = BorderSubtleLight.copy(alpha = 0.35f),
                        valueColor = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.weight(1f),
                        testTag = "stat_new_balaks",
                        onClick = { onNavigate(Screen.BalaksList) }
                    )
                }
            }
        }

        // Quick Actions Section (from design)
        item {
            Text(
                text = if (isGujarati) "ઝડપી ક્રિયાઓ" else "Quick Actions",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Balak Action
                Surface(
                    onClick = { onNavigate(Screen.AddEditBalak()) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_add_balak"),
                    shape = RoundedCornerShape(24.dp),
                    color = SaffronPrimary,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isGujarati) "+ બાલક ઉમેરો" else "Add Balak",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        )
                    }
                }

                // Mark Attendance Action
                Surface(
                    onClick = { onNavigate(Screen.Attendance("2026-08-16")) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("quick_action_mark_attendance"),
                    shape = RoundedCornerShape(24.dp),
                    color = NavySecondary,
                    shadowElevation = 0.dp
                ) {
                    Column(
                        modifier = Modifier.padding(vertical = 16.dp, horizontal = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(28.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isGujarati) "હાજરી પૂરો" else "Attendance",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(Screen.BalaksList) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quick_action_view_balaks"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.5f))
                ) {
                    Text(
                        text = if (isGujarati) "બાલકો જુઓ" else "View Balaks",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }

                OutlinedButton(
                    onClick = { onNavigate(Screen.Reports) },
                    modifier = Modifier
                        .weight(1f)
                        .height(44.dp)
                        .testTag("quick_action_reports"),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.5f))
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SaffronPrimary
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isGujarati) "રિપોર્ટ" else "Reports",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    )
                }
            }
        }

        // Upcoming Sabha Card (matching the exact Design HTML)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("upcoming_sabha_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text(
                                text = if (isGujarati) "આગામી બાળ સભા" else "Upcoming Sabha",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Sunday • 5:00 PM",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp
                                )
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(100.dp),
                            color = SaffronPrimary.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = "LIVE SOON",
                                color = SaffronPrimary,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Topic and Location 2-column split
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "TOPIC",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stats.upcomingSabha?.topic?.ifBlank { "Ahnik Vidhi" } ?: "Ahnik Vidhi",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }

                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .height(32.dp)
                                .background(BorderSubtleLight.copy(alpha = 0.4f))
                        )

                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(vertical = 4.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "LOCATION",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    letterSpacing = 0.5.sp
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stats.upcomingSabha?.location?.ifBlank { "BAPS Mandir" } ?: "BAPS Mandir",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { onNavigate(Screen.Attendance("2026-08-16")) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("upcoming_sabha_mark_attendance_button"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isGujarati) "હાજરી શરૂ કરો" else "Mark Sabha Attendance",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

