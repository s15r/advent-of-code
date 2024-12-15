fun main() {
    check(10092 == Day15("day15test.txt", isPart2 = false).solve())
    Day15("day15.txt", isPart2 = false).solve().println()
    check(9021 == Day15("day15test.txt", isPart2 = true).solve())
    Day15("day15.txt", isPart2 = true).solve().println()
}


class Day15(filename: String, private val isPart2: Boolean) {
    private val grid: MutableList<MutableList<Char>>
    private val moves: List<Char>

    init {
        val (grid, moves) = parseInput(readLines(filename))
        this.grid = grid
        this.moves = moves
    }

    fun solve(): Int {
        var position = grid.mapIndexedNotNull { y, line ->
            val x = line.mapIndexedNotNull { x, c -> if (c == '@') x else null }.firstOrNull()
            if (x != null) Pair(x, y) else null
        }.first()

        moves.forEach { move ->
            if (moveAndPushRecursive(position, move, true).first) {
                position = moveAndPushRecursive(position, move, false).second
            }
        }
        return grid.gpsSum()
    }

    private fun moveAndPushRecursive(
        position: Pair<Int, Int>,
        move: Char,
        dryRun: Boolean
    ): Pair<Boolean, Pair<Int, Int>> { // wasMoved -> newPosition
        val nextPoint = position.nextPoint(move)
        val nextPointValue = grid[nextPoint.second][nextPoint.first]
        return if (nextPointValue == '.') {
            if (!dryRun) grid.swap(position, nextPoint)
            true to nextPoint
        } else if (nextPointValue == 'O') {
            if (moveAndPushRecursive(nextPoint, move, dryRun).first) {
                if (!dryRun) grid.swap(position, nextPoint)
                true to nextPoint
            } else {
                // can't push
                false to position
            }
        } else if (nextPointValue in listOf('[', ']')) {
            val otherNextPoint = when (nextPointValue) {
                '[' -> nextPoint.copy(first = nextPoint.first + 1, nextPoint.second)
                ']' -> nextPoint.copy(first = nextPoint.first - 1, nextPoint.second)
                else -> error("Unknown other value $nextPointValue")
            }
            if (dryRun && (move == '>' || move == '<')) { // dryrun side way with two blocks
                moveAndPushRecursive(otherNextPoint, move, true).first to nextPoint
            } else if (moveAndPushRecursive(otherNextPoint, move, dryRun).first &&
                moveAndPushRecursive(nextPoint, move, dryRun).first
            ) {
                if (!dryRun) grid.swap(position, nextPoint)
                true to nextPoint
            } else {
                // can't push
                false to position
            }

        } else if (nextPointValue == '#') {
            // can't move
            false to position
        } else error("Unknown grid value $nextPointValue")
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
            val xSum = line.mapIndexedNotNull { x, c -> if (c == 'O' || c == '[') x + y * 100 else null }.sum()
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
                if (!isPart2) {
                    gridLines.add(line.toCharArray().toMutableList())
                } else {
                    gridLines.add(line.flatMap { c ->
                        when (c) {
                            '.' -> listOf('.', '.')
                            '#' -> listOf('#', '#')
                            'O' -> listOf('[', ']')
                            '@' -> listOf('@', '.')
                            else -> error("Unknown grid value $c")
                        }
                    }.toCharArray().toMutableList())
                }
            } else {
                moves.addAll(line.toCharArray().toList())
            }
        }
        return Pair(gridLines, moves)
    }
}