package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.text.TextPaint
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.text.style.CharacterStyle
import android.text.style.ClickableSpan
import android.view.View
import android.widget.CompoundButton
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.event.LoginEvent
import com.huaxi.hailuo.model.bean.LoginBean
import com.huaxi.hailuo.model.bean.RegistLoginBean
import com.huaxi.hailuo.model.bean.SignUpAgreementBean
import com.huaxi.hailuo.presenter.contract.LoginConract
import com.huaxi.hailuo.presenter.impl.LoginPresenter
import com.huaxi.hailuo.util.*
import kotlinx.android.synthetic.main.activity_regest.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity
import java.util.*

/**
 * 注册界面(节省时间，使用登录的presenter)
 */
class RegestActivity : BaseActivity<LoginConract.View, LoginConract.Presenter>(), LoginConract.View {
    private var mUrl: String? = null
    override fun signUpAgreementResult(data: SignUpAgreementBean?) {
        if (data == null) {
            return
        }
        mUrl = data.agreement_url

    }


    var webTitle: String? = null
    var mTimeCount: TimeCount? = null
    var ssList: MutableList<CharacterStyle>? = null

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
//        if (mTimeCount !=null) {
//            mTimeCount!!.finish()
//        }
    }

    override fun showErrorMsg(msg: String) {
//        if (mTimeCount !=null) {
//            mTimeCount!!.finish()
//        }
    }

    override fun showEmpty() {
    }

    override var mPresenter: LoginConract.Presenter = LoginPresenter()

    override fun getLayout(): Int = R.layout.activity_regest

    override fun initView() {
        StatusBarUtil.setPaddingSmart(this, tb_regist)
        initGotoWebData()
        iv_regist_return.setOnClickListener({
            backActivity()
        })
        tv_forget_get_code.setOnClickListener { getVeryCode() }
        regist.setOnClickListener({ regist() })

        cb_eye.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
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

    override fun initData() {
        mPresenter.signUpAgreement()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun regist() {

        if (TextUtils.isEmpty(phone_num.text.toString().trim())) {
            ToastUtil.showToast(mActivity, "手机号不能为空")
            return
        }
        if (TextUtils.isEmpty(very_code.text.toString().trim())) {
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
        mPresenter.regist(phone_num.text.toString().trim(), very_code.text.toString().trim(), pwd.text.toString().trim())
        UserUtil.saveMobile(mActivity, phone_num.text.toString().trim())

    }

    private fun getVeryCode(): Boolean {
        val userName = phone_num.text.toString().trim()


        if (TextUtils.isEmpty(userName) || !RegexUtil.IsTelephone(userName)) {
            ToastUtil.showToast(mActivity, "手机号格式不正确")
            return false
        }
        //获取验证码
        mPresenter.getVeryCode(userName, "1")

        return true
    }

    /**
     * 设置spannable点击规则
     */
    private fun initGotoWebData() {

        if (ssList == null) {
            ssList = ArrayList<CharacterStyle>()
        }
        ssList!!.clear()
        ssList!!.add(webSpan)
        val text = "确认表示您同意《用户服务协议》"
        SpannableUtils.setWebSpannableString(info, text, "《", "》", ssList, resources.getColor(R.color.global_blue_end))
    }


    private val webSpan = object : ClickableSpan() {
        override fun onClick(widget: View) {
            webTitle = "用户服务协议"
            if (TextUtils.isEmpty(mUrl)) {
                ToastUtil.showToast(mActivity, "协议错误")
                return
            }
            startActivity<WebActivity>(
                    WebActivity.WEB_URL_KEY to mUrl!!,
                    WebActivity.WEB_URL_TITLE to "用户服务协议")
        }

        override fun updateDrawState(ds: TextPaint) {
            ds.isUnderlineText = false
        }
    }

    override fun loginCompelete(data: LoginBean) {
    }

    override fun getVeryCodeResult(data: Any?) {
        ToastUtil.showToast(mActivity, "验证码发送成功")
        mTimeCount = TimeCount(tv_forget_get_code, 60000, 1000, "重新获取", true, R.drawable.shape_circle)
        mTimeCount!!.start()
    }

    override fun finishActivity() {
        finish()
    }

    override fun registComplete(data: RegistLoginBean?) {
        if (data != null) {
            UserUtil.saveToken(mActivity, data.token)
            UserUtil.saveUserId(mActivity, data.user_id)
            UserUtil.saveMobile(mActivity, phone_num.text.trim().toString())
            ToastUtil.showToast(mActivity, "注册成功")
            EventBus.getDefault().post(LoginEvent())
            startActivity<MainActivity>()
        }

    }

    override fun changeComplete() {

    }


}
