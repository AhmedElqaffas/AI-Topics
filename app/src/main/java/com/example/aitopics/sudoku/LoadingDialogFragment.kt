package com.example.aitopics.sudoku

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.aitopics.R


class LoadingDialogFragment : DialogFragment() {

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
        val width = activity!!.resources.displayMetrics.widthPixels
        val height = activity!!.resources.displayMetrics.heightPixels  / 2
        dialog!!.window!!.setLayout(width, height)
    }

    override fun onDestroyView() {
        if (dialog != null && retainInstance) {
            dialog!!.setDismissMessage(null)
        }
        super.onDestroyView()
    }

}