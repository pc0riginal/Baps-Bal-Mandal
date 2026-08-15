package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Balak
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBalakScreen(
    initialBalak: Balak?,
    isGujarati: Boolean,
    onSave: (Balak) -> Unit,
    onDelete: ((String) -> Unit)?,
    modifier: Modifier = Modifier
) {
    var firstName by remember { mutableStateOf(initialBalak?.firstName ?: "") }
    var lastName by remember { mutableStateOf(initialBalak?.lastName ?: "") }
    var dateOfBirth by remember { mutableStateOf(initialBalak?.dateOfBirth ?: "") }
    var age by remember { mutableIntStateOf(initialBalak?.age ?: 10) }
    var gender by remember { mutableStateOf(initialBalak?.gender ?: "Male") }
    var standard by remember { mutableIntStateOf(initialBalak?.standard ?: 5) }
    var parentName by remember { mutableStateOf(initialBalak?.parentName ?: "") }
    var parentMobile by remember { mutableStateOf(initialBalak?.parentMobile ?: "") }
    var address by remember { mutableStateOf(initialBalak?.address ?: "") }
    var school by remember { mutableStateOf(initialBalak?.school ?: "") }
    var bloodGroup by remember { mutableStateOf(initialBalak?.bloodGroup ?: "B+") }
    var notes by remember { mutableStateOf(initialBalak?.notes ?: "") }
    var interests by remember { mutableStateOf(initialBalak?.interests ?: "") }
    var skills by remember { mutableStateOf(initialBalak?.skills ?: "") }
    var assignedKaryakar by remember { mutableStateOf(initialBalak?.assignedKaryakar ?: "") }
    var active by remember { mutableStateOf(initialBalak?.active ?: true) }

    var standardDropdownExpanded by remember { mutableStateOf(false) }
    var genderDropdownExpanded by remember { mutableStateOf(false) }
    var bloodGroupDropdownExpanded by remember { mutableStateOf(false) }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    val isEditing = initialBalak != null && initialBalak.id.isNotBlank()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Form Title
        Column {
            Text(
                text = "BALAK MANAGEMENT",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = SaffronPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp,
                    fontSize = 10.sp
                )
            )
            Text(
                text = if (isEditing) (if (isGujarati) "બાલકની માહિતી સુધારો" else "Edit Balak Profile")
                       else (if (isGujarati) "નવો બાલક નોંધણી" else "Add New Balak"),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
        }

        // Section 1: Basic Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = firstName,
                        onValueChange = { firstName = it },
                        label = { Text("First Name *") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_first_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                        )
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = { lastName = it },
                        label = { Text("Last Name *") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_last_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Standard Dropdown
                    ExposedDropdownMenuBox(
                        expanded = standardDropdownExpanded,
                        onExpandedChange = { standardDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = "Std. $standard",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Standard *") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = standardDropdownExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("dropdown_standard"),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = standardDropdownExpanded,
                            onDismissRequest = { standardDropdownExpanded = false }
                        ) {
                            (1..10).forEach { std ->
                                DropdownMenuItem(
                                    text = { Text("Std. $std") },
                                    onClick = {
                                        standard = std
                                        age = std + 5
                                        standardDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Age Field
                    OutlinedTextField(
                        value = age.toString(),
                        onValueChange = { age = it.toIntOrNull() ?: age },
                        label = { Text("Age *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_age"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = SaffronPrimary,
                            unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                        )
                    )
                }

                // Date of Birth
                OutlinedTextField(
                    value = dateOfBirth,
                    onValueChange = { dateOfBirth = it },
                    label = { Text("Date of Birth (YYYY-MM-DD)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_dob"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )

                // Gender & Blood Group
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ExposedDropdownMenuBox(
                        expanded = genderDropdownExpanded,
                        onExpandedChange = { genderDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = gender,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Gender") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = genderDropdownExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = genderDropdownExpanded,
                            onDismissRequest = { genderDropdownExpanded = false }
                        ) {
                            listOf("Male", "Female").forEach { g ->
                                DropdownMenuItem(
                                    text = { Text(g) },
                                    onClick = {
                                        gender = g
                                        genderDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    ExposedDropdownMenuBox(
                        expanded = bloodGroupDropdownExpanded,
                        onExpandedChange = { bloodGroupDropdownExpanded = it },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = bloodGroup,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Blood Group") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupDropdownExpanded) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable),
                            shape = RoundedCornerShape(14.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = SaffronPrimary,
                                unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                            )
                        )
                        ExposedDropdownMenu(
                            expanded = bloodGroupDropdownExpanded,
                            onDismissRequest = { bloodGroupDropdownExpanded = false }
                        ) {
                            listOf("A+", "A-", "B+", "B-", "O+", "O-", "AB+", "AB-").forEach { bg ->
                                DropdownMenuItem(
                                    text = { Text(bg) },
                                    onClick = {
                                        bloodGroup = bg
                                        bloodGroupDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 2: Parent & Contact Info
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "Parent & Contact Details",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                )

                OutlinedTextField(
                    value = parentName,
                    onValueChange = { parentName = it },
                    label = { Text("Parent / Guardian Name *") },
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = SaffronPrimary) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_parent_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )

                OutlinedTextField(
                    value = parentMobile,
                    onValueChange = { parentMobile = it },
                    label = { Text("Parent Mobile Number *") },
                    leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = SaffronPrimary) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_parent_mobile"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Residential Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_address"),
                    maxLines = 2,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )
            }
        }

        // Section 3: Academic & Notes
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, BorderSubtleLight.copy(alpha = 0.35f)),
            elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
        ) {
            Column(
                modifier = Modifier.padding(18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Text(
                    text = "School & Special Observations",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                )

                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("School Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )

                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Interests & Hobbies (e.g. Kirtan, Quiz, Cricket)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Karyakar Notes & Observations") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_notes"),
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SaffronPrimary,
                        unfocusedBorderColor = BorderSubtleLight.copy(alpha = 0.4f)
                    )
                )

                // Active toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Active Balak Status",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (active) "Regularly attending sabha" else "Inactive / Temporarily relocated",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )
                    }

                    Switch(
                        checked = active,
                        onCheckedChange = { active = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = SaffronPrimary),
                        modifier = Modifier.testTag("switch_active_status")
                    )
                }
            }
        }

        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Save & Delete Buttons
        Button(
            onClick = {
                if (firstName.isBlank()) {
                    errorMessage = "Please enter First Name"
                } else if (parentMobile.isBlank()) {
                    errorMessage = "Please enter Parent Mobile Number"
                } else {
                    val balakToSave = (initialBalak ?: Balak()).copy(
                        firstName = firstName.trim(),
                        lastName = lastName.trim(),
                        dateOfBirth = dateOfBirth.trim(),
                        age = age,
                        gender = gender,
                        standard = standard,
                        parentName = parentName.trim(),
                        parentMobile = parentMobile.trim(),
                        address = address.trim(),
                        school = school.trim(),
                        bloodGroup = bloodGroup,
                        notes = notes.trim(),
                        interests = interests.trim(),
                        skills = skills.trim(),
                        assignedKaryakar = assignedKaryakar,
                        active = active
                    )
                    onSave(balakToSave)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("button_save_balak"),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (isEditing) "Update Balak" else "Save Balak",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White)
            )
        }

        if (isEditing && onDelete != null) {
            OutlinedButton(
                onClick = { onDelete(initialBalak.id) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("button_delete_balak"),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AbsentRed.copy(alpha = 0.5f)),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AbsentRed)
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = AbsentRed, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Delete / Remove Balak", color = AbsentRed, fontWeight = FontWeight.SemiBold)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

