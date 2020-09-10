package com.example.aitopics.minesweeper

class MineSweeperPlayer {

    private val cellsList = mutableListOf<Cell>()
    private val minesCellsList = mutableListOf<Cell>()
    private val safeCellsList = mutableListOf<Cell>()

    fun initializeCells(cellsList: MutableList<Cell>){
        this.cellsList.addAll(cellsList)
        spreadMines()
        countNeighborMines()
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
    private fun countNeighborMines(){
        for(cell in cellsList){
            // If mine, increment the count of the nearby mines in each of the neighboring cells
            if(cell.isMine()){
                getNeighborCells(cell.row, cell.column).forEach{
                    it.incrementNearbyMines()
                }
            }
        }
    }

    private fun getNeighborCells(row: Int, column: Int): List<Cell>{
        return cellsList.filter {
            (it.column == column - 1 && it.row == row)
            || (it.column == column && it.row == row - 1)
            ||(it.column == column + 1 && it.row == row)
            ||(it.column == column && it.row == row + 1 )
            ||(it.column == column - 1 && it.row == row - 1)
            ||(it.column == column + 1 && it.row == row + 1)
            ||(it.column == column + 1 && it.row == row - 1)
            ||(it.column == column - 1 && it.row == row + 1)
        }

    }
}