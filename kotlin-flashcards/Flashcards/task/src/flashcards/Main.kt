package flashcards

import java.io.File

fun main(args: Array<String>) {
    var importFileName = ""
    var exportFileName = ""
    if (args.isNotEmpty()) {
        for (i in args.indices step 2) {
            when (args[i]) {
                "-import" -> importFileName = args[i + 1]
                "-export" -> exportFileName = args[i + 1]
            }
        }
    }

    FlashCards(importFileName, exportFileName).start()
}

data class Card(val term: String, val definition: String, var errorCount: Int = 0)

class FlashCards(private val importFileName: String, private val exportFileName: String) {

    private val cards = mutableListOf<Card>()
    private val logs = mutableListOf<String>()
    private val SEPARATOR = "XXX"

    private fun logString(input: String): String {
        logs.add(input)
        return input
    }

    fun start() {

        if (importFileName.isNotEmpty()) {
            doImport(importFileName)
        }

        var exit = false
        while (!exit) {
            println(logString("Input the action (add, remove, import, export, ask, exit, log, hardest card, reset stats):"))
            when (logString(readLine()!!)) {
                "log" -> log()
                "hardest card" -> hardestCard()
                "reset stats" -> resetStats()
                "add" -> add()
                "remove" -> remove()
                "import" -> import()
                "export" -> export()
                "ask" -> ask()
                "exit" -> {
                    exit = true
                    println(logString("Bye bye!"))
                    if (exportFileName.isNotEmpty()) {
                        doExport(exportFileName)
                    }
                }
            }
            println(logString(""))
        }
    }

    private fun resetStats() {
        cards.forEach { it.errorCount = 0 }
        println(logString("Card statistics has been reset."))
    }

    private fun hardestCard() {
        val hardestCard = cards.maxBy { it.errorCount }
        if (hardestCard != null && hardestCard.errorCount > 0) {
            val hardestCards = cards.filter { it.errorCount == hardestCard.errorCount }
            if (hardestCards.size == 1) {
                println(logString("The hardest card is \"${hardestCard.term}\". You have ${hardestCard.errorCount} errors answering it."))
            } else {
                val hardestTerms = hardestCards.map(Card::term).joinToString(separator = "\", \"", prefix = "\"", postfix = "\"")
                println(logString("The hardest cards are ${hardestTerms}. You have ${hardestCard.errorCount} errors answering them."))

            }
        } else {
            println(logString("There are no cards with errors."))
        }
    }

    private fun log() {
        println(logString("File name:"))
        val fileName = logString(readLine()!!)
        val file = File(fileName)
        logs.forEach { file.writeText(it + "\n") }
        println(logString("The log has been saved."))
    }

    private fun ask() {
        println(logString("How many times to ask?"))
        repeat(logString(readLine()!!).toInt()) {
            val randomCard = cards.shuffled()[0]
            println(logString("Print the definition of \"${randomCard.term}\":"))
            val answer = logString(readLine()!!)
            if (answer == randomCard.definition) {
                println(logString("Correct answer"))
            } else {
                randomCard.errorCount += 1
                val cardEntry = cards.find { it.definition == answer }
                val suffix = cardEntry?.let { ", you've just written the definition of \"${it.term}\"." } ?: "."
                println(logString("Wrong answer. The correct one is \"${randomCard.definition}\"$suffix"))
            }
        }
    }

    private fun export() {
        println(logString("File name:"))
        val fileName = logString(readLine()!!)
        doExport(fileName)
    }

    private fun doExport(fileName: String) {
        val file = File(fileName)
        val serialisedCards = cards.map { "${it.term}$SEPARATOR${it.definition}$SEPARATOR${it.errorCount}" }.joinToString(separator = "\n")
        file.writeText(serialisedCards)
        println(logString("${cards.size} cards have been saved."))
    }

    private fun import() {
        println(logString("File name:"))
        val fileName = logString(readLine()!!)
        doImport(fileName)
    }

    private fun doImport(fileName: String) {
        val file = File(fileName)
        if (file.exists()) {
            val readLines = file.readLines()
            readLines.forEach { line ->
                line.split(SEPARATOR).let { it ->
                    val loadingCard = Card(term = it[0], definition = it[1], errorCount = it[2].toInt())
                    val existingCard = cards.find { it.term == loadingCard.term }
                    if (existingCard != null) cards.remove(existingCard)
                    cards.add(loadingCard)
                }
            }
            println(logString("${readLines.size} cards have been loaded."))
        } else {
            println(logString("File not found."))
        }
    }

    private fun remove() {
        println(logString("The card:"))
        val term = logString(readLine()!!)
        val foundCard = cards.find { it.term == term }
        if (foundCard != null) {
            cards.remove(foundCard)
            println(logString("The card has been removed."))
        } else {
            println(logString("Can't remove \"$term\": there is no such card."))
        }
    }

    private fun add() {
        println(logString("The card:"))
        val term = logString(readLine()!!)
        val foundCard = cards.find { it.term == term }
        if (foundCard == null) {
            println(logString("The definition of the card:"))
            val definition = logString(readLine()!!)

            if (cards.find { it.definition == definition } == null) {
                cards.add(Card(term = term, definition = definition))
                println(logString("The pair (\"$term\":\"$definition\") has been added."))
            } else {
                println(logString("The definition \"$definition\" already exists."))
            }
        } else {
            println(logString("The card \"$term\" already exists.\n"))
        }
    }
}

