package com.sommerval.sentimentali

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder

//Extend the RecyclerView.Adapter class//
class MyAdapter(dataList: List<Tweet>) :
    RecyclerView.Adapter<MyAdapter.CustomViewHolder>() {
    private val dataList: List<Tweet>

    inner class CustomViewHolder(//Get a reference to the Views in our layout//
        val myView: View
    ) :
        ViewHolder(myView) {
        var textUser: TextView

        init {
            textUser = myView.findViewById(R.id.user)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view: View = layoutInflater.inflate(R.layout.row_layout, parent, false)
        return CustomViewHolder(view)
    }

    //Set the data//
    override fun onBindViewHolder(
        holder: CustomViewHolder,
        position: Int
    ) {
//        holder.textUser.setText(dataList[position].user)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    init {
        this.dataList = dataList
    }
}