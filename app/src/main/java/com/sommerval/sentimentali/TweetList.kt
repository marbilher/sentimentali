package com.sommerval.sentimentali

import com.google.gson.annotations.SerializedName


class TweetList {
    @SerializedName("statuses")
    var tweets: ArrayList<Tweet>? = null
}