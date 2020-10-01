package com.example.aitopics.questionsbot

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.example.aitopics.R
import com.example.aitopics.sudoku.LoadingDialogFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.activity_questions.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.lang.Math.log
import java.nio.charset.StandardCharsets
import java.util.*


class QuestionsActivity : AppCompatActivity() {

    private val punctuation = "!\"#\$%&'()*+,-./:;<=>?@[\\]^_`{|}~"
    private val stopWords = arrayOf(
        "i",
        "me",
        "my",
        "myself",
        "we",
        "our",
        "ours",
        "ourselves",
        "you",
        "you're",
        "you've",
        "you'll",
        "you'd",
        "your",
        "yours",
        "yourself",
        "yourselves",
        "he",
        "him",
        "his",
        "himself",
        "she",
        "she's",
        "her",
        "hers",
        "herself",
        "it",
        "it's",
        "its",
        "itself",
        "they",
        "them",
        "their",
        "theirs",
        "themselves",
        "what",
        "which",
        "who",
        "whom",
        "this",
        "that",
        "that'll",
        "these",
        "those",
        "am",
        "is",
        "are",
        "was",
        "were",
        "be",
        "been",
        "being",
        "have",
        "has",
        "had",
        "having",
        "do",
        "does",
        "did",
        "doing",
        "a",
        "an",
        "the",
        "and",
        "but",
        "if",
        "or",
        "because",
        "as",
        "until",
        "while",
        "of",
        "at",
        "by",
        "for",
        "with",
        "about",
        "against",
        "between",
        "into",
        "through",
        "during",
        "before",
        "after",
        "above",
        "below",
        "to",
        "from",
        "up",
        "down",
        "in",
        "out",
        "on",
        "off",
        "over",
        "under",
        "again",
        "further",
        "then",
        "once",
        "here",
        "there",
        "when",
        "where",
        "why",
        "how",
        "all",
        "any",
        "both",
        "each",
        "few",
        "more",
        "most",
        "other",
        "some",
        "such",
        "no",
        "nor",
        "not",
        "only",
        "own",
        "same",
        "so",
        "than",
        "too",
        "very",
        "s",
        "t",
        "can",
        "will",
        "just",
        "don'",
        "don't",
        "should",
        "should've",
        "now",
        "d",
        "ll",
        "m",
        "o",
        "re",
        "ve",
        "y",
        "ain",
        "aren",
        "aren't",
        "couldn",
        "couldn't",
        "didn",
        "didn't",
        "doesn",
        "doesn't",
        "hadn",
        "hadn't",
        "hasn",
        "hasn't",
        "haven",
        "haven't",
        "isn",
        "isn't",
        "ma",
        "mightn",
        "mightn't",
        "mustn",
        "mustn't",
        "needn",
        "needn't",
        "shan",
        "shan't",
        "shouldn",
        "shouldn't",
        "wasn",
        "wasn't",
        "weren",
        "weren't",
        "won",
        "won't",
        "wouldn",
        "wouldn't"
    )

    private lateinit var files: MutableMap<String, String>
    private var filesIDFS = mapOf<String, Double>()
    private val filesWords = mutableMapOf<String, List<String>>()
    private var isAlreadyProcessing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_questions)

        initializeProcessing()
        customizeEditText()
        findAnswerButton.setOnClickListener {
            if(filesIDFS.isNotEmpty()){
                val answer = getAnswerToQuestion()
                answerTextView.text = answer
            }
            else{
                Toast.makeText(this, "Please wait until data is loaded", Toast.LENGTH_SHORT).show()
            }

        }
    }

    private fun initializeProcessing(){
        isAlreadyProcessing = true
        showLoadingDialogIfNotAlreadyShown()
        CoroutineScope(Dispatchers.Default).launch {
            loadFilesContents()
            processFilesContents()
        }.invokeOnCompletion {
            saveIDFSToFile()
            hideLoadingDialogIfExists()
        }

    }

    private suspend fun loadFilesContents(){
        val filesJob = CoroutineScope(IO).async {
            loadFiles()
        }
        files = filesJob.await()
    }

    /**
     * Given a directory name, return a dictionary mapping the filename of each
     * `.txt` file inside that directory to the file's contents as a string.
     */
    private fun loadFiles(): MutableMap<String, String>{
        val filesContentMap = mutableMapOf<String, String>()
        val foldersList = assets.list("")?.filter { it.contains(".txt") }
        foldersList?.forEach {
            filesContentMap[it] = readFile(it)
        }
        return filesContentMap
    }

    /**
     * Given a file name in assets, this method reads this file and returns its content as string
     */
    private fun readFile(filename: String): String{
        var fileContent = ""
        val inputStream = assets.open(filename)
        val bufferedReader = BufferedReader(InputStreamReader(inputStream, StandardCharsets.UTF_8))
        var line = bufferedReader.readLine()
        while (line != null) {
            fileContent += "\n$line"
            line = bufferedReader.readLine()
        }
        return fileContent
    }

    private suspend fun processFilesContents(){
        for (file in files) {
            filesWords[file.key] = tokenize(file.value)
        }
        getIDFS()
    }

    /**
     * Given a document (represented as a string), this method returns a list of all of the
     * words in that document, in order.
     * All words are converted to lowercase, and any punctuation or English stopwords are removed.
     */
    private fun tokenize(content: String): List<String>{
        val lowercaseContent = content.toLowerCase(Locale.ROOT)

        return lowercaseContent.split(Regex("[,.!:?;='\"() ]"))
            .toMutableList().filter{ !punctuation.contains(it) && it != "" && !stopWords.contains(it) }
    }

    /**
     * Computing IDFs is CPU intensive, so the results of computing the IDFs is saved to a file
     * and is loaded when the user reuses the activity instead of recomputing them
     */
    private suspend fun getIDFS(){
        filesIDFS = try{
            loadSavedIDFS()
        }catch(e: Exception){
            val idfJob = CoroutineScope(Dispatchers.Default).async {
                computeIDFS(filesWords)
            }
            idfJob.await()
        }
    }

    private fun loadSavedIDFS(): Map<String, Double> {
        val idfsString = readIDFSFromFile()
        val type = object : TypeToken<MutableMap<String, Double>>(){}.type
        return Gson().fromJson(idfsString, type)
    }

    private fun readIDFSFromFile(): String{
        val inputStream = openFileInput("idfs.txt")
        val reader = BufferedReader(InputStreamReader(inputStream))
        val stringBuilder = StringBuilder()
        var line: String? = reader.readLine()
        while(line != null){
            stringBuilder.append(line)
            line = reader.readLine()
        }
        reader.close()
        return stringBuilder.toString()
    }

    private fun saveIDFSToFile(){
        CoroutineScope(IO).launch {
            try{
                val outputStream = OutputStreamWriter(openFileOutput("idfs.txt", MODE_PRIVATE))
                outputStream.write(Gson().toJson(filesIDFS))
                outputStream.flush()
                outputStream.close()
            }catch (e: Exception){
                Log.d("QuestionsActivity","Couldn't save to file")
            }
        }
    }

    /**
     * Given a map between file or sentence and their list of words,
     * this method returns a map between words and their IDF values.
     */
    private fun computeIDFS(words: MutableMap<String, List<String>>): Map<String, Double> {
        val wordsIDFMap = mutableMapOf<String, Double>()
        for(file in words){
            for(word in file.value){
                if(!wordsIDFMap.containsKey(word)){
                    val wordFrequencyInAllDocuments = getNumberOfDocumentsContainingWord(words, word)
                    wordsIDFMap[word] = log((words.size / wordFrequencyInAllDocuments.toDouble()))
                }
            }
        }
        return wordsIDFMap
    }

    private fun getNumberOfDocumentsContainingWord(
        filesWords: MutableMap<String, List<String>>,
        word: String
    ): Int {
        var frequency = 0
        filesWords.forEach{ file ->
            if(word in file.value){
                frequency++
            }
        }
        return frequency
    }

    private fun getAnswerToQuestion(): String{
        val question = questionEditText.text.toString()
        val questionTokenized: Set<String> = tokenize(question).toSet()
        val topFile = getTopMatchingFile(questionTokenized)
        if(topFile.isEmpty()){
            Toast.makeText(this, "No result found", Toast.LENGTH_SHORT).show()
            return ""
        }
        val sentences = extractSentences(topFile)
        val sentencesIDF = computeIDFS(sentences)
        println(sentences.toString())
        return getTopMatchingSentence(questionTokenized, sentences, sentencesIDF)

    }

    /**
     * Returns the name of the file that best matches the query entered by the user
     */
    private fun getTopMatchingFile(questionTokenized: Set<String>): String{
        var maxTFIDF = 0.0
        var bestFile = ""
        filesWords.forEach{ file ->
            val tfidf = getFileTFIDF(questionTokenized, file)
            if(tfidf > maxTFIDF){
                maxTFIDF = tfidf
                bestFile = file.key
            }
        }
        return bestFile
    }

    private fun getFileTFIDF(questionTokenized: Set<String>, file: Map.Entry<String, List<String>>):Double{
        var currentTFIDF = 0.0
            questionTokenized.forEach{ word ->
            if(file.value.contains(word)){
                currentTFIDF += (filesIDFS[word]!! * getWordFrequency(word, file.value))
            }
        }
        return currentTFIDF
    }

    private fun getWordFrequency(word: String, fileText: List<String>): Int{
        return fileText.filter { it == word }.size
    }

    private fun extractSentences(file: String): MutableMap<String, List<String>>{
        val sentences = mutableMapOf<String, List<String>>()
        for(paragraph in files[file]!!.split("\n")){
            for(sentence in tokenizeSentences(paragraph)){
                val tokens = tokenize(sentence)
                if(!tokens.isNullOrEmpty()){
                    sentences[sentence] = tokens
                }
            }
        }
        return sentences
    }

    /**
     * Converts a paragraph into a list of sentences
     */
    private fun tokenizeSentences(paragraph: String): List<String> {
        return paragraph.split(Regex("\\.[^0-9]"))
    }

    private fun getTopMatchingSentence(
        questionTokenized: Set<String>,
        sentences: MutableMap<String, List<String>>,
        sentencesIDF: Map<String, Double>
    ): String{
        var bestIDF = 0.0
        val bestSentences = mutableListOf<String>()
        sentences.forEach{ sentence ->
            var currentIDF = 0.0
            questionTokenized.forEach { word ->
                if (sentence.value.contains(word)){
                    currentIDF += sentencesIDF[word]!!
                }
            }
            if(currentIDF > bestIDF){
                bestIDF = currentIDF
                bestSentences.clear()
                bestSentences.add(sentence.key)
            }
            else if(currentIDF == bestIDF){
                bestSentences.add(sentence.key)
            }
        }
        return if(bestSentences.size == 1){
            bestSentences[0]
        } else{
            bestSentences.forEach{
                print(it)
            }
            getSentenceBasedOnQueryDensity(questionTokenized, bestSentences)
        }
    }

    /**
     * Query term density is the proportion of words in the sentence that are also words in the query.
     * For example, if a sentence has 10 words, 3 of which are in the query,
     * then the sentence’s query term density is 0.3
     */
    private fun getSentenceBasedOnQueryDensity(
        questionTokenized: Set<String>,
        sentences: MutableList<String>
    ): String{
        var bestQueryDensity = 0.0
        var bestSentence = ""
        sentences.forEach { sentence ->
            var frequency = 0
            questionTokenized.forEach { word ->
                if (sentence.contains(word)){
                    frequency++
                }
            }
            val queryDensity = frequency / sentence.length.toDouble()
            if( queryDensity > bestQueryDensity){
                bestQueryDensity = queryDensity
                bestSentence = sentence
            }
        }
        return bestSentence
    }

    private fun showLoadingDialogIfNotAlreadyShown(){
        if(supportFragmentManager.findFragmentByTag("loading") == null){
            LoadingDialogFragment("Loading Data...").show(supportFragmentManager, "loading")
        }
    }

    private fun hideLoadingDialogIfExists() {
        supportFragmentManager.findFragmentByTag("loading")?.let { fragment ->
            (fragment as DialogFragment).dismiss()
        }
    }

    private fun customizeEditText(){
        questionEditText.isSingleLine = true
        questionEditText.setHorizontallyScrolling(false)
        questionEditText.maxLines = 10
        questionEditText.imeOptions = EditorInfo.IME_ACTION_DONE
    }
}