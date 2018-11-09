package com.huaxi.hailuo.ui.fragment

import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseFragment
import com.huaxi.hailuo.presenter.contract.MineverifyContract
import com.huaxi.hailuo.presenter.impl.MineverifyPresenter
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.StringUtil
import com.huaxi.hailuo.util.UserUtil
import com.huaxi.hailuo.util.Utils
import kotlinx.android.synthetic.main.fragment_verify_me.*

class VerifyMeFragment : BaseFragment<MineverifyContract.View, MineverifyContract.Presenter>(), MineverifyContract.View {
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

    override fun processExitLoginResult() {

    }

    override var mPresenter: MineverifyContract.Presenter = MineverifyPresenter()

    override fun getLayout(): Int = R.layout.fragment_verify_me

    override fun initView() {
        StatusBarUtil.setPaddingSmart(activity, verify_tb_me)


    }

    override fun initData() {
        version.text = "当前版本：" + Utils.getVersion(App.instance)
    }


    //未登录UI
    private fun refreshUI() {
        if (UserUtil.isLogin(App.instance)) {

            //设置手机号
            val PhoneNum = UserUtil.getMobile(App.instance)
            val encryptPhoneNum = StringUtil.encryptPhoneNum(PhoneNum)
            tv_user_phone_number.text = encryptPhoneNum
        } else {
            //未登录
            tv_user_phone_number.text = "未登录"
            tv_user_member_date.text = ""
        }

    }



}