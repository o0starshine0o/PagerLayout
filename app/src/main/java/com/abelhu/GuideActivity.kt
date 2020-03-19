package com.abelhu

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.ViewGroup
import com.abelhu.guide.Guide
import com.abelhu.guide.Help
import kotlinx.android.synthetic.main.activity_guide.*

class GuideActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guide)

        showGuide()
    }

    private fun showGuide() {
        top.post {
            val guide = Guide(baseContext).apply { addWindow(null, this@GuideActivity.top).addHelp(this, R.layout.item_guide_bottom, Help.BOTTOM, -100) }

            (window.decorView as ViewGroup).addView(guide)
        }
    }
}