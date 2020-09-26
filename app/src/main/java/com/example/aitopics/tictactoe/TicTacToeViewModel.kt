package com.example.aitopics.tictactoe

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter


class TicTacToeViewModel(application: Application) : AndroidViewModel(application) {

    var ticTacToeGame: TicTacToeGame = TicTacToeGame()
    private var hasAIFinishedTraining: MutableLiveData<Boolean> = MutableLiveData(false)
    private var hasGameConcluded: MutableLiveData<Boolean> = MutableLiveData(false)
    private val cellsList = MutableLiveData<MutableList<String>>()
    private var isAlreadyBeingTrained = false
    private val context = getApplication<Application>().applicationContext

    fun cellClicked(cellIndex: Int){
        cellsList.value = ticTacToeGame.cellClicked(cellIndex)
        if(ticTacToeGame.hasGameEnded()){
            hasGameConcluded.value = true
        }
    }

    fun startGame(): LiveData<MutableList<String>>{
        return cellsList
    }

    fun observeAITraining(): LiveData<Boolean>{
        return hasAIFinishedTraining
    }

    fun hasGameConcluded(): LiveData<Boolean>{
        return hasGameConcluded
    }

    fun getResult(): String{
        if(ticTacToeGame.playerWon != null){
            return "${ticTacToeGame.playerWon} Won!"
        }
        return "Draw"
    }

     suspend fun trainAI(numberOfTrials: Int){
        if(!isAlreadyBeingTrained){
            try{
                useExistingTrainingData()
                hasAIFinishedTraining.postValue(true)
                println("Existing data used")
            }catch(e: Exception){
                startNewTraining(numberOfTrials)
                saveTrainingDataToFile()
            }
        }
    }

    private fun startNewTraining(numberOfTrials: Int) {
        println("Started Training")
        isAlreadyBeingTrained = true
        for(i in 0..numberOfTrials){
            ticTacToeGame.reset()
            ticTacToeGame.train()
        }
        ticTacToeGame.reset()
        hasAIFinishedTraining.postValue(true)
        println("Done Training")
    }

    private fun saveTrainingDataToFile(){
        try{
            val outputStream = OutputStreamWriter(context.openFileOutput("train.txt", Context.MODE_PRIVATE))
            val mapToQValueList = mutableListOf<QValue>()
            TicTacToeModel.qValueMap.forEach{
                mapToQValueList.add(QValue(it.key, it.value))
            }
            outputStream.write(Gson().toJson(mapToQValueList))
            outputStream.flush()
            outputStream.close()
        }catch (e: Exception){
            println("Couldn't save to file")
        }
    }

    private fun useExistingTrainingData(){
        val trainingData = getStoredTrainingData()
        trainingData.forEach {
            TicTacToeModel.qValueMap[it.key] = it.reward
        }

    }

    private fun getStoredTrainingData(): MutableList<QValue>{
        val mapString = readStringFromFile()
        val type = object : TypeToken<MutableList<QValue>>(){}.type
        try{
            return Gson().fromJson(mapString, type)
        }catch (e: Exception){
            println(e.printStackTrace())
            throw Exception("Couldn't convert string to list")
        }
    }

    private fun readStringFromFile(): String{
        try{
            val inputStream = context.openFileInput("train.txt")
            val reader = BufferedReader(InputStreamReader(inputStream))
            val stringBuilder = StringBuilder()
            var line: String? = reader.readLine()
            while(line != null){
                stringBuilder.append(line)
                line = reader.readLine()
            }
            reader.close()
            return stringBuilder.toString()
        }catch (e: Exception){
            throw Exception("File not found")
        }
    }
}