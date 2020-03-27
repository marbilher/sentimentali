package com.sommerval.sentimentali.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [TweetWithSentiment::class], version = 1, exportSchema = false)
abstract class TweetDatabase : RoomDatabase() {

    abstract val tweetDatabaseDao: TweetDatabaseDao

    companion object {

        @Volatile
        private var INSTANCE: TweetDatabase? = null

        fun getInstance(context: Context): TweetDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                            context.applicationContext,
                            TweetDatabase::class.java,
                            "tweet_history_database"
                        )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}