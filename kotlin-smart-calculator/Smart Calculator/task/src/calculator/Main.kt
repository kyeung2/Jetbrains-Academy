package calculator

import java.lang.Exception
import java.math.BigInteger
import java.util.*
import kotlin.math.pow

// using extension functions
fun String.isLetter(): Boolean = "[a-zA-Z]+".toRegex().matches(this)
fun String.isDigit(): Boolean = "-?\\d+".toRegex().matches(this)
fun <E> MutableList<E>.pop(): E = removeAt(count() - 1)
fun <E> MutableList<E>.peek(): E = get(count() - 1)
fun <E> MutableList<E>.push(e: E) = add(e)

fun main() {
    SmartCalculator().start()
}

// moving variables logic to its own class
class Variables {

    private val variables = mutableMapOf<String, String>()

    fun exists(name: String): Boolean = variables.containsKey(name)

    fun setVariable(name: String, value: String) {
        variables[name] = value
    }

    fun setVariableFromList(infixElements: List<String>) {
        if (infixElements.size != 3 || infixElements[1] != "=" || !exists(infixElements[2]) && !infixElements[2].isDigit()) {
            println("Invalid assignment")
        } else if (!infixElements[0].isLetter()) {
            println("Invalid identifier")
        } else {
            setVariable(infixElements[0], infixElements[2])
        }
    }

    fun resolve(name: String): String? {
        if (name.isDigit()) {
            return name
        } else {
            var candidate = name
            repeat(variables.size) {
                if (variables.containsKey(candidate)) {
                    val value = variables[candidate]!!
                    if (value.isDigit()) {
                        return value
                    }
                    candidate = value
                }
            }
            return null
        }
    }
}

class SmartCalculator {

    private val variables = Variables()

    fun start() {
        var exit = false
        val scanner = Scanner(System.`in`)
        while (!exit) {
            when (val line = scanner.nextLine()!!) {
                "/exit" -> exit = true
                "/help" -> println("The program calculates the sum of numbers")
                else -> {
                    if (line.startsWith("/")) {
                        println("Unknown command")
                    } else if (line.isNotEmpty()) {
                        try {
                            val infixElements = getInfixElements(line)
                            when {
                                infixElements.size == 1 -> println(variables.resolve(infixElements[0])
                                        ?: "Unknown variable")
                                infixElements.contains("=") -> variables.setVariableFromList(infixElements)
                                else -> calculate(infixElements)
                            }
                        } catch (e: Exception) {
                            println("Invalid expression")
                        }
                    }
                }
            }
        }
        println("Bye!")
    }

    private fun getInfixElements(line: String): List<String> {
        val result = mutableListOf<String>()
        var operand = ""
        var addSubtractOperator = ""

        line.split(" ")
                .filter { it.isNotEmpty() }
                .forEach { token ->
                    if (token.isDigit()) {
                        if (operand.isLetter()) {
                            throw Exception("Invalid expression")
                        }
                        if (addSubtractOperator.isNotEmpty()) {
                            result.add(getFinalAddSubtractOperator(addSubtractOperator))
                            addSubtractOperator = ""
                        }
                        operand = token
                    } else {
                        token.toCharArray().forEach {
                            if (it.isDigit()) {
                                if (operand.isLetter()) {
                                    throw Exception("Invalid expression")
                                }
                                if (addSubtractOperator.isNotEmpty()) {
                                    result.add(getFinalAddSubtractOperator(addSubtractOperator))
                                    addSubtractOperator = ""
                                }
                                operand += it
                            } else if (it.isLetter()) {
                                if (operand.isDigit()) {
                                    throw Exception("Invalid expression")
                                }
                                if (addSubtractOperator.isNotEmpty()) {
                                    result.add(getFinalAddSubtractOperator(addSubtractOperator))
                                    addSubtractOperator = ""
                                }
                                operand += it
                            } else if (it == '=' || it == '(' || it == ')' || it == '*' || it == '/' || it == '^') {
                                if (operand.isNotEmpty()) {
                                    result.add(operand)
                                    operand = ""
                                } else if (addSubtractOperator.isNotEmpty()) {
                                    result.add(getFinalAddSubtractOperator(addSubtractOperator))
                                    addSubtractOperator = ""
                                }
                                result.add(it.toString())
                            } else if (it == '+') {
                                if (addSubtractOperator.contains("-")) {
                                    throw Exception("Invalid expression")
                                } else if (addSubtractOperator.isEmpty()) {
                                    if (operand.isNotEmpty()) {
                                        result.add(operand)
                                        operand = ""
                                    }
                                    addSubtractOperator = "+"
                                }
                            } else if (it == '-') {
                                if (addSubtractOperator.contains("+")) {
                                    throw Exception("Invalid expression")
                                } else {
                                    if (operand.isNotEmpty()) {
                                        result.add(operand)
                                        operand = ""
                                    }
                                    addSubtractOperator += it
                                }
                            }
                        }
                    }
                }

        if (operand.isNotEmpty()) {
            result.add(operand)
            operand = ""
        }
        return result.toList()
    }

    private fun getFinalAddSubtractOperator(addSubtractOperator: String): String =
            when {
                addSubtractOperator == "+" -> addSubtractOperator
                addSubtractOperator.length % 2 == 0 -> "+"
                else -> "-"
            }

    private fun calculate(infixElements: List<String>) {
        val postFixExpressionElements = getPostfixElements(infixElements)
        val result = calculatePostfixExpression(postFixExpressionElements)
        println(result)
    }

    private fun calculatePostfixExpression(postFixExpressionElements: List<String>): BigInteger {
        val stack = mutableListOf<BigInteger>()
        postFixExpressionElements.forEach { it ->
            if (it.isDigit()) {
                stack.push(BigInteger(it))
            } else if (it.isLetter()) {
                stack.push(variables.resolve(it)?.let { BigInteger(it) } ?: throw Exception("Unknown variable"))
            } else {
                val right = stack.pop()
                val left = stack.pop()
                stack.push(performOperation(left, right, it))
            }
        }

        return stack.pop()
    }

    private fun performOperation(left: BigInteger, right: BigInteger, operator: String): BigInteger = when (operator) {
        "+" -> left + right
        "-" -> left - right
        "*" -> left * right
        "/" -> left / right
        "^" -> left.pow(right.toInt())
        else -> throw Exception("Unknown operator")
    }

    private fun precedence(operator: String): Int = when (operator) {
        "+", "-" -> 1
        "*", "/" -> 2
        "^" -> 3
        else -> throw Exception("unknown operator $operator")
    }

    private fun getPostfixElements(infixExpressionElements: List<String>): List<String> {

        val result = mutableListOf<String>()
        val stack = mutableListOf<String>()

        infixExpressionElements.forEach {
            // 1 Add operands (numbers and variables) to the result (postfix notation) as they arrive.
            if (it.isDigit() || it.isLetter()) {
                result.add(it)
            }
            // 5 If the incoming element is a left parenthesis, push it on the stack.
            else if (it == "(") {
                stack.push(it)
            }
            // 6 If the incoming element is a right parenthesis, pop the stack and add operators to the result until you see a left parenthesis. Discard the pair of parentheses.
            else if (it == ")") {
                var operator = ""
                while (operator != "(") {
                    operator = stack.pop()
                    if (operator != "(") {
                        result.add(operator)
                    }
                }
            }
            // 2 If the stack is empty or contains a left parenthesis on top, push the incoming operator on the stack.
            else if (stack.isEmpty() || stack.peek() == "(") {
                stack.add(it)
            }
            // 3 If the incoming operator has higher precedence than the top of the stack, push it on the stack.
            else if (precedence(it) > precedence(stack.peek())) {
                stack.add(it)
            }
            // 4 If the incoming operator has lower or equal precedence than or to the top of the stack,
            // pop the stack and add operators to the result until you see an operator that has a smaller
            // precedence or a left parenthesis on the top of the stack; then add the incoming operator to the
            // stack.
            else if (precedence(it) <= precedence(stack.peek())) {
                while (
                        stack.isNotEmpty()
                        && stack.peek() != "("
                        && precedence(it) <= precedence(stack.peek())) {

                    val operator = stack.pop()
                    result.add(operator)
                }
                stack.push(it)
            }
        }

        // 7 At the end of the expression, pop the stack and add all operators to the result.
        stack.reverse()
        result.addAll(stack)

        return result.toList()
    }
}