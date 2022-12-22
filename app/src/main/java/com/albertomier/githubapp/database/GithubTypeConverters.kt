package com.albertomier.githubapp.database

import android.util.Log
import androidx.room.TypeConverter

object GithubTypeConverters {

    @TypeConverter
    @JvmStatic
    fun stringToIntList(data: String?): List<Int>? {
        return data?.let {
            it.split(",").map {
                try {
                    it.toInt()
                } catch (e: java.lang.NumberFormatException) {
                    Log.e("TAG", e.message.toString());
                    null
                }
            }.filterNotNull()
        }
    }

    @TypeConverter
    @JvmStatic
    fun intListToString(ints: List<Int>?): String? {
        return ints?.joinToString(",")
    }
}