fun main() {
    check(7 == Day23(readLines("day23test.txt")).part1())
    Day23(readLines("day23.txt")).part1().println()
}

class Day23(input: List<String>) {
    private val edges = input.flatMap { it.split("-").let { vertex -> listOf(vertex[0] to vertex[1]) } }
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
}
