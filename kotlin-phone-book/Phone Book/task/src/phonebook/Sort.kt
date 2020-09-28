package phonebook

import java.time.Duration

fun bubbleSort(records: Array<Record>, timeLimit: Duration): Pair<Duration, Boolean> {
    val start = System.currentTimeMillis()

    var previousDuration = duration(start)
    for (i in records.indices) {
        for (j in 0 until records.size - i - 1) {
            val a = records[j]
            val b = records[j + 1]
            if (a > b) {
                records[j] = b
                records[j + 1] = a
            }
        }

        if (i % 1000 == 0) {
//            val duration = duration(start)
//            println("$i :: " + duration.prettyPrint() + " difference ::" + (duration - previousDuration).prettyPrint())
//            previousDuration = duration

            // checking less often
            val currentDuration = duration(start)
            if (currentDuration > timeLimit) {
                return Pair(currentDuration, false)
            }
        }
    }
    return Pair(duration(start), true)
}

fun quickSort(records: Array<Record>): Duration {
    val start = System.currentTimeMillis()
    quickSort(records, 0, records.size - 1)
    return duration(start)
}

// implementation taken from https://chercher.tech/kotlin/quick-sort-kotlin, it contains a very short implementation if in-place sorting not used
private fun quickSort(records: Array<Record>, left: Int, right: Int) {
    val pivotIndex = partition(records, left, right)
    if (left < pivotIndex - 1) { // 2) Sorting left half
        quickSort(records, left, pivotIndex - 1)
    }
    if (pivotIndex < right) { // 3) Sorting right half
        quickSort(records, pivotIndex, right)
    }
}

private fun partition(records: Array<Record>, left: Int, right: Int): Int {
    var leftIndex = left
    var rightIndex = right
    val pivot = records[(left + right) / 2] // there are other ways to get the pivot also, e.g.

    // continue until we have scanned the entire range
    while (leftIndex <= rightIndex) {

        // these while loops stop each time they find an element on the wrong side of the pivot
        while (records[leftIndex] < pivot) leftIndex++ // 1) Find all elements on left that should be on right
        while (records[rightIndex] > pivot) rightIndex-- // 2) Find all elements on right that should be on left

        // 3) Swap elements, and move left and right indices
        if (leftIndex <= rightIndex) {
            swap(records, leftIndex, rightIndex, records[leftIndex], records[rightIndex])
            leftIndex++
            rightIndex--
        }
    }
    // finally the leftIndex, after the scanning will be the pivot element's index
    return leftIndex
}

private fun swap(records: Array<Record>, indexA: Int, indexB: Int, valueA: String, valueB: String) {
    records[indexA] = valueB
    records[indexB] = valueA

}