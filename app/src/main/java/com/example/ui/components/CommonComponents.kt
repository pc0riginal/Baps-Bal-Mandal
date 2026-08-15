package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.AttendanceStatus
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.AbsentRedBg
import com.example.ui.theme.AvatarContainer
import com.example.ui.theme.AvatarText
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.ExcusedBlue
import com.example.ui.theme.ExcusedBlueBg
import com.example.ui.theme.LateOrange
import com.example.ui.theme.LateOrangeBg
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.PresentGreenBg
import com.example.ui.theme.SaffronPrimary

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String? = null,
    badgeText: String? = null,
    icon: ImageVector? = null,
    badgeColor: Color = SaffronPrimary,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    borderColor: Color = BorderSubtleLight.copy(alpha = 0.35f),
    valueColor: Color = MaterialTheme.colorScheme.onSurface,
    modifier: Modifier = Modifier,
    testTag: String = "stat_card",
    onClick: (() -> Unit)? = null
) {
    Card(
        modifier = modifier
            .testTag(testTag)
            .then(if (onClick != null) Modifier.clickable { onClick() } else Modifier),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = containerColor
        ),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.sp
                    )
                )
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(badgeColor.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = badgeColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = valueColor,
                        fontSize = 24.sp
                    )
                )
                if (badgeText != null) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = Color(0xFFDCFCE7)
                    ) {
                        Text(
                            text = badgeText,
                            color = Color(0xFF15803D),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            if (subtitle != null) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}

@Composable
fun StatusBadge(
    status: AttendanceStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon, label) = when (status) {
        AttendanceStatus.PRESENT -> Quad(PresentGreenBg, PresentGreen, Icons.Default.Check, "Present")
        AttendanceStatus.ABSENT -> Quad(AbsentRedBg, AbsentRed, Icons.Default.Close, "Absent")
        AttendanceStatus.LATE -> Quad(LateOrangeBg, LateOrange, Icons.Default.Schedule, "Late")
        AttendanceStatus.EXCUSED -> Quad(ExcusedBlueBg, ExcusedBlue, Icons.Default.Info, "Excused")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        color = bgColor
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = textColor,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
fun BalakAvatar(
    name: String,
    gender: String = "Male",
    size: Int = 40,
    modifier: Modifier = Modifier
) {
    val initials = name.split(" ")
        .mapNotNull { it.firstOrNull()?.toString() }
        .take(2)
        .joinToString("")
        .ifEmpty { "B" }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(AvatarContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = initials,
            color = AvatarText,
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                fontSize = (size * 0.38f).sp
            )
        )
    }
}

@Composable
fun LowAttendanceAlertBanner(
    count: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (count <= 0) return

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("low_attendance_banner")
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF2F2)
        ),
        border = BorderStroke(1.dp, Color(0xFFFCA5A5).copy(alpha = 0.6f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFFEE2E2)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = AbsentRed,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Attention Required",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF991B1B)
                    )
                )
                Text(
                    text = "$count balaks have attendance below 70%. Tap to review & follow up.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = Color(0xFF7F1D1D),
                        fontSize = 12.sp
                    )
                )
            }

            Text(
                text = "View →",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = AbsentRed
                )
            )
        }
    }
}

@Composable
fun mandalTextFieldColors(
    onWhiteCard: Boolean = false
): androidx.compose.material3.TextFieldColors {
    val textColor = if (onWhiteCard) Color(0xFF0F172A) else MaterialTheme.colorScheme.onSurface
    val labelColor = if (onWhiteCard) Color(0xFF475569) else MaterialTheme.colorScheme.onSurfaceVariant
    val placeholderColor = if (onWhiteCard) Color(0xFF94A3B8) else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)

    return androidx.compose.material3.OutlinedTextFieldDefaults.colors(
        focusedTextColor = textColor,
        unfocusedTextColor = textColor,
        disabledTextColor = textColor.copy(alpha = 0.5f),
        focusedLabelColor = SaffronPrimary,
        unfocusedLabelColor = labelColor,
        focusedBorderColor = SaffronPrimary,
        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.6f),
        cursorColor = SaffronPrimary,
        focusedPlaceholderColor = placeholderColor,
        unfocusedPlaceholderColor = placeholderColor,
        focusedLeadingIconColor = SaffronPrimary,
        unfocusedLeadingIconColor = SaffronPrimary,
        focusedTrailingIconColor = SaffronPrimary,
        unfocusedTrailingIconColor = labelColor
    )
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)

