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

/**
 * 겔러리 엑티비티를 화면에 띄웁니다
 * @param itemId 갤러리 시작시 화면에 표시할 [ImageDataEntity] ID
 * @param keyword 이미지 목록을 구성할떄 참조할 검색 키워드
 * @param sharedElement 시작 트렌지션 뷰
 */
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

/**
 * 갤러리 엑티비티
 */
@AndroidEntryPoint
class GalleryActivity : AppCompatActivity() {
    lateinit var binding: GalleryActivityBinding
    private val viewModel: GalleryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 트렌지션이 이미지가 호출 완료 되기전까지 중지
        supportPostponeEnterTransition()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_gallery)
        window.decorView.setBackgroundColor(resources.getColor(android.R.color.black))
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#33000000")))

        // 툴바에 뒤로가기 설정
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.vm = viewModel
        binding.lifecycleOwner = this
        // 하단 정보 레이아웃을 네비게이션 바 위로 올림
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

        // 이미지 로드가 로드되길 기다려 트렌지션 수행
        Handler().postDelayed({
            startPostponedEnterTransition()
        }, 150)
    }

    /**
     * 화면 클릭시 집중모드 토글
     */
    fun onClickItem(v: View) {
        toggleImmersiveMode()
    }

    // 집중 모드 토글
    private fun toggleImmersiveMode() {
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

    /**
     * 툴바에서 뒤로 가기 기능 수행
     */
    override fun onOptionsItemSelected(item: MenuItem) =
        when (item.itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    /**
     * 하단 네비게이션바 높이
     * @return 하단 네비게이션바 높이
      */
    private fun getNavigationbarHeight(): Int {
        val resourceId: Int = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        return if (resourceId > 0) {
            resources.getDimensionPixelSize(resourceId)
        } else 0
    }
}