package com.huaxi.hailuo.ui.activity

import android.Manifest
import android.app.Activity
import com.creditx.xbehavior.sdk.CreditXAgent
import com.creditx.xbehavior.sdk.LoginMethod
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.UpgradeBean
import com.huaxi.hailuo.presenter.contract.LauncherContract
import com.huaxi.hailuo.presenter.impl.LauncherPresenter
import com.huaxi.hailuo.util.DialogUtil
import com.huaxi.hailuo.util.SharedPreferencesUtil
import com.huaxi.hailuo.util.ToastUtil
import com.huaxi.hailuo.util.UserUtil
import com.tbruyelle.rxpermissions2.RxPermissions
import com.tencent.bugly.crashreport.CrashReport
import me.drakeet.materialdialog.MaterialDialog
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class LauncherActivity : BaseActivity<LauncherContract.View, LauncherContract.Presenter>(), LauncherContract.View {

    override var mPresenter: LauncherContract.Presenter = LauncherPresenter()

    override fun getLayout(): Int = R.layout.activity_launcher

    override fun initView() {
    }

    override fun initData() {
        CrashReport.setUserId(UserUtil.getUserId(App.instance))
//        EventBus.getDefault().post(ServiceUpdateEvent("http://cdn.tianshenjr.com/app-debug.apk"))
        CreditXAgent.init(application, GlobalParams.KEXIN_KEY, "server")
        if (UserUtil.isLogin(mActivity)) {
            CreditXAgent.onUserLoginSuccess(LoginMethod.AUTO_LOGIN, UserUtil.getUserId(mActivity))
        }
        mPresenter.checkUpdate()
    }

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

    override fun checkUpdateResult(upgradeBean: UpgradeBean) {
        doAsync {
            Thread.sleep(1000)
            uiThread {
                checkResult(upgradeBean)
            }
        }
    }

    private fun checkResult(upgradeBean: UpgradeBean) {

        if ("2" == upgradeBean.is_ignore) {//当前是最新版本
//            startActivity<MainActivity>()
            //判断是否是第一次打开app
            if (!UserUtil.isFirst(mActivity)) {
                mActivity.startActivity<GuideActivity>(GuideActivity.ISGOTOMAIN to true)
                finish()
            } else {
                //判断用户是否登录
                if (UserUtil.isLogin(App.instance)) {
                    //已经登录且要去主页面
                    CrashReport.setUserId(UserUtil.getUserId(App.instance))
                    mActivity.startActivity<MainActivity>()
                    finish()
                } else {
                    //未登录 去登录
                    mActivity.startActivity<LoginActivity>()
                    finish()
                }
            }
        } else if ("1" == upgradeBean.is_ignore) {
            val alert = MaterialDialog(mActivity)
            alert.setTitle("版本更新")
                    .setMessage(upgradeBean.version_info)
                    .setPositiveButton("下载", {
                        val rxPermissions = RxPermissions(mActivity)
                        rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe { aBoolean ->
                            if (aBoolean) {
                                DialogUtil.showUpdateDialog(upgradeBean.download_url)
                            } else {
                                ToastUtil.showToast(mActivity, "请打开存储权限")
                            }
                        }
                    })
                    .setNegativeButton("忽略", {
                        if ("1" == upgradeBean.force_upgrade) { //强制升级
                            mActivity.finish()
                        } else {
                            startActivity<MainActivity>()
                            mActivity.finish()
                        }
                        alert.dismiss()
                    })
                    .setCanceledOnTouchOutside(false)
                    .show()
        }
        //1:正常视图  2.审核视图
        if (upgradeBean.is_review == "1") {
            App.instance.mIsVerify = false
        } else if (upgradeBean.is_review == "2") {
            App.instance.mIsVerify = true
        }

        //将客服的URL保存到本地
        SharedPreferencesUtil.getInstance(mActivity).putString(GlobalParams.SERVICE_URL, upgradeBean.service_url)
        //将身份认证页面是否可以修改名字和身份证号的状态保存在本地
        UserUtil.saveIsEditIdentity(mActivity, upgradeBean.is_edit_identity)
    }
}