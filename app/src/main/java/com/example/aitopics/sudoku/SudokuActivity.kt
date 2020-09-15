package com.example.aitopics.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import androidx.activity.viewModels
import androidx.core.view.children
import com.example.aitopics.R
import kotlinx.android.synthetic.main.activity_sudoku.*

class SudokuActivity : AppCompatActivity() {

    private val sudokuViewModel: SudokuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudoku)
        val blocksList = sudokuViewModel.getBlocks()
        setGridBlocks(blocksList)
        sudokuViewModel.initializeSudokuGenerator()
    }

    override fun onDestroy() {
        for(child in sudokuContainer.children){
            (child as GridLayout).removeAllViews()
        }
        super.onDestroy()
    }

    private fun setGridBlocks(sudokuBlocksList: MutableList<SudokuBlock>){

        for(block in 0 until sudokuContainer.childCount){
            setBlockCells(sudokuContainer.getChildAt(block) as GridLayout, sudokuBlocksList[block])
        }
    }

    private fun setBlockCells(layoutBlock: GridLayout, sudokuBlock: SudokuBlock){
        layoutBlock.columnCount = 3
        layoutBlock.rowCount = 3

        // If there are no cells already created, create them
        if(sudokuBlock.cellsList.isEmpty()){
            val cells = mutableListOf<Cell>()
            for (row in 0 until 3){
                for (column in 0 until 3) {
                    val cell = Cell( this, row + (3 * sudokuBlock.row), column + (3 * sudokuBlock.column), sudokuBlock)
                    cells.add(cell)
                    cell.layoutParams = setCellLayoutParams(cell)
                    layoutBlock.addView(cell)
                }
            }
            sudokuBlock.cellsList = cells
        }
        else{
            for(cell in sudokuBlock.cellsList){
                cell.layoutParams = setCellLayoutParams(cell)
                layoutBlock.addView(cell)
            }
        }

    }

    private fun setCellLayoutParams(cell: Cell): GridLayout.LayoutParams? {
        val params = GridLayout.LayoutParams()
        params.height = GridLayout.LayoutParams.WRAP_CONTENT
        params.width = GridLayout.LayoutParams.WRAP_CONTENT
        params.rightMargin = 3
        params.topMargin = 3
        params.columnSpec = GridLayout.spec(cell.column - (3 * cell.parent.column), 1, 1f)
        params.rowSpec = GridLayout.spec(cell.row - (3 * cell.parent.row), 1, 1f)
        return params
    }
}