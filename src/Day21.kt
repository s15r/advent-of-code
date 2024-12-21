import Day21.Pad
import kotlin.math.abs

fun main() {
    check(126384 == Day21(readLines("day21test.txt")).part1())
    Day21(readLines("day21.txt")).part1().println()
}

class Day21(private val input: List<String>) {
    fun part1(): Int {
        val humanDirectionalPad = HumanDirectionalPad()
        val numPad = NumPadByRobot(
            pad = RobotDirectionalPad(
                pad = RobotDirectionalPad(
                    pad = humanDirectionalPad
                )
            )
        )
        val totalDistance = input.sumOf { line -> calculateComplexity(line, numPad) }
        return totalDistance
    }

    private fun calculateComplexity(line: String, numPad: NumPadByRobot): Int {
        val clicks = line.map { char -> numPad.requestButton(char) }.sum()
        val numericPart = line.mapNotNull { char -> if (char == 'A') null else char }.joinToString("").toInt()
        val result = numericPart * clicks
        return result
    }

    interface Pad {
        fun requestButton(value: Char): Int
        fun copy(): Pad
    }

    private class HumanDirectionalPad(val clicks: MutableList<Char> = mutableListOf()) : Pad {
        override fun requestButton(value: Char): Int {
            clicks.add(value)
            return 1
        }

        override fun copy(): Pad = HumanDirectionalPad(clicks = clicks.toMutableList())
    }

    private class RobotDirectionalPad(
        var currentValue: Char = 'A',
        private val pad: Pad,
        val clicks: MutableList<Char> = mutableListOf(),
    ) : Pad {
        override fun requestButton(value: Char): Int {
            clicks.add(value)
            return directionalPadSourceTargetToPossibleMoves[currentValue to value]?.let { moveVariants ->
                val newValueToRequiredClicks = requestFromPad(pad, moveVariants, value)
                currentValue = newValueToRequiredClicks.first
                newValueToRequiredClicks.second
            } ?: error("no path found")
        }

        override fun copy(): Pad = RobotDirectionalPad(
            clicks = clicks.toMutableList(),
            currentValue = currentValue,
            pad = pad.copy()
        )
    }

    private class NumPadByRobot(
        private var currentValue: Char = 'A',
        private val pad: RobotDirectionalPad,
        val clicks: MutableList<Char> = mutableListOf()
    ) {
        fun requestButton(value: Char): Int {
            clicks.add(value)
            return numPadSourceTargetToPossibleMoves[currentValue to value]?.let { moveVariants ->
                val newValueToRequiredClicks =
                    requestFromPad(pad, moveVariants, value)
                currentValue = newValueToRequiredClicks.first
                newValueToRequiredClicks.second
            } ?: error("no path found")
        }
    }

    data class Move(val isHorizontal: Boolean, val steps: Int)

    companion object {
        val numPad = arrayOf(
            arrayOf('7', '8', '9'),
            arrayOf('4', '5', '6'),
            arrayOf('1', '2', '3'),
            arrayOf('X', '0', 'A')
        )

        private val numPadSourceTargetToPossibleMoves = padSourceTargetToPossibleMoves(numPad)
        private fun padSourceTargetToPossibleMoves(pad: Array<Array<Char>>) =
            pad.flatMapIndexed { ySource, sourceLine ->
                sourceLine.flatMapIndexed { xSource, source ->
                    pad.flatMapIndexed { yTarget, targetLine ->
                        targetLine.mapIndexed { xTarget, target ->
                            val deltaX = xTarget - xSource
                            val deltaY = yTarget - ySource
                            if (source == target) {
                                source to target to listOf(Move(true, 0) to Move(false, 0))
                            } else {
                                val xMove = Move(isHorizontal = true, steps = deltaX)
                                val yMove = Move(isHorizontal = false, steps = deltaY)
                                val legalMoveVariants = mutableListOf<Pair<Move, Move>>()
                                if (pad[ySource][xSource + deltaX] != 'X') {
                                    legalMoveVariants.add(xMove to yMove)
                                }
                                if (pad[ySource + deltaY][xSource] != 'X') {
                                    legalMoveVariants.add(yMove to xMove)
                                }
                                source to target to legalMoveVariants
                            }
                        }
                    }
                }
            }.associate { it.first to it.second }


        private val directionalPad = arrayOf(
            arrayOf('X', '^', 'A'),
            arrayOf('<', 'v', '>'),
        )

        private val directionalPadSourceTargetToPossibleMoves = padSourceTargetToPossibleMoves(directionalPad)
    }
}

fun requestFromPad(
    pad: Pad,
    moveVariants: List<Pair<Day21.Move, Day21.Move>>,
    value: Char
): Pair<Char, Int> {

    fun moveAndCount(usedPad: Pad, times: Int, buttonToPress: Char) = (0..<abs(times)).sumOf {
        usedPad.requestButton(buttonToPress)
    }

    fun move(move: Day21.Move, usedPad: Pad) = when {
        move.isHorizontal && move.steps < 0 -> moveAndCount(usedPad, move.steps, '<')
        move.isHorizontal && move.steps > 0 -> moveAndCount(usedPad, move.steps, '>')
        !move.isHorizontal && move.steps < 0 -> moveAndCount(usedPad, move.steps, '^')
        !move.isHorizontal && move.steps > 0 -> moveAndCount(usedPad, move.steps, 'v')
        else -> 0
    }

    val bestVariant = moveVariants.minBy {
        val copyPad = pad.copy()
        move(it.first, copyPad) + move(it.second, copyPad)
    }
    val xAndYSteps = move(bestVariant.first, pad) + move(bestVariant.second, pad)

    val aPress = pad.requestButton('A')
    return value to (xAndYSteps + aPress)
}