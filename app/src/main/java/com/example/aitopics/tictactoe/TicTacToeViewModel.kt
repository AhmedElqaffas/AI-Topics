package com.example.aitopics.tictactoe

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class TicTacToeViewModel: ViewModel() {

    var ticTacToeGame: TicTacToeGame = TicTacToeGame()
    private var hasAIFinishedTraining: MutableLiveData<Boolean> = MutableLiveData(false)
    private var hasGameConcluded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val cellsList = MutableLiveData<MutableList<String>>()
    private var isAlreadyBeingTrained = false

    fun cellClicked(cellIndex: Int){
        cellsList.value = ticTacToeGame.cellClicked(cellIndex)
        if(ticTacToeGame.hasGameEnded()){
            hasGameConcluded.value = true
        }
    }

    fun startGame(): LiveData<MutableList<String>>{
        return cellsList
    }

    fun observeAITraining(): LiveData<Boolean>{
        return hasAIFinishedTraining
    }

    fun hasGameConcluded(): LiveData<Boolean>{
        return hasGameConcluded
    }

    fun getResult(): String{
        if(ticTacToeGame.playerWon != null){
            return "${ticTacToeGame.playerWon} Won!"
        }
        return "Draw"
    }

     suspend fun trainAI(numberOfTrials: Int){
        if(!isAlreadyBeingTrained){
            println("Started Training")
            isAlreadyBeingTrained = true
            for(i in 0..numberOfTrials){
                ticTacToeGame.reset()
                ticTacToeGame.train()
            }
            ticTacToeGame.reset()
            hasAIFinishedTraining.postValue(true)
            println("Done Training")
        }
    }
}