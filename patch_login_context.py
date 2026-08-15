with open('/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'r') as f:
    content = f.read()

import re

# Add ContextWrapper import
content = content.replace(
    "import androidx.compose.ui.platform.LocalContext",
    "import androidx.compose.ui.platform.LocalContext\nimport android.content.Context\nimport android.content.ContextWrapper"
)

# Add findActivity helper at the bottom
helper = """
tailrec fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
"""
content += helper

# Fix the context check
old_check = """                                        if (context is Activity) {
                                            onSendPhoneCode(context, phoneNumber)
                                        } else {
                                            errorMessage = "Cannot send code: context is not an Activity."
                                        }"""
new_check = """                                        val activity = context.findActivity()
                                        if (activity != null) {
                                            onSendPhoneCode(activity, phoneNumber)
                                        } else {
                                            errorMessage = "Cannot send code: Activity not found."
                                        }"""
content = content.replace(old_check, new_check)

with open('/app/src/main/java/com/example/ui/screens/LoginScreen.kt', 'w') as f:
    f.write(content)
