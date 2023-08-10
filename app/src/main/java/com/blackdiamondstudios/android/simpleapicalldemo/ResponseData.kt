package com.blackdiamondstudios.android.simpleapicalldemo

data class ResponseData (
    val message: String,
    val user_id: Int,
    val myMatter : String,
    val profile_details : ProfileDetails ,
    val data_list : List<DataListDetail>
)

data class ProfileDetails(
    val profile_completed : Boolean,
    val rating : Double
    )

data class DataListDetail(
val user_id : Int,
        val my_matter :String
        )