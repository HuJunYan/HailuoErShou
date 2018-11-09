package com.huaxi.hailuo.ui.activity

import android.text.TextUtils
import android.view.KeyEvent
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.event.SetPwdEvent
import com.huaxi.hailuo.model.bean.SetPasswordBean
import com.huaxi.hailuo.presenter.contract.SetPwdContract
import com.huaxi.hailuo.presenter.impl.SetPwdPresenter
import com.huaxi.hailuo.util.RegexUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.ToastUtil
import com.huaxi.hailuo.util.UserUtil
import kotlinx.android.synthetic.main.activity_set_pwd.*
import org.greenrobot.eventbus.EventBus


/**
 * 设置密码页面
 */
class SetPwdActivity : BaseActivity<SetPwdContract.View, SetPwdContract.Presenter>(), SetPwdContract.View {

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

    override var mPresenter: SetPwdContract.Presenter = SetPwdPresenter()

    override fun getLayout(): Int = R.layout.activity_set_pwd

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, setpwd_tb)
        iv_regist_return.setOnClickListener({
            EventBus.getDefault().post(SetPwdEvent())
            finish()
        })
        confirm_reg.setOnClickListener({
            if (TextUtils.isEmpty(pwd.text.toString().trim())) {
                ToastUtil.showToast(mActivity, "密码不能为空")
                return@setOnClickListener
            }

            if (TextUtils.isEmpty(tv_pwd.text.toString().trim())) {
                ToastUtil.showToast(mActivity, "确认密码不能为空")
                return@setOnClickListener
            }

            if (pwd.text.toString().trim().equals(tv_pwd.text.toString().trim())) {
                ToastUtil.showToast(mActivity, "两次密码不一致")
                return@setOnClickListener
            }

            if (!RegexUtil.IsPassword(pwd.text.toString().trim())) {
                ToastUtil.showToast(mActivity, "请输入数字或字母、长度6到18位的密码")
                return@setOnClickListener
            }

            mPresenter.setPwd(pwd.text.toString().trim(), confim_pwd.text.toString().trim())
        })

    }

    override fun initData() {

    }

    override fun setPwdComplete(data: SetPasswordBean?) {
        if (data == null) {
            ToastUtil.showToast(mActivity, "数据错误")
            return
        }
        UserUtil.saveUserId(mActivity, data!!.user_id)
        EventBus.getDefault().post(SetPwdEvent())

    }

    override fun finishActivity() {
        finish()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        return if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() === 0) {
            EventBus.getDefault().post(SetPwdEvent())
            finish()
            true
        } else super.onKeyDown(keyCode, event)
    }

}
