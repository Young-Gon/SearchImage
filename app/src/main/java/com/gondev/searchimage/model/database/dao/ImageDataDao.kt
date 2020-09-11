package com.gondev.searchimage.model.database.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gondev.searchimage.model.database.entity.ImageDataEntity

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

    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%' AND id = :id")
    suspend fun findInitialImage(keyword: String, id: Int): ImageDataEntity

    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%' AND id< :key ORDER BY id DESC LIMIT :requestedLoadSize")
    suspend fun findPrevImages(keyword: String, key: Int, requestedLoadSize: Int): List<ImageDataEntity>

    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%' AND :key < id ORDER BY id ASC LIMIT :requestedLoadSize")
    suspend fun findNextImages(keyword: String, key: Int, requestedLoadSize: Int): List<ImageDataEntity>
}