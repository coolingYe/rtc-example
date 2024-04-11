package com.example.rtc_example

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSONArray
import com.example.rtc_example.databinding.ActivityVideoBinding
import com.zeewain.common.utils.CommonUtils
import com.zeewain.rtc.IRtcEngineEventHandler
import com.zeewain.rtc.RtcEngine
import com.zeewain.rtc.RtcEngineConfig
import com.zeewain.rtc.model.CameraConfig
import pub.devrel.easypermissions.EasyPermissions

class VideoActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBinding: ActivityVideoBinding

    private lateinit var mConfig: RtcEngineConfig

    private var mRtcEngine: RtcEngine? = null

    private lateinit var mHandler: Handler

    companion object {
        const val TAG = "VideoActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_video)
        mHandler = Handler(Looper.getMainLooper())

        val windowInsetsController =
            WindowCompat.getInsetsController(window, window.decorView)
        // Configure the behavior of the hidden system bars.
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())

        checkPermission()

        mBinding.btnSend.setOnClickListener {
            if (mRtcEngine?.sendChatMessage(mBinding.editChat.text.toString()) == 0) {
                mBinding.editChat.setText("")
            }
        }
    }

    /**
     * Request related permissions
     */
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
        // Create RtcEngineConfig object.
        mConfig = RtcEngineConfig()
        mConfig.apply {
            context = this@VideoActivity
            // Channel Type, Normal: 0, Fusion: 1.
            channelProfile = RtcEngine.CHANNEL_TYPE_NORMAL
            // Room ID, App ID, Token generated in the console.
            roomId = "888888888"
            appId = getString(R.string.app_id)
            token = getString(R.string.token)
            userId = CommonUtils.getRandomString(8)
            displayName = CommonUtils.getRandomString(8)
            eventHandler = iRtcEngineEventHandler
        }

        // Create and initialize RtcEngine.
        mRtcEngine = RtcEngine.create(mConfig)
        // Set camera parameters.
        mRtcEngine?.setupCameraConfig(CameraConfig(CameraConfig.CAMERA_DIRECTION.CAMERA_FRONT))

        //Join Room.
        if (mRtcEngine?.joinChannel() == 0) {
            // Enable video streaming.
            mRtcEngine?.enableVideo()
            // Enable local video preview.
            mRtcEngine?.setupLocalVideo(mBinding.localPlay)
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
                    mRtcEngine?.setupRemoteVideo(mBinding.remotePlay, uid)
                }
            }
        }

        override fun onUserMessage(p0: String, p1: String) {
            mHandler.post {
                Toast.makeText(this@VideoActivity, p1, Toast.LENGTH_SHORT).show()
            }
        }

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsGranted(p0: Int, p1: MutableList<String>) {
        Log.d(TAG, "Permission success")
        joinChannel()
    }

    override fun onPermissionsDenied(p0: Int, p1: MutableList<String>) {
        Log.d(TAG, "Permission failed")
    }

    override fun onDestroy() {
        mRtcEngine?.leaveChannel()
        mHandler.post(RtcEngine::destroy)
        mRtcEngine = null
        super.onDestroy()
    }
}