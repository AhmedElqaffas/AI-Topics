package com.example.aitopics.sudoku

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity

class Cell(con: Context, val row: Int, val column: Int, val parent: SudokuBlock):
    androidx.appcompat.widget.AppCompatTextView(con) {

    val possibleValues: MutableList<Int> = (1..9).toMutableList()
    var value = 0
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