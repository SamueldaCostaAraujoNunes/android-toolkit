package dev.forcetower.toolkit.bindings

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import jp.wasabeef.glide.transformations.BlurTransformation
import jp.wasabeef.glide.transformations.SupportRSBlurTransformation
import timber.log.Timber

@BindingAdapter(value = [
    "imageUrl",
    "imageUri",
    "clipCircle",
    "listener",
    "dontTransform",
    "blurImage",
    "useBlurSupport",
    "blurRadius",
    "blurSampling",
    "fallbackResource"
], requireAll = false)
fun imageUrl(
    imageView: ImageView,
    imageUrl: String?,
    imageUri: Uri?,
    clipCircle: Boolean?,
    listener: RequestListener<Drawable>?,
    dontTransform: Boolean?,
    blurImage: Boolean?,
    useBlurSupport: Boolean?,
    blurRadius: Int?,
    blurSampling: Int?,
    fallbackResource: Int?
) {
    val load = imageUrl ?: imageUri ?: return
    var request = Glide.with(imageView).load(load)
    if (clipCircle == true) request = request.circleCrop()
    if (dontTransform == true) request = request.dontTransform()
    if (fallbackResource != null) request = request.error(fallbackResource)
    if (blurImage == true) {
        request = if (useBlurSupport == true)
            request.transform(SupportRSBlurTransformation(blurRadius ?: 15, blurSampling ?: 3))
        else
            request.transform(BlurTransformation(blurRadius ?: 15, blurSampling ?: 3))
    }

    if (listener != null) {
        Timber.d("Listener is not null...")
        request = request.listener(object : RequestListener<Drawable> {
            override fun onResourceReady(
                resource: Drawable?,
                model: Any?,
                target: Target<Drawable>?,
                dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                return listener.onResourceReady(resource, model, target, dataSource, isFirstResource)
            }

            override fun onLoadFailed(
                e: GlideException?,
                model: Any?,
                target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                return listener.onLoadFailed(e, model, target, isFirstResource)
            }
        })
    }
    request.into(imageView)
}

interface ImageLoadListener {
    fun onImageLoaded()
    fun onImageLoadFailed()
}