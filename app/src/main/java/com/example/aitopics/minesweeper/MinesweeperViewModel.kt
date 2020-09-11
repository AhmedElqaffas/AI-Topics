package com.example.aitopics.minesweeper

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MinesweeperViewModel: ViewModel() {

    private val cellsList = mutableListOf<Cell>()
    private var mineSweeperPlayer = MineSweeperPlayer()
    var hasPlayerWon = MutableLiveData(false)
    var resultText = ""
    var resultTextColor: Int = 0

    fun initializeCells(cellsList: MutableList<Cell>): MutableList<Cell>{
        if(this.cellsList.isEmpty()){
            this.cellsList.addAll(cellsList)
            mineSweeperPlayer.initializeCells(cellsList)
            return mutableListOf()
        }
        return this.cellsList
    }

    fun makeMove(cell: Cell){
        postResult(mineSweeperPlayer.makeMove(cell))
    }

    fun makeAIMove(){
        postResult(mineSweeperPlayer.makeAIMove())
    }

    fun reset(){
        hasPlayerWon.value = false
        mineSweeperPlayer = MineSweeperPlayer()
        for(cell in cellsList){
            cell.reset()
        }
        mineSweeperPlayer.initializeCells(cellsList)
    }

    private fun postResult(result: Int){
        if(result == MineSweeperPlayer.WON){
            resultText = "You Won"
            resultTextColor = Color.parseColor("#2EFF00")
            hasPlayerWon.value = true
        }
        else if(result == MineSweeperPlayer.LOST){
            resultText = "You Lost"
            resultTextColor = Color.parseColor("#FF0000")
            hasPlayerWon.value = true
        }
        // else = game hasn't concluded so do nothing
    }
}