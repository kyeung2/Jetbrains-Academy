package phonebook

import java.io.File

fun main() {

    val directory = File("/Users/kye/Desktop/directory.txt").readLines()
    val names = File("/Users/kye/Desktop/find.txt").readLines()

    PhoneBook(directory).search(names)
}