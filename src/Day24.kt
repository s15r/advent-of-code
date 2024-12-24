fun main() {
    check(2024L == Day24(readLines("day24test.txt")).part1())
    Day24(readLines("day24.txt")).part1().println()
}

class Day24(input: List<String>) {
    private var gates = input.mapNotNull { line ->
        if (line.contains(": ")) line.split(": ")
            .let { it[0] to it[1].toInt() } else null
    }.toMap().toMutableMap()

    private var operations = input.mapNotNull { line ->
        if (line.contains("-> ")) {
            val arrowSplit = line.split("-> ")
            val opsFrag = arrowSplit[0].split(" ")
            Operation(opsFrag[0], opsFrag[2], opsFrag[1], arrowSplit[1].trim())
        } else null
    }

    private val targetGateToOperation = operations.associateBy { it.targetGate }

    fun part1(): Long {
        targetGateToOperation.filter { it.key.startsWith("z") }.forEach { operation ->
            resolveOperation(operation.value)
        }
        return readResult()
    }

    private fun resolveOperation(operation: Operation): Int {
        val gate1 = gates[operation.gate1] ?: run { resolveOperation(targetGateToOperation[operation.gate1]!!) }
        val gate2 = gates[operation.gate2] ?: run { resolveOperation(targetGateToOperation[operation.gate2]!!) }
        val result = when (operation.operation) {
            "AND" -> gate1 and gate2
            "OR" -> gate1 or gate2
            "XOR" -> gate1 xor gate2
            else -> error("Unknown operation")
        }
        gates[operation.targetGate] = result
        return result
    }

    private fun readResult(): Long {
        val bitString = gates.filter { it.key.startsWith("z") }
            .toList()
            .sortedBy { it.first }
            .map { it.second }.reversed()
            .joinToString("")

        return bitString.toLong(2)
    }

    private data class Operation(val gate1: String, val gate2: String, val operation: String, val targetGate: String)
}
