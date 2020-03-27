package com.sommerval.sentimentali.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface TweetDatabaseDao {

    @Insert
    fun insert(night: TweetWithSentiment)

    @Update
    fun update(night: TweetWithSentiment)

    @Query("SELECT * from tweet_with_sentiment_table WHERE tweetId = :key")
    fun get(key: Long): TweetWithSentiment?

    @Query("DELETE FROM tweet_with_sentiment_table")
    fun clear()

    @Query("SELECT * FROM tweet_with_sentiment_table ORDER BY tweetId DESC LIMIT 1")
    fun getTweet(): TweetWithSentiment?

    @Query("SELECT * FROM tweet_with_sentiment_table ORDER BY tweetId DESC")
    fun getAllTweets(): LiveData<List<TweetWithSentiment>>
}