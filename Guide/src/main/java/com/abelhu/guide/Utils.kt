package com.abelhu.guide

import android.view.View
import java.util.concurrent.atomic.AtomicInteger

fun generateViewId(): Int {
    while (true) {
        // 通过反射获取id生成器
        val clazz = View::class.java
        val field = clazz.getDeclaredField("sNextGeneratedId")
        field.isAccessible = true
        val nextGeneratedId = field.get(null) as AtomicInteger
        val result: Int = nextGeneratedId.get()
        // aapt-generated IDs have the high byte nonzero; clamp to the range under that.
        var newValue = result + 1
        if (newValue > 0x00FFFFFF) newValue = 1 // Roll over to 1, not 0.
        if (nextGeneratedId.compareAndSet(result, newValue)) {
            return result
        }
    }
}