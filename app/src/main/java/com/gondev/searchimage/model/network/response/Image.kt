package com.gondev.searchimage.model.network.response

import android.provider.MediaStore
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import java.util.*


data class Result(
    val documents: List<Image>?,
    val meta: Pagination
)

data class Image(
    val collection: String, // "blog",
    val datetime: Date, //"2018-12-21T20:46:05.000+09:00",
    val display_sitename: String, // "티스토리",
    val doc_url: String, //"http://heathersummer.tistory.com/74",
    val height: Int, //573,
    val image_url: String, //"http://cfile5.uf.tistory.com/image/99AA874D5C1CCD7D129E65",
    val thumbnail_url: String, //"https://search3.kakaocdn.net/argon/130x130_85_c/Acj7WdO98cj",
    val width: Int, //580
) {
    fun toEntity(keyword: String) =
        ImageDataEntity(
            id = 0,
            keyword = keyword,
            doc_url = doc_url,
            collection = collection,
            datetime = datetime,
            display_sitename = display_sitename,
            image_url = image_url,
            thumbnail_url = thumbnail_url,
            width = width,
            height = height,
        )
}

data class Pagination(
    val is_end: Boolean,
    val total_count: Int,
    val pageable_count: Int,
)