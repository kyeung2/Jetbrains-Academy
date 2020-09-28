import java.lang.Math.*
import java.util.Scanner

fun main() {
    val scanner = Scanner(System.`in`)

    val u = scanner.nextLine().split(" ").map(String::toDouble)
    val v = scanner.nextLine().split(" ").map(String::toDouble)
    print(angelInDegrees(u, v))
}

private fun angelInDegrees(u: List<Double>, v: List<Double>): Double {
    val uLength = length(u)
    val vLength = length(v)
    val dotProduct = dotProduct(u, v)
    val cos = dotProduct / (uLength * vLength)
    val radianAngle = acos(cos)
    return toDegrees(radianAngle)
}

private fun length(vector: List<Double>) = sqrt(pow(vector[0], 2.0) + pow(vector[1], 2.0))

private fun dotProduct(u: List<Double>, v: List<Double>): Double = (u[0] * v[0]) + (u[1] * v[1])
