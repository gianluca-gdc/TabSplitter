package com.gianluca_gdc.tabsplitter.util

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint

/**
 * convert bitmap to adjusted high contrast BW image for OCR accuracy
 */
fun convertToGrayscale(src: Bitmap): Bitmap {
    val width = src.width
    val height = src.height

    // desaturation and contrast boost
    val gray = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(gray)
    val paint = Paint()
    val cm = ColorMatrix().apply {
        setSaturation(0f)             // remove color
        // boost contrast around midpoint
        val contrast = 1.5f
        val translate = (-0.25f * 255)
        postConcat(ColorMatrix(floatArrayOf(
            contrast, 0f,       0f,       0f, translate,
            0f,       contrast, 0f,       0f, translate,
            0f,       0f,       contrast, 0f, translate,
            0f,       0f,       0f,       1f, 0f
        )))
    }
    paint.colorFilter = ColorMatrixColorFilter(cm)
    canvas.drawBitmap(src, 0f, 0f, paint)


    return gray
}