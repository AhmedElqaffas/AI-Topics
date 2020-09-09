package com.example.aitopics

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PathFinderViewModel: ViewModel() {

    private val pathFinder = PathFinder()
    var errorMessagesLiveData = MutableLiveData<String?>()

    fun initializeBlocks(cells: MutableList<Block>): LiveData<Block>{
        return pathFinder.initializeBlocks(cells)
    }

    fun changeBlockType(cell: Block, type: String){
        val message = pathFinder.changeBlockType(cell, type)
        postToastMessage(message)
    }

    fun startAlgorithm(){
        val message = pathFinder.startAlgorithm()
        postToastMessage(message)
    }

    fun reset(){
        pathFinder.reset()
    }

    private fun postToastMessage(message: String?){
        if(!message.isNullOrEmpty()){
            errorMessagesLiveData.value = message
            /* Set it to null again to indicate that the data has been handled already
             (error message would have been already sent to the activity, and toast displayed)
             Removing this line will cause the toast to keep appearing every time the screen is rotated
            */
            errorMessagesLiveData.value = null
        }
    }
}