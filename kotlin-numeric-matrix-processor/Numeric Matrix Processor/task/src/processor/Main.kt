package processor

import kotlin.math.absoluteValue
import kotlin.math.pow

fun main() = MatrixCalculator().start()

typealias  Matrix = Array<DoubleArray>

// must be a nicer way to do this
fun Matrix.print() {

    fun formatDouble(d: Double): String {
        // normalise -0 to 0
        val value = if (d == 0.0) d.absoluteValue else d
        // round to to decimal places
        var formatted = "%.2f".format(value)
        // remove .00 from formatted string
        formatted = if (formatted.endsWith(".00")) formatted.substringBefore(".00") else formatted
        // add padding for column
        return formatted
    }

    fun pad(text: String, width: Int): String = " ".repeat(width - text.length) + text

    val columnWidths = (this[0].indices).map { columnIndex ->
        column(columnIndex)
                .map { d -> formatDouble(d).length }
                .max()
    }

    forEach {
        println(it
                .mapIndexed { index, d -> pad(formatDouble(d), columnWidths[index]!!) }
                .joinToString(separator = " "))
    }
}


fun Matrix.transposeMainDiagonal(): Matrix = Array(size) {
    column(it)
}

fun Matrix.transposeSideDiagonal(): Matrix = Array(size) {
    val column = column(size - 1 - it)
    column.reverse()
    column
}

fun Matrix.transposeVertical(): Matrix = Array(size) {
    val copyOf = this[it].copyOf()
    copyOf.reverse()
    copyOf
}

fun Matrix.transposeHorizontal(): Matrix = Array(size) {
    this[size - 1 - it].copyOf()
}

fun Matrix.column(columnIndex: Int): DoubleArray = map { it[columnIndex] }.toDoubleArray()

fun Matrix.minor(rowIndex: Int, columnIndex: Int): Matrix {
    val filteredRows = filterIndexed { index, _ -> index != rowIndex }
    return filteredRows.mapIndexed { _, column ->
        column.filterIndexed { index, _ -> index != columnIndex }.toDoubleArray()
    }.toTypedArray()
}

fun Matrix.calculateDeterminant(): Double {
    // base case 1
    if (size == 1) {
        return this[0][0]
    }
    // base case 2
    if (size == 2) {
        return (this[0][0] * this[1][1]) - (this[0][1] * this[1][0])
    }
    // Laplace expansion
    return this[0].mapIndexed { columnIndex, value ->
        value * cofactor(0, columnIndex)
    }.sum()
}

fun Matrix.cofactor(rowIndex: Int, columnIndex: Int): Double =
        (-1.0).pow(rowIndex + 1 + columnIndex + 1) * minor(rowIndex, columnIndex).calculateDeterminant()

fun Matrix.inverse(): Matrix = adjoint() * (1 / calculateDeterminant())

fun Matrix.adjoint(): Matrix = Array(size) { rowIndex ->
    this[rowIndex].mapIndexed { columnIndex, _ -> cofactor(rowIndex, columnIndex) }.toDoubleArray()
}.transposeMainDiagonal()

operator fun Matrix.plus(that: Matrix): Matrix = Array(size) {
    this[it].zip(that[it]) { x, y -> x + y }.toDoubleArray()
}

operator fun Matrix.times(constant: Double): Matrix = Array(size) {
    this[it].map { value -> value * constant }.toDoubleArray()
}

operator fun Matrix.times(that: Matrix): Matrix = Array(size) { rowIndex ->

    val thisRow = this[rowIndex]
    DoubleArray(that[0].size) { columnIndex ->
        val thatColumn = that.column(columnIndex)
        thisRow.zip(thatColumn) { x, y -> x * y }.sum()
    }
}

class MatrixCalculator {

    private companion object {
        const val mainMode = """
            1. Add matrices
            2. Multiply matrix to a constant
            3. Multiply matrices
            4. Transpose matrix
            5. Calculate a determinant
            6. Inverse matrix
            0. Exit
        """
        const val transposeMode = """
            1. Main diagonal
            2. Side diagonal
            3. Vertical line
            4. Horizontal line
        """
    }

    fun start() =
            try {
                var exit = false
                while (!exit) {
                    when (getMode(mainMode)) {
                        1 -> add().print()
                        2 -> multiplyByConstant().print()
                        3 -> dotProduct().print()
                        4 -> transpose().print()
                        5 -> calculateDeterminant()
                        6 -> inverse().print()
                        else -> exit = true
                    }
                    println()
                }
            } catch (e: Exception) {
                println("ERROR")
            }

    private fun inverse(): Matrix {
        print("Enter matrix size: ")
        val dimension = readDimensions()
        println("Enter matrix: ")
        val matrix = createMatrix(dimension)
        println("The result is:")

        return matrix.inverse()
    }

    private fun calculateDeterminant() {

        print("Enter matrix size: ")
        val dimension = readDimensions()
        println("Enter matrix: ")
        val matrix = createMatrix(dimension)
        println("The result is:")
        println(matrix.calculateDeterminant())
    }

    private fun transpose(): Matrix {
        val mode = getMode(transposeMode)
        println("Enter matrix size: ")
        val dimension = readDimensions()
        println("Enter matrix: ")
        val matrix = createMatrix(dimension)

        return when (mode) {
            1 -> matrix.transposeMainDiagonal()
            2 -> matrix.transposeSideDiagonal()
            3 -> matrix.transposeVertical()
            else -> matrix.transposeHorizontal()
        }
    }

    private fun getMode(modeText: String): Int {
        println(modeText.trimIndent())
        return readLine()!!.toInt()
    }

    private fun dotProduct(): Matrix {
        println("Enter size of first matrix: ")
        val dimensionA = readDimensions()
        println("Enter first matrix: ")
        val matrixA = createMatrix(dimensionA)
        println("Enter size of second matrix: ")
        val dimensionB = readDimensions()
        println("Enter second matrix:")
        val matrixB = createMatrix(dimensionB)
        println("The multiplication result is:")
        return matrixA * matrixB
    }

    private fun add(): Matrix {
        val dimensionA = readDimensions()
        val matrixA = createMatrix(dimensionA)
        val dimensionB = readDimensions()
        if (dimensionA != dimensionB) {
            throw Exception()
        }
        val matrixB = createMatrix(dimensionB)

        return matrixA + matrixB
    }

    private fun multiplyByConstant(): Matrix {
        val matrix = createMatrix(readDimensions())
        val constant = readLine()!!.toDouble()
        return matrix * constant
    }

    private fun readDimensions(): Pair<Int, Int> {
        val map: List<Int> = readLine()!!.split(" ").filter(String::isNotEmpty).map(String::toInt)
        return Pair(map[0], map[1])
    }

    private fun createMatrix(dimension: Pair<Int, Int>): Matrix =
            Array(dimension.first) {
                val columnElements = readLine()!!.split(" ").filter(String::isNotEmpty)
                if (columnElements.size != dimension.second) {
                    throw Exception()
                }
                columnElements.map(String::toDouble).toDoubleArray()
            }
}

