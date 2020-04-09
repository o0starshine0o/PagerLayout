package com.abelhu.guide

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.NinePatchDrawable
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.*

object NinePatchBitmapFactory {
    private const val NO_COLOR = 0x00000001

    fun createNinePatchDrawable(res: Resources?, bitmap: Bitmap): NinePatchDrawable {
        val rangeLists = checkBitmap(bitmap)
        val trimBitmap = trimBitmap(bitmap)
        return createNinePatchWithCapInsets(res, trimBitmap, rangeLists.rangeListX, rangeLists.rangeListY)
    }

    private fun createNinePatchWithCapInsets(res: Resources?, bitmap: Bitmap?, rangeListX: List<Range>?, rangeListY: List<Range>?): NinePatchDrawable {
        val buffer = getByteBuffer(rangeListX, rangeListY)
        return NinePatchDrawable(res, bitmap, buffer.array(), Rect(), null)
    }

    private fun getByteBuffer(rangeListX: List<Range>?, rangeListY: List<Range>?): ByteBuffer {
        val buffer = ByteBuffer.allocate(4 + 4 * 7 + 4 * 2 * rangeListX!!.size + 4 * 2 * rangeListY!!.size + 4 * 9).order(ByteOrder.nativeOrder())
        buffer.put(0x01.toByte()) // was serialised
        buffer.put((rangeListX.size * 2).toByte()) // x div
        buffer.put((rangeListY.size * 2).toByte()) // y div
        buffer.put(0x09.toByte()) // color
        // skip
        buffer.putInt(0)
        buffer.putInt(0)
        // padding
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        buffer.putInt(0)
        // skip 4 bytes
        buffer.putInt(0)
        for (range in rangeListX) {
            buffer.putInt(range.start)
            buffer.putInt(range.end)
        }
        for (range in rangeListY) {
            buffer.putInt(range.start)
            buffer.putInt(range.end)
        }
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        buffer.putInt(NO_COLOR)
        return buffer
    }

    private fun checkBitmap(bitmap: Bitmap): RangeLists {
        val width = bitmap.width
        val height = bitmap.height
        val rangeListX: MutableList<Range> = ArrayList()
        var pos = -1
        for (i in 1 until width - 1) {
            val color = bitmap.getPixel(i, 0)
            val alpha = Color.alpha(color)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            if (alpha == 255 && red == 0 && green == 0 && blue == 0) {
                if (pos == -1) {
                    pos = i - 1
                }
            } else {
                if (pos != -1) {
                    val range = Range()
                    range.start = pos
                    range.end = i - 1
                    rangeListX.add(range)
                    pos = -1
                }
            }
        }
        if (pos != -1) {
            val range = Range()
            range.start = pos
            range.end = width - 2
            rangeListX.add(range)
        }
        val rangeListY: MutableList<Range> = ArrayList()
        pos = -1
        for (i in 1 until height - 1) {
            val color = bitmap.getPixel(0, i)
            val alpha = Color.alpha(color)
            val red = Color.red(color)
            val green = Color.green(color)
            val blue = Color.blue(color)
            if (alpha == 255 && red == 0 && green == 0 && blue == 0) {
                if (pos == -1) {
                    pos = i - 1
                }
            } else {
                if (pos != -1) {
                    val range = Range()
                    range.start = pos
                    range.end = i - 1
                    rangeListY.add(range)
                    pos = -1
                }
            }
        }
        if (pos != -1) {
            val range = Range()
            range.start = pos
            range.end = height - 2
            rangeListY.add(range)
        }
        val rangeLists = RangeLists()
        rangeLists.rangeListX = rangeListX
        rangeLists.rangeListY = rangeListY
        return rangeLists
    }

    private fun trimBitmap(bitmap: Bitmap) = Bitmap.createBitmap(bitmap, 1, 1, bitmap.width - 2, bitmap.height - 2)

    class RangeLists {
        var rangeListX: List<Range>? = null
        var rangeListY: List<Range>? = null
    }

    class Range {
        var start = 0
        var end = 0
    }
}