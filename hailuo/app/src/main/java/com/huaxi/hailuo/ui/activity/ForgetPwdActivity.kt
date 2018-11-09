package com.huaxi.hailuo.ui.activity

import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.widget.CompoundButton
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.presenter.contract.ForgetPwdContract
import com.huaxi.hailuo.presenter.impl.ForgetPwdPresenter
import com.huaxi.hailuo.util.RegexUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.TimeCount
import com.huaxi.hailuo.util.ToastUtil
import kotlinx.android.synthetic.main.activity_forget_pwd.*

/**
 * 密码修改页面
 */

class ForgetPwdActivity : BaseActivity<ForgetPwdContract.View, ForgetPwdContract.Presenter>(), ForgetPwdContract.View {
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

    override var mPresenter: ForgetPwdContract.Presenter = ForgetPwdPresenter()

    override fun getLayout(): Int = R.layout.activity_forget_pwd

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_forget_pwd)
        iv_forget_pwd.setOnClickListener({ backActivity() })

        tv_get_code_forget.setOnClickListener({
            getVeryCode()
        })
        confim.setOnClickListener({
            changePwd()
        })
        cb_forget_eye.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                //设置为明文显示
                et_pwd_forget.transformationMethod = HideReturnsTransformationMethod.getInstance()
            } else {
                //设置为密文显示
                et_pwd_forget.transformationMethod = PasswordTransformationMethod.getInstance()
            }
            //光标在最后显示
            et_pwd_forget.setSelection(et_pwd_forget.length())
        })

    }

    private fun changePwd() {
        if (TextUtils.isEmpty(et_forget_phone_num.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "手机号不能为空")
            return
        }
        if (!RegexUtil.IsTelephone(et_forget_phone_num.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "手机号格式不正确")
            return
        }
        if (TextUtils.isEmpty(phone_code.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "验证码不能为空")
            return
        }
        if (TextUtils.isEmpty(et_pwd_forget.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "密码不能为空")
            return
        }
        if (!RegexUtil.IsPassword(et_pwd_forget.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "请输入数字或字母、长度6到18位的密码")
            return
        }
        mPresenter.changePwd(et_forget_phone_num.text.toString().trim(), phone_code.text.toString().trim(), et_pwd_forget.text.toString().trim())

    }

    override fun initData() {
    }


    override fun getVeryCodeResult(data: Any) {
        ToastUtil.showToast(mActivity, "验证码发送成功")
        mTimeCount = TimeCount(tv_get_code_forget, 60000, 1000, "重新获取", true, R.drawable.shape_circle)
        mTimeCount!!.start()

    }


    override fun changeComplete() {
        ToastUtil.showToast(mActivity, "密码修改成功")
        finish()

    }

    private fun getVeryCode(): Boolean {
        val userName = et_forget_phone_num.text.toString().trim()


        if (TextUtils.isEmpty(userName) || !RegexUtil.IsTelephone(userName)) {
            ToastUtil.showToast(mActivity, "手机号格式不正确")
            return false
        }
        //获取验证码
        mPresenter.getVeryCode(userName, "5")

        return true
    }
}
