package com.example.aitopics.tictactoe

/**
 * This class is used for serialization, instead of serializing the Q-value map, each entry is converted
 * to an object of this class and then serialized.
 */
data class QValue(val key: QValueKey, val reward: Double)