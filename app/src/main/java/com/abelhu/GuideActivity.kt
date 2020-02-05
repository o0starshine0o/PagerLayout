package com.abelhu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import android.widget.Toast
import com.abelhu.guide.Guide
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        top.setOnClickListener { Toast.makeText(baseContext, "click left view", Toast.LENGTH_SHORT).show() }
        show.setOnClickListener { showGuideBottom() }

        // 在view绘制完成后添加guide
        window.decorView.post { showGuideBottom() }
    }

    private fun showGuideBottom() {
        val guide = Guide(baseContext)
        guide.addWindow(top, R.layout.item_guide_bottom, -100).setOnClickListener {
            Toast.makeText(baseContext, "click guide", Toast.LENGTH_SHORT).show()
            guide.dismiss()
            showGuideLeft()
        }
        (window.decorView as ViewGroup).addView(guide)
    }

    private fun showGuideLeft() {
        val guide = Guide(baseContext)
        guide.addWindow(top, R.layout.item_guide_left, 100, Guide.Window.LEFT).setOnClickListener {
            Toast.makeText(baseContext, "click guide", Toast.LENGTH_SHORT).show()
            guide.dismiss()
            showGuideTop()
        }
        (window.decorView as ViewGroup).addView(guide)
    }

    private fun showGuideTop() {
        val guide = Guide(baseContext)
        guide.addWindow(bottom, R.layout.item_guide_top, 100, Guide.Window.TOP).setOnClickListener {
            Toast.makeText(baseContext, "click guide", Toast.LENGTH_SHORT).show()
            guide.dismiss()
            showGuideRight()
        }
        (window.decorView as ViewGroup).addView(guide)
    }

    private fun showGuideRight() {
        val guide = Guide(baseContext)
        guide.addWindow(bottom, R.layout.item_guide_right, -100, Guide.Window.RIGHT).setOnClickListener {
            Toast.makeText(baseContext, "click guide", Toast.LENGTH_SHORT).show()
            guide.dismiss()
        }
        (window.decorView as ViewGroup).addView(guide)
    }
}