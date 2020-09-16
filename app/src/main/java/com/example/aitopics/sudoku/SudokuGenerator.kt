package com.example.aitopics.sudoku

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch

class SudokuGenerator(private val blocksList: List<SudokuBlock>) {

    /**
     * Solve the Constraint Satisfaction Problem (CSP)
     */
      suspend fun generateSudoku():  Boolean {
        setArcs()
        val backTrackResult = backtrack()
        chooseCellsToShowAndHide(backTrackResult)
        return backTrackResult != null
    }

    /**
     * The arcs are the connections between variables. In sudoku, the variables are the cells
     * and the connected cells are those who are on the same row or column
     */
    private fun setArcs(){
        for(block in blocksList){
            setBlocksNeighbors(block)
            setCellsNeighbors(block)
        }
    }

    private fun setBlocksNeighbors(block: SudokuBlock){
        block.neighborsList = getNeighborBlocks(block)

    }

    private fun setCellsNeighbors(block: SudokuBlock){
        for (cell in block.cellsList) {
            cell.neighborsList = getNeighborCells(cell)
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
    private fun consistent(assignment: MutableMap<Cell, Int>): Boolean{
        for(entry in assignment){
            for(otherEntry in assignment){
                if(entry.key == otherEntry.key)
                    continue
                if(entry.key.neighborsList.contains(otherEntry.key) && entry.value == otherEntry.value){
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
            if(consistent(assignment)){
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

    /**
     * Neighbor cells are those on the same row, column and block of the cell
     */
    private fun getNeighborCells(cell: Cell): List<Cell>{
        val neighbors = mutableListOf<Cell>()
        // Add cells in the same block
        cell.parent.cellsList.forEach {
            if(it != cell)
                neighbors.add(it)
        }
        // Add cells in same row or column
        for(block in cell.parent.neighborsList){
            for(neighborBlockCell in block.cellsList){
                if(areCellsInSameRowOrColumn(neighborBlockCell, cell)){
                    neighbors.add(neighborBlockCell)
                }
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

    private fun areCellsInSameRowOrColumn(potentialNeighbor: Cell, current: Cell): Boolean{
        return potentialNeighbor.row == current.row || potentialNeighbor.column == current.column
    }

    private fun chooseCellsToShowAndHide(backTrackResult: MutableMap<Cell, Int>?) {
        hideSomeCells(backTrackResult!!)
        showRestOfCells(backTrackResult)
    }

    /**
     * We will hide only 17 cell for the user to find their values
     */
    private fun hideSomeCells(assignment: MutableMap<Cell, Int>){
        for(i in 0..16){
            var randomCell = assignment.keys.random()
            while(randomCell.hidden){
                randomCell = assignment.keys.random()
            }
            randomCell.hidden = true
        }
    }

    private fun showRestOfCells(assignment: MutableMap<Cell, Int>){
        CoroutineScope(Main).launch {
            for(cell in assignment){
                cell.key.value = cell.value
                cell.key.showValue()
            }
        }
    }
}

