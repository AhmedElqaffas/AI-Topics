package com.example.aitopics.minesweeper

class MineSweeperPlayer {

    companion object{
        const val WON = 1
        const val LOST = 0
    }

    private val cellsList = mutableListOf<Cell>()
    private val minesCellsList = mutableListOf<Cell>()
    private val safeCellsList = mutableListOf<Cell>()
    // The knowledge base is a list of a list of cells and the number of mines in them
    // for ex: [ [ [a,b,c: 1] ] , [ x,y,z: 2 ] ]
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
        else if(areAllNonMinesClicked()){
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

    /**
     * If cell has 0 mines nearby, then all neighbors are safe. If cell has n mines nearby, where n
     * is the number of neighbors, then all neighbors are mine. Else, add a statement to knowledge base
     */
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
            findAndRemoveSubsets()
            updateMinesAndSafeLists()
            removeKnownMinesAndSafesFromKB()
    }

    /**
     * If the knowledge base has two sentences: {a,b,c : 2} , and {b,c: 1}
     * then we can remove the subset from the bigger set, meaning that {a,b,c: 2} can be simplified
     * to {a: 1}
     */
    private fun findAndRemoveSubsets() {
        for (i in 0 until knowledgeBase.size - 1) {
            for (j in i + 1 until knowledgeBase.size) {
                // Check if j is subset of i
                if(!removeSubset(knowledgeBase[i], knowledgeBase[j], i)){
                    // If not, check if i is subset of j
                    removeSubset(knowledgeBase[j], knowledgeBase[i], j)
                }
            }
        }
    }

    /**
     * If the larger set contains all elements of the smaller set, remove the cells present in the
     * smaller set from the larger set and update the number of mines in the larger set.
     * Note that Pair is immutable, so we use pair.copy to edit the Int.
     */
    private fun removeSubset(largerSet: Pair<MutableList<Cell>, Int>,
                             smallerSet: Pair<MutableList<Cell>, Int>, largerSetIndex: Int): Boolean{
        if (largerSet.first.containsAll(smallerSet.first)) {
            largerSet.first.removeAll(smallerSet.first)
            knowledgeBase[largerSetIndex] = largerSet.copy(largerSet.first,
                largerSet.second - smallerSet.second )

            return true
        }
        return false
    }

    /**
     * Checks all knowledge base entries to check if any sentence can be removed, that is, the sentence
     * contains 0 mines nearby, or n mines nearby, where n is the number of cells in the sentence.
     * If a sentence like the ones mentioned above is found, the cells are added to either mines list
     * or safe list (according to the condition), and the KB entry is removed
     */
    private fun updateMinesAndSafeLists(){
        // Can't remove entry from list while looping on list, so we need to create a temp list
        // to store entries we need to remove, and remove them after the loop terminates
        val tobeRemovedKnowledge = mutableListOf<Pair<MutableList<Cell>, Int>>()
        for(i in 0 until knowledgeBase.size) {
            if (knowledgeBase[i].first.size == knowledgeBase[i].second) {
                tobeRemovedKnowledge.add(knowledgeBase[i])
                knowledgeBase[i].first.forEach { addToMines(it) }
            } else if (knowledgeBase[i].second == 0) {
                tobeRemovedKnowledge.add(knowledgeBase[i])
                knowledgeBase[i].first.forEach { addToSafe(it) }
            }
        }
        for(c in tobeRemovedKnowledge){
            knowledgeBase.remove(c)
        }
    }

    /**
     * Checks knowledge base entries to check if any sentence can be simplified, that is, a sentence
     * contains one or more cells that are now known to be mines or safe. This method removes those
     * cells from the sentence.
     */
    private fun removeKnownMinesAndSafesFromKB(){
        for(i in 0 until knowledgeBase.size){
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

    /**
     * Choose a random cell that isn't already revealed or known to be a mine
     */
    private fun makeRandomMove(): Int{
        var randomCell = cellsList.random()
        while(randomCell.isRevealed || minesCellsList.contains(randomCell)){
            randomCell = cellsList.random()
        }
       return makeMove(randomCell)
    }

    private fun areAllNonMinesClicked(): Boolean{
        return cellsClickedCount == (8*8) - 10
    }
}