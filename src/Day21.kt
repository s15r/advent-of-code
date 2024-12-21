import kotlin.math.abs

fun main() {
    check((29 * 68L) == Day21(listOf("029A")).part1())
    check(126384L == Day21(readLines("day21test.txt")).part1())
    Day21(readLines("day21.txt")).part1().println()
    Day21(readLines("day21.txt")).part2().println()
}

private val numPad = arrayOf(
    arrayOf('7', '8', '9'),
    arrayOf('4', '5', '6'),
    arrayOf('1', '2', '3'),
    arrayOf('X', '0', 'A')
)

private val directionalPad = arrayOf(
    arrayOf('X', '^', 'A'),
    arrayOf('<', 'v', '>'),
)

data class Move(val isHorizontal: Boolean, val steps: Int)
private val numPadSourceTargetToPossibleMoves = padSourceTargetToPossibleMoves(numPad)
private val directionalPadSourceTargetToPossibleMoves = padSourceTargetToPossibleMoves(directionalPad)

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


class Day21(private val input: List<String>) {
    fun part1(): Long {
        val humanDirectionalPad = HumanDirectionalPad
        val numPad = NumPadByRobot(
            subPad = RobotDirectionalPad(
                subPad = RobotDirectionalPad(
                    subPad = humanDirectionalPad
                )
            )
        )

        val totalDistance = input.sumOf { line -> calculateComplexity(line, numPad) }
        return totalDistance
    }

    private fun calculateComplexity(line: String, numPad: NumPadByRobot): Long {
        val clicks = line.map { char ->
            numPad.requestNumPadButton(char)
        }.sum()
        val numericPart = line.mapNotNull { char -> if (char == 'A') null else char }.joinToString("").toInt()
        val result = numericPart * clicks
        return result
    }

    fun part2(): Long {
        val humanDirectionalPad = HumanDirectionalPad
        val numPad = NumPadByRobot(
            subPad = recursiveRobotDirectionalPad(25, humanDirectionalPad)
        )
        val totalDistance = input.sumOf { line -> calculateComplexity(line, numPad) }
        return totalDistance
    }

    private fun recursiveRobotDirectionalPad(remaining: Int, pad: Pad): RobotDirectionalPad {
        return if (remaining == 1) {
            RobotDirectionalPad(subPad = pad)
        } else {
            recursiveRobotDirectionalPad(remaining - 1, RobotDirectionalPad(subPad = pad))
        }
    }
}

private interface Pad {
    fun newValueToStepCount(
        moveVariants: List<Pair<Move, Move>>,
        value: Char
    ): Pair<Char, Long>
}

private object HumanDirectionalPad : Pad {
    override fun newValueToStepCount(
        moveVariants: List<Pair<Move, Move>>,
        value: Char
    ): Pair<Char, Long> {
        return value to moveVariants.minOf { abs(it.first.steps) + abs(it.second.steps) + 1L }
    }
}

private class RobotDirectionalPad(
    var currentValue: Char = 'A',
    private val subPad: Pad,
    val mem: MutableMap<Pair<Move, Move>, Long> = mutableMapOf()
) : Pad {

    override fun newValueToStepCount(
        moveVariants: List<Pair<Move, Move>>,
        value: Char
    ): Pair<Char, Long> {
        val bestVariantSteps = moveVariants.minOf {
            this.mem[it.first to it.second] ?: this.run {
                val steps = moveLateral(it.first) + moveLateral(it.second) + this.moveToButton('A')
                this.mem[it.first to it.second] = steps
                steps
            }
        }
        return value to bestVariantSteps
    }

    private fun moveLateral(move: Move): Long = when {
        move.isHorizontal && move.steps < 0 -> moveAndCount(move.steps, '<')
        move.isHorizontal && move.steps > 0 -> moveAndCount(move.steps, '>')
        !move.isHorizontal && move.steps < 0 -> moveAndCount(move.steps, '^')
        !move.isHorizontal && move.steps > 0 -> moveAndCount(move.steps, 'v')
        else -> 0
    }

    private fun moveAndCount(times: Int, buttonToPress: Char) = (0..<abs(times)).sumOf {
        this.moveToButton(buttonToPress)
    }

    private fun moveToButton(value: Char): Long {
        return directionalPadSourceTargetToPossibleMoves[currentValue to value]
            ?.let { moveVariants ->
                val newValueToRequiredClicks = subPad.newValueToStepCount(moveVariants, value)
                currentValue = newValueToRequiredClicks.first
                newValueToRequiredClicks.second
            } ?: error("no path found")
    }
}

private class NumPadByRobot(
    private var currentValue: Char = 'A',
    private val subPad: RobotDirectionalPad
) {
    fun requestNumPadButton(value: Char): Long {
        return numPadSourceTargetToPossibleMoves[currentValue to value]?.let { moveVariants ->
            val newValueToRequiredClicks = subPad.newValueToStepCount(moveVariants, value)
            currentValue = newValueToRequiredClicks.first
            newValueToRequiredClicks.second
        } ?: error("no path found")
    }
}