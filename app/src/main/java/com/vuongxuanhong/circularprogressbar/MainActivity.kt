package com.vuongxuanhong.circularprogressbar

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        progressbar.postDelayed({
            progressbar.valueWithAnimation = 50f
        }, 2000)

        progressbar.postDelayed({
            progressbar.valueWithAnimation = 20f
        }, 5000)
    }
}
