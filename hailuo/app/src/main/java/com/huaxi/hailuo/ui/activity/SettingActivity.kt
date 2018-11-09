package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.LogOutEvent
import com.huaxi.hailuo.presenter.contract.LoginOutContract
import com.huaxi.hailuo.presenter.impl.LoginOutPresenter
import com.huaxi.hailuo.util.SharedPreferencesUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.UserUtil
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_setting.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

/**
 * 设置界面
 */
class SettingActivity : BaseActivity<LoginOutContract.View, LoginOutContract.Presenter>(), LoginOutContract.View {


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

    override var mPresenter: LoginOutContract.Presenter = LoginOutPresenter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayout(): Int = R.layout.activity_setting

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_setting)
        setting.setOnClickListener({ backActivity() })
        about_we.setOnClickListener({
            startActivity<AboutWeActivity>()
        })
        rl_change_pwd.setOnClickListener({
            startActivity<ChangePwdActivity>()
        })
        rl_login_out.setOnClickListener({
            mPresenter.loginOut()

        })

    }

    override fun initData() {
        CrashReport.setUserId("")
    }


    override fun loginOutComplete() {
        val serviceOnlinePath = SharedPreferencesUtil.getInstance(mActivity).getString(GlobalParams.SERVICE_URL)
        SharedPreferencesUtil.getInstance(mActivity).clearSp()
        UserUtil.setIsFirst(mActivity, true)
        SharedPreferencesUtil.getInstance(mActivity).putString(GlobalParams.SERVICE_URL, serviceOnlinePath)
        EventBus.getDefault().post(LogOutEvent())
        startActivity<LoginActivity>()
    }

    override fun finishActivity() {
        finish()
    }

}
