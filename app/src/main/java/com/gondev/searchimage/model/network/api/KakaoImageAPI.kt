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
     * https://developers.kakao.com/docs/latest/ko/daum-search/dev-guide#search-image
     * 다음 검색 서비스에서 질의어로 이미지를 검색합니다. 앱 어드민 키를 헤더에 담아 GET으로 요청합니다.
     * 원하는 검색어와 함께 결과 형식 파라미터를 선택적으로 추가할 수 있습니다.
     * 응답 바디는 meta, documents로 구성된 JSON 객체입니다.
     *
     * @param query 검색을 원하는 질의어
     * @param sort 결과 문서 정렬 방식, accuracy(정확도순) 또는 recency(최신순), 기본 값 accuracy
     * @param page 결과 페이지 번호, 1~50 사이의 값, 기본 값 1
     * @param size 한 페이지에 보여질 문서 수, 1~80 사이의 값, 기본 값 80
     */
    @GET("v2/search/image")
    suspend fun requestImageList(
        @Query("query") query: String,
        @Query("page") page: Int = 1,
        @Query("size") size: Int = 30,
        @Query("sort") sort: String = "accuracy",
    ): Result
}