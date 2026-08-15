import re

with open('/app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

new_methods = """
    fun signInWithGoogle(idToken: String, onComplete: (Boolean, String?) -> Unit) {
        val credential = com.google.firebase.auth.GoogleAuthProvider.getCredential(idToken, null)
        signInWithCredential(credential, onComplete)
    }

    fun signInWithCredential(credential: com.google.firebase.auth.AuthCredential, onComplete: (Boolean, String?) -> Unit) {
        auth?.signInWithCredential(credential)
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onComplete(true, null)
                } else {
                    onComplete(false, task.exception?.message ?: "Login failed")
                }
            } ?: onComplete(false, "Firebase not initialized. Add google-services.json via Secrets.")
    }

    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {
"""

content = content.replace("    fun login(email: String, password: String, onComplete: (Boolean, String?) -> Unit) {", new_methods)

with open('/app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'w') as f:
    f.write(content)
