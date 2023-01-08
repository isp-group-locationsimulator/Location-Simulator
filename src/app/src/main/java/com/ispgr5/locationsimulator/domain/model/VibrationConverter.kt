package com.ispgr5.locationsimulator.domain.model

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Type Converter Class for a Vibration Object.
 * This class helps the Room Database to handle a List of Vibration by
 * Converting the List to a String and back.
 * Room will automatically call the @TypeConverter functions to Convert the data
 */
@ProvidedTypeConverter
class VibrationConverter {

    /**
     * This function converts a List of Vibration to a String by using Gson
     */
    @TypeConverter
    fun vibrationListToString(vibrationList : List<Vibration>) : String{
        val type = object : TypeToken<List<Vibration>>(){}.type
        return Gson().toJson(vibrationList, type)
    }

    /**
     * This function converts a String to a List of Vibrations by using Gson
     */
    @TypeConverter
    fun vibrationStrToVibrationList(vibrationStr : String) : List<Vibration>{
        val type = object : TypeToken<List<Vibration>>(){}.type
        return Gson().fromJson(vibrationStr, type)
    }
}