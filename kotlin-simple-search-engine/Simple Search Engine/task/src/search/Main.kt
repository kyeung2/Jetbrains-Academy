package search

import java.io.File
import java.util.*

fun main(args: Array<String>) {
    if (args.size == 2 && args[0] == "--data") {
        val lines = File(args[1]).readLines()
        SearchEngine(lines, Scanner(System.`in`)).start()
    }
}

enum class MatchingStrategy { ALL, ANY, NONE }

class SearchEngine(private val data: List<String>, private val scanner: Scanner) {

    private val invertedIndex: Map<String, Set<Int>>

    init {
        val temp: MutableMap<String, MutableSet<Int>> = mutableMapOf()
        data.forEachIndexed { lineIndex, line ->
            val words = line.split(" ").filter { it.isNotEmpty() }.map { it.toLowerCase() }
            words.forEach {
                temp.putIfAbsent(it, mutableSetOf(lineIndex))?.add((lineIndex))
            }
        }
        invertedIndex = temp.toMap()
    }

    fun start() {
        var exit = false
        while (!exit) {
            println()
            when (selectOption()) {
                1 -> search()
                2 -> printAllDate()
                0 -> exit = true
                else -> println("\nIncorrect option! Try again.")
            }
        }
        println("\nBye!")
    }

    private fun selectOption(): Int {
        println("""
        === Menu ===
        1. Find a person.
        2. Print all data.
        0. Exit.
    """.trimIndent())
        return scanner.nextLine().toInt()
    }

    private fun getMatchingStrategy(): MatchingStrategy {
        println("Select a matching strategy: ${MatchingStrategy.values().joinToString()}")
        return MatchingStrategy.valueOf(scanner.nextLine().toUpperCase())
    }

    private fun search() {
        val strategy = getMatchingStrategy()
        println("\nEnter a name or email to search all suitable people.")
        val searchTerms = scanner.nextLine().toLowerCase().split(" ").filter { it.isNotEmpty() }
        val foundLines = when (strategy) {
            MatchingStrategy.ALL -> searchAll(searchTerms)
            MatchingStrategy.ANY -> searchAny(searchTerms)
            else -> searchNone(searchTerms)
        }
        println(if (foundLines.isNotEmpty()) {
            println("${foundLines.size} persons found:")
            foundLines.joinToString("\n")
        } else
            "No matching people found.")
    }

    private fun searchAll(searchTerms: List<String>): List<String> {
        val foundII = invertedIndex.filterKeys { searchTerms.contains(it) }
        if (foundII.size == searchTerms.size) {
            val superSet = foundII.values.flatten().toSet()
            val all = foundII.values.fold(superSet, { acc, set -> acc union set })
            return all.map { data[it] }
        }
        return emptyList()
    }

    private fun searchAny(searchTerms: List<String>): List<String> {
        val foundII = invertedIndex.filterKeys { searchTerms.contains(it) }
        return foundII.values.flatten().toSet().map { data[it] }
    }

    private fun searchNone(searchTerms: List<String>): List<String> {
        val allThatContain = invertedIndex.filterKeys { searchTerms.contains(it) }.values.flatten().toSet()
        val foundIndices = invertedIndex.values.flatten().toSet().filter { !allThatContain.contains(it) }
        return foundIndices.map { data[it] }
    }

    private fun printAllDate() {
        println("\n=== List of people ===\n${data.joinToString("\n")}")
    }
}