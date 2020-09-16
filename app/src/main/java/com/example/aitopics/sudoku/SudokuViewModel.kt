package com.example.aitopics.sudoku

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class SudokuViewModel: ViewModel() {

    private val blocksList = mutableListOf<SudokuBlock>()
    private var sudokuGenerator: SudokuGenerator? = null
    private var isSudokuGenerated: MutableLiveData<Boolean> = MutableLiveData(false)

    fun getBlocks(): MutableList<SudokuBlock>{
        if(this.blocksList.isEmpty()){
            this.blocksList.addAll(createBlocks())
        }
        return this.blocksList
    }

    private fun createBlocks(): MutableList<SudokuBlock>{
        val sudokuBlocksList: MutableList<SudokuBlock> = mutableListOf()
        // Instead of creating two nested loops, I created one loop only with the help of row and column
        // variables to control the row,column values given to each block
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

    fun initializeSudokuGenerator(): LiveData<Boolean>{
        if(sudokuGenerator == null) {
            sudokuGenerator = SudokuGenerator(blocksList)
            generateSudoku()
        }
        return isSudokuGenerated
    }

    private fun generateSudoku(){
        CoroutineScope(Dispatchers.Default).launch{
         isSudokuGenerated.postValue(sudokuGenerator!!.generateSudoku())
        }
    }
}