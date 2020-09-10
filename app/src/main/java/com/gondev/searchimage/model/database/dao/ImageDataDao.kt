package com.gondev.searchimage.model.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import java.util.*

@Dao
interface ImageDataDao {

    /**
     * 서버로 부터 받은 이미지 데이터를 최신순으로 찾습니다
     * @return pageing에서 사용하는 DataSource형태로 넘깁니다
     */
    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%'")
    fun findImage(keyword: String): DataSource.Factory<Int, ImageDataEntity>

    @Query("SELECT * FROM image_data")
    fun findAll(): DataSource.Factory<Int, ImageDataEntity>

    /**
     * 서버로 부터 받은 데이터를 추가합니다
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: List<ImageDataEntity>)
}

/*
@Transaction
suspend fun ImageDataDao.insert(entity: List<ImageDataEntity>) =
    entity.map {
        insert(it)
    }.size*/
