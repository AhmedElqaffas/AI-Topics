package com.example.aitopics.sudoku

/**
 * Instead of just using 81 separated cells, I decided to add another layer, blocks. Each block
 * consists of 3*3 cells
 */
class SudokuBlock(val row: Int, val column: Int){
    // The 3*3 cells in this block
    var cellsList = listOf<Cell>()
    // The block on the same row or column of this block
    var neighborsList = listOf<SudokuBlock>()
}