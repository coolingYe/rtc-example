package com.example.rtc_example

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSONArray
import com.example.rtc_example.databinding.ActivityMainBinding
import com.zeewain.rtc.IRtcEngineEventHandler
import com.zeewain.rtc.RtcEngine
import com.zeewain.rtc.RtcEngineConfig
import com.zeewain.rtc.model.CameraConfig
import com.zeewain.utils.CommonUtils
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mConfig: RtcEngineConfig

    private lateinit var mRtcEngine: RtcEngine

    private lateinit var mHandler: Handler

    companion object {
        const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        mHandler = Handler(Looper.getMainLooper())

        checkPermission()

        mBinding.btnSend.setOnClickListener {
            if (mRtcEngine.sendChatMessage(mBinding.editChat.text.toString()) == 0) {
                mBinding.editChat.setText("")
            }
        }
    }

    private fun checkPermission() {
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.CAMERA
        )
        if (!EasyPermissions.hasPermissions(this, *permissions)) {
            EasyPermissions.requestPermissions(this, "Please provide permissions", 1, *permissions)
        } else joinChannel()
    }

    private fun joinChannel() {
        mConfig = RtcEngineConfig()
        mConfig.apply {
            context = this@MainActivity
            roomId = "888888888"
            channelProfile = 0
            appId = getString(R.string.app_id)
            token = getString(R.string.token)
            userId = CommonUtils.getRandomString(8)
            displayName = CommonUtils.getRandomString(8)
            eventHandler = iRtcEngineEventHandler
        }

        mRtcEngine = RtcEngine.create(mConfig)
        mRtcEngine.setupCameraConfig(CameraConfig(CameraConfig.CAMERA_DIRECTION.CAMERA_FRONT))
        if (mRtcEngine.joinChannel() == 0) {
            mRtcEngine.enableVideo()
            mRtcEngine.setupLocalVideo(mBinding.localPlay)
        } else Toast.makeText(this, "Failed to join channel", Toast.LENGTH_SHORT).show()
    }

    private val iRtcEngineEventHandler = object : IRtcEngineEventHandler {
        override fun onError(p0: Int) {

        }

        override fun onJoinChannelSuccess(p0: String) {

        }

        override fun onLeaveChannel() {

        }

        override fun onCloseChannel() {

        }

        override fun onUserJoined(p0: String) {

        }

        override fun onUserOffline(p0: String) {

        }

        override fun onUserOnline(p0: JSONArray) {

        }

        override fun onRemoteVideoStateChanged(uid: String, trackId: String, state: Boolean) {
            mHandler.post {
                if (state) {
                    mRtcEngine.setupRemoteVideo(mBinding.remotePlay, uid)
                }
            }
        }

        override fun onUserMessage(p0: String, p1: String) {
            mHandler.post {
                Toast.makeText(this@MainActivity, p1, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>) {
        joinChannel()
    }

    override fun onPermissionsDenied(p0: Int, p1: MutableList<String>) {

    }
}