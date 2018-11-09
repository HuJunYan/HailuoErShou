package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CompoundButton
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.LoginBean
import com.huaxi.hailuo.model.bean.RegistLoginBean
import com.huaxi.hailuo.model.bean.SignUpAgreementBean
import com.huaxi.hailuo.presenter.contract.LoginConract
import com.huaxi.hailuo.presenter.impl.LoginPresenter
import com.huaxi.hailuo.util.RegexUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.TimeCount
import com.huaxi.hailuo.util.ToastUtil
import kotlinx.android.synthetic.main.activity_change_pwd.*

/**
 * 密码修改页面
 */

class ChangePwdActivity : BaseActivity<LoginConract.View, LoginConract.Presenter>(), LoginConract.View {


    override fun signUpAgreementResult(data: SignUpAgreementBean?) {
        //注册协议
    }

    private var mTimeCount: TimeCount? = null

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

    override var mPresenter: LoginConract.Presenter = LoginPresenter()

    override fun getLayout(): Int = R.layout.activity_change_pwd

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, change_pwd_tb)
        change_pwd_return.setOnClickListener({ backActivity() })

        get_code.setOnClickListener({
            getVeryCode()
        })
        tv_change_confim.setOnClickListener({
            changePwd()
        })
        cb_change_eye.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //设置为明文显示
                pwd.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //设置为密文显示
                pwd.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            //光标在最后显示
            pwd.setSelection(pwd.length())
        })

    }

    private fun changePwd() {
        if (TextUtils.isEmpty(phone_num.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "手机号不能为空")
            return
        }
        if (TextUtils.isEmpty(et_change_phone_code.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "验证码不能为空")
            return
        }
        if (TextUtils.isEmpty(pwd.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "密码不能为空")
            return
        }
        if (!RegexUtil.IsPassword(pwd.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "请输入数字或字母、长度6到18位的密码")
            return
        }
        mPresenter.changePwd(phone_num.text.toString().trim(), et_change_phone_code.text.toString().trim(), pwd.text.toString().trim())

    }

    override fun initData() {
    }

    override fun loginCompelete(data: LoginBean) {
    }

    override fun getVeryCodeResult(data: Any?) {
        mTimeCount = TimeCount(get_code, 60000, 1000, "重新获取", true, R.drawable.shape_circle)
        mTimeCount!!.start()

    }

    override fun finishActivity() {
        finish()
    }

    override fun registComplete(data: RegistLoginBean?) {
    }

    override fun changeComplete() {
        ToastUtil.showToast(mActivity, "密码修改成功")

    }

    private fun getVeryCode(): Boolean {
        val userName = phone_num.text.toString().trim()


        if (TextUtils.isEmpty(userName) || !RegexUtil.IsTelephone(userName)) {
            ToastUtil.showToast(mActivity, "手机号格式不正确")
            return false
        }
        //获取验证码
        mPresenter.getVeryCode(userName, "4")

        return true
    }
}
