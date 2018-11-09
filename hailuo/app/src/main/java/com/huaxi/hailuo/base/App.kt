package com.huaxi.hailuo.base

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import cn.jpush.android.api.JPushInterface
import com.balsikandar.crashreporter.CrashReporter
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.util.Utils
import com.liulishuo.filedownloader.FileDownloader
import com.moxie.client.manager.MoxieSDK
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.tencent.bugly.crashreport.CrashReport
import com.umeng.analytics.MobclickAgent
import com.umeng.commonsdk.UMConfigure
import kotlin.properties.Delegates


class App : MultiDexApplication(), Application.ActivityLifecycleCallbacks {

    //当前APP是否是审核视图
    var mIsVerify: Boolean = false

    //当前环境
    var mCurrentHost = HOST.DEV

    // DEV开发,PRE预发布,PRO上线
    enum class HOST {
        DEV, PRE, PRO
    }


    private var resumeCount: Int = 0
    private val mAllActivities: LinkedHashSet<Activity> = LinkedHashSet()

    companion object {
        var instance: App by Delegates.notNull()
        var isOnResume: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

        //判断程序是否重复启动
        var isApplicationRepeat = Utils.isApplicationRepeat(this)
        if (isApplicationRepeat) {
            return
        }

        MoxieSDK.init(this)
        instance = this
        JPushInterface.setDebugMode(false)// 设置开启日志,发布时请关闭日志
        JPushInterface.init(instance) // 初始化 JPush
        //初始化Logger的TAG
        LogUtil.init(true)
        // dex突破65535的限制
        MultiDex.install(this)
        registerActivityLifecycleCallbacks(this)
        //魔蝎淘宝认证
//        LeakCanary.install(this)
        initSmartRefreshLayout()
        FileDownloader.setupOnApplicationOnCreate(instance)
        //APP Crash后保存log日志 log日志在/Android/data/package-name/files/crashLog
        CrashReporter.initialize(this, Utils.getCrashLogPath())

        /*注意: 即使您已经在AndroidManifest.xml中配置过appkey和channel值，
        也需要在App代码中调用初始化接口（如需要使用AndroidManifest.xml中配置好的appkey和channel值，
        UMConfigure.init调用中appkey和channel参数请置为null）。*/
        UMConfigure.init(applicationContext, GlobalParams.YOUMENG_APP_KEY, null, UMConfigure.DEVICE_TYPE_PHONE, null)
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL)

        //腾讯bugly
        CrashReport.initCrashReport(applicationContext, GlobalParams.BUGLY_APP_ID, true)

    }

    /**
     * 保存Activity的引用
     */
    fun addActivity(act: Activity) {
        mAllActivities.add(act)
    }

    /**
     * 清除Activity的引用
     */
    fun removeActivity(act: Activity) {
        mAllActivities.remove(act)
    }

    /**
     * 得到当前栈顶的Activity
     */
    fun getCurrentActivity(): Activity {
        return mAllActivities.last()
    }

    fun getActivityList(): MutableSet<Activity> {
        return mAllActivities
    }

    /**
     * 消息中心推送时，应用关闭，只打开当前Activity，size = 1
     * 此时重新打开应用
     */
    fun isBackground(): Boolean {
        val list = getActivityList()
        return list.size == 1
    }

    /**
     * 退出App
     */
    fun exitApp() {
        synchronized(mAllActivities) {
            for (act in mAllActivities) {
                act.finish()
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid())
        System.exit(0)
    }

    /**
     * 初始化下拉刷新控件
     */
    private fun initSmartRefreshLayout() {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreater { context, _ ->
            ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate)//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreater { context, _ ->
            //指定为经典Footer，默认是 BallPulseFooter
            ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate)
        }
    }


    override fun onActivityResumed(activity: Activity) {
        isOnResume = true
        resumeCount++
    }

    override fun onActivityPaused(activity: Activity) {
        resumeCount--
        if (resumeCount == 0) {
            isOnResume = false
        }
    }

    override fun onActivityStarted(activity: Activity?) {
    }

    override fun onActivityDestroyed(activity: Activity?) {
    }

    override fun onActivitySaveInstanceState(activity: Activity?, outState: Bundle?) {
    }

    override fun onActivityStopped(activity: Activity?) {
    }

    override fun onActivityCreated(activity: Activity?, savedInstanceState: Bundle?) {
    }
}