package com.app.padams.data.local.db

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromFloatArray(value: FloatArray?): String? {
        return value?.joinToString(",")
    }

    @TypeConverter
    fun toFloatArray(value: String?): FloatArray? {
        if (value.isNullOrEmpty()) return null
        return value.split(",").map { it.toFloat() }.toFloatArray()
    }
}
