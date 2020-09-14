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

    /**
     * 서버로 부터 받은 데이터를 추가합니다
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(entity: List<ImageDataEntity>)

    /**
     * 특정 이미지를 검색 합니다
     * @param id 검색할 이미지 ID
     * @param keyword 검색할 이미지 키워드 없으면 ""
     */
    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%' AND id = :id")
    suspend fun findInitialImage(keyword: String, id: Int): ImageDataEntity

    /**
     * 검색 key를 기준으로 이전 목록을 requestedLoadSize 만큼 가저 옵니다
     * @param key 검색할 이미지 ID
     * @param keyword 검색할 이미지 키워드
     * @param requestedLoadSize 가저올 이미지 목록 결과 크기
     */
    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%' AND id< :key ORDER BY id DESC LIMIT :requestedLoadSize")
    suspend fun findPrevImages(keyword: String, key: Int, requestedLoadSize: Int): List<ImageDataEntity>

    /**
     * 검색 key를 기준으로 다음 목록을 requestedLoadSize 만큼 가저 옵니다
     * @param key 검색할 이미지 ID
     * @param keyword 검색할 이미지 키워드
     * @param requestedLoadSize 가저올 이미지 목록 결과 크기
     */
    @Query("SELECT * FROM image_data WHERE keyword like :keyword || '%' AND :key < id ORDER BY id ASC LIMIT :requestedLoadSize")
    suspend fun findNextImages(keyword: String, key: Int, requestedLoadSize: Int): List<ImageDataEntity>
}