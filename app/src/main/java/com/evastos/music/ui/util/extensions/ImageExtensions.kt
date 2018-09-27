package com.evastos.music.ui.util.extensions

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.evastos.music.R
import com.evastos.music.inject.module.GlideRequests

fun GlideRequests.loadImage(
    imagePath: String?,
    imageView: ImageView,
    onLoaded: (() -> Unit)? = null
) {
    clear(imageView)
    if (imagePath != null) {
        load(Uri.parse(imagePath))
                .centerCrop()
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        imageView.setImageResource(R.drawable.ic_broken_image)
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        onLoaded?.invoke()
                        return false
                    }
                })
                .into(imageView)
    } else {
        imageView.scaleType = ImageView.ScaleType.CENTER_INSIDE
        imageView.setImageResource(R.drawable.ic_broken_image)
    }
}
