package com.zeewain.rtc_example

import android.Manifest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.alibaba.fastjson.JSONArray
import com.zeewain.common.utils.CommonUtils
import com.zeewain.rtc.IRtcEngineEventHandler
import com.zeewain.rtc.RtcEngine
import com.zeewain.rtc.RtcEngineConfig
import com.zeewain.rtc.model.CameraConfig
import com.zeewain.rtc_example.databinding.ActivityFusionVideoBinding
import pub.devrel.easypermissions.EasyPermissions
import java.util.Objects

class FusionVideoActivity : AppCompatActivity(), EasyPermissions.PermissionCallbacks {

    private lateinit var mBinding: ActivityFusionVideoBinding

    private lateinit var mConfig: RtcEngineConfig

    private var mRtcEngine: RtcEngine? = null

    private lateinit var mHandler: Handler

    companion object {
        const val TAG = "FusionActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_fusion_video)
        mHandler = Handler(Looper.getMainLooper())

        checkPermission()
    }

    /**
     * Request related permission.
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

    /**
     *
     */
    private fun joinChannel() {
        // Create RtcEngineConfig object.
        mConfig = RtcEngineConfig()
        mConfig.apply {
            context = this@FusionVideoActivity
            // Channel Type, Normal: 0, Fusion: 1.
            channelProfile = RtcEngine.CHANNEL_TYPE_FUSION
            // Room ID, App ID, Token generated in the console.
            roomId = "999999999"
            appId = getString(R.string.app_id)
            token = getString(R.string.fusion_token)
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
                if (Objects.equals(uid, "_fusion_merge")) {
                    mRtcEngine?.setupRemoteVideo(mBinding.remotePlay, uid)
                }
            }
        }

        override fun onUserMessage(p0: String, p1: String) {
            mHandler.post {
                Toast.makeText(this@FusionVideoActivity, p1, Toast.LENGTH_SHORT).show()
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
        joinChannel()
    }

    override fun onPermissionsDenied(p0: Int, p1: MutableList<String>) {

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUI()
        }
    }

    private fun hideSystemUI() {
        val decorView = window.decorView
        decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION)
    }

    override fun onDestroy() {
        mRtcEngine?.leaveChannel()
        mHandler.post(RtcEngine::destroy)
        mRtcEngine = null
        super.onDestroy()
    }
}