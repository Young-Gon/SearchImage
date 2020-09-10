package com.gondev.searchimage.model.database.converter

import androidx.room.TypeConverter
import java.util.*

/**
 * Date 형식을 디비에 읽고 씁니다
 */
class TimeConverter {
    @TypeConverter
    fun fromTimestamp(value: Long) = Date(value)

    @TypeConverter
    fun dateToTimestamp(date: Date?) = date?.time?:0

}
