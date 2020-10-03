package com.example.aitopics.mainmenu

import android.content.Intent
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.appcompat.app.AppCompatActivity
import com.example.aitopics.R
import com.example.aitopics.minesweeper.MinesweeperActivity
import com.example.aitopics.pathfinder.PathFinderActivity
import com.example.aitopics.questionsbot.QuestionsActivity
import com.example.aitopics.sudoku.SudokuActivity
import com.example.aitopics.tictactoe.TicTacToeActivity
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(), ButtonsRecyclerAdapter.ButtonsRecyclerInteraction {

    private lateinit var buttonsRecycler: ButtonsRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeRecyclerViewAdapter()
    }

    private fun initializeRecyclerViewAdapter(){
        buttonsRecycler = ButtonsRecyclerAdapter(getButtonsList(), this)
        buttonsRecyclerView.adapter = buttonsRecycler
        buttonsRecyclerView.addItemDecoration(RecyclerViewItemDecoration(2,
            convertDpToPixel(8f).toInt(), true))
    }

    private fun getButtonsList(): List<Pair<String, Int>>{

        return listOf(
            Pair(resources.getString(R.string.path_finder), R.drawable.path_finder_image),
            Pair(resources.getString(R.string.assisted_minesweeper), R.drawable.minesweeper_image),
            Pair(resources.getString(R.string.sudoku), R.drawable.sudoku_image),
            Pair(resources.getString(R.string.tic_tac_toe), R.drawable.tictactoe_image),
            Pair(resources.getString(R.string.arsenal_bot), R.drawable.bot_image)
        )
    }

    override fun onItemClicked(activityName: String) {
        val intent = when(activityName){
            resources.getString(R.string.path_finder) -> Intent(this, PathFinderActivity::class.java)
            resources.getString(R.string.assisted_minesweeper) -> Intent(this, MinesweeperActivity::class.java)
            resources.getString(R.string.sudoku) -> Intent(this, SudokuActivity::class.java)
            resources.getString(R.string.tic_tac_toe) -> Intent(this, TicTacToeActivity::class.java)
            resources.getString(R.string.arsenal_bot) -> Intent(this, QuestionsActivity::class.java)
            else -> null
        }
        startActivity(intent)
    }

    private fun convertDpToPixel(dp: Float): Float {
        return dp * (resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
    }

}