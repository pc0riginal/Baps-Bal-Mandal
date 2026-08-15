import re

with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'r') as f:
    content = f.read()

attendance_old = """        batch.commit()
        
        // Also update Sabha session summary"""

attendance_new = """        batch.commit().addOnFailureListener { Log.e("Repo", "Failed to commit attendance", it) }
        
        // Optimistic update of attendance records
        val newRecords = mutableListOf<AttendanceRecord>()
        for ((balakId, status) in statusMap) {
            val note = notesMap[balakId] ?: ""
            val id = "att-${date}-${balakId}"
            val record = AttendanceRecord(
                id = id,
                balakId = balakId,
                sabhaId = sabhaId,
                date = date,
                status = status.name,
                note = note,
                markedAt = System.currentTimeMillis(),
                markedBy = markedByName,
                mandalId = mandalId
            )
            newRecords.add(record)
        }
        val currentRecords = _attendanceRecords.value.filter { it.date != date }.toMutableList()
        currentRecords.addAll(newRecords)
        _attendanceRecords.value = currentRecords

        // Also update Sabha session summary"""

content = content.replace(attendance_old, attendance_new)

with open('app/src/main/java/com/example/data/repository/BalMandalRepository.kt', 'w') as f:
    f.write(content)
print("Patched attendance")
