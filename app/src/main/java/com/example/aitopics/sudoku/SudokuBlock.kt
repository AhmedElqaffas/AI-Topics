package com.example.aitopics.sudoku

class SudokuBlock(val row: Int, val column: Int){
    var cellsList = listOf<Cell>()
    var neighborsList = listOf<SudokuBlock>()

}