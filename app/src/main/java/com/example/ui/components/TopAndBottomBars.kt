package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.outlined.Assessment
import androidx.compose.material.icons.outlined.CheckCircleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.PeopleOutline
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.ui.theme.AvatarContainer
import com.example.ui.theme.AvatarText
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.SaffronPrimary
import com.example.ui.theme.SurfaceVariantLight
import com.example.viewmodel.Screen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalMandalTopAppBar(
    title: String,
    currentUser: UserProfile?,
    canNavigateBack: Boolean,
    onNavigateBack: () -> Unit,
    isGujarati: Boolean,
    onToggleLanguage: () -> Unit,
    onRoleBadgeClick: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            Column {
                Text(
                    text = (currentUser?.mandalName ?: "BAPS Bal Mandal").uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = SaffronPrimary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp,
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 19.sp
                    ),
                    maxLines = 1
                )
            }
        },
        navigationIcon = {
            if (canNavigateBack) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("top_app_bar_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            } else {
                Spacer(modifier = Modifier.width(16.dp))
            }
        },
        actions = {
            // Gujarati / English switch pill
            Surface(
                shape = RoundedCornerShape(100.dp),
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .testTag("language_toggle_button")
                    .clickable { onToggleLanguage() }
            ) {
                Text(
                    text = if (isGujarati) "ગુજ" else "EN",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // User Avatar Pill
            val initials = currentUser?.name?.split(" ")
                ?.mapNotNull { it.firstOrNull()?.toString() }
                ?.take(2)
                ?.joinToString("") ?: "KM"

            Box(
                modifier = Modifier
                    .padding(end = 16.dp)
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(AvatarContainer)
                    .then(if (onRoleBadgeClick != null) Modifier.clickable { onRoleBadgeClick() } else Modifier)
                    .testTag("user_avatar_top_bar"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = initials,
                    color = AvatarText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background
        )
    )
}

@Composable
fun BalMandalBottomNavigation(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        Triple(Screen.Dashboard, "Home", Pair(Icons.Filled.Home, Icons.Outlined.Home)),
        Triple(Screen.BalaksList, "Balaks", Pair(Icons.Filled.People, Icons.Outlined.PeopleOutline)),
        Triple(Screen.Attendance("2026-08-16"), "Attendance", Pair(Icons.Filled.CheckCircle, Icons.Outlined.CheckCircleOutline)),
        Triple(Screen.Reports, "Reports", Pair(Icons.Filled.Assessment, Icons.Outlined.Assessment)),
        Triple(Screen.More, "More", Pair(Icons.Filled.MoreHoriz, Icons.Outlined.MoreHoriz))
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(SurfaceVariantLight)
            .testTag("bottom_nav_bar")
    ) {
        HorizontalDivider(
            thickness = 1.dp,
            color = BorderSubtleLight.copy(alpha = 0.5f)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            items.forEach { (screen, label, icons) ->
                val isSelected = when (screen) {
                    is Screen.Dashboard -> currentScreen is Screen.Dashboard
                    is Screen.BalaksList -> currentScreen is Screen.BalaksList || currentScreen is Screen.BalakDetail || currentScreen is Screen.AddEditBalak
                    is Screen.Attendance -> currentScreen is Screen.Attendance
                    is Screen.Reports -> currentScreen is Screen.Reports
                    is Screen.More -> currentScreen is Screen.More || currentScreen is Screen.KaryakarManagement || currentScreen is Screen.Activities
                    else -> false
                }

                Column(
                    modifier = Modifier
                        .clickable { onNavigate(screen) }
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                        .testTag("nav_item_${label.lowercase()}"),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(if (isSelected) SaffronPrimary.copy(alpha = 0.18f) else Color.Transparent)
                            .padding(horizontal = 16.dp, vertical = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) icons.first else icons.second,
                            contentDescription = label,
                            tint = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 10.sp,
                            color = if (isSelected) SaffronPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }
        }
    }
}

