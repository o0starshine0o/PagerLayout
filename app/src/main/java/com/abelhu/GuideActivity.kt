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
        show.setOnClickListener { showGuide() }

        // 在view绘制完成后添加guide
        window.decorView.post { showGuide() }
    }

    private fun showGuide() {
        val guide = Guide(baseContext).addWindow(top)
        guide.setOnClickListener {
            Toast.makeText(baseContext, "click guide", Toast.LENGTH_SHORT).show()
            guide.dismiss()
        }
        (window.decorView as ViewGroup).addView(guide)
    }
}