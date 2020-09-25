package com.example.aitopics.tictactoe

import java.util.Collections.max
import kotlin.random.Random

/**
 * Contains the data that we want to preserve through the session
 */
object TicTacToeModel {
    // The key to this map is a pair of:
    // - The current state, i.e. the list of String representing what each cell value is (X,O,empty)
    // - A pair of: string representing the character to put (X,O) and an int representing where to
    // put it
     val qValueMap = mutableMapOf<Pair<MutableList<String>, Pair<String, Int>>, Double>()

    private const val epsilon = 0.2
    private const val alpha = 0.5

    fun getQValue(state: MutableList<String>, action: Pair<String, Int>): Double{
        if(!qValueMap.containsKey(Pair(state, action))){
            return 0.0
        }
        return qValueMap[Pair(state, action)]!!
    }

    /**
     * Update Q-learning model, given an old state, an action taken
     * in that state, a new resulting state, and the reward received
     * from taking that action.
     */
    fun update(stateBeforeAction: MutableList<String>, action: Pair<String, Int>,
               stateAfterAction: MutableList<String>, reward: Int, player: String){

        val bestFutureQValue = getBestFutureQValue(stateAfterAction, player)
        updateQValue(stateBeforeAction, action, reward,  bestFutureQValue)
    }

    /**
     * Given a state `state`, consider all possible `(state, action)`
     * pairs available in that state and return the maximum of all
     * of their Q-values.
     * Use 0 as the Q-value if a `(state, action)` pair has no
     * Q-value in `self.q`. If there are no available actions in
     * `state`, return 0.
     */
    private fun getBestFutureQValue(state: MutableList<String>, player: String): Double {
        val possibleActions = mutableListOf<Double>()
        for(i in 0 until state.size){
            if(state[i] == ""){
                possibleActions.add(getQValue(state, Pair(player, i)))
            }
        }

        if(possibleActions.size > 0)
            return max(possibleActions)
        return 0.0
    }

    /**
     * Update the Q-value for the state `state` and the action `action`
     * given the previous Q-value `old_q`, a current reward `reward`,
     * and an estiamte of future rewards `future_rewards`.
     *
     * Use the formula:
     * Q(s, a) <- old value estimate + alpha * (new value estimate - old value estimate)
     *
     * where `old value estimate` is the previous Q-value,
     * `alpha` is the learning rate, and `new value estimate`
     * is the sum of the current reward and estimated future rewards.
     */
    private fun updateQValue(stateBeforeAction: MutableList<String>, action: Pair<String, Int>,
        reward: Int, bestFutureQValue: Double){

        val oldQValue = getQValue(stateBeforeAction, action)
        qValueMap[Pair(stateBeforeAction, action)] = oldQValue +
                (alpha * (reward + bestFutureQValue - oldQValue))

    }

    fun chooseAction(state: MutableList<String>, player: String): Pair<String, Int>{
        // Whether to choose a random action (with probability epsilon) or choose best known action
        val shouldChooseRandomly = Random.nextFloat() < epsilon

        if(shouldChooseRandomly){
            return chooseRandomAction(state, player)
        }

        return chooseBestAction(state, player)

    }

     fun chooseRandomAction(state: MutableList<String>, player: String): Pair<String, Int>{
        val actions = mutableListOf<Pair<String, Int>>()
        for(i in 0 until state.size){
            if(state[i] == ""){
                actions.add(Pair(player, i))
            }
        }
        return actions.random()
    }

     fun chooseBestAction(state: MutableList<String>, player: String): Pair<String, Int>{
        var bestAction: Pair<String, Int> = Pair("",0)
        var maxQValue = -2.0
        for(i in 0 until state.size){
            if(state[i] == ""){
                val qValue = getQValue(state, Pair(player, i))
                if(qValue > maxQValue){
                    maxQValue = qValue
                    bestAction = Pair(player, i)
                }
            }
        }
        return bestAction
    }
}