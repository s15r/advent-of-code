import kotlin.math.abs

fun main() {
    check(14 == Day8(readLines("day8test.txt")).countAntinodes())
    Day8(readLines("day8.txt")).countAntinodes().println()
}

class Day8(private val input: List<String>) {
    private val frequencyToPoints = input.flatMapIndexed { y, line ->
        line.mapIndexed { x, value -> if (value != '.') FrequencyNode(x, y, value) else null }
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

    private fun antinodes(frequencyA: FrequencyNode, frequencyB: FrequencyNode): List<AntiNode> {
        val xDiff = abs(frequencyA.x - frequencyB.x)
        val yDiff = abs(frequencyA.y - frequencyB.y)
        return listOf(
            frequencyA.antiNode(frequencyB, xDiff, yDiff),
            frequencyB.antiNode(frequencyA, xDiff, yDiff),
        ).filter {
            it.x < input[0].length && it.y < input.size &&
                    it.x >= 0 && it.y >= 0
        }
    }

    private fun FrequencyNode.antiNode(other: FrequencyNode, xDiff: Int, yDiff: Int) =
        AntiNode(
            if (this.x < other.x) this.x - xDiff else this.x + xDiff,
            if (this.y < other.y) this.y - yDiff else this.y + yDiff
        )
}


data class FrequencyNode(val x: Int, val y: Int, val value: Char)
data class AntiNode(val x: Int, val y: Int)

