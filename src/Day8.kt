import kotlin.math.abs

fun main() {
    check(14 == Day8(readLines("day8test.txt"), onlyDirectPair = true).countAntinodes())
    Day8(readLines("day8.txt"), onlyDirectPair = true).countAntinodes().println()
    check(34 == Day8(readLines("day8test.txt"), onlyDirectPair = false).countAntinodes())
    Day8(readLines("day8.txt"), onlyDirectPair = false).countAntinodes().println()
}

class Day8(private val input: List<String>, private val onlyDirectPair: Boolean) {
    private val frequencyToPoints = input.flatMapIndexed { y, line ->
        line.mapIndexed { x, value -> if (value != '.') FrequencyNode(Point(x, y), value) else null }
    }
        .filterNotNull()
        .groupBy { it.value }

    fun countAntinodes(): Int = frequencyToPoints.flatMap {
        antiNodesOfFrequency(it.value)
    }.distinct()
        .count()

    private fun antiNodesOfFrequency(frequencyNodes: List<FrequencyNode>) =
        frequencyNodes.indices.flatMap { v ->
            frequencyNodes.indices.flatMap { h ->
                if (h > v) antinodes(frequencyNodes[v], frequencyNodes[h]) else emptyList()
            }
        }

    private fun antinodes(frequencyA: FrequencyNode, frequencyB: FrequencyNode): List<Point> {
        val xDiff = abs(frequencyA.point.x - frequencyB.point.x)
        val yDiff = abs(frequencyA.point.y - frequencyB.point.y)
        return if (onlyDirectPair) {
            directAntinodes(frequencyA, frequencyB, xDiff, yDiff)
        } else {
            recursiveAntinodes(frequencyA, frequencyB, xDiff, yDiff) +
                    recursiveAntinodes(frequencyB, frequencyA, xDiff, yDiff)
        }
    }

    private fun recursiveAntinodes(
        fromFrequency: FrequencyNode,
        toFrequency: FrequencyNode,
        xDiff: Int,
        yDiff: Int
    ): MutableList<Point> {
        var antinode = fromFrequency.point
        val antinodes = mutableListOf(antinode)
        var nextAntinode = antinode.antinode(toFrequency.point, xDiff, yDiff)
        while (nextAntinode.isInGrid()) {
            antinodes.add(nextAntinode)
            val previous = antinode
            antinode = nextAntinode
            nextAntinode = antinode.antinode(previous, xDiff, yDiff)
        }
        return antinodes
    }

    private fun directAntinodes(
        frequencyA: FrequencyNode,
        frequencyB: FrequencyNode,
        xDiff: Int,
        yDiff: Int
    ) = listOf(
        frequencyA.point.antinode(frequencyB.point, xDiff, yDiff),
        frequencyB.point.antinode(frequencyA.point, xDiff, yDiff),
    ).filter { it.isInGrid() }

    private fun Point.isInGrid() = this.x < input[0].length && this.y < input.size &&
            this.x >= 0 && this.y >= 0

    private fun Point.antinode(other: Point, xDiff: Int, yDiff: Int) =
        Point(
            if (this.x < other.x) this.x - xDiff else this.x + xDiff,
            if (this.y < other.y) this.y - yDiff else this.y + yDiff
        )
}

data class FrequencyNode(val point: Point, val value: Char)
data class Point(val x: Int, val y: Int)
