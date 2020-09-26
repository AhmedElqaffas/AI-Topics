package com.example.aitopics.tictactoe

/**
 * Action is the move that the player or AI makes, it is defined as the symbol to insert and in which
 * cell it should be inserted
 */
data class Action(val symbol: String, val index: Int)