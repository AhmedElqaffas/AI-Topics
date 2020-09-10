package com.example.aitopics.minesweeper

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity

class Cell(context: Context, val row: Int, val column: Int ):
    androidx.appcompat.widget.AppCompatTextView(context) {

    private var isMine = false
    private var minesNearby = 0

    init{
        setBackgroundColor(Color.BLACK)
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setTypeface(null, Typeface.BOLD)
    }

    fun setMine(){
        isMine = true
    }

    fun isMine(): Boolean{
        return isMine
    }

    fun incrementNearbyMines(){
        minesNearby++
    }

    fun getCellText(): String{
        if(isMine){
            return "*"
        }
        else{
            return minesNearby.toString()
        }
    }
}