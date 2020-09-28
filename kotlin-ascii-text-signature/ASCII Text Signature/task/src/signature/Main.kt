package signature

import signature.Font.MEDIUM
import signature.Font.ROMAN
import java.io.File
import java.util.*

enum class Font { ROMAN, MEDIUM }
data class FontMetaData(val height: Int, val characterCount: Int, val spaceWidth: Int)
data class Character(val symbol: String, val width: Int, val ascii: Array<String>)

fun main() {
    val scanner = Scanner(System.`in`)
    print("Enter name and surname: ")
    val name = scanner.nextLine().trim()
    print("Enter person's status: ")
    val status = scanner.nextLine().trim()
    AsciiTextSignature().printAscii(name, status)
}

class AsciiTextSignature {

    private val asciiFonts = AsciiFonts()

    fun printAscii(name: String, status: String) {
        val nameCharacters: List<Character> = convertToCharacters(name, ROMAN)
        val statusCharacters: List<Character> = convertToCharacters(status, MEDIUM)
        val nameWidth = nameCharacters.sumBy { it.width }
        val statusWidth = statusCharacters.sumBy { it.width }
        val textWidth = if (nameWidth > statusWidth) nameWidth else statusWidth
        val boarder = "8".repeat(textWidth + 8)
        val (namePad, namePadRight) = getPad(nameWidth, statusWidth)
        val (statusPad, statusPadRight) = getPad(statusWidth, nameWidth)

        println(boarder)
        repeat(asciiFonts.getFontMetaData(ROMAN).height) { heightIndex ->
            println("88$namePad" + nameCharacters.joinToString("") { it.ascii[heightIndex] } + "${namePadRight}88")
        }
        repeat(asciiFonts.getFontMetaData(MEDIUM).height) { heightIndex ->
            println("88$statusPad" + statusCharacters.joinToString("") { it.ascii[heightIndex] } + "${statusPadRight}88")
        }
        println(boarder)
    }

    private fun convertToCharacters(name: String, font: Font) = name.toCharArray()
            .map { it.toString() }
            .map { chars -> asciiFonts.getCharacters(font).first { it.symbol == chars } }

    private fun getPad(widthA: Int, widthB: Int): Pair<String, String> = if (widthA > widthB) {
        Pair("  ", "  ")
    } else {
        val pad = " ".repeat((widthB - widthA) / 2 + 2)
        val padRight = if ((widthB - widthA) % 2 == 1) "$pad " else pad
        Pair(pad, padRight)
    }
}

class AsciiFonts {
    private val characters: Map<Font, Array<Character>>
    private val metaData: Map<Font, FontMetaData>

    init {
        val (romanMeta, romanCharacters) = loadFromFile(10, "/Users/kye/IdeaProjects/ASCII Text Signature/roman.txt")
        val (mediumMeta, mediumCharacters) = loadFromFile(5, "/Users/kye/IdeaProjects/ASCII Text Signature/medium.txt")
        metaData = mapOf(ROMAN to romanMeta, MEDIUM to mediumMeta)
        characters = mapOf(ROMAN to romanCharacters, MEDIUM to mediumCharacters)
    }

    fun getCharacters(font: Font): Array<Character> = characters[font]!!

    fun getFontMetaData(font: Font): FontMetaData = metaData[font]!!

    private fun loadFromFile(spaceWidth: Int, fontPath: String): Pair<FontMetaData, Array<Character>> {
        val scanner = Scanner(File(fontPath))
        val meta = scanner.nextLine().split(" ").map { it.toInt() }
        val fontData = FontMetaData(meta[0], meta[1], spaceWidth)
        val characters = Array(fontData.characterCount + 1) {
            if (it == 0)
                createSpaceCharacter(fontData)
            else
                loadCharacter(scanner, fontData.height)
        }
        return Pair(fontData, characters)
    }

    private fun createSpaceCharacter(fontData:FontMetaData): Character = Character(" ", fontData.spaceWidth, Array(fontData.height) {
        " ".repeat(fontData.spaceWidth)
    })

    private fun loadCharacter(scanner: Scanner, characterHeight: Int): Character {
        val meta = scanner.nextLine().split(" ")
        val symbol = meta[0]
        val width = meta[1].toInt()
        val ascii = Array(characterHeight) { scanner.nextLine() }
        return Character(symbol, width, ascii)
    }
}