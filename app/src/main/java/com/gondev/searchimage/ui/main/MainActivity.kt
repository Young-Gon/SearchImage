package com.gondev.searchimage.ui.main

import android.os.Bundle
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.GridLayoutManager
import com.gondev.searchimage.BR
import com.gondev.searchimage.R
import com.gondev.searchimage.databinding.ImageItemBinding
import com.gondev.searchimage.databinding.MainActivityBinding
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import com.gondev.searchimage.ui.DataBindingAdapter
import com.gondev.searchimage.ui.gallery.startGalleryActivity
import com.gondev.searchimage.util.EventObserver
import dagger.hilt.android.AndroidEntryPoint

/**
 * 메인 화면 입니다
 */
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private val viewModel: MainViewModel by viewModels()
    private val imm by lazy { getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.lifecycleOwner = this

        // 리사이클러 뷰 아이템에서 데이터 바인딩을 돕도록 하는 아답타
        val adapter = DataBindingAdapter<ImageDataEntity, ImageItemBinding>(
            layoutResId = R.layout.item_image,
            bindingVariableId = BR.item,
            diffCallback = object : DiffUtil.ItemCallback<ImageDataEntity>() {
                override fun areItemsTheSame(oldItem: ImageDataEntity, newItem: ImageDataEntity) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(oldItem: ImageDataEntity, newItem: ImageDataEntity) =
                    oldItem == newItem
            },
            lifecycleOwner = this,
            BR.vm to viewModel,
        )
        binding.recyclerView.adapter = adapter

        // 검색은 실시간으로 되니 검색후 키보드가 필요 없을 경우 키보드를 숨길 수 있도록
        // 검색 엑션 버튼을 달아 키보드를 숨기는 용도로 사용한다
        binding.editTextSearch.setOnEditorActionListener { textView: TextView, i: Int, keyEvent: KeyEvent? ->
            imm.hideSoftInputFromWindow(textView.windowToken, 0)
            true
        }

        // 갤러리 화면 시작
        viewModel.requestStartImageDetailActivity.observe(this, EventObserver { item ->
            val index = adapter.currentList?.indexOf(item) ?: return@EventObserver
            val view = (binding.recyclerView.layoutManager as GridLayoutManager).findViewByPosition(index)
                ?: return@EventObserver

            startGalleryActivity(item.id, viewModel.keyword.value ?: "", view)
        })

        // 네트워크 에러가 났을 경우 노티
        viewModel.state.observe(this, { state ->
            if(state is Error)
                Toast.makeText(this, R.string.error_network,Toast.LENGTH_SHORT).show()
        })
    }
}