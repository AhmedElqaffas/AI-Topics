package com.example.aitopics

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PathFinderViewModel: ViewModel() {


    var errorMessagesLiveData = MutableLiveData<String?>()
    var algorithmRunning = MutableLiveData<Boolean>()
    private var pathFinder = PathFinder()
    private val blocksList = mutableListOf<Block>()


    /**
     * If there are no existing data, this method initializes the blocks, else, it only returns
     * the existing data
     */
    fun initializeBlocks(cells: MutableList<Block>): MutableList<Block>{
        if(blocksList.isEmpty()){
            blocksList.addAll(cells)
            pathFinder.initializeBlocks(blocksList)
            return mutableListOf()
        }

        return blocksList
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
        algorithmRunning.value = false
        pathFinder = PathFinder()
        for(block in blocksList){
            block.reset()
        }
        pathFinder.initializeBlocks(blocksList)
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
        else if(message != null && message.isEmpty()){
            algorithmRunning.value = true
        }
    }
}