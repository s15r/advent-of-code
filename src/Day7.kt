fun main() {
    check(3749L == Day7(streamLines("day7test.txt")).sumTrueTestValues())
    println(Day7(streamLines("day7.txt")).sumTrueTestValues())
}

class Day7(lines: Sequence<String>) {
    private val equations = lines.map { it.toEquation() }

    fun sumTrueTestValues(): Long {
        return equations.map { equation ->
            val initialValue = equation.operands.first()
            val validEquationResult = validEquationResultOrZero(
                initialValue = initialValue,
                operands = equation.operands.subList(1, equation.operands.size),
                validTestResult = equation.testValue
            )
            validEquationResult
        }.sum()
    }

    private fun validEquationResultOrZero(initialValue: Long, operands: List<Long>, validTestResult: Long): Long {
        val validEquationResult = operands
            .fold(CarryResult(listOf(initialValue))) { carry, operand ->
                CarryResult(
                    carry.combinedResults.flatMap { combinedResult ->
                        Operator.entries.map {
                            when (it) {
                                Operator.PLUS -> combinedResult + operand
                                Operator.MULTIPLY -> combinedResult * operand
                            }
                        }.toList()
                    }
                )
            }.combinedResults.firstOrNull { it == validTestResult } ?: 0
        return validEquationResult
    }
}

data class CarryResult(val combinedResults: List<Long>)

data class Equation(val testValue: Long, val operands: List<Long>)
enum class Operator {
    PLUS,
    MULTIPLY
}

fun String.toEquation() =
    this.split(":").let { byColon ->
        Equation(
            testValue = byColon[0].toLong(),
            operands = byColon[1].trim().split(" ").map { it.toLong() })
    }