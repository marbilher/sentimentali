package com.sommerval.sentimentali

import com.google.gson.annotations.SerializedName


data class Tweet (var text: String, var created_at: String)

//    @SerializedName("created_at")     //Read up on the serialized name annotation
//    var dateCreated: String? = null
//
//    @SerializedName("id")
//    var id: String? = null
//
//    @SerializedName("text")
//    var text: String? = null
//
//    @SerializedName("in_reply_to_status_id")
//    var inReplyToStatusId: String? = null
//
//    @SerializedName("in_reply_to_user_id")
//    var inReplyToUserId: String? = null
//
//    @SerializedName("in_reply_to_screen_name")
//    var inReplyToScreenName: String? = null
//
//    @SerializedName("user")
//    var user: TwitterUser? = null
//    override fun toString(): String {
//        return text!!
//    }
//}