package com.gondev.searchimage.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.gondev.searchimage.model.database.converter.TimeConverter
import com.gondev.searchimage.model.database.dao.ImageDataDao
import com.gondev.searchimage.model.database.entity.ImageDataEntity

/**
 * 데이터베이스 모듈입니다
 * 모든 entity와 dao를 관리하고
 * 마이그레이션을 진행 합니다
 */
@Database(
    entities = [
        ImageDataEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(TimeConverter::class)
abstract class AppDatabase: RoomDatabase() {

    abstract fun getImageDataDao(): ImageDataDao
}