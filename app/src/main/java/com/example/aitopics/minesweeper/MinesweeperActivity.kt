package com.example.aitopics.minesweeper

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import androidx.activity.viewModels
import com.example.aitopics.R
import kotlinx.android.synthetic.main.activity_minesweeper.*

class MinesweeperActivity : AppCompatActivity() {

    private val minesweeperViewModel: MinesweeperViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minesweeper)

        val cellsList = setGridCells(8,8)
        initializeCells(cellsList)
    }

    override fun onDestroy() {
        gameContainer.removeAllViews()
        super.onDestroy()
    }

    private fun setGridCells(rows: Int, columns: Int): MutableList<Cell> {
        gameContainer.columnCount = columns
        gameContainer.rowCount = rows
        val cells = mutableListOf<Cell>()
        for (row in 0 until rows){
            for (column in 0 until columns) {
                val cell = Cell(this, row, column)
                cells.add(cell)
                cell.layoutParams = setCellLayoutParams(cell)
                gameContainer.addView(cell)
            }
        }

        return cells
    }

    private fun setCellLayoutParams(cell: Cell): GridLayout.LayoutParams? {
        val params = GridLayout.LayoutParams()
        params.height = GridLayout.LayoutParams.WRAP_CONTENT
        params.width = GridLayout.LayoutParams.WRAP_CONTENT
        params.rightMargin = 5
        params.topMargin = 5
        params.columnSpec = GridLayout.spec(cell.column, 1, 1f)
        params.rowSpec = GridLayout.spec(cell.row, 1, 1f)
        return params
    }

    private fun initializeCells(cells: MutableList<Cell>){
        val cellsList = minesweeperViewModel.initializeCells(cells)
        // If this is the first time to call initializeCells, it will return empty list, the loop
        // won't be executed, else (screen rotated), we will set the current cells to the cells in
        // the model
        for(cell in cellsList){
            cell.layoutParams = setCellLayoutParams(cell)
            gameContainer.addView(cell)
        }
    }
}