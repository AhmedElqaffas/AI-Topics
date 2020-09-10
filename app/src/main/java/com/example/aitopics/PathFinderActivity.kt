package com.example.aitopics

import android.content.ClipData
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_path_finder.*

class PathFinderActivity : AppCompatActivity() {

    private val pathFinderViewModel: PathFinderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_finder)

        val blocks = addGridCells(8, 8)
        initializeBlocks(blocks)
        observeErrorMessages()
        observeAlgorithmState()
        setDragListeners()


    }

    override fun onDestroy() {
        gameGridLayout.removeAllViews()
        super.onDestroy()
    }

    private fun addGridCells(rows: Int, columns: Int): MutableList<Block> {
        gameGridLayout.rowCount = rows
        gameGridLayout.columnCount = columns
        val cells = mutableListOf<Block>()
        for (row in 0 until rows){
            for (column in 0 until columns){
                val cell = Block(this, row, column)
                cell.setBackgroundColor(Color.GRAY)
                cells.add(cell)

                cell.layoutParams = setCellLayoutParams(cell)
                gameGridLayout.addView(cell)

                cell.setOnDragListener { _, event ->
                    dropListener(cell, event)
                }

                cell.setOnClickListener{
                    cell.reset()
                }
            }
        }
        return cells
    }

    private fun setCellLayoutParams(cell: Block): GridLayout.LayoutParams? {
        val params = GridLayout.LayoutParams()
        params.height = GridLayout.LayoutParams.WRAP_CONTENT
        params.width = GridLayout.LayoutParams.WRAP_CONTENT
        params.rightMargin = 5
        params.topMargin = 5
        params.columnSpec = GridLayout.spec(cell.column, 1, 1f)
        params.rowSpec = GridLayout.spec(cell.row, 1, 1f)
        return params
    }


    private fun initializeBlocks(blocks: MutableList<Block>) {
        val blocksList = pathFinderViewModel.initializeBlocks(blocks)
        // If this is the first time to call initializeBlocks, it will return empty list, the loop
        // won't be executed, else (screen rotated), we will set the current cells to the cells in
        // the model
        for(cell in blocksList){
            cell.layoutParams = setCellLayoutParams(cell)
            gameGridLayout.addView(cell)
        }
    }

    private fun setDragListeners(){
        wallBlock.setOnTouchListener{view: View, _: MotionEvent ->
            dragBlock(wallBlock)
            view.performClick()
            return@setOnTouchListener true
        }
        startBlock.setOnTouchListener{view: View, _: MotionEvent ->
            dragBlock(startBlock)
            view.performClick()
            return@setOnTouchListener true
        }
        endBlock.setOnTouchListener{view: View, _: MotionEvent ->
            dragBlock(endBlock)
            view.performClick()
            return@setOnTouchListener true
        }
    }

    private fun dragBlock(view: ImageView){
        val data = ClipData.newPlainText("type", view.tag.toString())
        view.startDrag(data, View.DragShadowBuilder(view), null, 0)
    }

    private fun dropListener(cell: Block, event: DragEvent): Boolean{
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> return true
            DragEvent.ACTION_DRAG_ENTERED -> return true
            DragEvent.ACTION_DRAG_LOCATION -> return true
            DragEvent.ACTION_DRAG_EXITED -> return true
            DragEvent.ACTION_DROP -> {
                pathFinderViewModel.changeBlockType(cell,
                    event.clipData.getItemAt(0).text.toString())
                return true
            }
            DragEvent.ACTION_DRAG_ENDED -> return true
            else -> {
            }
        }
        return false
    }

    private fun observeErrorMessages(){
        pathFinderViewModel.errorMessagesLiveData.observe(this){
            it?.apply {
                Toast.makeText(this@PathFinderActivity, it, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Observes whether the algorithm is running or not
     */
    private fun observeAlgorithmState(){
        pathFinderViewModel.algorithmRunning.observe(this){
            setUserInteraction(!it)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.findPath -> pathFinderViewModel.startAlgorithm()
            R.id.reset -> pathFinderViewModel.reset()
        }
        return super.onOptionsItemSelected(item)
    }

    /**
     * Enables or disables user interaction based on the parameter passed to it
     */
    private fun setUserInteraction(state: Boolean){
        for(cell in gameGridLayout.children){
            cell.isClickable = state
        }
        wallBlock.isEnabled = state
        startBlock.isEnabled = state
        endBlock.isEnabled = state
    }
}