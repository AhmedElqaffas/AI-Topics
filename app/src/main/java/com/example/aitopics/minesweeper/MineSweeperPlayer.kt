package com.example.aitopics.minesweeper

class MineSweeperPlayer {

    companion object{
        const val WON = 1
        const val LOST = 0
    }

    private val cellsList = mutableListOf<Cell>()
    private val minesCellsList = mutableListOf<Cell>()
    private val safeCellsList = mutableListOf<Cell>()
    private val knowledgeBase: MutableList<Pair<MutableList<Cell>, Int>> = mutableListOf()
    private var cellsClickedCount = 0

    fun initializeCells(cellsList: MutableList<Cell>){
        this.cellsList.addAll(cellsList)
        spreadMines()
        countNeighborMines()
    }

    fun makeMove(cell: Cell): Int{
        if(cell.isRevealed){
            return -1
        }
        cellsClickedCount ++
        cell.reveal()
        if(cell.isMine()){
            return LOST
        }
        else if(cellsClickedCount == (8*8) - 10){
            return WON
        }
        else{
            safeCellsList.add(cell)
            addMoveResultToKnowledge(cell)
            updateKnowledgeBase()
            return -1
        }
    }

    fun makeAIMove(): Int{
        if(safeCellsList.isEmpty() || safeCellsList.all { it.isRevealed }){
            return makeRandomMove()
        }
        else{
            return makeMove(safeCellsList.filter { !it.isRevealed }.random())
        }
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

    private fun getNeighborCells(row: Int, column: Int): MutableList<Cell>{
        return cellsList.filter {
            (it.column == column - 1 && it.row == row)
            || (it.column == column && it.row == row - 1)
            ||(it.column == column + 1 && it.row == row)
            ||(it.column == column && it.row == row + 1 )
            ||(it.column == column - 1 && it.row == row - 1)
            ||(it.column == column + 1 && it.row == row + 1)
            ||(it.column == column + 1 && it.row == row - 1)
            ||(it.column == column - 1 && it.row == row + 1)
        }.toMutableList()

    }

    private fun addMoveResultToKnowledge(cell: Cell){
        val neighbors = getNeighborCells(cell.row, cell.column)
        when (cell.minesNearby) {
            0 -> {
                neighbors.forEach {
                    addToSafe(it)
                }
            }
            neighbors.size -> {
                neighbors.forEach {
                    addToMines(it)
                }
            }
            else -> {
                // We only add sentences to KB when we don't know whether the cells are mines or safe
                knowledgeBase.add(Pair(neighbors, cell.minesNearby))
            }
        }
    }

    private fun updateKnowledgeBase(){
            removeSubsets()
            removeKnownMinesAndSafes()
    }


    private fun removeSubsets() {
        for (i in 0 until knowledgeBase.size) {
            for (j in 0 until knowledgeBase.size) {
                if(j == i){
                    continue
                }
                if (knowledgeBase[i].first.containsAll(knowledgeBase[j].first)) {
                    knowledgeBase[i].first.removeAll(knowledgeBase[j].first)
                    knowledgeBase[i] = knowledgeBase[i].copy(
                        knowledgeBase[i].first,
                        knowledgeBase[i].second - knowledgeBase[j].second
                    )
                } else if (knowledgeBase[j].first.containsAll(knowledgeBase[i].first)) {
                    knowledgeBase[j].first.removeAll(knowledgeBase[i].first)
                    knowledgeBase[j] = knowledgeBase[j].copy(
                        knowledgeBase[j].first,
                        knowledgeBase[j].second - knowledgeBase[i].second
                    )
                }
            }
        }
    }

    private fun removeKnownMinesAndSafes(){
        val tobeRemovedKnowledge = mutableListOf<Pair<MutableList<Cell>, Int>>()
        for(i in 0 until knowledgeBase.size){
            if(knowledgeBase[i].first.size == knowledgeBase[i].second){
                tobeRemovedKnowledge.add(knowledgeBase[i])
                knowledgeBase[i].first.forEach { addToMines(it) }
            }
            else if(knowledgeBase[i].second == 0){
                tobeRemovedKnowledge.add(knowledgeBase[i])
                knowledgeBase[i].first.forEach { addToSafe(it) }
            }
            val tobeRemoved = mutableListOf<Cell>()
            for(cell in knowledgeBase[i].first){
                if(minesCellsList.contains(cell)){
                    tobeRemoved.add(cell)
                    knowledgeBase[i] = knowledgeBase[i].copy(knowledgeBase[i].first, knowledgeBase[i].second - 1)
                }
                else if(safeCellsList.contains(cell)){
                    tobeRemoved.add(cell)
                }
            }
            for(c in tobeRemoved){
                knowledgeBase[i].first.remove(c)
            }

            if(knowledgeBase[i].first.size == knowledgeBase[i].second){
                tobeRemovedKnowledge.add(knowledgeBase[i])
                knowledgeBase[i].first.forEach { addToMines(it) }
            }
            else if(knowledgeBase[i].second == 0){
                tobeRemovedKnowledge.add(knowledgeBase[i])
                knowledgeBase[i].first.forEach { addToSafe(it) }
            }
        }
        for(c in tobeRemovedKnowledge){
            knowledgeBase.remove(c)
        }
    }

    private fun addToSafe(cell: Cell){
        if(!safeCellsList.contains(cell)){
            safeCellsList.add(cell)
        }
    }

    private fun addToMines(cell: Cell){
        if(!minesCellsList.contains(cell)){
            minesCellsList.add(cell)
        }
    }

    private fun makeRandomMove(): Int{
        var randomCell = cellsList.random()
        while(randomCell.isRevealed || minesCellsList.contains(randomCell)){
            randomCell = cellsList.random()
        }
       return makeMove(randomCell)
    }
}