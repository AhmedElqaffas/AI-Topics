package com.example.aitopics.minesweeper

import android.graphics.Color
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MinesweeperViewModel: ViewModel() {

    private val cellsList = mutableListOf<Cell>()
    private var mineSweeperPlayer = MineSweeperPlayer()
    var hasGameConcluded = MutableLiveData(false)
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
        checkIfGameEnded(mineSweeperPlayer.makeMove(cell))
    }

    fun makeAIMove(){
        checkIfGameEnded(mineSweeperPlayer.makeAIMove())
    }

    fun reset(){
        hasGameConcluded.value = false
        mineSweeperPlayer = MineSweeperPlayer()
        for(cell in cellsList){
            cell.reset()
        }
        mineSweeperPlayer.initializeCells(cellsList)
    }

    private fun checkIfGameEnded(turnResult: Int){
        if(turnResult == MineSweeperPlayer.WON){
            showAllMines()
            customizeResultText("You Won", Color.parseColor("#2EFF00"))
            notifyActivityThatGameEnded()
        }
        else if(turnResult == MineSweeperPlayer.LOST){
            customizeResultText("You Lost", Color.parseColor("#FF0000"))
            notifyActivityThatGameEnded()
        }
        // else = game hasn't concluded so do nothing
    }

    private fun notifyActivityThatGameEnded(){
        hasGameConcluded.value = true
    }

    private fun customizeResultText(text: String, color: Int){
        resultText = text
        resultTextColor = color
    }

    private fun showAllMines(){
        for(cell in cellsList){
            if(cell.isMine()){
                cell.reveal()
            }
        }
    }
}