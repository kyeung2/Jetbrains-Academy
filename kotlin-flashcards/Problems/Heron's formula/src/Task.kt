import java.lang.Math.sqrt
import java.util.Scanner

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    val(a, b, c) = DoubleArray(3) { readLine()!!.toDouble() }
    val p = (a + b + c) / 2.0
    println(sqrt(p * (p - a) * (p - b) * (p - c)))
}