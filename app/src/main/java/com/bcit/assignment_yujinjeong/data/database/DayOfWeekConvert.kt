package com.bcit.assignment_yujinjeong.data.database

import androidx.room.TypeConverter
import java.time.DayOfWeek

/**
 * This class is responsible for converting enum to string or string to enum.
 */
class  DayOfWeekConvert {

    @TypeConverter
    //Converting set of DayOfWeek(enum) to string separated with ','.
    fun fromDayOfWeekSet(days: Set<DayOfWeek>?): String {

        return days?.joinToString(",") {it.name} ?: ""
    }

    @TypeConverter
    //Converting day string to set of DayOfWeek Enums.
    fun toDayOfWeekSet(data: String?): Set<DayOfWeek> {

        if(data.isNullOrEmpty()) return emptySet()

        //Split string and mapping each name to DayOfWeek
        return data.split(",")
            //Converting each string back to Enum DayOfWeek constant.
            ?.map { DayOfWeek.valueOf(it) }
            //Removes duplicate words "DayOfWeek". If set is null return empty set.
            ?.toSet()?:emptySet()
    }

}