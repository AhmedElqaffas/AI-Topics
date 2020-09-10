package com.example.aitopics.minesweeper

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity

class Cell(context: Context, val row: Int, val column: Int ):
    androidx.appcompat.widget.AppCompatTextView(context) {

    init{
        setBackgroundColor(Color.BLACK)
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setTypeface(null, Typeface.BOLD)
    }
}