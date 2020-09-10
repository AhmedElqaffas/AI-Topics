package com.example.aitopics.pathfinder

import com.example.aitopics.pathfinder.Block

/**
 * This is the data structure used to implement Breadth-First Search algorithm
 */
class QueueFrontier {
    private val frontier = mutableListOf<Block>()

    fun add(block: Block){
        frontier.add(block)
    }

    fun doesBlockExists(block: Block): Boolean{
        return frontier.contains(block)
    }

    fun removeNode(): Block {
        if(frontier.isEmpty()){
            throw Exception("Empty Frontier")
        }
        else{
            val node = frontier[0]
            frontier.removeAt(0)
            return node
        }
    }
}