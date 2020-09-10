package com.gondev.searchimage.ui

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Transformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.transition.DrawableCrossFadeTransition
import com.bumptech.glide.request.transition.Transition
import com.bumptech.glide.request.transition.TransitionFactory
import com.bumptech.glide.signature.ObjectKey
import com.gondev.searchimage.R
import timber.log.Timber


@BindingAdapter("visibleGone")
fun View.showHide(show: Boolean) {
    visibility = if (show) View.VISIBLE else View.GONE
}

@BindingAdapter("action")
fun EditText.setActionBinding(listener: TextView.OnEditorActionListener) {
    setOnEditorActionListener(listener)
}

@BindingAdapter("items")
fun <T> RecyclerView.setItems(items: PagedList<T>?) {
    // layoutManager를 실수로 넣지 않을 경우
    // list에 item이 있어도 RecyclerView에는 아이템이 나타나지 않는다
    // 이런 실수를 방지 하려면 이런 경우 아에 앱을 빨리 죽여 버리는게 낫다
    if (layoutManager == null)
        throw NullPointerException("layoutManager가 없습니다")

    (this.adapter as? PagedListAdapter<T, *>)?.run {
        submitList(items)
    }
}

@BindingAdapter("hasFixedSize")
fun RecyclerView.hasFixedSize(fix: Boolean) {
    setHasFixedSize(fix)
}

@BindingAdapter("src", "thumbnail", "src_width", "src_height", requireAll = true)
fun ImageView.bindImage(src: String?, thumbnail: String?, width: Int?, height: Int?) {
    if (src == null) {
        setImageResource(R.drawable.ic_empty_image)
        return
    }

    var glide = Glide.with(context).load(src)

    // 스틸 썸네일이 없는 경우가 있다
    // 있는 경우만 썸네일을 넣어 주자
    if (thumbnail != null) {
        glide = glide.thumbnail(
            Glide.with(context).load(thumbnail)
                .transition(DrawableTransitionOptions.withCrossFade(300))
                .apply(getGlideRequestOption(thumbnail))
        )
    }

    glide
        .apply(getGlideRequestOption(src, width, height))
        .transition(DrawableTransitionOptions.with(DrawableAlwaysCrossFadeFactory()))
        .into(this)
}

/**
 * Best strategy to load images using Glide
 * https://android.jlelse.eu/best-strategy-to-load-images-using-glide-image-loading-library-for-android-e2b6ba9f75b2
 */
fun getGlideRequestOption(imageName: String, width: Int? = null, height: Int? = null) =
    RequestOptions()
        .diskCacheStrategy(DiskCacheStrategy.ALL)
        .signature(ObjectKey(imageName)).also { option ->
            if (width != null && height != null)
                option.override(width, height)
        }

/**
 * 이미지 교체시 faid-in이 말을 듣지 않을때
 * 해당 클래스로 처리
 * https://stackoverflow.com/questions/53664645/how-to-apply-animation-on-cached-image-in-glide
 */
class DrawableAlwaysCrossFadeFactory : TransitionFactory<Drawable> {
    private val resourceTransition: DrawableCrossFadeTransition = DrawableCrossFadeTransition(
        300,
        true
    ) //customize to your own needs or apply a builder pattern

    override fun build(dataSource: DataSource?, isFirstResource: Boolean): Transition<Drawable> {
        return resourceTransition
    }
}

@BindingAdapter("enable")
fun View.setEnableBinding(enable: Boolean) {
    isEnabled = enable
}