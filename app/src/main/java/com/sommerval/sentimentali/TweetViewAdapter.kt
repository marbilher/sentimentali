package com.sommerval.sentimentali


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.cell_tweet.view.*

public  class TweetViewAdapter(val tweet:List<Tweet>):RecyclerView.Adapter<Cell>(){

    override fun getItemCount(): Int {

        return this.tweet.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Cell {

        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.cell_tweet,parent,false)
        return Cell(view)
    }

    override fun onBindViewHolder(cell: Cell, row: Int) {

        cell.itemView.textViewTweetText.text = this.tweet.get(row).text
//        cell.itemView.textViewTweetCreate.text = this.tweet.get(row).createDate
    }

}

public  class Cell(view: View):RecyclerView.ViewHolder(view)