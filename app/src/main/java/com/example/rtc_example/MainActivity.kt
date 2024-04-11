package com.example.rtc_example

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.rtc_example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mHandler: Handler

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mHandler = Handler(Looper.getMainLooper())

        mBinding.btnVideo.setOnClickListener {
            startActivity(Intent(this, VideoActivity::class.java))
        }

        mBinding.btnFusion.setOnClickListener {
            startActivity(Intent(this, FusionActivity::class.java))
        }
    }

}