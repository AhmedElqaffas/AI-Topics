package com.example.aitopics.sudoku

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity

/**
 * The cell is the variable in this CSP (Constraint Satisfaction Problem)
 */
class Cell(con: Context, val row: Int, val column: Int, val parent: SudokuBlock):
    androidx.appcompat.widget.AppCompatTextView(con) {

    // Any cell can have any value from 1-9
    val possibleValues: MutableList<Int> = (1..9).toMutableList()
    // The value this cell got
    var value = 0
    // The cells on the same row or column of this cell, in addition to the cells in the same block
    // of this cell
    var neighborsList = mutableListOf<Cell>()

    // Clue cells are revealed to the users to help them determine the value of the hidden cells
    var isClue = false

    init {
        setBackgroundColor(Color.GRAY)
        gravity = Gravity.CENTER
        setTypeface(null, Typeface.BOLD)
        setOnClickListener {
            incrementValueIfNotClue()
        }
    }

    fun showCell(value: Int){
        this.value = value
        text = value.toString()
    }

    /**
     * Neighbor cells are those on the same row, column and block of the cell
     */
     fun setNeighborCells(){
        // Add cells in the same block
        parent.cellsList.forEach {
            if(it != this)
                neighborsList.add(it)
        }
        // Add cells in same row or column
        for(block in parent.neighborsList){
            for(neighborBlockCell in block.cellsList){
                if(isCellInSameRowOrColumn(neighborBlockCell)){
                    neighborsList.add(neighborBlockCell)
                }
            }
        }
    }

    fun reset(){
        value = 0
        isClue = false
        text = ""
        setTextColor(Color.BLACK)
    }

    private fun isCellInSameRowOrColumn(potentialNeighbor: Cell): Boolean{
        return potentialNeighbor.row == row || potentialNeighbor.column == column
    }

    /**
     * If this cell is not a clue, i.e its value is not already shown to the user, that means that
     * it is left for the user to interact with and determine its value.
     * Instead of using editTexts and letting users type a number, each time a non-clue cell is clicked,
     * Its value in incremented by 1
     */
    private fun incrementValueIfNotClue(){
        if(!isClue){
            value++
            if(value == 10){
                value = 1
            }
            text = value.toString()
            setTextColor(Color.RED)
        }
    }
}