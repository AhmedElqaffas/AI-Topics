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
     val qValueMap = mutableMapOf<QValueKey, Double>()

    private const val epsilon = 0.2
    private const val alpha = 0.5

    fun getQValue(key: QValueKey): Double{
        if(!qValueMap.containsKey(key)){
            return 0.0
        }
        return qValueMap[key]!!
    }

    /**
     * Update Q-learning model, given an old state, an action taken
     * in that state, a new resulting state, and the reward received
     * from taking that action.
     */
    fun update(stateBeforeAction: MutableList<String>, action: Action,
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
        val possibleActionsValues = mutableListOf<Double>()
        for(i in 0 until state.size){
            if(state[i] == " "){
                possibleActionsValues.add(getQValue(QValueKey(state, Action(player, i))))
            }
        }

        if(possibleActionsValues.size > 0)
            return max(possibleActionsValues)
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
    private fun updateQValue(stateBeforeAction: MutableList<String>, action: Action,
        reward: Int, bestFutureQValue: Double){

        val oldQValue = getQValue(QValueKey(stateBeforeAction, action))
        qValueMap[QValueKey(stateBeforeAction, action)] = oldQValue +
                (alpha * (reward + bestFutureQValue - oldQValue))

    }

    fun chooseAction(state: MutableList<String>, player: String): Action{
        // Whether to choose a random action (with probability epsilon) or choose best known action
        val shouldChooseRandomly = Random.nextFloat() < epsilon

        if(shouldChooseRandomly){
            return chooseRandomAction(state, player)
        }

        return chooseBestAction(state, player)

    }

     fun chooseRandomAction(state: MutableList<String>, player: String): Action{
        val actions = mutableListOf<Action>()
        for(i in 0 until state.size){
            if(state[i] == " "){
                actions.add(Action(player, i))
            }
        }
        return actions.random()
    }

     fun chooseBestAction(state: MutableList<String>, player: String): Action{
        var bestAction = Action("",0)
        var maxQValue = -2.0
        for(i in 0 until state.size){
            if(state[i] == " "){
                val qValue = getQValue(QValueKey(state, Action(player, i)))
                if(qValue > maxQValue){
                    maxQValue = qValue
                    bestAction = Action(player, i)
                }
            }
        }
        return bestAction
    }
}