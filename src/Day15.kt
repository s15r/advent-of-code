fun main() {
    check( 10092 == Day15("day15test.txt").part1())
    Day15("day15.txt").part1().println()
}


class Day15(filename: String) {
    private val grid: MutableList<MutableList<Char>>
    private val moves: List<Char>

    init {
        val (grid, moves) = parseInput(readLines(filename))
        this.grid = grid
        this.moves = moves
    }

    fun part1(): Int {
        var position = grid.mapIndexedNotNull { y, line ->
            val x = line.mapIndexedNotNull { x, c -> if (c == '@') x else null }.firstOrNull()
            if (x != null) Pair(x, y) else null
        }.first()

        moves.forEach { move ->
            position = moveAndPushRecursive(position, move).second
        }
        return grid.gpsSum()
    }

    private fun moveAndPushRecursive(
        position: Pair<Int, Int>,
        move: Char
    ): Pair<Boolean, Pair<Int, Int>> { // wasMoved -> newPosition
        val nextPoint = position.nextPoint(move)
        return if (grid[nextPoint.second][nextPoint.first] == '.') {
            grid.swap(position, nextPoint)
            true to nextPoint
        } else if (grid[nextPoint.second][nextPoint.first] == 'O') {
            if (moveAndPushRecursive(nextPoint, move).first) {
                grid.swap(position, nextPoint)
                true to nextPoint
            } else {
                // can't push
                false to position
            }
        } else if (grid[nextPoint.second][nextPoint.first] == '#') {
            // can't move
            false to position
        } else error("Unknown grid value ${grid[nextPoint.second][nextPoint.first]}")
    }

    private fun Pair<Int, Int>.nextPoint(move: Char): Pair<Int, Int> {
        return when (move) {
            '>' -> this.copy(first = first + 1, second)
            '<' -> this.copy(first = first - 1, second)
            '^' -> this.copy(first = first, second - 1)
            'v' -> this.copy(first = first, second + 1)
            else -> error("Unknown move $move")
        }
    }

    private fun MutableList<MutableList<Char>>.swap(a: Pair<Int, Int>, b: Pair<Int, Int>) {
        val aVal = this[a.second][a.first]
        val bVal = this[b.second][b.first]
        this[a.second][a.first] = bVal
        this[b.second][b.first] = aVal
    }

    private fun MutableList<MutableList<Char>>.gpsSum(): Int {
        return this.mapIndexed { y, line ->
            val xSum = line.mapIndexedNotNull { x, c -> if (c == 'O') x + y * 100 else null }.sum()
            xSum
        }.sum()
    }

    private fun parseInput(input: List<String>): Pair<MutableList<MutableList<Char>>, List<Char>> {
        var isGrid = true
        val moves = mutableListOf<Char>()
        val gridLines = mutableListOf<MutableList<Char>>()
        for (line in input) {
            if (isGrid && line.isEmpty()) {
                isGrid = false
            }
            if (isGrid) {
                gridLines.add(line.toCharArray().toMutableList())
            } else {
                moves.addAll(line.toCharArray().toList())
            }
        }
        return Pair(gridLines, moves)
    }

}