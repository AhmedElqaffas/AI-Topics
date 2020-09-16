package com.example.aitopics.sudoku

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.GridLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.example.aitopics.R
import kotlinx.android.synthetic.main.activity_sudoku.*

class SudokuActivity : AppCompatActivity() {

    private val sudokuViewModel: SudokuViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sudoku)
        val blocksList = sudokuViewModel.getBlocks()
        setGridBlocks(blocksList)
        showLoadingPopupUntilSudokuReady()

    }

    override fun onDestroy() {
        removeAllCellViews()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.sudoku_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.checkAnswer -> checkAnswer()
            R.id.reset -> sudokuViewModel.resetGame()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkAnswer(){
        val toast: Toast = if(sudokuViewModel.validateAnswer()){
            Toast.makeText(this, "Correct Solution", Toast.LENGTH_LONG)
        } else{
            Toast.makeText(this, "Try Again", Toast.LENGTH_LONG)
        }
        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    private fun removeAllCellViews(){
        for(child in sudokuContainer.children){
            (child as GridLayout).removeAllViews()
        }
    }

    /**
     * Assigns each SudokuBlock to its position in the layout
     */
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
                    // I needed the cells rows and columns to range from 0..8 to be able to get the
                    // neighbors of each cell later on. To avoid having the cells rows and columns from
                    // getting stuck in the loops range (0..3), I use the row and column of the parent block
                    // to convert the 0..3 range to 0..8 range
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

    private fun showLoadingPopupUntilSudokuReady(){
        showLoadingDialogIfNotAlreadyShown()
        sudokuViewModel.initializeSudokuGenerator().observe(this){
            if(it) {
                hideLoadingDialogIfExists()
            }
            else{
                showLoadingDialogIfNotAlreadyShown()
            }
        }
    }

    private fun showLoadingDialogIfNotAlreadyShown(){
        if(supportFragmentManager.findFragmentByTag("loading") == null){
            LoadingDialogFragment().show(supportFragmentManager, "loading")
        }
    }

    private fun hideLoadingDialogIfExists() {
        supportFragmentManager.findFragmentByTag("loading")?.let { fragment ->
            (fragment as DialogFragment).dismiss()
        }
    }
}