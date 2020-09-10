package com.example.aitopics.pathfinder

import android.content.Context
import android.graphics.Color

/**
 * This is the node data structure of the search algorithm
 * The state of each node is its (i,j) coordinates
 */
class Block(private val cont: Context, val row: Int, val column: Int):
    androidx.appcompat.widget.AppCompatImageView(cont){

    var isWall = false
    var isStart = false
    var isEnd = false

    var parent: Block? = null

    fun setWall(){
        isWall = true
        isEnd = false
        isStart = false
        setBackgroundColor(Color.BLACK)
    }

    fun setStart(){
        isWall = false
        isEnd = false
        isStart = true
        setBackgroundColor(Color.GREEN)
    }

    fun setEnd(){
        isWall = false
        isEnd = true
        isStart = false
        setBackgroundColor(Color.RED)
    }

    fun setVisited(){
        setBackgroundColor(Color.YELLOW)
    }

    fun setBelongsToShortestPath(){
        setBackgroundColor(Color.GREEN)
    }

    fun reset(){
        isWall = false
        isEnd = false
        isStart = false
        parent = null
        setBackgroundColor(Color.GRAY)
    }

    fun resetColorOnly(){
        if(!isStart && !isWall && !isEnd)
            setBackgroundColor(Color.GRAY)
        else if(isEnd)
            setBackgroundColor(Color.RED)
    }
}