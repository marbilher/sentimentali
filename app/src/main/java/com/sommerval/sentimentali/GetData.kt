package com.sommerval.sentimentali

import retrofit2.Call
import retrofit2.http.GET


interface GetData {

    //Wrap the response in a Call object with the type of the expected result
    @GET("tweets.json?q=disneyland")
    fun getAllUsers()
            : Call<TweetList?>;
}
