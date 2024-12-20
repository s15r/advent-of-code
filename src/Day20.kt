import java.util.*
import kotlin.math.abs

fun main() {
    check(44 == Day20(readLines("day20test.txt"), maxCheatTime = 2, minSavings = 2).part1())
    Day20(readLines("day20.txt"), maxCheatTime = 2, minSavings = 100).part1().println()

    check(44 == Day20(readLines("day20test.txt"), maxCheatTime = 2, minSavings = 2).part2())
    check(1422 == Day20(readLines("day20.txt"), maxCheatTime = 2, minSavings = 100).part2())
    Day20(readLines("day20.txt"), maxCheatTime = 20, minSavings = 100).part2().println()
}

class Day20(input: List<String>, private val maxCheatTime: Int, private val minSavings: Int) {
    private val grid: List<List<Char>> = input.map { line -> line.map { it } }
    private val start = firstCoordinate('S')
    private val end = firstCoordinate('E')

    private val visitedToBestScore = mutableMapOf<Coordinate, Int>()
    private val toVisitInNormalGrid = PriorityQueue<Pair<Coordinate, Int>>(compareBy { it.second })
    private val cheatPathToSavings = mutableMapOf<Pair<Coordinate, Coordinate>, Int>()

    private fun firstCoordinate(lookupChar: Char) =
        grid.mapIndexedNotNull { y, line ->
            line.mapIndexedNotNull { x, c ->
                if (c == lookupChar) Coordinate(
                    x,
                    y
                ) else null
            }.firstOrNull()
        }.first()

    fun part1(): Int {
        val normalBestPath = findNormalBestPathSteps()

        // skipping borders, check each block
        val result = grid.mapIndexed { y, line ->
            if (y > 0 && y < grid.size - 1) {
                line.mapIndexed { x, c ->
                    if (x > 0 && x < line.size - 1 && c == '#') {
                        findPathForWallPosition(Coordinate(x, y), normalBestPath)
                    } else {
                        0
                    }
                }.sum()
            } else 0
        }.sum()

        return result
    }

    fun part2(): Int {
        val normalBestPathSteps = findNormalBestPathSteps()

        return (1..grid.size).flatMap { y ->
            (1..<grid[0].size).mapNotNull { x ->
                val cheatStart = Coordinate(x, y)
                if (cheatStart.isInGrid() && !cheatStart.isBlocked()) {
                    val cheatStartEndPair = findStartEndPair(x, y, cheatStart)
                    cheatStartEndPair.sumOf {
                        calcUniqueSavings(it, normalBestPathSteps)
                    }
                } else null
            }
        }.sum()
    }

    private fun findStartEndPair(
        x: Int,
        y: Int,
        cheatStart: Coordinate,
    ): List<Pair<Coordinate, Coordinate>> {
        return (2..maxCheatTime).flatMap { cheatSteps ->
            (-cheatSteps..cheatSteps).flatMap { xCheatTime ->
                val yMaxCheatTime = maxCheatTime - abs(xCheatTime)
                (-yMaxCheatTime..yMaxCheatTime).mapNotNull { yCheatTime ->
                    val cheatEnd = Coordinate(x + xCheatTime, y + yCheatTime)
                    if (cheatEnd.isInGrid() && !cheatEnd.isBlocked()) {
                        val cheatStartEnd = cheatStart to cheatEnd
                        cheatStartEnd
                    } else {
                        null
                    }
                }
            }
        }
    }

    private fun calcUniqueSavings(
        cheatStartEnd: Pair<Coordinate, Coordinate>,
        normalBestPathSteps: Int,
    ): Int {
        return if (!cheatPathToSavings.containsKey(cheatStartEnd)) {
            val cheatSteps =
                abs(cheatStartEnd.first.x - cheatStartEnd.second.x) + abs(cheatStartEnd.first.y - cheatStartEnd.second.y)
            val savedSteps =
                savedStepWithCheat(cheatStartEnd.first, cheatStartEnd.second, normalBestPathSteps, cheatSteps)
            return if (savedSteps >= minSavings) {
                cheatPathToSavings[cheatStartEnd] = savedSteps
                1
            } else {
                cheatPathToSavings[cheatStartEnd] = 0
                0
            }
        } else {
            0 // don't count twice
        }
    }

    private fun findNormalBestPathSteps(): Int {
        val startPosition = start to 0
        toVisitInNormalGrid.addAll(nextSteps(startPosition.first, startPosition.second, visitedToBestScore))
        val normalBestPath = findStepsToTargetPositionFromStart(end)
        return normalBestPath
    }

    private fun findPathForWallPosition(skip: Coordinate, normalBestPathSteps: Int): Int {
        neighboursInGrid(skip)

        val cheatStartPositions = neighbourInGridNotBlocked(skip)
        val cheatEndPositions = neighbourInGridNotBlocked(skip)

        return cheatStartPositions.map { cheatStart ->
            cheatEndPositions.map { cheatEnd ->
                val savedSteps = savedStepWithCheat(cheatStart, cheatEnd, normalBestPathSteps, 2)
                if (savedSteps >= minSavings) {
                    1
                } else {
                    0
                }
            }.sum()
        }.sum()
    }

    private fun savedStepWithCheat(
        cheatStart: Coordinate,
        cheatEnd: Coordinate,
        normalBestPathSteps: Int,
        cheatSteps: Int
    ): Int {
        val startToCheatStart = stepsToTargetFromStart(cheatStart)
        val cheatEndToFinish = normalBestPathSteps - stepsToTargetFromStart(cheatEnd)
        val totalStepsWithCheat =
            startToCheatStart + cheatEndToFinish + cheatSteps
        val savedSteps = normalBestPathSteps - totalStepsWithCheat
        return savedSteps
    }

    private fun findStepsToTargetPositionFromStart(targetPosition: Coordinate): Int {
        while (toVisitInNormalGrid.isNotEmpty()) {
            val position = toVisitInNormalGrid.remove()
            toVisitInNormalGrid.addAll(nextSteps(position.first, position.second, visitedToBestScore))
            // calculate the entire grid, not only the best path
        }
        return stepsToTargetFromStart(targetPosition)
    }

    private fun stepsToTargetFromStart(targetPosition: Coordinate) =
        visitedToBestScore[targetPosition] ?: error("target not found")


    private fun nextSteps(
        position: Coordinate,
        currentScore: Int,
        visitedToBestScore: MutableMap<Coordinate, Int>
    ): List<Pair<Coordinate, Int>> {
        return neighbourInGridNotBlocked(position)
            .map { it to currentScore + 1 }
            .filter { new -> visitedToBestScore[new.first]?.let { it > new.second } ?: true }
            .onEach { visitedToBestScore[it.first] = it.second }
            .toList()
    }

    private fun neighbourInGridNotBlocked(position: Coordinate) = neighboursInGrid(position)
        .filter { !it.isBlocked() }

    private fun neighboursInGrid(position: Coordinate) =
        listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0).asSequence().map {
            Coordinate(position.x + it.first, position.y + it.second)
        }.filter {
            it.isInGrid()
        }

    private fun Coordinate.isInGrid() = x in 1..<grid[0].size - 1 && y in 1..<grid.size - 1
    private fun Coordinate.isBlocked() = grid[y][x] == '#'
}
