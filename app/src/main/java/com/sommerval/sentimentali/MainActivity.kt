package com.sommerval.sentimentali

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"

    private var myRecyclerView: RecyclerView? = null


    var context: Context = this // what are best practices here?
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        table_main.layoutManager = LinearLayoutManager(this)

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
                val body = Gson().toJson(response.body())
                val parser = JsonParser()
                val retVal: String = parser.parse(body).toString()
                Log.i(TAG, "received from API: $body")
                Toast.makeText(this@MainActivity, "Successful response", Toast.LENGTH_SHORT).show()
                parseTweets(body)
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

    private  fun parseTweets(fromString: String){

        val jsonArray = JSONObject(fromString).getJSONArray("statuses")

        var list = ArrayList<Tweet>()

        for (x in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(x)
            val tweetText = jsonObject.getString("text")
//            val tweetCreate = jsonObject.getString("created_at")

            list.add(Tweet(tweetText))
        }
        table_main.adapter = TweetViewAdapter(list)
    }
}