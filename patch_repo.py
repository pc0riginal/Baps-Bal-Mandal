import re

with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

add_balak_old = """        firestore?.collection("balaks")?.document(newId)?.set(newBalak)
        return newId"""
add_balak_new = """        firestore?.collection("balaks")?.document(newId)?.set(newBalak)?.addOnFailureListener { Log.e("Repo", "Failed to add Balak", it) }
        _balaks.value = _balaks.value + newBalak
        return newId"""
content = content.replace(add_balak_old, add_balak_new)

update_balak_old = """        firestore?.collection("balaks")?.document(withTimestamp.id)?.set(withTimestamp)"""
update_balak_new = """        firestore?.collection("balaks")?.document(withTimestamp.id)?.set(withTimestamp)?.addOnFailureListener { Log.e("Repo", "Failed to update Balak", it) }
        _balaks.value = _balaks.value.map { if (it.id == withTimestamp.id) withTimestamp else it }"""
content = content.replace(update_balak_old, update_balak_new)

delete_balak_old = """    fun deleteBalak(id: String) {
        firestore?.collection("balaks")?.document(id)?.delete()
    }"""
delete_balak_new = """    fun deleteBalak(id: String) {
        firestore?.collection("balaks")?.document(id)?.delete()?.addOnFailureListener { Log.e("Repo", "Failed to delete Balak", it) }
        _balaks.value = _balaks.value.filter { it.id != id }
    }"""
content = content.replace(delete_balak_old, delete_balak_new)


with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'w') as f:
    f.write(content)
print("Patched repository")
