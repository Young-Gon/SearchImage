package com.gondev.searchimage.ui.main

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DiffUtil
import com.gondev.searchimage.R
import com.gondev.searchimage.BR
import com.gondev.searchimage.databinding.ImageItemBinding
import com.gondev.searchimage.databinding.MainActivityBinding
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import com.gondev.searchimage.ui.DataBindingAdapter
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        binding.recyclerView.adapter = DataBindingAdapter<ImageDataEntity, ImageItemBinding>(
            layoutResId = R.layout.item_image,
            bindingVariableId = BR.item,
            diffCallback = object : DiffUtil.ItemCallback<ImageDataEntity>() {
                override fun areItemsTheSame(
                    oldItem: ImageDataEntity,
                    newItem: ImageDataEntity
                ) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ImageDataEntity,
                    newItem: ImageDataEntity
                ) =
                    oldItem == newItem
            },
            lifecycleOwner = this,
            BR.vm to viewModel,
        )
    }
}