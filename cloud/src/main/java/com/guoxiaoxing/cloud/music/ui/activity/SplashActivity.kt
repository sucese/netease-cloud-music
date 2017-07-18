package com.guoxiaoxing.cloud.music.ui.activity

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager

import com.guoxiaoxing.cloud.music.R
import com.guoxiaoxing.cloud.music.uitl.PermissionHelper
import com.orhanobut.logger.Logger

class SplashActivity : Activity() {

    private var mPermissionHelper: PermissionHelper? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_splash)

        // 当系统为6.0以上时，需要申请权限
        mPermissionHelper = PermissionHelper(this)
        mPermissionHelper?.setOnApplyPermissionListener {
            Logger.d("All of requested permissions has been granted, so run app logic.")
            runApp()
        }
        if (Build.VERSION.SDK_INT < 23) {
            // 如果系统版本低于23，直接跑应用的逻辑
            Logger.d("The api level of system is lower than 23, so run app logic directly.")
            runApp()
        } else {
            // 如果权限全部申请了，那就直接跑应用逻辑
            if (mPermissionHelper!!.isAllRequestedPermissionGranted) {
                Logger.d("All of requested permissions has been granted, so run app logic directly.")
                runApp()
            } else {
                // 如果还有权限为申请，而且系统版本大于23，执行申请权限逻辑
                Logger.i("Some of requested permissions hasn't been granted, so apply permissions first.")
                mPermissionHelper?.applyPermissions()
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        mPermissionHelper?.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        mPermissionHelper?.onActivityResult(requestCode, resultCode, data)
    }

    private fun runApp() {
        val intent = Intent(this@SplashActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
