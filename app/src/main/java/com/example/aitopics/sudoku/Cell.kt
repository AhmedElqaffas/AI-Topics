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
    // The correct value of this cell
    var correctValue = 0
    // The value shown now
    var shownValue = 0
    // The cells on the same row or column of this cell, in addition to the cells in the same block
    // of this cell
    var neighborsList = mutableListOf<Cell>()
    // Clue cells are revealed to the users to help them determine the value of the hidden cells
    var isClue = false
    // isRevealed indicates whether the a cell value is presented to the user or not, it is not
    // necessarily the correct value as it may be set by the user
    var isRevealed = false
    // isCorrectValueRevealed is only true if the cell is a clue or revealed by the AI assist,
    // hence, we can be sure that the shown value is correct
    var isCorrectValueRevealed = false

    init {
        setBackgroundColor(Color.GRAY)
        gravity = Gravity.CENTER
        setTypeface(null, Typeface.BOLD)
        setOnClickListener {
            incrementValueIfNotClue()
        }
    }

    fun showCell(){
        isRevealed = true
        isCorrectValueRevealed = true
        shownValue = correctValue
        text = correctValue.toString()
    }

    fun makeAIMove(){
        setTextColor(Color.GREEN)
        showCell()
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
        correctValue = 0
        shownValue = 0
        isClue = false
        isRevealed = false
        isCorrectValueRevealed = false
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
            shownValue++
            if(shownValue == 10){
                shownValue = 1
            }
            text = shownValue.toString()
            setTextColor(Color.RED)
            // The user may change a correct value cell that the AI revealed to a wrong value
            // So, isCorrectValueRevealed should be set to false if the user entered any value
            // As we can't be sure that it is indeed the correct value
            isCorrectValueRevealed = false
            isRevealed = true
        }
    }
}