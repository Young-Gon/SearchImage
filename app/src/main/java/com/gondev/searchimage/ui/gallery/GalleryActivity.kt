package com.gondev.searchimage.ui.gallery

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NavUtils
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import com.gondev.searchimage.BR
import com.gondev.searchimage.R
import com.gondev.searchimage.databinding.GalleryActivityBinding
import com.gondev.searchimage.databinding.ViewpagerImageItemBinding
import com.gondev.searchimage.model.database.entity.ImageDataEntity
import com.gondev.searchimage.ui.DataBindingAdapter
import dagger.hilt.android.AndroidEntryPoint

fun AppCompatActivity.startGalleryActivity(itemId: Int, keyword: String, sharedElement: View) {
    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
        this,
        sharedElement,
        ViewCompat.getTransitionName(sharedElement)!!
    )
    startActivity(
        Intent(this, GalleryActivity::class.java).apply {
            putExtra("itemId", itemId)
            putExtra("keyword", keyword)
        },
        option.toBundle()
    )
}

@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    lateinit var binding: GalleryActivityBinding
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportPostponeEnterTransition()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        window.decorView.setBackgroundColor(resources.getColor(android.R.color.black))
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#33000000")))

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        (binding.frameLayout2.layoutParams as ConstraintLayout.LayoutParams).bottomMargin =
            getNavigationbarHeight()

        binding.viewPager.adapter = DataBindingAdapter<ImageDataEntity, ViewpagerImageItemBinding>(
            layoutResId = R.layout.item_viewpager_image,
            bindingVariableId = BR.item,
            diffCallback = object : DiffUtil.ItemCallback<ImageDataEntity>() {
                override fun areItemsTheSame(oldItem: ImageDataEntity, newItem: ImageDataEntity) =
                    oldItem.id == newItem.id

                override fun areContentsTheSame(
                    oldItem: ImageDataEntity,
                    newItem: ImageDataEntity
                ) =
                    oldItem == newItem
            },
            lifecycleOwner = this,
            BR.vm to viewModel
        )

        Handler().postDelayed({
            startPostponedEnterTransition()
        }, 150)
    }

    fun onClickItem(v: View) {
        toggleHideBar()
    }

    fun toggleHideBar() {
        val uiOptions: Int = window.decorView.systemUiVisibility
        var newUiOptions = uiOptions

        if (uiOptions or View.SYSTEM_UI_FLAG_IMMERSIVE == uiOptions) {
            binding.groupDescription.visibility = View.VISIBLE
        } else {
            binding.groupDescription.visibility = View.GONE
        }

        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_FULLSCREEN
        newUiOptions = newUiOptions xor View.SYSTEM_UI_FLAG_IMMERSIVE
        window.decorView.systemUiVisibility = newUiOptions
    }

    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    fun getNavigationbarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}