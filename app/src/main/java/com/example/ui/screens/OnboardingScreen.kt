package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationCity
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TempleHindu
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.UserProfile
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.NavySecondary
import com.example.ui.theme.SaffronDark
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(
    currentUser: UserProfile?,
    onCompleteOnboarding: (name: String, phone: String, mandalName: String, mandalCity: String, role: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf(currentUser?.name?.takeIf { it.isNotBlank() && it != "Karyakar" } ?: "") }
    var phone by remember { mutableStateOf(currentUser?.phone ?: "") }
    var mandalName by remember { mutableStateOf(currentUser?.mandalName ?: "") }
    var mandalCity by remember { mutableStateOf(currentUser?.mandalCity ?: "") }
    var selectedRole by remember { mutableStateOf(if (currentUser?.isAdmin == true) "admin" else "karyakar") }
    var roleDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val roles = listOf(
        "karyakar" to "Bal Mandal Karyakar",
        "admin" to "Sanchalak (Mandal Admin)"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        SaffronPrimary,
                        SaffronDark,
                        Color(0xFF2B1408)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            // Divine Emblem
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)
                        .background(Color.White),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "ॐ",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "Jai Swaminarayan! 🙏",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                ),
                textAlign = TextAlign.Center
            )

            Text(
                text = "Let's set up your Bal Mandal information to get started.",
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = Color.White.copy(alpha = 0.88f)
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("onboarding_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Karyakar & Mandal Setup",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NavySecondary
                        )
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. Karyakar Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            errorMessage = null
                        },
                        label = { Text("Your Full Name *") },
                        placeholder = { Text("e.g. Bipin Patel") },
                        leadingIcon = {
                            Icon(Icons.Default.AccountCircle, contentDescription = null, tint = SaffronPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 2. Phone Number
                    OutlinedTextField(
                        value = phone,
                        onValueChange = {
                            phone = it
                            errorMessage = null
                        },
                        label = { Text("Mobile / WhatsApp Number *") },
                        placeholder = { Text("e.g. +91 98250 12345") },
                        leadingIcon = {
                            Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 3. Mandal / Mandir Name
                    OutlinedTextField(
                        value = mandalName,
                        onValueChange = {
                            mandalName = it
                            errorMessage = null
                        },
                        label = { Text("Bal Mandal / Mandir Name *") },
                        placeholder = { Text("e.g. BAPS Swaminarayan Mandir") },
                        leadingIcon = {
                            Icon(Icons.Default.TempleHindu, contentDescription = null, tint = SaffronPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4. City / Kshetra
                    OutlinedTextField(
                        value = mandalCity,
                        onValueChange = {
                            mandalCity = it
                            errorMessage = null
                        },
                        label = { Text("City / Region / Kshetra *") },
                        placeholder = { Text("e.g. Surat, Rajkot, London, etc.") },
                        leadingIcon = {
                            Icon(Icons.Default.LocationCity, contentDescription = null, tint = SaffronPrimary)
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.5f)
                        )
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // 5. Role Selector
                    ExposedDropdownMenuBox(
                        expanded = roleDropdownExpanded,
                        onExpandedChange = { roleDropdownExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = roles.find { it.first == selectedRole }?.second ?: "Bal Mandal Karyakar",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Your Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleDropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.5f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = roleDropdownExpanded,
                            onDismissRequest = { roleDropdownExpanded = false }
                        ) {
                            roles.forEach { (roleKey, roleLabel) ->
                                DropdownMenuItem(
                                    text = { Text(roleLabel) },
                                    onClick = {
                                        selectedRole = roleKey
                                        roleDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    if (errorMessage != null) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = errorMessage ?: "",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodySmall,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Submit Button
                    Button(
                        onClick = {
                            if (name.isBlank()) {
                                errorMessage = "Please enter your Full Name"
                            } else if (phone.isBlank()) {
                                errorMessage = "Please enter your Mobile Number"
                            } else if (mandalName.isBlank()) {
                                errorMessage = "Please enter your Bal Mandal or Mandir Name"
                            } else if (mandalCity.isBlank()) {
                                errorMessage = "Please enter your City or Region"
                            } else {
                                val fullMandalDisplayName = if (mandalName.contains(mandalCity, ignoreCase = true)) {
                                    mandalName.trim()
                                } else {
                                    "${mandalName.trim()} - ${mandalCity.trim()}"
                                }
                                onCompleteOnboarding(
                                    name.trim(),
                                    phone.trim(),
                                    fullMandalDisplayName,
                                    mandalCity.trim(),
                                    selectedRole
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("button_complete_onboarding"),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Save & Launch Dashboard",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "BAPS Swaminarayan Sanstha • Bal Pravrutti",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.75f),
                    fontSize = 11.sp
                )
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
