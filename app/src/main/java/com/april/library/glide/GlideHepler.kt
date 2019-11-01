package com.april.library.glide

import android.util.SparseArray
import android.view.View
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions

fun Fragment.loadImage(url: String, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .into(imageView)
}

fun Fragment.circleImage(url: String, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .apply(imageView.getOptions(null))
        .into(imageView)
}

fun Fragment.roundImage(url: String, radiusDP: Int, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .apply(imageView.getOptions(radiusDP))
        .into(imageView)
}

fun FragmentActivity.loadImage(url: String, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .into(imageView)
}

fun FragmentActivity.circleImage(url: String, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .apply(imageView.getOptions(null))
        .into(imageView)
}

fun FragmentActivity.roundImage(url: String, radiusDP: Int, imageView: ImageView) {
    Glide.with(this)
        .load(url)
        .apply(imageView.getOptions(radiusDP))
        .into(imageView)
}

fun ImageView.loadImage(url: String) {
    Glide.with(this)
        .load(url)
        .into(this)
}

fun ImageView.circleImage(url: String) {
    Glide.with(this)
        .load(url)
        .apply(getOptions(null))
        .into(this)
}

fun ImageView.roundImage(url: String, radiusDP: Int) {
    Glide.with(this)
        .load(url)
        .apply(getOptions(radiusDP))
        .into(this)
}

private val circleOptions by lazy { RequestOptions.circleCropTransform() }
private val roundOptionsArray by lazy { SparseArray<RequestOptions>() }

private fun View.getOptions(radiusDP: Int? = 0): RequestOptions {
    return if (radiusDP == null) {
        circleOptions
    } else {
        var options: RequestOptions? = roundOptionsArray.get(radiusDP)
        return if (options != null) {
            options
        } else {
            options = RequestOptions.bitmapTransform(RoundedCorners(
                (radiusDP * (resources.displayMetrics.density) + 0.5f).toInt()
            ))
            roundOptionsArray.put(radiusDP, options)
            options
        }
    }
}
