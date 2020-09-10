package com.gondev.searchimage.model.network.api

import com.gondev.searchimage.model.network.response.Result
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * GIPHY 서버로 부터 데이터를 받아옵니다
 */
interface KakaoImageAPI {

    /**
     * 이미지 검색
     * @param query 검색을 원하는 질의어
     * @param sort 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
     * @param page 결과 페이지 번호, 1~50 사이의 값, 기본 값 1
     * @param size 한 페이지에 보여질 문서 수, 1~80 사이의 값, 기본 값 80
     */
    @GET("v2/search/image")
    suspend fun requestImageList(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 20,
        @Query("sort") sort: String = "accuracy",
    ): Result
}