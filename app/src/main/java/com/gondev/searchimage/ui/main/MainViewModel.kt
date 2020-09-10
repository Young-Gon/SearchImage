package com.gondev.searchimage.ui.main

import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.gondev.searchimage.model.database.dao.ImageDataDao
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import com.gondev.searchimage.model.network.State
import com.gondev.searchimage.model.network.api.KakaoImageAPI
import com.gondev.searchimage.util.Event
import kotlinx.coroutines.*
import timber.log.Timber
import java.util.*

/**
 * 한번에 가저올 Image 목록 크기 입니다
 */
const val PAGE_SIZE = 30

class MainViewModel @ViewModelInject constructor(
    val dao: ImageDataDao,
    val api: KakaoImageAPI
) : ViewModel() {

    /**
     * 네트워크 상태를 나타냅니다
     */
    val state = MutableLiveData<State>(State.loading())

    val keyword = MutableLiveData("")

    private var job: Job? = null

    val imageList = keyword.switchMap { query ->
        job?.apply {
            Timber.e("JOB CANCELED")
            cancel()
        }
        job = Job()

        liveData(Dispatchers.Default + job!!) {
            Timber.d("Add New Keyword=${query}")
            delay(1000)

            emitSource(
                LivePagedListBuilder(dao.findImage(query), PAGE_SIZE)
                    .setBoundaryCallback(object :
                        PagedList.BoundaryCallback<ImageDataEntity>() {
                        override fun onZeroItemsLoaded() {
                            super.onZeroItemsLoaded()
                            loadDataFromNetwork()
                        }

                        override fun onItemAtEndLoaded(itemAtEnd: ImageDataEntity) {
                            super.onItemAtEndLoaded(itemAtEnd)
                            loadDataFromNetwork()
                        }
                    })
                    .build()
            )
        }
    }

    /**
     * 검색어가 바뀔떄 마다 페이지를 1로 초기화 해준다
     */
    private var page = keyword.map {
        1
    }.value ?: 1


    /**
     * 네트워크로 부터 offset 이후 부터 PAGE_SIZE 만큼 데이터를 가저 옵니다
     * 가저온 데이터는 데이터베이스에 저장합니다
     */
    private fun loadDataFromNetwork() {
        val query = this.keyword.value
        Timber.i("search query=${query}, offset=${page}")
        if (query == null || query.isEmpty()) {
            state.value = State.success()
            return
        }

        viewModelScope.launch {
            state.value = State.loading()
            try {
                val result = api.requestImageList(
                    query = query,
                    page = page,
                    size = PAGE_SIZE
                )
                if (result.documents == null) {
                    state.value = State.success()
                    return@launch
                }

                dao.insert(result.documents.map { it.toEntity(query) })
                page++
                state.value = State.success()
            } catch (e: Exception) {
                Timber.e(e)
                state.value = State.error(e)
            }
        }
    }

    val requestStartImageDetailActivity = MutableLiveData<Event<Pair<View, ImageDataEntity>>>()
    fun onclickItem(v: View, item: ImageDataEntity) {
        requestStartImageDetailActivity.value = Event(v to item)
    }

    fun onClickSearch(): Boolean {
        return true
    }
}