package com.sommerval.sentimentali

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response



class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    private var myAdapter: MyAdapter? = null

    private var myRecyclerView: RecyclerView? = null


    var context: Context = this // what are best practices here?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


//Create a handler for the RetrofitInstance interface//
        val service = RetrofitClient.getRetrofitInstance(context)!!.create(GetData::class.java)
        val call: Call<TweetList?> = service.getAllUsers()

//Execute the request asynchronously//
        call.enqueue(object : Callback<TweetList?> {
            //Handle a successful response//
            override fun onResponse(
                call: Call<TweetList?>,
                response: Response<TweetList?>
            ) {
                Log.i(TAG, response.body().toString())
                Toast.makeText(this@MainActivity, "Successful response", Toast.LENGTH_SHORT).show()
                //                loadDataList(response.body());
            }

            //Handle execution failures//
            override fun onFailure(
                call: Call<TweetList?>,
                throwable: Throwable
            ) {

//If the request fails, then display the following toast//
                Toast.makeText(this@MainActivity, "Failed API call", Toast.LENGTH_SHORT).show()
            }
        })
    }

    //Display the retrieved data as a list//
    private fun loadDataList(usersList: List<Tweet>) {

//Get a reference to the RecyclerView//
        myRecyclerView = findViewById(R.id.myRecyclerView)
        myAdapter = MyAdapter(usersList)

//Use a LinearLayoutManager with default vertical orientation//
        val layoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@MainActivity)
        myRecyclerView?.setLayoutManager(layoutManager)

//Set the Adapter to the RecyclerView//
        myRecyclerView?.setAdapter(myAdapter)
    }
}