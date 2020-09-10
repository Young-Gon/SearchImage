package com.gondev.searchimage.model.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

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