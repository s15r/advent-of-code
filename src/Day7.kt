fun main() {
    check(3749L == Day7(streamLines("day7test.txt"), part1Operators).sumTrueTestValues())
    println(Day7(streamLines("day7.txt"), part1Operators).sumTrueTestValues()) // 5837374519342
    check(11387L == Day7(streamLines("day7test.txt"), part2Operators).sumTrueTestValues())
    println(Day7(streamLines("day7.txt"), part2Operators).sumTrueTestValues())  // 492383931650959
}

val part1Operators = listOf(Operator.PLUS, Operator.MULTIPLY)
val part2Operators = listOf(Operator.PLUS, Operator.MULTIPLY, Operator.CONCAT)

class Day7(lines: Sequence<String>, private val supportedOperators: List<Operator>) {
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
                        supportedOperators.map { it.applyOperator(combinedResult, operand) }.toList()
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
    MULTIPLY,
    CONCAT;

    fun applyOperator(combinedResult: Long, operand: Long) = when (this) {
        PLUS -> combinedResult + operand
        MULTIPLY -> combinedResult * operand
        CONCAT -> (combinedResult.toString() + operand).toLong()
    }
}

fun String.toEquation() =
    this.split(":").let { byColon ->
        Equation(
            testValue = byColon[0].toLong(),
            operands = byColon[1].trim().split(" ").map { it.toLong() })
    }