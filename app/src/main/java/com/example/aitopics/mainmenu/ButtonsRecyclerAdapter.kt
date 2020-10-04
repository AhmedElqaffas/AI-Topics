package com.example.aitopics.mainmenu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.aitopics.R
import kotlinx.android.synthetic.main.item_button.view.*

class ButtonsRecyclerAdapter(private val buttonsList: List<Pair<String, Int>>,
                             private val interactionListener: ButtonsRecyclerInteraction):
    RecyclerView.Adapter<ButtonsRecyclerAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        init{
            setClickListener()
        }

        fun bindButtonData(buttonData: Pair<String, Int>) {
            itemView.buttonImage.setImageResource(buttonData.second)
            itemView.buttonLabel.text = buttonData.first
        }

        private fun setClickListener(){
            itemView.setOnClickListener{
                interactionListener.onItemClicked(itemView.buttonLabel.text.toString())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflated = LayoutInflater.from(parent.context).inflate(R.layout.item_button, parent, false)
        return ViewHolder(inflated)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindButtonData(buttonsList[position])
    }

    override fun getItemCount(): Int {
        return buttonsList.size
    }

    interface ButtonsRecyclerInteraction{
        fun onItemClicked(activityName: String)
    }
}