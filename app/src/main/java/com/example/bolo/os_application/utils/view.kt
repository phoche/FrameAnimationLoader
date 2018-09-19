package com.example.bolo.os_application.utils

import android.widget.ImageView
import com.example.bolo.os_application.loader.ViewSize

/**
 * Create by bolo on 19/09/2018
 */
fun ImageView.calculateImageViewSize(): ViewSize {

    return let {
        val metrics = it.context.resources.displayMetrics

        var viewWidth = it.width
        if (viewWidth <= 0) viewWidth = it.layoutParams.width
        if (viewWidth <= 0) viewWidth = getImageViewFieldValue(it, "mMaxWidth")
        if (viewWidth <= 0) viewWidth = metrics.widthPixels

        var viewHeight = it.height
        if (viewHeight <= 0) viewHeight = it.layoutParams.height
        if (viewHeight <= 0) viewHeight = getImageViewFieldValue(it, "mMaxHeight")
        if (viewHeight <= 0) viewHeight = metrics.heightPixels

        ViewSize(viewWidth, viewHeight)
    }
}

private fun getImageViewFieldValue(imageView: ImageView, fieldName: String): Int {
    var value = 0
    try {
        val field = ImageView::class.java.getDeclaredField(fieldName)
        field.isAccessible = true
        val fieldValue = field.getInt(imageView)
        if (fieldValue > 0 && fieldValue < Int.MAX_VALUE) {
            value = fieldValue
        }
    } catch (e: Exception) {
    }
    return value
}