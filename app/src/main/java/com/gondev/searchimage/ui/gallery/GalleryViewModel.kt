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

class GalleryViewModel @ViewModelInject constructor(
    val dao: ImageDataDao,
    @Assisted private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    /*val images = LivePagedListBuilder(
        dao.findImage(savedStateHandle["keyword"] ?: ""), 20
    ).setInitialLoadKey(savedStateHandle["itemId"]).build()*/
    val images = LivePagedListBuilder(
        ViewPagerDataSource.Factory(
            dao,
            savedStateHandle["keyword"] ?: "",
            viewModelScope,
        ), 10
    ).setInitialLoadKey(savedStateHandle["itemId"]).build()

    val currentPosition = MutableLiveData(0)
    fun onPageSelected(position: Int) {
        currentPosition.value = position
    }

    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.KOREA)
    fun getDate(date: Date?): String {
        return sdf.format(date ?: return "")
    }
}

class ViewPagerDataSource(
    private val dao: ImageDataDao,
    private val keyword: String,
    private val viewModelScope: CoroutineScope,
) : ItemKeyedDataSource<Int, ImageDataEntity>() { // 팩토리 클래스
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

    override fun loadInitial(
        params: LoadInitialParams<Int>,
        callback: LoadInitialCallback<ImageDataEntity>
    ) {
        viewModelScope.launch {
            callback.onResult(listOf(dao.findInitialImage(keyword,params.requestedInitialKey ?: 0)))
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<ImageDataEntity>) {
        viewModelScope.launch {
            callback.onResult(dao.findNextImages(keyword, params.key, params.requestedLoadSize))
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<ImageDataEntity>) {
        viewModelScope.launch {
            callback.onResult(dao.findPrevImages(keyword, params.key, params.requestedLoadSize).asReversed())
        }
    }

}