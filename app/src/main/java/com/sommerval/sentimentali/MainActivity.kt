package com.sommerval.sentimentali

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlinx.coroutines.*


import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions
import com.ibm.watson.natural_language_understanding.v1.model.Features
import com.ibm.watson.natural_language_understanding.v1.model.SentimentOptions


class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"
    var context: Context = this // what are best practices here?
    private var myRecyclerView: RecyclerView? = null

    var sentiment: String? = null

    class AskWatsonTask(var incomingText: String, context: Context) {
        val watsonKey : String = context.getString(R.string.key_watson);
         val authenticator = IamAuthenticator(watsonKey)
         val naturalLanguageUnderstanding = NaturalLanguageUnderstanding("2020-03-25", authenticator)
        val targets = ArrayList<String>()
        lateinit var response: AnalysisResults

        init {
            targets.add("Disneyland")   //this should be synched with tweets target
        }

         fun execute(): AnalysisResults? {

             val sentiment = SentimentOptions.Builder()
                 .targets(targets)
                 .build()
             val features = Features.Builder()
                 .sentiment(sentiment)
                 .build()
             val parameters = AnalyzeOptions.Builder()
                 .text(incomingText)
                 .features(features)
                 .build()
             var response = naturalLanguageUnderstanding
                 .analyze(parameters)
                 .execute()
                 .getResult()

             Log.i("MyActivity", "received from Watson: $response")

             return response;   //add incomingText to response object before sending to db

         }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val incomingText = "I really dislike Disneyland. It is honestly the worst place."
        val task = AskWatsonTask(incomingText, this)

        CoroutineScope(Dispatchers.IO).launch {
            task.execute()
        }


        table_main.layoutManager = LinearLayoutManager(this)

//Create a handler for the RetrofitInstance interface//
//        val service = RetrofitClient.getRetrofitInstance(context)!!.create(GetData::class.java)
//        val call: Call<TweetList?> = service.getAllUsers()
//
////Execute the request asynchronously//
//        call.enqueue(object : Callback<TweetList?> {
//            //Handle a successful response//
//            override fun onResponse(
//                call: Call<TweetList?>,
//                response: Response<TweetList?>
//            ) {
//                val body = Gson().toJson(response.body())
//                val parser = JsonParser()
//                val retVal: String = parser.parse(body).toString()
//                Log.i(TAG, "received from API: $body")
//                Toast.makeText(this@MainActivity, "Successful response", Toast.LENGTH_SHORT).show()
//                parseTweets(body)
//            }
//
//            //Handle execution failures//
//            override fun onFailure(
//                call: Call<TweetList?>,
//                throwable: Throwable
//            ) {
//
//                //If the request fails, then display the following toast//
//                Toast.makeText(this@MainActivity, "Failed API call", Toast.LENGTH_SHORT).show()
//            }
//        })
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