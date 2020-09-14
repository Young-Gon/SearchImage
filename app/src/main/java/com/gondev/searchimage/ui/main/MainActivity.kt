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

        binding.editTextSearch.setOnEditorActionListener { textView: TextView, i: Int, keyEvent: KeyEvent? ->
            imm.hideSoftInputFromWindow(textView.windowToken, 0)
            true
        }

        viewModel.requestStartImageDetailActivity.observe(this, EventObserver { item ->
            val index = adapter.currentList?.indexOf(item) ?: return@EventObserver
            val view = (binding.recyclerView.layoutManager as GridLayoutManager).findViewByPosition(index)
                ?: return@EventObserver

            startGalleryActivity(item.id, viewModel.keyword.value ?: "", view)
        })

        viewModel.state.observe(this, { state ->
            if(state is Error)
                Toast.makeText(this, R.string.error_network,Toast.LENGTH_SHORT).show()
        })
    }
}