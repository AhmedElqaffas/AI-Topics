package com.example.aitopics.pathfinder

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class PathFinder{

    // List of block in the game
    private val blocksList: MutableList<Block> = mutableListOf()
    private val visitedBlocks = mutableListOf<Block>()
    private val queueFrontier = QueueFrontier()
    private var solutionFound = false


    fun initializeBlocks(blocksList: MutableList<Block>){
        this.blocksList.addAll(blocksList)
    }

    fun changeBlockType(cell: Block, blockType: String): String?{
        when (blockType) {
            "wall" -> {
                cell.setWall()
            }
            "start" -> {
                if(getStartNode() != null){
                    return "There is already a starting point"
                }
                cell.setStart()
            }
            else -> {
                if (getEndNode() != null){
                    return "There is already an end point"
                }
                cell.setEnd()
            }
        }
        return null
    }

    fun startAlgorithm(): String{
        val startNode = getStartNode()
        val endNode = getEndNode()
        if(startNode != null && endNode != null){
            visitedBlocks.add(startNode)
            return findPath(startNode, endNode)
        } else if(startNode == null){
            return "Insert a starting point"
        } else{
            return "Insert an end point"
        }
    }

    fun restartAnimation(){
        // If the user clicked restart animation before finding the solution, do nothing
        if(!solutionFound)
            return

        for(block in blocksList){
            block.resetColorOnly()
        }
        solutionFound()
    }

    private fun getStartNode(): Block?{
        for(block in blocksList){
            if(block.isStart){
                return block
            }
        }
        return null
    }

    private fun getEndNode(): Block?{
        for(block in blocksList){
            if(block.isEnd){
                return block
            }
        }
        return null
    }

    private fun findPath(startingNode: Block, endingNode: Block): String{
        var currentBlock = startingNode
        while(true) {
            // put the neighbors in the queue to be visited
            addPossibleNextStatesToQueue(currentBlock, endingNode)
            if(solutionFound)
                return ""
            // Select the first node in the queue to visit
            val nextNode = visitNextNode()
            if(nextNode != null){
                currentBlock = nextNode
            }
            else{
                // The queue was empty, so no solution
                return "No path found"
            }
        }
    }

    private fun addPossibleNextStatesToQueue(currentBlock: Block, endingNode: Block) {
        val neighbors: List<Block> = getNeighbors(currentBlock)
        neighbors.forEach {
            if (it == endingNode) {
                it.parent = currentBlock
                solutionFound()
                // If this neighbor is already visited or already in the queue to be visited, don't
                // put it in the queue again
            } else if (!queueFrontier.doesBlockExists(it) && !visitedBlocks.contains(it)) {
                it.parent = currentBlock
                queueFrontier.add(it)
            }
        }
    }

    private fun visitNextNode(): Block?{
        return try {
            val currentBlock = getNextNode()
            visitedBlocks.add(currentBlock)
            currentBlock
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Returns the neighbors (blocks to the left,top,right,bottom) of the current block, excluding
     * walls and starting block
     */
    private fun getNeighbors(currentBlock: Block): List<Block>{
        val column = currentBlock.column
        val row = currentBlock.row
        val neighborsList = mutableListOf<Block>()

        for(block in blocksList){
            if((block.row == row - 1 && block.column == column)
                || (block.row == row + 1 && block.column == column)
                || (block.row == row && block.column - 1 == column)
                || (block.row == row && block.column + 1 == column)){

                if(!block.isWall && !block.isStart) {
                    neighborsList.add(block)
                }
            }
        }

        return neighborsList
    }

    private fun getNextNode(): Block {
        return queueFrontier.removeNode()
    }

     private suspend fun backtrackPath(endBlock: Block) {
        // Paint a visited node yellow every few milliseconds
        visitedBlocks.forEach {
            // skip the start node to keep its color green
            if(it != visitedBlocks[0]){
                delay(200)
                // setVisited() changes the background color so it must be done from main thread
                withContext(Main){
                    it.setVisited()
                }
            }
        }

        // Backtrack the shortest path
        var currentBlock = endBlock
        while (currentBlock.parent != null) {
            delay(200)
            withContext(Main) {
                currentBlock.setBelongsToShortestPath()
            }
            currentBlock = currentBlock.parent!!
        }
    }

     private fun solutionFound(){
        solutionFound = true
        GlobalScope.launch(IO) {
            backtrackPath(getEndNode()!!)
        }
    }
    
}