package converter

import converter.Unit.*
import converter.UnitType.*
import java.util.*
import kotlin.math.absoluteValue

fun main() {
    var exit: Boolean
    val scanner = Scanner(System.`in`)
    do {
        print("Enter what you want to convert (or exit): ")
        val input = scanner.nextLine()!!
        exit = input == "exit"
        if (!exit) {
            with(input.split(" ")
                    .filter { it.isNotEmpty() }
                    .filter { it.toLowerCase() != "degree" }
                    .filter { it.toLowerCase() != "degrees" }

            ) {

                try {
                    val inputValue = this[0].toDouble()
                    val inputUnit = getUnit(this[1])
                    val outputUnit = getUnit(this[3])
                    if (inputUnit == null || outputUnit == null) {
                        println("Conversion from ${inputUnit?.plural ?: "???"} to ${outputUnit?.plural ?: "???"} is impossible.")
                    } else if (inputUnit.unitType != outputUnit.unitType) {
                        println("Conversion from ${inputUnit.plural} to ${outputUnit.plural} is impossible.")
                    } else if (inputValue < 0.0 && (inputUnit.unitType == LENGTH || inputUnit.unitType == WEIGHT)) {
                        println("${inputUnit.unitType.displayText} shouldn't be negative")
                    } else {
                        val outputValue = inputUnit.unitType.convert(inputValue, inputUnit, outputUnit)
                        val inputUnitSuffix = if (inputValue.absoluteValue == 1.0) inputUnit.singular else inputUnit.plural
                        val outputUnitSuffix = if (outputValue.absoluteValue == 1.0) outputUnit.singular else outputUnit.plural
                        println("$inputValue $inputUnitSuffix is $outputValue $outputUnitSuffix")
                    }
                } catch (e: Exception) {
                    println("Parse error")
                }

            }
        }
    } while (!exit)
}

fun getUnit(input: String): Unit? =
        Unit.values().firstOrNull {
            val l = input.toLowerCase()
            it.symbols.contains(l) || l == it.singular.toLowerCase() || l == it.plural.toLowerCase()
        }

typealias ConvertFunction = (input: Double, inputUnit: Unit, outputUnit: Unit) -> Double

val baseRatioConvert: ConvertFunction = { input, inputUnit, outputUnit ->
    if (inputUnit == outputUnit) {
        input
    } else {
        val baseValue = input * inputUnit.baseRatio
        baseValue / outputUnit.baseRatio
    }
}

val temperatureConvert: ConvertFunction = { input, inputUnit, outputUnit ->
    when {
        inputUnit == outputUnit -> input
        inputUnit == CELSIUS && outputUnit == FAHRENHEIT -> input * (9.0 / 5.0) + 32
        inputUnit == FAHRENHEIT && outputUnit == CELSIUS -> (input - 32) * (5.0 / 9.0)
        inputUnit == KELVIN && outputUnit == CELSIUS -> input - 273.15
        inputUnit == CELSIUS && outputUnit == KELVIN -> input + 273.15
        inputUnit == FAHRENHEIT && outputUnit == KELVIN -> (input + 459.67) * (5.0 / 9.0)
        inputUnit == KELVIN && outputUnit == FAHRENHEIT -> input * (9.0 / 5.0) - 459.67
        else -> -1.0 //this cannot happen
    }
}

enum class UnitType(val displayText: String, val convert: ConvertFunction) {
    LENGTH("Length", baseRatioConvert),
    WEIGHT("Weight", baseRatioConvert),
    TEMPERATURE("Temperature", temperatureConvert)
}

enum class Unit(val symbols: Array<String>, val singular: String, val plural: String, val baseRatio: Double, val unitType: UnitType) {
    METER(arrayOf("m"), "meter", "meters", 1.0, LENGTH),
    KILOMETER(arrayOf("km"), "kilometer", "kilometers", 1000.0, LENGTH),
    CENTIMETER(arrayOf("cm"), "centimeter", "centimeters", 0.01, LENGTH),
    MILLIMETER(arrayOf("mm"), "millimeter", "millimeters", 0.001, LENGTH),
    MILE(arrayOf("mi"), "mile", "miles", 1609.35, LENGTH),
    YARD(arrayOf("yd"), "yard", "yards", 0.9144, LENGTH),
    FOOT(arrayOf("ft"), "foot", "feet", 0.3048, LENGTH),
    INCH(arrayOf("in"), "inch", "inches", 0.0254, LENGTH),
    GRAM(arrayOf("g"), "gram", "grams", 1.0, WEIGHT),
    KILOGRAM(arrayOf("kg"), "kilogram", "kilograms", 1000.0, WEIGHT),
    MILLIGRAM(arrayOf("mg"), "milligram", "milligrams", 0.001, WEIGHT),
    POUND(arrayOf("lb"), "pound", "pounds", 453.592, WEIGHT),
    OUNCE(arrayOf("oz"), "ounce", "ounces", 28.349, WEIGHT),
    CELSIUS(arrayOf("celsius", "dc", "c"), "degree Celsius", "degrees Celsius", -1.0, TEMPERATURE),
    FAHRENHEIT(arrayOf("fahrenheit", "df", "f"), "degree Fahrenheit", "degrees Fahrenheit", -1.0, TEMPERATURE),
    KELVIN(arrayOf("k"), "Kelvin", "Kelvins", -1.0, TEMPERATURE),
}