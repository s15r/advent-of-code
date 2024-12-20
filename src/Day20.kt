import java.util.*

fun main() {
    check(44 == Day20(readLines("day20test.txt"), 2).part1())
    Day20(readLines("day20.txt"), 100).part1().println()
}

class Day20(input: List<String>, private val minSavings: Int) {
    private val grid: List<List<Char>> = input.map { line -> line.map { it } }
    private val start = firstCoordinate('S')
    private val end = firstCoordinate('E')

    private val visitedToBestScore = mutableMapOf<Coordinate, Int>()
    private val toVisitInNormalGrid = PriorityQueue<Pair<Coordinate, Int>>(compareBy { it.second })

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
        val startPosition = start to 0
        toVisitInNormalGrid.addAll(nextSteps(startPosition.first, startPosition.second, visitedToBestScore))
        val normalBestPath = findStepsToTargetPositionFromStart(end)

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

    private fun findPathForWallPosition(skip: Coordinate, normalBestPathSteps: Int): Int {
        val cheatStartPositions = neighbourInGridNotBlocked(skip)
        val cheatEndPositions = neighbourInGridNotBlocked(skip)

        return cheatStartPositions.map { cheatStart ->
            cheatEndPositions.map { cheatEnd ->
                val savedSteps = savedStepWithCheat(cheatStart, normalBestPathSteps, cheatEnd)
                if (savedSteps >= minSavings) {
                    1
                } else {
                    0
                }
            }.sum()
        }.sum()
    }

    private fun savedStepWithCheat(cheatStart: Coordinate, normalBestPathSteps: Int, cheatEnd: Coordinate): Int {
        val startToCheatStart = findStepsToTargetPositionFromStart(cheatStart)
        val cheatEndToFinish = normalBestPathSteps - findStepsToTargetPositionFromStart(cheatEnd)
        val totalStepsWithCheat = startToCheatStart + cheatEndToFinish + 2
        val savedSteps = normalBestPathSteps - totalStepsWithCheat
        return savedSteps
    }

    private fun findStepsToTargetPositionFromStart(targetPosition: Coordinate): Int {
        while (toVisitInNormalGrid.isNotEmpty()) {
            val position = toVisitInNormalGrid.remove()
            toVisitInNormalGrid.addAll(nextSteps(position.first, position.second, visitedToBestScore))
            // calculate the entire grid, not only the best path
        }
        return visitedToBestScore[targetPosition] ?: error("target not found")
    }


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
        .filter { grid[it.y][it.x] != '#' }

    private fun neighboursInGrid(position: Coordinate) =
        listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0).asSequence().map {
            Coordinate(position.x + it.first, position.y + it.second)
        }.filter {
            it.x in 1..<grid[0].size - 1 && it.y in 1..<grid.size - 1
        }
}
