with open('/app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

import re

# Replace auth and firestore lazy init with try-catch nullable
content = content.replace(
    "private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }",
    """private val auth: FirebaseAuth? by lazy {
        try { FirebaseAuth.getInstance() } catch (e: Exception) { null }
    }"""
)

content = content.replace(
    "private val firestore: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }",
    """private val firestore: FirebaseFirestore? by lazy {
        try { FirebaseFirestore.getInstance() } catch (e: Exception) { null }
    }"""
)

# Replace auth usages
content = content.replace("auth.addAuthStateListener", "auth?.addAuthStateListener")
content = content.replace("auth.signInWithEmailAndPassword", "auth?.signInWithEmailAndPassword")
content = content.replace("auth.signOut", "auth?.signOut")

# Replace firestore usages with firestore?
content = content.replace("firestore.collection", "firestore?.collection")
content = content.replace("firestore.batch()", "firestore?.batch()")
# Wait, batch() returns WriteBatch? so we need to handle that.
# Actually, I'll rewrite the file safely.
