package com.example.aitopics.sudoku

import androidx.lifecycle.ViewModel

class SudokuViewModel: ViewModel() {

    private val blocksList = mutableListOf<SudokuBlock>()
    private lateinit var sudokuGenerator: SudokuGenerator

    fun getBlocks(): MutableList<SudokuBlock>{
        if(this.blocksList.isEmpty()){
            this.blocksList.addAll(createBlocks())
        }
        return this.blocksList
    }

    private fun createBlocks(): MutableList<SudokuBlock>{
        val sudokuBlocksList: MutableList<SudokuBlock> = mutableListOf()
        var row = -1
        var column = -1
        for(block in 0..8){
            column += 1
            if(block % 3 == 0){
                row += 1
                column = 0
            }
            sudokuBlocksList.add(SudokuBlock(row, column))
        }
        return sudokuBlocksList
    }

    fun initializeSudokuGenerator(){
        sudokuGenerator = SudokuGenerator(this.blocksList)
    }
}