import re

with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

old_block = """                    firestore?.collection("users")?.document(user.uid)?.get()?.addOnSuccessListener { doc ->
                        if (doc.exists()) {
                            _currentUser.value = doc.toObject(UserProfile::class.java)
                        } else {
                            val newProfile = UserProfile(
                                uid = user.uid,
                                name = user.displayName ?: user.email?.substringBefore("@")?.replace(".", " ")?.capitalizeWords() ?: "Karyakar",
                                email = user.email ?: "",
                                role = "karyakar",
                                mandalId = "mandal-001"
                            )
                            firestore?.collection("users")?.document(user.uid)?.set(newProfile)
                            _currentUser.value = newProfile
                        }
                        startListeners()
                    }"""

new_block = """                    val fallbackProfile = UserProfile(
                        uid = user.uid,
                        name = user.displayName ?: user.email?.substringBefore("@")?.replace(".", " ")?.capitalizeWords() ?: "Karyakar",
                        email = user.email ?: "",
                        role = "karyakar",
                        mandalId = "mandal-001"
                    )
                    val task = firestore?.collection("users")?.document(user.uid)?.get()
                    if (task != null) {
                        task.addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                _currentUser.value = doc.toObject(UserProfile::class.java)
                            } else {
                                firestore.collection("users").document(user.uid).set(fallbackProfile)
                                _currentUser.value = fallbackProfile
                            }
                            startListeners()
                        }.addOnFailureListener {
                            _currentUser.value = fallbackProfile
                            startListeners()
                        }
                    } else {
                        _currentUser.value = fallbackProfile
                        startListeners()
                    }"""

if old_block in content:
    content = content.replace(old_block, new_block)
    with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'w') as f:
        f.write(content)
    print("Patched successfully")
else:
    print("Old block not found")
