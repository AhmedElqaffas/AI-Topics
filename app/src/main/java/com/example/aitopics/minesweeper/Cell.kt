package com.example.aitopics.minesweeper

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ImageSpan
import android.view.Gravity
import androidx.core.content.res.ResourcesCompat
import com.example.aitopics.R


class Cell(context: Context, val row: Int, val column: Int):
    androidx.appcompat.widget.AppCompatTextView(context) {

    var isRevealed = false
    var minesNearby = 0
    private var isMine = false


    init{
        setBackgroundColor(Color.BLACK)
        text = "    "
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

    fun setCellText(){
        text = if(isMine){
            val span = SpannableString(" ")
            val drawable = ResourcesCompat.getDrawable(resources, R.drawable.ic_mine, context.theme)
            drawable?.setBounds(0, 0, drawable.intrinsicWidth, drawable.intrinsicHeight)
            val imageSpan = ImageSpan(drawable!!)
            span.setSpan(imageSpan, 0, 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            span
        } else{
            minesNearby.toString()
        }
    }

    fun reveal(){
        setCellText()
        isRevealed = true
    }

    fun reset(){
        isRevealed = false
        minesNearby = 0
        isMine = false
        text = "    "
    }
}