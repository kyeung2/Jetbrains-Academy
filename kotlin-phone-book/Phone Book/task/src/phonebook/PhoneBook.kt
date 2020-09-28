package phonebook

import java.time.Duration

class PhoneBook(directory: List<String>) {

    private val unsortedRecords: Array<Record>

    init {
        unsortedRecords = directory
                .map { line ->
                    val split = line.split(" ", limit = 2)
                    split[1]
                }
                .toTypedArray()
    }

    fun search(names: List<String>) {

        val duration = linear(names)
        bubbleAndJump(names, duration.multipliedBy(10))
        quickAndBinary(names)
        hashTable(names)
    }

    private fun linear(names: List<String>): Duration {

        val (linearSearchDuration, linearSearchFound) = timedSearch(unsortedRecords, names, ::linearSearch)
        println("Start searching (linear search)...")
        println("Found $linearSearchFound / 500 entries. Time taken: ${linearSearchDuration.prettyPrint()}")
        println()
        return linearSearchDuration
    }

    private fun bubbleAndJump(names: List<String>, timeout: Duration) {

        println("Start searching (bubble sort + jump search)...")
        val bubbleSortedRecords = unsortedRecords.copyOf()
        val (bubbleSortDuration, successful) = bubbleSort(bubbleSortedRecords, timeout)
        if (successful) {

            val (jumpSearchDuration, jumpSearchFound) = timedSearch(bubbleSortedRecords, names, ::jumpSearch)
            println("Found $jumpSearchFound / 500 entries. Time taken: ${(bubbleSortDuration + jumpSearchDuration).prettyPrint()}")
            println("Sorting time: ${bubbleSortDuration.prettyPrint()}")
            println("Searching time: ${jumpSearchDuration.prettyPrint()}")
            println()
        } else {

            val (linearDuration, linearSearchFound) = timedSearch(unsortedRecords, names, ::linearSearch)
            println("Found $linearSearchFound / 500 entries. Time taken: ${(linearDuration + bubbleSortDuration).prettyPrint()}")
            println("Sorting time: ${bubbleSortDuration.prettyPrint()} - STOPPED, moved to linear search")
            println("Searching time: ${linearDuration.prettyPrint()}")
            println()
        }
    }

    private fun quickAndBinary(names: List<String>) {

        println("Start searching (quick sort + binary search)...")
        val binarySortedRecords = unsortedRecords.copyOf()
        val quickSortDuration = quickSort(binarySortedRecords)
        val (binarySearchDuration, binarySearchFound) = timedSearch(binarySortedRecords, names, ::binarySearch)
        println("Found $binarySearchFound / 500 entries. Time taken: ${(binarySearchDuration + quickSortDuration).prettyPrint()}")
        println("Sorting time: ${quickSortDuration.prettyPrint()}")
        println("Searching time: ${binarySearchDuration.prettyPrint()}")
        println()
    }


    private fun hashTable(names: List<String>) {

        println("Start searching (hash table)...")
        val hashStart = System.currentTimeMillis()
        val hash = unsortedRecords.toHashSet()
        val hashDuration = duration(hashStart)
        val searchStart = System.currentTimeMillis()
        val found = names.filter { hash.contains(it) }.count()
        val searchDuration = duration(searchStart)
        println("Found $found / 500 entries. Time taken: ${(hashDuration + searchDuration).prettyPrint()}")
        println("Creating time: ${hashDuration.prettyPrint()}")
        println("Searching time: ${searchDuration.prettyPrint()}")
        println()
    }

}