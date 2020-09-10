package com.example.aitopics

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.aitopics.minesweeper.MinesweeperActivity
import com.example.aitopics.pathfinder.PathFinderActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pathFinderButton.setOnClickListener {
            startActivity(Intent(this, PathFinderActivity::class.java))
        }

        assistedMinesweeper.setOnClickListener {
            startActivity(Intent(this, MinesweeperActivity::class.java))
        }
    }
}