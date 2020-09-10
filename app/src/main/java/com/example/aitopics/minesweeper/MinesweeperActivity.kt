package com.example.aitopics.minesweeper

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.GridLayout
import com.example.aitopics.R
import com.example.aitopics.pathfinder.Block
import kotlinx.android.synthetic.main.activity_minesweeper.*

class MinesweeperActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_minesweeper)

        val cellsList = setGridCells(8,8)
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
}