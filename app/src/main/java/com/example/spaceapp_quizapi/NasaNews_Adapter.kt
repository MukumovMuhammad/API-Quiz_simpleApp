package com.example.spaceapp_quizapi

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NasaNews_Adapter():RecyclerView.Adapter<NasaNews_Adapter.viewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
       val view : View = LayoutInflater.from(parent.context).inflate(R.layout.nasa_rvitem_layout, parent,false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    inner class viewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val title = itemView.findViewById<TextView>(R.id.t_title)
        val expl = itemView.findViewById<TextView>(R.id.t_text)
        val img = itemView.findViewById<ImageView>(R.id.img)
    }
}