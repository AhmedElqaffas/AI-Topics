package com.example.aitopics.sudoku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.aitopics.R
import kotlinx.android.synthetic.main.fragment_loading_dialog.*


class LoadingDialogFragment(private val message: String) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_loading_dialog, container, false)
    }

    override fun onResume() {
        super.onResume()
        setDimensions()
        setMessage()
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }

    private fun setDimensions(){
        val width = activity!!.resources.displayMetrics.widthPixels
        val height = activity!!.resources.displayMetrics.heightPixels  / 2
        dialog!!.window!!.setLayout(width, height)
    }

    private fun setMessage(){
        loadingMessage.text = message
    }
}