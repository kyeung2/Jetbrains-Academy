package phonebook

import kotlin.math.sqrt


fun linearSearch(records: Array<Record>, name: String): Boolean =
        records.firstOrNull { it == name } != null

fun binarySearch(records: Array<Record>, name: String): Boolean {

    var left = 0
    var right = records.size - 1
    // base case when left == right, last check
    while (left <= right) {
        val middle = (left + right) / 2
        val currentName = records[middle]

        when {
            name == currentName -> return true
            name > currentName -> {
                left = middle + 1
            }
            else -> {
                right = middle - 1
            }
        }
    }

    return false
}

fun jumpSearch(sortedRecords: Array<Record>, name: String): Boolean {
    val blockSize = sqrt(sortedRecords.size.toDouble()).toInt()
    var previous = 0
    for (i in blockSize until sortedRecords.size step blockSize) {
        if (blockContains(previous, i, sortedRecords, name)) {
            return true
        }
        previous = i
    }

    return blockContains(previous, sortedRecords.size - 1, sortedRecords, name)
}

private fun blockContains(blockStart: Int, blockEnd: Int, sortedRecords: Array<Record>, name: String): Boolean {

    if (sortedRecords[blockEnd] == name) {
        return true
    } else if (sortedRecords[blockEnd] < name) {

        for (j in blockEnd downTo blockStart) {
            if (sortedRecords[j] == name) {
                return true
            }
        }
    }

    return false
}
