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
import kotlinx.android.synthetic.main.activity_path_finder.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PathFinderActivity : AppCompatActivity() {

    private val pathFinderViewModel: PathFinderViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_path_finder)

        val blocks = addGridCells(8, 8)
        initializeBlocks(blocks)
        observeErrorMessages()

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

    private fun addGridCells(rows: Int, columns: Int): MutableList<Block> {
        gameGridLayout.rowCount = rows
        gameGridLayout.columnCount = columns
        val cells = mutableListOf<Block>()
        for (row in 0 until rows){
            for (column in 0 until columns){
                val cell = Block(this, row, column)
                cell.setBackgroundColor(Color.GRAY)
                cells.add(cell)
                val params = setCellLayoutParams(cell)
                cell.layoutParams = params
                gameGridLayout.addView(cell)

                cell.setOnDragListener { _, event ->
                    dropListener(cell, event)
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
        pathFinderViewModel.initializeBlocks(blocks).observe(this){
            print("")

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
                Toast.makeText(this@PathFinderActivity, this, Toast.LENGTH_LONG).show()
            }
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
}