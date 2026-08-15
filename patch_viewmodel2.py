import re

with open('app/src/main/java/com/example/viewmodel/BalMandalViewModel.kt', 'r') as f:
    content = f.read()

old_request = """                        com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption.Builder(com.example.BuildConfig.WEB_CLIENT_ID)
                            .build()"""

new_request = """                        com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption.Builder(com.example.BuildConfig.WEB_CLIENT_ID)
                            .setFilterByAuthorizedAccounts(false)
                            .build()"""

content = content.replace(old_request, new_request)

with open('app/src/main/java/com/example/viewmodel/BalMandalViewModel.kt', 'w') as f:
    f.write(content)
