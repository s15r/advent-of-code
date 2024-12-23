import java.util.*

fun main() {
    check(7 == Day23(readLines("day23test.txt")).part1())
    Day23(readLines("day23.txt")).part1().println()
    check("co,de,ka,ta" == Day23(readLines("day23test.txt")).part2())
    Day23(readLines("day23.txt")).part2().println()
}

class Day23(input: List<String>) {
    private val edges =
        input.flatMap { it.split("-").sorted().let { vertex -> mutableListOf(vertex[0] to vertex[1]) } }.toSet()
    private val edgesIndexed = edges.fold(mutableMapOf<String, MutableList<String>>()) { map, toFold ->
        indexEdge(map, toFold.first, toFold.second)
        indexEdge(map, toFold.second, toFold.first)
        map
    }

    private fun indexEdge(
        folding: MutableMap<String, MutableList<String>>,
        key: String,
        value: String,
    ) {
        (folding[key] ?: run {
            val newList = mutableListOf<String>()
            folding[key] = newList
            newList
        }).add(value)
    }

    fun part1(): Int {
        val result = edgesIndexed.keys.filter { it.startsWith("t") }
            .flatMap { tVertex ->
                val secondVertices = edgesIndexed[tVertex] ?: emptyList()
                secondVertices.flatMap { secondVertex ->
                    val thirdEdges = edgesIndexed[secondVertex] ?: emptyList()
                    thirdEdges.mapNotNull { thirdEdge ->
                        if (edgesIndexed[thirdEdge]?.contains(tVertex) == true) {
                            listOf(tVertex, secondVertex, thirdEdge).sorted()
                        } else {
                            null
                        }
                    }
                }
            }
            .toSet()
        return result.size
    }


    fun part2(): String {
        val visited = mutableSetOf<List<String>>() // sorted pair
        val stack = Stack<Pair<String, Set<Set<String>>>>()
        return edges.sortedBy { it.first }.map { edge ->
            val edgeSorted = edge.toList().sorted()
            if (!visited.contains(edgeSorted)) {
                stack.push(edge.first to mutableSetOf(mutableSetOf(edge.first, edge.second)))
                stack.push(edge.second to mutableSetOf(mutableSetOf(edge.first, edge.second)))
                dfs(stack, visited)
            } else {
                emptyList()
            }
        }.maxBy { it.size }.joinToString(",")
    }

    private fun dfs(
        stack: Stack<Pair<String, Set<Set<String>>>>,
        visited: MutableSet<List<String>>,
    ): List<String> {
        var largest = emptyList<String>()
        while (stack.isNotEmpty()) {
            val next = stack.pop()

            val vertex = next.first
            val nextVertices = edgesIndexed[vertex]
            val newSet = next.second.toMutableSet()
            nextVertices?.sorted()?.forEach { targetVertex ->
                val edgeSorted = listOf(vertex, targetVertex).sorted()
                if (!visited.contains(edgeSorted)) {
                    visited.add(edgeSorted)

                    val previousSetsExtendedWithVertex = next.second.filter { previousUniqueSet ->
                        nextVertices.containsAll(previousUniqueSet)
                    }.map { previousSetsWithVertexOverlap ->
                        previousSetsWithVertexOverlap + vertex
                    }
                    newSet.addAll(previousSetsExtendedWithVertex)
                    newSet.add(setOf(vertex, targetVertex))
                    stack.push(targetVertex to newSet)
                }
            }
            val newLargest = newSet.maxBy { it.size }
            if (largest.size < newLargest.size) {
                largest = newLargest.sorted()
            }
        }
        return largest
    }
}
