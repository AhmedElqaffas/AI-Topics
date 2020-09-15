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
    var neighborsList = listOf<Cell>()

    init {
        setBackgroundColor(Color.GRAY)
        gravity = Gravity.CENTER
        setTypeface(null, Typeface.BOLD)
    }

    fun showValue(){
        text = value.toString()
    }
}