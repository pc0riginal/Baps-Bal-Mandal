with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

sabha_old = """        firestore?.collection("sabhas")?.document(latestSabha.id)?.set(latestSabha)
    }"""
sabha_new = """        firestore?.collection("sabhas")?.document(latestSabha.id)?.set(latestSabha)?.addOnFailureListener { Log.e("Repo", "Failed to update Sabha", it) }
        val currentSabhas = _sabhas.value.filter { it.id != latestSabha.id }.toMutableList()
        currentSabhas.add(latestSabha)
        _sabhas.value = currentSabhas.sortedByDescending { it.date }
    }"""
content = content.replace(sabha_old, sabha_new)

activity_old = """    fun addActivity(activity: MandalActivity) {
        val newId = if (activity.id.isNotBlank()) activity.id else "act-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        firestore?.collection("activities")?.document(newId)?.set(activity.copy(id = newId, mandalId = mandalId))
    }"""
activity_new = """    fun addActivity(activity: MandalActivity) {
        val newId = if (activity.id.isNotBlank()) activity.id else "act-${UUID.randomUUID().toString().take(8)}"
        val mandalId = _currentUser.value?.mandalId ?: "mandal-001"
        val newAct = activity.copy(id = newId, mandalId = mandalId)
        firestore?.collection("activities")?.document(newId)?.set(newAct)?.addOnFailureListener { Log.e("Repo", "Failed to add activity", it) }
        _activities.value = _activities.value + newAct
    }"""
content = content.replace(activity_old, activity_new)

with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'w') as f:
    f.write(content)
print("Patched sabha and activity")
