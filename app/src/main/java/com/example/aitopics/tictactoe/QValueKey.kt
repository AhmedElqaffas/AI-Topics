package com.example.aitopics.tictactoe

/**
 * The QValue map consists of :
 * - Keys, which are pairs of the state and the action applied on that state
 * - Values (i.e, rewards)
 * This class represents the keys
 */
data class QValueKey(val state: MutableList<String>, val action: Action)