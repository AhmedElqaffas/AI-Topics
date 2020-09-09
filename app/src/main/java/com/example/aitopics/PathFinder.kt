package com.example.aitopics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main

class PathFinder{

    private val blocksList: MutableList<Block> = mutableListOf()
    private val blocksListLiveData = MutableLiveData<Block>()
    private lateinit var endBlock: Block
    private lateinit var startBlock: Block
    private val visitedBlocks = mutableListOf<Block>()
    private val queueFrontier = QueueFrontier()
    private var step = 0L

    fun initializeBlocks(blocksList: MutableList<Block>): LiveData<Block> {
        this.blocksList.addAll(blocksList)
        return blocksListLiveData
    }

    fun changeBlockType(cell: Block, blockType: String): String?{
        when (blockType) {
            "wall" -> {
                cell.setWall()
            }
            "start" -> {
                for(block in blocksList){
                    if (block.isStart){
                        return "There is already a starting point"
                    }
                }
                cell.setStart()
                startBlock = cell
            }
            else -> {
                for(block in blocksList){
                    if (block.isEnd){
                        return "There is already an end point"
                    }
                }
                cell.setEnd()
                endBlock = cell
            }
        }
        return null
    }

    fun startAlgorithm(): String{
        val startNode = getStartNode()
        val endNode = getEndNode()
        return if(startNode != null && endNode != null){
            visitedBlocks.add(startNode)
            return findPath(startNode, endNode)
        } else if(startNode == null){
            "Insert a starting point"
        } else{
            "Insert an end point"
        }
    }

    fun reset(){
        queueFrontier.clearFrontier()
        visitedBlocks.clear()
        for(block in blocksList){
            block.reset()
        }
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
        var neighbors: List<Block>
        while(true) {
            neighbors = getNeighbors(currentBlock)
            neighbors.forEach {
                if (it == endingNode) {
                    it.parent = currentBlock
                    solutionFound()
                    return ""
                } else if (!queueFrontier.doesBlockExists(it) && !visitedBlocks.contains(it)) {
                    it.parent = currentBlock
                    queueFrontier.add(it)
                }
            }

            try {
                currentBlock = getNextNode()
                setBlockAsVisited(currentBlock)
                step += 1000
            } catch (e: Exception) {
                return "No path found"
            }

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

    private fun getNextNode(): Block{
        return queueFrontier.removeNode()
    }

    private suspend fun backtrackPath(endBlock: Block) {

        visitedBlocks.forEach {

                delay(500)
                println("------------------------------------------  ${Thread.currentThread().name}")

            withContext(Main){
                println("------*******************-------  ${Thread.currentThread().name}")
                it.setVisited()
            }
        }

        var currentBlock = endBlock
        while (currentBlock.parent != null) {
            withContext(Main) {
                currentBlock.setBelongsToShortestPath()
            }
            currentBlock = currentBlock.parent!!
        }
    }

    private fun setBlockAsVisited(block: Block){
        visitedBlocks.add(block)
    }

    private fun solutionFound(){
        GlobalScope.launch(IO) {
            backtrackPath(endBlock)
            queueFrontier.clearFrontier()
            visitedBlocks.clear()
        }
    }
    
}