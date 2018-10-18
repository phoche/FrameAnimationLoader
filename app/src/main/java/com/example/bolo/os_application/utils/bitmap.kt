package com.example.bolo.os_application.utils

import android.graphics.BitmapFactory

/**
 * Create by bolo on 15/10/2018
 */
fun BitmapFactory.Options.calculateSampleSize(reqWidth: Int, reqHeight: Int)
        : Int {
    val width = outWidth
    val height = outHeight

    var inSampleSize = 2
    if (width > reqWidth || height > reqHeight) {
        val widthRound = Math.round(width * 1f / reqWidth)
        val heightRound = Math.round(height * 1f / reqWidth)

        inSampleSize = Math.max(widthRound, heightRound)
    }
    return inSampleSize
}