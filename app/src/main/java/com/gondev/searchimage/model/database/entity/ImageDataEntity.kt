package com.gondev.searchimage.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * 이미지 테이블 엔티티 입니다
 * 이미지 정보가 디비에 저장되는 모양을 정해줍니다
 */
@Entity(tableName = "image_data")
data class ImageDataEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val keyword: String,
    val doc_url: String,
    val collection: String,
    val datetime: Date,
    val display_sitename: String,
    val image_url: String,
    val thumbnail_url: String,
    val width: Int,
    val height: Int,
)