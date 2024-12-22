import java.util.LinkedList

fun main() {
    check(37327623L == Day22(readLines("day22test.txt")).part1())
    Day22(readLines("day22.txt")).part1().println()
    check(23L == Day22(listOf("1", "2", "3", "2024")).part2())
    Day22(readLines("day22.txt")).part2().println()
}

class Day22(input: List<String>) {
    private val initialSecretNumbers = input.map { it.toLong() }
    fun part1(): Long {
        return initialSecretNumbers.map { initialSecretNumber ->
            val after2000 = (1..2000).fold(initialSecretNumber) { secretNumber, _ ->
                nextSecretNumber(secretNumber)
            }
            after2000
        }.sum()
    }

    private fun nextSecretNumber(secretNumber: Long): Long {
        val step1Multiply = secretNumber * 64L
        val step1Mixed = step1Multiply.mix(secretNumber)
        val step1Pruned = step1Mixed.prune()
        val step2Division = step1Pruned / 32
        val step2Mixed = step2Division.mix(step1Pruned)
        val step2Pruned = step2Mixed.prune()
        val step3Multiply = step2Pruned * 2048
        val step3Mixed = step3Multiply.mix(step2Pruned)
        val step3Pruned = step3Mixed.prune()
        return step3Pruned
    }

    fun part2(): Long {
        val globalScore = mutableMapOf<FourChanges, Long>()
        initialSecretNumbers.map { initialSecretNumber ->
            val after2000 = (1..2000).fold(
                SecretNumberCarry(
                    initialSecretNumber,
                    initialSecretNumber.toPrice(),
                    LinkedList(),
                    mutableMapOf(),
                    globalScore
                )
            ) { carry, _ ->
                val nextSecret = nextSecretNumber(carry.previousSecret)
                val nextPrice = nextSecret.toPrice()
                val change = nextPrice - carry.previousPrice
                addNextChange(change, nextPrice, carry.changes, carry.changesToFirstPrice, globalScore)
                SecretNumberCarry(nextSecret, nextPrice, carry.changes, carry.changesToFirstPrice, globalScore)
            }
            after2000
        }
        return globalScore.maxOf { it.value }
    }

    private fun addNextChange(
        change: Int,
        nextPrice: Int,
        changes: LinkedList<Int>,
        changesToFirstPrice: MutableMap<FourChanges, Int>,
        globalScore: MutableMap<FourChanges, Long>
    ) {
        changes.add(change)
        if (changes.size >= 4) {
            val fourChanges = FourChanges(
                changes[changes.size - 4],
                changes[changes.size - 3],
                changes[changes.size - 2],
                changes[changes.size - 1]
            )
            changesToFirstPrice[fourChanges] ?: run {
                globalScore[fourChanges] = (globalScore[fourChanges] ?: 0) + nextPrice
                changesToFirstPrice[fourChanges] = nextPrice
            }
            changes.remove()
        }
    }

    data class SecretNumberCarry(
        val previousSecret: Long,
        val previousPrice: Int,
        val changes: LinkedList<Int>,
        val changesToFirstPrice: MutableMap<FourChanges, Int>,
        val globalScore: MutableMap<FourChanges, Long>,
    )

    data class FourChanges(val a: Int, val b: Int, val c: Int, val d: Int)

    private fun Long.toPrice(): Int {
        val asString = this.toString()
        return asString[asString.length - 1].code - '0'.code
    }

    private fun Long.mix(oldSecret: Long) = this xor oldSecret // becomes new secret
    private fun Long.prune() = this % 16777216 // becomes new secret
}
