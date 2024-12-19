import Day19.Radix.Companion.charToRadix

fun main() {
    check(6 == Day19(readLines("day19test.txt")).part1())
    Day19(readLines("day19.txt")).part1().println()
}

class Day19(input: List<String>) {
    private val towels = Radix.toRadix(input[0].split(", "))
    private val patterns = input.drop(2)

    fun part1(): Int {
        var index = 0
        return patterns.count { pattern ->
            index++
            patternExists(pattern)
        }
    }

    private fun patternExists(pattern: String): Boolean {
        return resolvePattern(0, pattern, towels)
    }

    private fun resolvePattern(atIndex: Int, pattern: String, radix: Radix): Boolean {
        val charAsRadix = charToRadix[pattern[atIndex]] ?: error("not a radix")
        val currentRadix = radix.next[charAsRadix]
        if (currentRadix == null) {
            return false
        } else {
            return if (pattern.length - 1 == atIndex) {
                true // finished successfully
            } else {
                // continue current towel
                val recursiveWithCurrentCurrentTowel = resolvePattern(atIndex + 1, pattern, currentRadix)
                if (recursiveWithCurrentCurrentTowel) {
                    return true
                }
                if (currentRadix.isEnd) {
                    // try other towels
                    val recursiveWithNewTowel = resolvePattern(atIndex + 1, pattern, towels)
                    if (recursiveWithNewTowel) {
                        return true
                    }
                }
                return false
            }
        }
    }


    private class Radix(var isEnd: Boolean, val next: Array<Radix?> = Array(5) { null }) {

        private fun add(input: String, atRadix: Radix) {
            val nextChar = input[0]
            val radix = charToRadix[nextChar] ?: error("unknown radix char $nextChar")
            val isLastChar = input.length == 1
            if (atRadix.next[radix] == null) {
                atRadix.next[radix] = Radix(isLastChar)
            }
            if (!isLastChar) {
                add(input.drop(1), atRadix.next[radix]!!)
            } else {
                atRadix.next[radix]!!.isEnd = true
            }
        }

        companion object {
            val charToRadix = mapOf('w' to 0, 'u' to 1, 'b' to 2, 'r' to 3, 'g' to 4)
            fun toRadix(input: List<String>): Radix {
                val radix = Radix(false)
                input.forEach { radix.add(it, radix) }
                return radix
            }
        }
    }

}
