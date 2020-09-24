package com.example.aitopics.sudoku

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class SudokuGenerator(private val blocksList: List<SudokuBlock>) {

    private var backTrackResult: MutableMap<Cell, Int>? = null

    /**
     * Solve the Constraint Satisfaction Problem (CSP)
     */
      suspend fun generateSudoku():  Boolean {
        setArcs()
        backTrackResult = backtrack()
        backTrackResult?.let {
            setCellsValues(it)
            chooseAndShowClues(backTrackResult)
        }

        return backTrackResult != null
    }

    fun assistPlayer(){
        val randomCell = prioritizeAndChooseCell(backTrackResult!!)
        randomCell?.makeAIMove()
    }

    /**
     * The algorithm should first choose a cell that is not revealed yet. If all cells are revealed,
     * th algorithm should choose cells that are revealed but their isCorrectValueRevealed is false
     */
    private fun prioritizeAndChooseCell(backTrackResult: MutableMap<Cell, Int>): Cell?{
        var cellsMap = backTrackResult.filter {
            !it.key.isRevealed
        }
        // If all cells are revealed
        if (cellsMap.isEmpty()){
            cellsMap = backTrackResult.filter{
                !it.key.isCorrectValueRevealed
            }
        }

        return if(cellsMap.isNotEmpty()){
            cellsMap.keys.random()
        }
        else{
            null
        }
    }

    /**
     * The arcs are the connections between variables. In sudoku, the variables are the cells
     * and the connected cells are those who are on the same row or column.
     */
    private fun setArcs(){
        //If the game is reset, the arcs will still be the same, no need to set them again
        // So we check on a block and see if its connections is set, then the rest of the blocks and
        // cells are set too. If not, set the connections.
        if(blocksList[0].neighborsList.isEmpty()){
            for(block in blocksList){
                setBlocksNeighbors(block)
                setCellsNeighbors(block)
            }
        }

    }

    private fun setBlocksNeighbors(block: SudokuBlock){
        block.neighborsList = getNeighborBlocks(block)

    }

    private fun setCellsNeighbors(block: SudokuBlock){
        for (cell in block.cellsList) {
            cell.setNeighborCells()
        }
    }

    private fun isAssignmentComplete(assignment: MutableMap<Cell, Int>): Boolean{
        if(assignment.size < 81){
            return false
        }
        return true
    }

    /**
     * Checks the consistency of assigned variables values. If two variables (cells) on the same row
     * or column or in the same block have the same value, this method returns false as the
     * constraint is violated. Otherwise, it returns true
     */
     fun isAssignmentConsistent(assignment: MutableMap<Cell, Int>): Boolean{
        // Convert the map keys to list to be able to navigate the map by index
        val keys = assignment.keys.toList()
        for(entryIndex in 0 until keys.size - 1){
            for(otherEntryIndex in entryIndex + 1 until keys.size){
                // If cells are neighbors and have the same value, return false
                if(keys[entryIndex].neighborsList.contains(keys[otherEntryIndex])
                    && assignment[keys[entryIndex]] == assignment[keys[otherEntryIndex]]){
                    return false
                }
            }
        }
        return true
    }

    /**
     * To randomize the sudoku, I shuffle the values list
     * This is not efficient in backtracking, but the efficient way will always yield the same
     * sudoku structure
     */
    private fun getDomainValues(cell: Cell): List<Int>{
        return cell.possibleValues.shuffled()
    }

    /**
     * Select the first unassigned variable
     */
    private fun selectUnassignedVariable(assignment: MutableMap<Cell, Int>): Cell?{
        for(block in blocksList){
            for(cell in block.cellsList){
                if(!assignment.contains(cell)){
                    return cell
                }
            }
        }
        return null
    }

    private fun backtrack(assignment: MutableMap<Cell, Int> = mutableMapOf()): MutableMap<Cell, Int>?{
        if(isAssignmentComplete(assignment)){
            return assignment
        }
        val nextVariable = selectUnassignedVariable(assignment)!!
        for(value in getDomainValues(nextVariable)){
            assignment[nextVariable] = value
            // If the assignment is consistent till now, take another step by calling backtrack()
            // Otherwise, choose another value for this variable
            if(isAssignmentConsistent(assignment)){
                val result = backtrack(assignment)
                if(!result.isNullOrEmpty()){
                    return result
                }
            }
            // remove the proposed assignment to try new one
            assignment.remove(nextVariable)
        }
        // No solution could be found with this assignment
        return null
    }

    /**
     * Block neighbors are those on the same row or column of that block
     */
    private fun getNeighborBlocks(block: SudokuBlock): List<SudokuBlock>{
        val neighbors = mutableListOf<SudokuBlock>()
        for(entry in blocksList){
            if(entry == block){
                continue
            }
            if(areBlocksInSameColumn(entry, block) || areBlocksInSameRow(entry,block)){
                neighbors.add(entry)
            }
        }
        return neighbors
    }

    private fun areBlocksInSameColumn(potentialNeighbor: SudokuBlock, current: SudokuBlock): Boolean{
        return potentialNeighbor.column == current.column &&
                (potentialNeighbor.row == current.row + 1 || potentialNeighbor.row == current.row + 2
                        || potentialNeighbor.row == current.row - 1  || potentialNeighbor.row == current.row - 2)
    }

    private fun areBlocksInSameRow(potentialNeighbor: SudokuBlock, current: SudokuBlock): Boolean{
        return potentialNeighbor.row == current.row &&
                (potentialNeighbor.column == current.column + 1 || potentialNeighbor.column == current.column + 2
                        || potentialNeighbor.column == current.column - 1  || potentialNeighbor.column == current.column - 2)
    }

    private fun setCellsValues(backTrackResult: MutableMap<Cell, Int>) {
        for(entry in backTrackResult.keys){
            entry.correctValue = backTrackResult[entry]!!
        }
    }

    private fun chooseAndShowClues(backTrackResult: MutableMap<Cell, Int>?){
        val cellsToShow = chooseCellsToShow(backTrackResult!!)
        showChosenCells(cellsToShow)
    }

    /**
     * We will show only 17 cell for the user as clues
     */
    private fun chooseCellsToShow(assignment: MutableMap<Cell, Int>): MutableList<Cell> {
        var randomCell: Cell?
        val randomCellsList = mutableListOf<Cell>()
        for(i in 0..16){
            randomCell = assignment.keys.random()
            while(randomCell!!.isClue){
                randomCell = assignment.keys.random()
            }
            randomCell.isClue = true
            randomCellsList.add(randomCell)
        }
        return randomCellsList
    }

    private fun showChosenCells(cellsList: MutableList<Cell>){
        CoroutineScope(Main).launch {
            cellsList.forEach {
                it.showCell()
            }
        }
    }
}

