package com.huaxi.hailuo.util

import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Process
import android.support.v4.content.FileProvider
import android.text.TextUtils
import com.huaxi.hailuo.base.App
import com.meituan.android.walle.WalleChannelReader
import org.jetbrains.anko.doAsync
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
object Utils {

    /**
     * 判断程序是否重复启动
     */
    fun isApplicationRepeat(applicationContext: Context): Boolean {

        val pid = android.os.Process.myPid()
        var processName: String? = null
        val am = applicationContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val l = am.runningAppProcesses
        val i = l.iterator()
        while (i.hasNext()) {
            val info = i.next()
            try {
                if (info.pid == pid) {
                    processName = info.processName
                }
            } catch (e: Exception) {
            }

        }
        return processName == null || !processName.equals(applicationContext.packageName, ignoreCase = true)
    }

    /**
     * 判断网络是否好用
     */
    fun isNetworkConnected(): Boolean {
        val connectivityManager = App.instance.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo != null
    }


    /**
     * 得到okhttp 缓存目录
     * /Android/data/package-name/cache/okhttp
     */
    fun getOKHttpCachePath(): String {
        val path = App.instance.externalCacheDir.canonicalPath + File.separator + "okhttp"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }

    /**
     * 得到crash log目录
     * /Android/data/package-name/files/log/crashLog
     */
    fun getCrashLogPath(): String {
        val path = App.instance.getExternalFilesDir("log").canonicalPath + File.separator + "crashLog"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }

    /**
     * 得到http log目录
     * /Android/data/package-name/files/log/httpLog
     */
    fun getHttpLogPath(): String {
        val path = App.instance.getExternalFilesDir("log").canonicalPath + File.separator + "httpLog"
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }

    /**
     * 下载更新apk包地址
     * /Android/data/package-name/files/apk
     */
    fun getDownLoadAppPath(): String {
        val path = App.instance.getExternalFilesDir("apk").canonicalPath
        val file = File(path)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file.canonicalPath
    }



    /**
     * 写入log到文本文件中
     */
    fun writerLog(log: String) {

        doAsync {
            try {
                val path = getHttpLogPath()
                val file = File(path, "http.txt")
                if (!file.exists()) {
                    file.createNewFile()
                } else {
                    //文件超过100k就清空重新写
                    val length = file.length()
                    val mb = length / 1024
                    if (mb > 100) {
                        val fileWriter = FileWriter(file)
                        fileWriter.write("")
                        fileWriter.flush()
                        fileWriter.close()
                    }
                }
                val out = FileWriter(file, true)
                val bw = BufferedWriter(out)
                bw.newLine()
                bw.write(log)
                bw.flush()
                bw.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    }

    /**
     * 获取当前应用的版本号
     */
    fun getVersion(context: Context): String {
        try {
            val manager = context.packageManager
            val info = manager.getPackageInfo(context.packageName, 0)
            return info.versionName
        } catch (e: Exception) {
            e.printStackTrace()
            return "1.0.0"
        }
    }

    fun downloadAPP(context: Context, url: String) {
        val intent = Intent()
        intent.action = "android.intent.action.VIEW"
        val content_url = Uri.parse(url)
        intent.data = content_url
        intent.setClassName("com.android.browser", "com.android.browser.BrowserActivity")
        context.startActivity(intent)
    }


    /**
     * 获取apk是否是Debug包
     */
    fun isApkDebugable(context: Context): Boolean {
        try {
            val info = context.applicationInfo
            return info.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0
        } catch (e: Exception) {
        }
        return false
    }

    /**
     * 获取apk签名md5值
     */
    fun getSignMd5Str(context: Context): String {
        try {
            val packageInfo = context.getPackageManager().getPackageInfo(context.packageName, PackageManager.GET_SIGNATURES)
            val signs = packageInfo.signatures
            val sign = signs[0]
            return EncryptUtil.md5(sign.toByteArray())
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return ""
    }

    /**
     * 获取渠道号  默认2000 -> server
     * @return
     */
    fun getChannelId(): String {
        var channel_id = WalleChannelReader.getChannel(App.instance)
        if (TextUtils.isEmpty(channel_id)) {
            channel_id = "2000"
        }
        return channel_id!!
    }

    /**
     * 安装apk
     */
    fun installApk(activity: Activity, targetFilePath: String) {
        val apkFile = File(targetFilePath)
        if (!apkFile.exists()) {
            return
        }
        val intent = Intent(Intent.ACTION_VIEW)
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
            val apkUri = FileProvider.getUriForFile(activity, "com.huaxi.hailuo.fileprovider", apkFile)
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive")
        }
        activity.startActivity(intent)
        Process.killProcess(Process.myPid())
    }

}


