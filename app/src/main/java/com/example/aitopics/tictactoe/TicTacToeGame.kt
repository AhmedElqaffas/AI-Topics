package com.example.aitopics.tictactoe

import android.util.Log
import java.lang.Exception

/**
 * An instance of this class is created for each game played
 */
class TicTacToeGame {

    private var state = mutableListOf(" "," "," "," "," "," "," "," "," ")
    // Null if no player has won, X if X won, O if O won
    var playerWon: String? = null
    // current player,  either X or O
    private var player = "X"

    fun cellClicked(cellIndex: Int): MutableList<String>{
        if(state[cellIndex] == " "){
            userPlayed(cellIndex)
        }
        return state
    }

    fun train(){
        val lastActions = mutableMapOf<String, Action>()
        val lastStates = mutableMapOf<String, MutableList<String>>()

        while(!hasGameEnded()){
            // Start the game with a random move, otherwise, use chooseAction()
            val action =
            if(state.all { it == " " })
                TicTacToeModel.chooseRandomAction(state, player)
            else
                TicTacToeModel.chooseAction(state, player)

            lastActions[player] = action
            // .toMutableList to assign by value not by reference, we don't need lastStates[player]
            // to keep changing with 'state' list changing
            lastStates[player] = state.toMutableList()
            doAction(action)
            giveRewards(lastActions, lastStates)
            switchPlayerTurn()
        }

    }

    fun hasGameEnded(): Boolean{
        return playerWon != null || hasPlayerWon() || areAllCellsFilled()
    }

    fun reset(){
        state = mutableListOf(" " ," "," "," "," "," "," "," "," ")
        player = "X"
        playerWon = null
    }

    private fun userPlayed(cellIndex: Int) {
        state[cellIndex] = player
        if(!hasGameEnded()){
            switchPlayerTurn()
            makeAIMove()
            if(!hasGameEnded()){
                switchPlayerTurn()
            }
        }
    }

    private fun switchPlayerTurn(){
        player = if(player == "X")
            "O"
        else "X"
    }

    private fun getOtherPlayer(): String{
        return if(player == "X")
            "O"
        else "X"
    }

    private fun makeAIMove(){
        try{
            doAction(TicTacToeModel.chooseBestAction(state, player))
        }catch (e: Exception){
            Log.i("TicTacToeGame","No Move exists")
        }

    }

    private fun doAction(action: Action){
        state[action.index] = action.symbol
    }

    private fun giveRewards(lastActions: MutableMap<String, Action>,
        lastStates: MutableMap<String, MutableList<String>>){

        // If current player won, reward him and punish last player's move as it led to him losing
        if(hasPlayerWon()){
            TicTacToeModel.update(lastStates[player]!!, lastActions[player]!!, state, 1, player)
            TicTacToeModel.update(lastStates[getOtherPlayer()]!!, lastActions[getOtherPlayer()]!!, state, -1, player)
        }
        // If no winner, then we can now safely say that last player's move was neutral
        // However, we don't know if the current player's move will backfire next turn or not
        else{
            if(!lastStates[getOtherPlayer()].isNullOrEmpty()){
                TicTacToeModel.update(lastStates[getOtherPlayer()]!!, lastActions[getOtherPlayer()]!!, state, 0, player)
            }

        }
    }

    private fun areAllCellsFilled(): Boolean{
        return !state.contains(" ")
    }

    private fun hasPlayerWon(): Boolean{
        val hasWon =  ((state[0] != " " && state[1] != " " && state[2] != " " && state[0] == state [1] && state[1] == state [2])
            || (state[3] != " " && state[4] != " " && state[5] != " " && state[3] == state [4] && state[4] == state [5])
            || (state[6] != " " && state[7] != " " && state[8] != " " && state[6] == state [7] && state[7] == state [8])
            || (state[0] != " " && state[3] != " " && state[6] != " " && state[0] == state [3] && state[3] == state [6])
            || (state[1] != " " && state[4] != " " && state[7] != " " && state[1] == state [4] && state[4] == state [7])
            || (state[2] != " " && state[5] != " " && state[8] != " " && state[2] == state [5] && state[5] == state [8])
            || (state[0] != " " && state[4] != " " && state[8] != " " && state[0] == state [4] && state[4] == state [8])
            || (state[2] != " " && state[4] != " " && state[6] != " " && state[2] == state [4] && state[4] == state [6]))
        if(hasWon){
            playerWon = player
        }
        return hasWon
    }
}