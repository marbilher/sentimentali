package com.sommerval.sentimentali


import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.facebook.stetho.Stetho
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.ibm.cloud.sdk.core.security.IamAuthenticator
import com.ibm.watson.natural_language_understanding.v1.NaturalLanguageUnderstanding
import com.ibm.watson.natural_language_understanding.v1.model.AnalysisResults
import com.ibm.watson.natural_language_understanding.v1.model.AnalyzeOptions
import com.ibm.watson.natural_language_understanding.v1.model.Features
import com.ibm.watson.natural_language_understanding.v1.model.SentimentOptions
import com.sommerval.sentimentali.database.TweetDatabase
import com.sommerval.sentimentali.database.TweetDatabaseDao
import com.sommerval.sentimentali.database.TweetWithSentiment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlin.collections.arrayListOf

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private val TAG = "MyActivity"

    class AskWatsonTask(tweetList: ArrayList<Tweet>, context: Context) {
        val watsonKey : String = context.getString(R.string.key_watson);
         val authenticator = IamAuthenticator(watsonKey)
         val naturalLanguageUnderstanding = NaturalLanguageUnderstanding("2020-03-25", authenticator)
        val targets = ArrayList<String>()
        val myTweets = tweetList;
        val watsonContext = context;

        private lateinit var tweetDao: TweetDatabaseDao
        private lateinit var db: TweetDatabase

        init {
            targets.add("Disneyland")   //this should be synched with tweets target
        }

        suspend fun execute(tweet: Tweet): AnalysisResults? {
            Log.i("MyActivity", "sending to Watson: $tweet")


            val sentiment = SentimentOptions.Builder()
//                 .targets(targets)    //Twitter truncates tweets which means they might not contain "Disney"
                 .build()               //By removing the target "Disney" Watson analyzes the general tone of the entire tweet
             val features = Features.Builder()
                 .sentiment(sentiment)
                 .build()
             val parameters = AnalyzeOptions.Builder()
                 .text(tweet.toString())
                 .language("en")    //If language is not specified, Watson will return error on unrecognized languages
                 .features(features)
                 .build()
             var response = naturalLanguageUnderstanding
                 .analyze(parameters)
                 .execute()
                 .getResult()

             Log.i("MyActivity", "received from Watson: $response")
            insertAndGetTweet(tweet, response.sentiment.document.label)

             return response;

         }

        //DB functions******************************************************************************// //clean code says NOT to make dramatic comments
        fun createDb() {
            db = Room.databaseBuilder(watsonContext, TweetDatabase::class.java, "tweet_history_database")
                .allowMainThreadQueries()   //Read up on this
                .build()
            tweetDao = db.tweetDatabaseDao
        }

        fun insertAndGetTweet(tweetText: Tweet, sentiment: String) {
            createDb()

            val tweetDbObject = TweetWithSentiment()
            tweetDbObject.tweetText = tweetText.toString()
            tweetDbObject.tweetSentiment = sentiment
            tweetDao.insert(tweetDbObject)
            val tonight = tweetDao.getTweet()
            if (tonight != null) {
                Log.i("MyActivity", "sentiment received from db: ${tonight.tweetSentiment}")
            } else {
                Log.i("MyActivity", "db returns null: $tonight")
            }

        }
        //******************************************************************************************//


         suspend fun loop() {           //need to read up on suspending functions
            for(tweet in myTweets) {
                    execute(tweet);
            }
        }
    }

    //sample data for linechart
    val entries1 = mutableListOf<Entry>()
    val entries2 = mutableListOf<Entry>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Stetho.initializeWithDefaults(this);



        val revenueComp1 = arrayOf(10000f, 20000f, 30000f, 40000f)
        val revenueComp2 = arrayListOf(12000f, 23000f, 35000f, 48000f)

        revenueComp1.forEachIndexed { index, element -> (
            entries1.add(Entry(index.toFloat(), element))
                )}

        revenueComp2.forEachIndexed { index, element -> (
            entries2.add(Entry(index.toFloat(), element))
            )}


            val lineDataSet1 = LineDataSet(entries1, "Company 1")
        lineDataSet1.color = Color.RED
        lineDataSet1.setDrawValues(false)
        lineDataSet1.setAxisDependency(YAxis.AxisDependency.LEFT)

        val lineDataSet2 = LineDataSet(entries2, "Company 2")
        lineDataSet2.color = Color.BLUE
        lineDataSet1.setDrawValues(false)
        lineDataSet2.setAxisDependency(YAxis.AxisDependency.LEFT)

        val workingDataSet = LineData(lineDataSet1, lineDataSet2)

        lineChart.data = workingDataSet;

        lineChart.axisLeft.mAxisMaximum = 1f
        lineChart.axisLeft.mAxisMinimum = -1f
        lineChart.axisLeft.mAxisRange = 2f

        lineChart.invalidate()





        table_main.layoutManager = LinearLayoutManager(this)

    //Create a handler for the RetrofitInstance interface//
            val service = RetrofitClient.getRetrofitInstance(this)!!.create(GetData::class.java)
            val call: Call<TweetList?> = service.getAllUsers()

    //Execute the request asynchronously//
            call.enqueue(object : Callback<TweetList?> {
                //Handle a successful response//
                override fun onResponse(
                    call: Call<TweetList?>,
                    response: Response<TweetList?>
                ) {
                    val body = Gson().toJson(response.body())
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
        val task = AskWatsonTask(list, this)

        CoroutineScope(Dispatchers.IO).launch {
            task.loop()
        }


        for (x in 0 until jsonArray.length()){
            val jsonObject = jsonArray.getJSONObject(x)
            val tweetText = jsonObject.getString("text")
//            val tweetCreate = jsonObject.getString("created_at")

            list.add(Tweet(tweetText))
        }
        table_main.adapter = TweetViewAdapter(list)
    }
}