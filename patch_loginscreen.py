with open('/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

# Add phone auth states
state_declarations = """    var email by remember { mutableStateOf("mahesh.patel@baps.org") }
    var password by remember { mutableStateOf("swaminarayan123") }
    var phoneNumber by remember { mutableStateOf("+1") }
    var verificationCode by remember { mutableStateOf("") }
    var isPhoneMode by remember { mutableStateOf(false) }
    var codeSent by remember { mutableStateOf(false) }
    var verificationId by remember { mutableStateOf("") }
"""
content = content.replace('    var email by remember { mutableStateOf("mahesh.patel@baps.org") }', state_declarations)
content = content.replace('    var password by remember { mutableStateOf("swaminarayan123") }', '')

# Replace Login Card content
import re
new_login_card = """
                    // Toggle Buttons
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                        OutlinedButton(onClick = { isPhoneMode = false }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = if (!isPhoneMode) SaffronPrimary else Color.Gray), border = BorderStroke(1.dp, if (!isPhoneMode) SaffronPrimary else Color.LightGray)) {
                            Text("Email")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        OutlinedButton(onClick = { isPhoneMode = true }, modifier = Modifier.weight(1f), colors = ButtonDefaults.outlinedButtonColors(contentColor = if (isPhoneMode) SaffronPrimary else Color.Gray), border = BorderStroke(1.dp, if (isPhoneMode) SaffronPrimary else Color.LightGray)) {
                            Text("Phone")
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    if (!isPhoneMode) {
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it; errorMessage = null },
                            label = { Text("Email") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it; errorMessage = null },
                            label = { Text("Password") },
                            visualTransformation = PasswordVisualTransformation(),
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { onLogin(email, password) },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                        ) {
                            Text("Sign In with Email")
                        }
                    } else {
                        if (!codeSent) {
                            OutlinedTextField(
                                value = phoneNumber,
                                onValueChange = { phoneNumber = it; errorMessage = null },
                                label = { Text("Phone Number (+1...)") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onSendCode(phoneNumber) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Text("Send Code")
                            }
                        } else {
                            OutlinedTextField(
                                value = verificationCode,
                                onValueChange = { verificationCode = it; errorMessage = null },
                                label = { Text("Verification Code") },
                                modifier = Modifier.fillMaxWidth(),
                                singleLine = true
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { onVerifyCode(verificationId, verificationCode) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = SaffronPrimary)
                            ) {
                                Text("Verify & Login")
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    Text("OR", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    OutlinedButton(
                        onClick = { onGoogleSignIn() },
                        modifier = Modifier.fillMaxWidth().height(48.dp)
                    ) {
                        Text("Sign In with Google", color = MaterialTheme.colorScheme.onSurface)
                    }
"""

# Very simple replace inside LoginScreen:
# I will just write a new LoginScreen.kt because it's too much to regex safely
