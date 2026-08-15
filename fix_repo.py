with open('/app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

import re

# startListeners usages
content = content.replace("firestore.collection(\"balaks\")", "firestore?.collection(\"balaks\")")
content = content.replace("firestore.collection(\"attendance\")", "firestore?.collection(\"attendance\")")
content = content.replace("firestore.collection(\"sabhas\")", "firestore?.collection(\"sabhas\")")
content = content.replace("firestore.collection(\"activities\")", "firestore?.collection(\"activities\")")
content = content.replace("firestore.collection(\"karyakars\")", "firestore?.collection(\"karyakars\")")

# login/logout usages
content = content.replace("auth.signInWithEmailAndPassword", "auth?.signInWithEmailAndPassword")
content = content.replace("auth.signOut", "auth?.signOut")

# auth?.signInWithEmailAndPassword returns a Task? which requires ?.addOnCompleteListener
content = content.replace(".addOnCompleteListener", "?.addOnCompleteListener")
# Wait, auth.signInWithEmailAndPassword(...).addOnCompleteListener might be split across lines. Let's fix that block manually.
