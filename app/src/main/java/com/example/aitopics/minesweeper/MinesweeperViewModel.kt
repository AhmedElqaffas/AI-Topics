package com.example.aitopics.minesweeper

import androidx.lifecycle.ViewModel

class MinesweeperViewModel: ViewModel() {

    private val cellsList = mutableListOf<Cell>()
    private var mineSweeperPlayer = MineSweeperPlayer()

    fun initializeCells(cellsList: MutableList<Cell>): MutableList<Cell>{
        if(this.cellsList.isEmpty()){
            this.cellsList.addAll(cellsList)
            mineSweeperPlayer.initializeCells(cellsList)
            return mutableListOf()
        }
        return cellsList
    }
}