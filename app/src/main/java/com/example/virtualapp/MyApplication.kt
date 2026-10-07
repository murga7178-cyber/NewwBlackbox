package com.example.virtualapp

import android.app.Application
import android.content.Context
import android.util.Log
import top.niunaijun.blackbox.BlackBoxCore
import top.niunaijun.blackbox.app.configuration.ClientConfiguration
import java.io.File

class MyApplication : Application() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)
        try {
            BlackBoxCore.get().doAttachBaseContext(base, object : ClientConfiguration() {
                override fun getHostPackageName(): String = base.packageName
            })
        } catch (e: Throwable) {
            Log.e("VirtualApp", "BlackBox attachBaseContext error: ${e.message}")
        }
    }

    override fun onCreate() {
        super.onCreate()
        try {
            BlackBoxCore.get().doCreate()
        } catch (e: Throwable) {
            Log.e("VirtualApp", "BlackBox doCreate error: ${e.message}")
        }
        val virtualDir = File(filesDir, "virtual")
        if (!virtualDir.exists()) virtualDir.mkdirs()
        Log.d("VirtualApp", "Virtual dir ready: ${virtualDir.absolutePath}")
    }

    companion object {
        fun copyApkToVirtual(context: Context, packageName: String): String? {
            return try {
                val src = context.packageManager
                    .getApplicationInfo(packageName, 0).sourceDir
                val destDir = File(context.filesDir, "virtual/$packageName")
                if (!destDir.exists()) destDir.mkdirs()
                val dest = File(destDir, "base.apk")
                File(src).copyTo(dest, overwrite = true)
                dest.absolutePath
            } catch (e: Exception) {
                Log.e("VirtualApp", "APK copy fail: ${e.message}")
                null
            }
        }
    }
}
