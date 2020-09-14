package com.gondev.searchimage.ui.gallery

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.DataSource
import androidx.paging.ItemKeyedDataSource
import androidx.paging.LivePagedListBuilder
import com.gondev.searchimage.model.database.dao.ImageDataDao
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 갤러리에서 사용하는 뷰모델입니다
 */
class GalleryViewModel @ViewModelInject constructor(
    val dao: ImageDataDao,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    /**
     * 이미지 목록 입니다
     */
    val images = LivePagedListBuilder(
        ViewPagerDataSource.Factory(
            dao,
            savedStateHandle["keyword"] ?: "",
            viewModelScope,
        ), 10
    ).setInitialLoadKey(savedStateHandle["itemId"]).build()

    /**
     * 뷰 페이지에서 사용자가 보고 있는 페이지 위치 입니다
     */
    val currentPosition = MutableLiveData(0)
    fun onPageSelected(position: Int) {
        currentPosition.value = position
    }

    /**
     * 하단 정보 설명 창에서 사용하는 날짜 포멧 변환 함수 입니다
     */
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    fun getDate(date: Date?): String {
        return sdf.format(date ?: return "")
    }
}

/**
 * 갤러리에서 사용하는 이미지 목록을 만들기 위해서 제공하는 [DataSource] 입니다
 * [paging](https://developer.android.com/topic/libraries/architecture/paging?hl=ko) 라이브러리는
 * 페이징시 내부 데이터를 페이지 단위로 불러드리기 위해 [DataSource]를 제공 받는데
 * 이 [DataSource]를 상속받아 페이징 요청이 오면 함게 넘어온 데이터를 기준으로 페이징된 데이터를 보내면 됩니다
 * [room](https://developer.android.com/topic/libraries/architecture/room) 라이브러리는 기본적인
 * [DataSource]를 구현해 주지만 갤러리에서 필요로 하는 특정 index부터 시작하여 좌우로 페이징하는
 * [DataSource]는 지원하지 않는 관계로 직접 구현 하였습니다
 */
class ViewPagerDataSource(
    private val dao: ImageDataDao,
    private val keyword: String,
    private val viewModelScope: CoroutineScope,
) : ItemKeyedDataSource<Int, ImageDataEntity>() {
    /**
     * 팩토리 클래스
     * [paging](https://developer.android.com/topic/libraries/architecture/paging?hl=ko) 라이브러리에서
     * [DataSource]를 원하는 시점에 만들 수 있도록 Factory 클레스를 재공 해줘야 합니다
     */
    class Factory(
        private val dao: ImageDataDao,
        private val keyword: String,
        private val viewModelScope: CoroutineScope,
    ) : DataSource.Factory<Int, ImageDataEntity>() {
        override fun create(): DataSource<Int, ImageDataEntity> {
            return ViewPagerDataSource(dao, keyword, viewModelScope)
        }
    }

    override fun getKey(item: ImageDataEntity) = item.id

    /**
     * 최초로 로드 되는 데이터 요청
     * @param params 초기화에 필요한 정보가 담겨있는 파라메터
     * @param callback 데이터가 로드 되고 나서 결과값을 넘겨줄 콜백 함수 (이렇게 하는 이유는 데티터 로드를 비동기로 하기 때문입니다)
     */
    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<ImageDataEntity>
    ) {
        viewModelScope.launch {
            callback.onResult(listOf(dao.findInitialImage(keyword,params.requestedInitialKey ?: 0)))
        }
    }

    /**
     * 이전 페이지 로드 요청
     * @param params 데이터 로드에 필요한 정보가 담겨있는 파라메터
     * @param callback 데이터가 로드 되고 나서 결과값을 넘겨줄 콜백 함수
     */
    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<ImageDataEntity>) {
        viewModelScope.launch {
            callback.onResult(dao.findNextImages(keyword, params.key, params.requestedLoadSize))
        }
    }

    /**
     * 다음 페이지 로드 요청
     * @param params 데이터 로드에 필요한 정보가 담겨있는 파라메터
     * @param callback 데이터가 로드 되고 나서 결과값을 넘겨줄 콜백 함수
     */
    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<ImageDataEntity>) {
        viewModelScope.launch {
            callback.onResult(dao.findPrevImages(keyword, params.key, params.requestedLoadSize).asReversed())
        }
    }

}