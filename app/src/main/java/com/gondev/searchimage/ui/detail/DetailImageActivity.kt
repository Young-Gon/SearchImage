package com.gondev.searchimage.ui.detail

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.gondev.searchimage.R

fun AppCompatActivity.startDetailImageActivity(
    itemId: Int,
    sharedElement: View
) {
    val option = ActivityOptionsCompat.makeSceneTransitionAnimation(
        this,
        sharedElement,
        ViewCompat.getTransitionName(sharedElement)!!
    )
    startActivity(
        Intent(this, DetailImageActivity::class.java).apply {
            putExtra("itemId",itemId)
        },
        option.toBundle()
    )
}

class DetailImageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_image)


    }
}