package com.sommerval.sentimentali.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tweet_with_sentiment_table")
data class TweetWithSentiment(
    @PrimaryKey(autoGenerate = true)
    var tweetId: Long = 0L,

    @ColumnInfo(name = "tweet_text")
    var tweetText: String? = "No sentiment written to field" ,      //Refactor to avoid potential NPE

    @ColumnInfo(name = "start_time_milli")
    val startTimeMilli: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "end_time_milli")
    var endTimeMilli: Long = startTimeMilli,

    @ColumnInfo(name = "tweet_sentiment")
    var tweetSentiment: String? = "No sentiment written to field"  //Refactor to avoid potential NPE
)