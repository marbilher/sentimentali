package com.sommerval.sentimentali

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.sommerval.sentimentali.database.TweetDatabase
import com.sommerval.sentimentali.database.TweetDatabaseDao
import com.sommerval.sentimentali.database.TweetWithSentiment
import org.junit.Assert.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException


@RunWith(AndroidJUnit4::class)
class TweetDatabaseTest {

    private lateinit var tweetDao: TweetDatabaseDao
    private lateinit var db: TweetDatabase

    @Before
    fun createDb() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        // Using an in-memory database because the information stored here disappears when the
        // process is killed.
        db = Room.inMemoryDatabaseBuilder(context, TweetDatabase::class.java)
            // Allowing main thread queries, just for testing.
            .allowMainThreadQueries()
            .build()
        tweetDao = db.tweetDatabaseDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetTweet() {
        val tweet = TweetWithSentiment()
        tweetDao.insert(tweet)
        val tonight = tweetDao.getTweet()
        assertEquals(tonight?.tweetSentiment, "No sentiment written to field")
    }
}
