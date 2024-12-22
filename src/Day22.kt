fun main() {
    check(37327623L == Day22(readLines("day22test.txt")).part1())
    Day22(readLines("day22.txt")).part1().println()
}

class Day22(input: List<String>) {
    private val initialSecretNumbers = input.map { it.toLong() }
    fun part1(): Long {
        return initialSecretNumbers.map { initialSecretNumber ->
            val after2000 = (1..2000).fold(initialSecretNumber) { secretNumber, _ ->
                val step1Multiply = secretNumber * 64L
                val step1Mixed = step1Multiply.mix(secretNumber)
                val step1Pruned = step1Mixed.prune()
                val step2Division = step1Pruned / 32
                val step2Mixed = step2Division.mix(step1Pruned)
                val step2Pruned = step2Mixed.prune()
                val step3Multiply = step2Pruned * 2048
                val step3Mixed = step3Multiply.mix(step2Pruned)
                val step3Pruned = step3Mixed.prune()
                step3Pruned
            }
            after2000
        }.sum()
    }

    private fun Long.mix(oldSecret: Long) = this xor oldSecret // becomes new secret
    private fun Long.prune() = this % 16777216 // becomes new secret
}
