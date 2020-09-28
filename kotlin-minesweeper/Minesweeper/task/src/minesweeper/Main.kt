package minesweeper

fun main() {

    MineSweeperGame().start()
}

class Tile(val location: Pair<Int, Int>, var containsMine: Boolean = false, var adjacentMineCount: Int = 0, var explored: Boolean = false, var marked: Boolean = false, var steppedOnMine: Boolean = false) {

    override fun toString(): String = when {
        steppedOnMine && containsMine -> "X"
        marked -> "*"
        !explored || containsMine -> "."
        else -> if (adjacentMineCount > 0) adjacentMineCount.toString() else {
            "/"
        }
    }
}

class MineSweeperGame {

    private var initialisedGrid = false
    private val grid: Array<Array<Tile>> = Array(9) { rowIndex ->
        Array(9) { tileIndex ->
            Tile(location = Pair(rowIndex, tileIndex))
        }
    }

    fun start() {
        print("How many mines do you want on the field? ")
        val mineCount = readLine()!!.toInt()
        draw()
        while (!completedGame()) {
            print("Set/unset mines marks or claim a cell as free: ")
            val inputs = readLine()!!.split(" ")
            val location = Pair(inputs[1].toInt() - 1, inputs[0].toInt() - 1)// my coordinate system is opposite, slightly confusing
            val tile = grid[location.first][location.second]

            when (inputs[2]) {
                "free" -> {
                    if (!initialisedGrid) {
                        initGrid(location, mineCount)
                        initialisedGrid = true
                    }
                    if (tile.containsMine) {
                        grid.flatten().forEach { it.steppedOnMine = true }
                    } else {
                        exploreTile(tile)
                    }
                }
                "mine" -> {
                    tile.marked = !tile.marked
                }
            }
            draw()
        }
        println(if (grid[0][0].steppedOnMine) "You stepped on a mine and failed!" else "Congratulations! You found all mines!")
    }

    private fun initGrid(firstFreeLocation: Pair<Int, Int>, mineCount: Int) {

        val bombGridLocations =
                (0..80).toList().shuffled()
                        .map { count -> Pair(count / 9, count % 9) }
                        .filter { it != firstFreeLocation }
                        .subList(0, mineCount)

        println(bombGridLocations.map { "(${it.second + 1}, ${it.first + 1})" })

        grid.flatten()
                .forEach { tile -> tile.containsMine = bombGridLocations.contains(tile.location) }

        grid.flatten().filter { it.containsMine }
                .forEach { tile ->
                    val adjacentTiles = findAdjacentTiles(tile)
                    adjacentTiles.forEach { if (!it.containsMine) it.adjacentMineCount++ }
                }

    }

    private fun exploreTile(tile: Tile) {
        tile.explored = true
        // extra condition from the output error, if exploring and we find a tile that is marked incorrectly we override it
        if (tile.marked && !tile.containsMine) {
            tile.marked = false
        }
        if (tile.adjacentMineCount == 0) {// only if we are not a numbered tile recurse
            findAdjacentTiles(tile)
                    .filter { !it.explored }
                    .forEach {
                        exploreTile(it)
                    }
        }
    }

    private fun completedGame(): Boolean {
        val steppedOnMine = grid[0][0].steppedOnMine
        val markedAllMinesCorrectly = grid.flatten().none { it.marked.xor(it.containsMine) }
        val unexploredAreBombs = grid.flatten().filter { !it.explored }.all { it.containsMine }

        return initialisedGrid && (steppedOnMine || markedAllMinesCorrectly || unexploredAreBombs)
    }

    private fun draw() {
        println(" |123456789|")
        println("-|---------|")
        grid.forEachIndexed { rowIndex, row ->
            print("${rowIndex + 1}|")
            row.forEach { tile ->
                print(tile)
            }
            println("|")
        }
        println("-|---------|")
    }

    private fun findAdjacentTiles(tile: Tile): List<Tile> {
        val location = tile.location
        return when {
            // corners
            location == Pair(0, 0) -> listOf(grid[0][1], grid[1][0], grid[1][1])
            location == Pair(0, 8) -> listOf(grid[0][7], grid[1][7], grid[1][8])
            location == Pair(8, 0) -> listOf(grid[7][0], grid[7][1], grid[8][1])
            location == Pair(8, 8) -> listOf(grid[8][7], grid[7][7], grid[7][8])
            // sides
            location.first == 0 -> listOf(grid[0][location.second - 1], grid[0][location.second + 1], grid[1][location.second - 1], grid[1][location.second], grid[1][location.second + 1])
            location.first == 8 -> listOf(grid[8][location.second - 1], grid[8][location.second + 1], grid[7][location.second - 1], grid[7][location.second], grid[7][location.second + 1])
            location.second == 0 -> listOf(grid[location.first - 1][0], grid[location.first + 1][0], grid[location.first + 1][1], grid[location.first][1], grid[location.first - 1][1])
            location.second == 8 -> listOf(grid[location.first - 1][8], grid[location.first + 1][8], grid[location.first + 1][7], grid[location.first][7], grid[location.first - 1][7])
            // middle
            else -> listOf(
                    grid[location.first + 1][location.second + 1],
                    grid[location.first + 1][location.second],
                    grid[location.first + 1][location.second - 1],
                    grid[location.first][location.second + 1],
                    grid[location.first][location.second - 1],
                    grid[location.first - 1][location.second + 1],
                    grid[location.first - 1][location.second],
                    grid[location.first - 1][location.second - 1]
            )
        }
    }
}