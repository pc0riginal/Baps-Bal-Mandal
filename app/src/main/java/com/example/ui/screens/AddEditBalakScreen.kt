package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.Balak
import com.example.ui.components.mandalTextFieldColors
import com.example.ui.theme.AbsentRed
import com.example.ui.theme.BorderSubtleLight
import com.example.ui.theme.PresentGreen
import com.example.ui.theme.SaffronPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBalakScreen(
    initialBalak: Balak? = null,
    isGujarati: Boolean = false,
    onSave: (Balak) -> Unit,
    onDelete: ((String) -> Unit)? = null,
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
    var showDeleteConfirm by remember { mutableStateOf(false) }

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
                        onValueChange = {
                            firstName = it
                            errorMessage = null
                        },
                        label = { Text("First Name *") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_first_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = mandalTextFieldColors()
                    )

                    OutlinedTextField(
                        value = lastName,
                        onValueChange = {
                            lastName = it
                            errorMessage = null
                        },
                        label = { Text("Last Name *") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_last_name"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                        colors = mandalTextFieldColors()
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
                            colors = mandalTextFieldColors()
                        )
                        ExposedDropdownMenu(
                            expanded = standardDropdownExpanded,
                            onDismissRequest = { standardDropdownExpanded = false }
                        ) {
                            (1..12).forEach { std ->
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

                    // Gender Dropdown
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
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                                .testTag("dropdown_gender"),
                            shape = RoundedCornerShape(14.dp),
                            colors = mandalTextFieldColors()
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
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = dateOfBirth,
                        onValueChange = { dateOfBirth = it },
                        label = { Text("Date of Birth (YYYY-MM-DD)") },
                        placeholder = { Text("2015-05-15") },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_dob"),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
                    )

                    OutlinedTextField(
                        value = if (age > 0) age.toString() else "",
                        onValueChange = { age = it.toIntOrNull() ?: age },
                        label = { Text("Age") },
                        modifier = Modifier
                            .weight(0.6f)
                            .testTag("input_age"),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
                    )
                }

                OutlinedTextField(
                    value = school,
                    onValueChange = { school = it },
                    label = { Text("School Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_school"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = mandalTextFieldColors()
                )

                // Blood Group Dropdown
                ExposedDropdownMenuBox(
                    expanded = bloodGroupDropdownExpanded,
                    onExpandedChange = { bloodGroupDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = bloodGroup,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Blood Group") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = bloodGroupDropdownExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                            .testTag("dropdown_blood_group"),
                        shape = RoundedCornerShape(14.dp),
                        colors = mandalTextFieldColors()
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

        // Section 2: Guardian Details
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
                    text = "Guardian & Contact Details",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                )

                OutlinedTextField(
                    value = parentName,
                    onValueChange = {
                        parentName = it
                        errorMessage = null
                    },
                    label = { Text("Parent / Guardian Name *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_parent_name"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words),
                    colors = mandalTextFieldColors()
                )

                OutlinedTextField(
                    value = parentMobile,
                    onValueChange = {
                        parentMobile = it
                        errorMessage = null
                    },
                    label = { Text("Parent Mobile (WhatsApp) *") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_parent_mobile"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = mandalTextFieldColors()
                )

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Residential Address") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_address"),
                    minLines = 2,
                    maxLines = 3,
                    shape = RoundedCornerShape(14.dp),
                    colors = mandalTextFieldColors()
                )
            }
        }

        // Section 3: Mandal & Activities
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
                    text = "Mandal & Talents",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = SaffronPrimary
                    )
                )

                OutlinedTextField(
                    value = interests,
                    onValueChange = { interests = it },
                    label = { Text("Interests & Hobbies") },
                    placeholder = { Text("e.g. Kirtan, Satsang Quiz, Drawing, Cricket") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_interests"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = mandalTextFieldColors()
                )

                OutlinedTextField(
                    value = skills,
                    onValueChange = { skills = it },
                    label = { Text("Skills & Mukhpath") },
                    placeholder = { Text("e.g. Vachanamrut Mukhpath, Speech, Singing") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_skills"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = mandalTextFieldColors()
                )

                OutlinedTextField(
                    value = assignedKaryakar,
                    onValueChange = { assignedKaryakar = it },
                    label = { Text("Assigned Karyakar") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_assigned_karyakar"),
                    singleLine = true,
                    shape = RoundedCornerShape(14.dp),
                    colors = mandalTextFieldColors()
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Special Notes / Remarks") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_notes"),
                    minLines = 2,
                    maxLines = 4,
                    shape = RoundedCornerShape(14.dp),
                    colors = mandalTextFieldColors()
                )

                // Active Status Switch
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Active Status",
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )
                        Text(
                            text = if (active) "Balak is actively attending" else "Balak is inactive / paused",
                            style = MaterialTheme.typography.labelSmall.copy(color = if (active) PresentGreen else Color.Gray)
                        )
                    }
                    Switch(
                        checked = active,
                        onCheckedChange = { active = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = SaffronPrimary,
                            checkedTrackColor = SaffronPrimary.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("switch_active")
                    )
                }
            }
        }

        // Error message banner
        if (errorMessage != null) {
            Text(
                text = errorMessage ?: "",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        // Save & Action Buttons
        Button(
            onClick = {
                if (firstName.isBlank()) {
                    errorMessage = "Please enter balak's first name."
                } else if (lastName.isBlank()) {
                    errorMessage = "Please enter balak's last name."
                } else if (parentMobile.isBlank()) {
                    errorMessage = "Please enter parent's contact number."
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
                        interests = interests.trim(),
                        skills = skills.trim(),
                        assignedKaryakar = assignedKaryakar.trim(),
                        notes = notes.trim(),
                        active = active
                    )
                    onSave(balakToSave)
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 50.dp)
                .testTag("button_save_balak"),
            shape = RoundedCornerShape(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = SaffronPrimary,
                contentColor = Color.White
            )
        ) {
            Icon(Icons.Default.Check, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isEditing) "Save Changes" else "Register Balak",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                maxLines = 1
            )
        }

        if (isEditing && onDelete != null && initialBalak != null) {
            OutlinedButton(
                onClick = { showDeleteConfirm = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .testTag("button_delete_balak"),
                shape = RoundedCornerShape(16.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 14.dp, vertical = 10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = AbsentRed),
                border = BorderStroke(1.dp, AbsentRed.copy(alpha = 0.5f))
            ) {
                Icon(Icons.Default.Delete, contentDescription = null, tint = AbsentRed)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Delete Balak", color = AbsentRed, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, maxLines = 1)
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    if (showDeleteConfirm && initialBalak != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Balak Profile") },
            text = { Text("Are you sure you want to delete ${initialBalak.fullName}? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete?.invoke(initialBalak.id)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AbsentRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
