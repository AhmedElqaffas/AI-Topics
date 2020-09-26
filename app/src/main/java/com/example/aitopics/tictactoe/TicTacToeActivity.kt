package com.example.aitopics.tictactoe

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.children
import androidx.fragment.app.DialogFragment
import com.example.aitopics.R
import com.example.aitopics.sudoku.LoadingDialogFragment
import kotlinx.android.synthetic.main.activity_tic_tac_toe.*
import kotlinx.coroutines.*

class TicTacToeActivity : AppCompatActivity() {

    private val ticTacToeViewModel: TicTacToeViewModel by viewModels()

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.tictactoe_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.reset -> resetGame()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tic_tac_toe)
        showLoadingPopupUntilAITrained()
        setClickListeners()
        observeCellChanges()
        observeGameConclusion()
        trainAI()
    }

    private fun showLoadingPopupUntilAITrained(){
        ticTacToeViewModel.observeAITraining().observe(this){
            if(!it){
                showLoadingDialogIfNotAlreadyShown()
            }
            else{
                hideLoadingDialogIfExists()
            }
        }
    }

    private fun showLoadingDialogIfNotAlreadyShown(){
        if(supportFragmentManager.findFragmentByTag("loading") == null){
            LoadingDialogFragment("Training your opponent").show(supportFragmentManager, "loading")
        }
    }

    private fun hideLoadingDialogIfExists() {
        supportFragmentManager.findFragmentByTag("loading")?.let { fragment ->
            (fragment as DialogFragment).dismiss()
        }
    }

    private fun setClickListeners(){
        ticTacToeContainer.children.forEach {
            it.setOnClickListener {cell ->
                ticTacToeViewModel.cellClicked(ticTacToeContainer.indexOfChild(cell))
            }
        }
    }

    private fun observeCellChanges(){
        ticTacToeViewModel.startGame().observe(this){
            for(i in 0 until it.size){
                val textView = (ticTacToeContainer.getChildAt(i) as TextView)
                textView.text = it[i]
                if(it[i] == "O") {
                    textView.setTextColor(Color.RED)
                }else{
                    textView.setTextColor(Color.BLACK)
                }
            }
        }
    }

    private fun observeGameConclusion(){
        ticTacToeViewModel.hasGameConcluded().observe(this){
            if(it){
                setUserInteraction(false)
                Toast.makeText(this, ticTacToeViewModel.getResult(), Toast.LENGTH_SHORT).show()
            }
            else{
                setUserInteraction(true)
            }
        }
    }

    private fun trainAI(){
        CoroutineScope(Dispatchers.Default).launch {
            ticTacToeViewModel.trainAI(150000)
        }
    }

    private fun setUserInteraction(state: Boolean){
        for(c in ticTacToeContainer.children){
            c.isClickable = state
        }
    }

    private fun resetGame(){
        ticTacToeContainer.children.forEach {
            (it as TextView).text = ""
        }
        ticTacToeViewModel.resetGame()
    }
}