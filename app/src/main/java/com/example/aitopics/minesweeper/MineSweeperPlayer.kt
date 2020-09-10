package com.example.aitopics.minesweeper

class MineSweeperPlayer {

    private val cellsList = mutableListOf<Cell>()
    private val minesCellsList = mutableListOf<Cell>()
    private val safeCellsList = mutableListOf<Cell>()

    fun initializeCells(cellsList: MutableList<Cell>){
        this.cellsList.addAll(cellsList)
        spreadMines()
        getNeighborMines()
    }

    /**
     * This method spreads 10 mines across different random cells
     */
    private fun spreadMines(){
        val cloneCellsList = mutableListOf<Cell>()
        cloneCellsList.addAll(cellsList)

        var chosenMineCell: Cell
        for(i in 0 until 10){
            chosenMineCell = cloneCellsList.random()
            chosenMineCell.setMine()
            cloneCellsList.remove(chosenMineCell)

        }
    }

    /**
     * Loops over the non-mine cells to determine how many mine cells are next to it
     */
    private fun getNeighborMines(){
        for(cell in cellsList){
            // If mine, increment the count of the nearby mines in each of the neighboring cells
            if(cell.isMine()){
                incrementNeighborCellsIndicator(cell.row, cell.column)
            }
        }
    }

    private fun incrementNeighborCellsIndicator(row: Int, column: Int){
        val neighborCells = cellsList.filter {
            (it.column == column - 1 && it.row == row)
            || (it.column == column && it.row == row - 1)
            ||(it.column == column + 1 && it.row == row)
            ||(it.column == column && it.row == row + 1 )
            ||(it.column == column - 1 && it.row == row - 1)
            ||(it.column == column + 1 && it.row == row + 1)
            ||(it.column == column + 1 && it.row == row - 1)
            ||(it.column == column - 1 && it.row == row + 1)
        }.forEach{
            it.incrementNearbyMines()
        }
    }
}