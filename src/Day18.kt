import java.util.*

fun main() {
    check(22 == Day18(readLines("day18test.txt"), 7, 12).part1())
    Day18(readLines("day18.txt"), 71, 1024).part1().println()
    Day18(readLines("day18test.txt"), 7, 12).part2().println()
    Day18(readLines("day18.txt"), 71, 1024).part2().println()
}

class Day18(input: List<String>, private val gridSize: Int, private val blockLimit: Int) {
    private val allBlocked = input.map { line ->
        line.split(",").let { Coordinate(it[0].toInt(), it[1].toInt()) }
    }

    private val blocked = allBlocked.take(blockLimit).toMutableSet()

    fun part1(): Int {
        var position = Coordinate(0, 0) to 0
        val toVisit = PriorityQueue<Pair<Coordinate, Int>>(compareBy { it.second })

        val visitedToBestScore = mutableMapOf<Coordinate, Int>()
        toVisit.addAll(nextSteps(position.first, position.second, visitedToBestScore))
        while (toVisit.isNotEmpty()) {
            position = toVisit.remove()
            toVisit.addAll(nextSteps(position.first, position.second, visitedToBestScore))
            if (position.first.x == gridSize - 1 && position.first.y == gridSize - 1) {
                return position.second
            }
        }
        return 0
    }

    fun part2(): String {
        var blocks = blockLimit
        while (blocks < allBlocked.size) {
            val nextBlock = allBlocked[blocks++]
            blocked.add(nextBlock)
            if (part1() == 0) {
                return nextBlock.let { "${it.x},${it.y}" }
            }
        }
        error("always a path found")
    }

    private fun nextSteps(position: Coordinate, currentScore: Int, visitedToBestScore: MutableMap<Coordinate, Int>): List<Pair<Coordinate, Int>> {
        return listOf(0 to 1, 1 to 0, 0 to -1, -1 to 0).map {
            Coordinate(position.x + it.first, position.y + it.second)
        }.filter {
            it.x in 0..<gridSize && it.y in 0..<gridSize &&
                    !blocked.contains(it)
        }.map { it to currentScore + 1 }
            .filter { new -> visitedToBestScore[new.first]?.let { it > new.second } ?: true }
            .onEach { visitedToBestScore[it.first] = it.second }
            .toList()
    }
}
