package com.example.aitopics.sudoku

class SudokuGenerator(private val blocksList: List<SudokuBlock>) {

    init{
        generateSudoku()
    }

    /**
     * Enforce arc consistency, then solve the Constraint Satisfaction Problem (CSP)
     */
    private fun generateSudoku(){
        setBlocksNeighbors()
        runAC3()
        backtrack()
        for(block in blocksList){
            block.cellsList.forEach {
                it.showValue()
            }
        }
    }

    private fun setBlocksNeighbors(){
        for(block in blocksList){
            block.neighborsList = getNeighborBlocks(block)
        }
    }

    private fun revise(x: Cell, y: Cell): Boolean{
        var revised = false
        val tobe_removed = mutableListOf<Int>()
        for(x_value in x.possibleValues){
            var foundValidValue = false
            for(y_value in y.possibleValues){
                if(y_value != x_value){
                    foundValidValue = true
                    break
                }
            }
            if(!foundValidValue){
                revised = true
                tobe_removed.add(x_value)
            }

            tobe_removed.forEach {
                x.possibleValues.remove(it)
            }
        }
        return revised
    }

    private fun runAC3(/*arcs: List<Pair<Cell, Cell>>? = null*/): Boolean{
        //if(arcs.isNullOrEmpty()){
            val arcs = getArcs()
        //}
        for(arc in arcs){
            if(revise(arc.first, arc.second)){
                if(arc.first.possibleValues.isNullOrEmpty()){
                    return false
                }
                for(neighbor in arc.first.neighborsList){
                    if(neighbor != arc.second){
                        arcs.add(Pair(neighbor, arc.first))
                    }
                }
            }
        }
        return true
    }

    private fun getArcs(): MutableList<Pair<Cell, Cell>>{
        val arcs = mutableListOf<Pair<Cell, Cell>>()
        for(block in blocksList){
            for(cell in block.cellsList){
                cell.neighborsList = getNeighborCells(cell)
                cell.neighborsList.forEach {
                    //if(!arcs.contains(Pair(it,cell))){
                        arcs.add(Pair(cell, it))
                    //}
                }
            }
        }
        return arcs
    }

    private fun assignmentComplete(assignment: MutableMap<Cell, Int>): Boolean{
        if(assignment.size < 81){
            return false
        }
        return true
    }

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

    private fun orderDomainValues(cell: Cell): List<Int>{
        return cell.possibleValues
    }

    private fun selectUnassignedVariable(assignment: MutableMap<Cell, Int>): Cell{
        var selectedVariable: Cell? = null
        for(block in blocksList){
            for(cell in block.cellsList){
                if(!assignment.contains(cell)){
                    return cell
                }
            }
        }
        return selectedVariable!!
    }

    private fun backtrack(assignment: MutableMap<Cell, Int> = mutableMapOf()): MutableMap<Cell, Int>?{
        if(assignmentComplete(assignment)){
            for(cell in assignment){
                cell.key.value = cell.value
                cell.key.showValue()
            }
            return assignment
        }
        val nextVariable = selectUnassignedVariable(assignment)
        for(value in orderDomainValues(nextVariable)){
            assignment[nextVariable] = value
            if(consistent(assignment)){
                val result = backtrack(assignment)
                if(!result.isNullOrEmpty()){
                    return result
                }
                assignment.remove(nextVariable)
            }
            assignment.remove(nextVariable)
        }
        return null
    }

    private fun getNeighborBlocks(block: SudokuBlock): List<SudokuBlock>{
        val neighbors = mutableListOf<SudokuBlock>()
        for(entry in blocksList){
            if(entry == block){
                continue
            }
            if(areInSameColumn(entry, block) || areInSameRow(entry,block)){
                neighbors.add(entry)
            }
        }
        return neighbors
    }

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
                if(areInSameColumn(neighborBlockCell, cell) || areInSameRow(neighborBlockCell,cell)){
                    neighbors.add(neighborBlockCell)
                }
            }
        }
        return neighbors
    }

    private fun <T: Any> areInSameColumn(potentialNeighbor: T, current: T): Boolean{
        if(potentialNeighbor is Cell && current is Cell){
            return potentialNeighbor.column == current.column/*&&
                    (potentialNeighbor.row == current.row + 1 || potentialNeighbor.row == current.row + 2
                            || potentialNeighbor.row == current.row - 1  || potentialNeighbor.row == current.row - 2)*/
        }
        else if(potentialNeighbor is SudokuBlock && current is SudokuBlock){
            return potentialNeighbor.column == current.column &&
                    (potentialNeighbor.row == current.row + 1 || potentialNeighbor.row == current.row + 2
                            || potentialNeighbor.row == current.row - 1  || potentialNeighbor.row == current.row - 2)
        }
        return false
    }

    private fun <T: Any> areInSameRow(potentialNeighbor: T, current: T): Boolean{
        if(potentialNeighbor is Cell && current is Cell){
            return potentialNeighbor.row == current.row/*potentialNeighbor.row == current.row &&
                    (potentialNeighbor.column == current.column + 1 || potentialNeighbor.column == current.column + 2
                            || potentialNeighbor.column == current.column - 1  || potentialNeighbor.column == current.column - 2)*/
        }
        else if(potentialNeighbor is SudokuBlock && current is SudokuBlock){
            return potentialNeighbor.row == current.row &&
                    (potentialNeighbor.column == current.column + 1 || potentialNeighbor.column == current.column + 2
                            || potentialNeighbor.column == current.column - 1  || potentialNeighbor.column == current.column - 2)
        }
        return false
    }
}

