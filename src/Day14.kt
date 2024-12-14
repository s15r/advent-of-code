fun main() {
    check(12 == Day14(roomWidth = 11, roomHeight = 7, input = readLines("day14test.txt")).part1())
    Day14(roomWidth = 101, roomHeight = 103, readLines("day14.txt")).part1().println()
    Day14(roomWidth = 101, roomHeight = 103, readLines("day14.txt")).part2().println()
}


class Day14(val roomWidth: Int, val roomHeight: Int, input: List<String>) {
    private val robots = input.map { parseRobot(it) }

    private fun parseRobot(line: String): Robot {
        fun MatchResult.parseInt() = this.groups[0]!!.value.toInt()
        return "-?\\d+".toRegex().findAll(line).toList().let {
            Robot(Coordinate(it[0].parseInt(), it[1].parseInt()), it[2].parseInt(), it[3].parseInt())
        }
    }

    fun part1(): Int {
        return robots.map {
            it.move(100)
        }.groupBy { it.quadrant() }
            .mapNotNull {
                if (it.key != 0) it.value.size
                else null
            }.reduce { acc, p -> acc * p }
    }

    fun part2(): Int {
        (1..10000).map { seconds ->
            val result = robots.map {
                it.move(seconds)
            }
            val image = (0..<roomHeight).map {
                " ".repeat(roomWidth).toCharArray()
            }.toMutableList()

            result.forEach {
                image[it.y][it.x] = 'X'
            }
            val longLines = image.map { line ->
                val lineChars = line.filterIndexed { index, char ->
                    if (index == line.size - 1) false
                    else if (char == 'X' && (line[index + 1] == 'X')) true
                    else false
                }.count()
                lineChars
            }.count { it > 5 }

            if (longLines > 5) {
                image.forEach {
                    it.forEach { print(it) }
                    print("\n")
                }
                "".println()
                "--------------^ $seconds".println()
                "".println()
            }
        }
        return 0
    }

    private fun Coordinate.quadrant(): Int {
        val middleX = roomWidth - 1 - roomWidth / 2
        val middleY = roomHeight - 1 - roomHeight / 2
        return if (x == middleX || y == middleY) 0
        else if (x < middleX && y < middleY) 1
        else if (x > middleX && y < middleY) 2
        else if (x < middleX) 3
        else 4
    }

    private fun Robot.move(seconds: Int): Coordinate {
        return this.coordinate.copy(
            x = move(this.coordinate.x, this.vx, seconds, roomWidth),
            y = move(this.coordinate.y, this.vy, seconds, roomHeight)
        )
    }
}

private fun move(x: Int, vx: Int, times: Int, wrapX: Int) = ((x + vx * times) % wrapX)
    .let { if (it < 0) wrapX + it else it }

data class Robot(var coordinate: Coordinate, val vx: Int, val vy: Int)
data class Coordinate(val x: Int, val y: Int)
