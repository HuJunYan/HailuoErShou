package com.huaxi.hailuo.ui.activity

import android.app.Activity
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.text.TextUtils
import android.view.View
import cn.jpush.android.api.JPushInterface
import com.creditx.xbehavior.sdk.CreditXAgent
import com.creditx.xbehavior.sdk.LoginMethod
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.LoginEvent
import com.huaxi.hailuo.event.SetPwdEvent
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.LoginBean
import com.huaxi.hailuo.model.bean.RegistLoginBean
import com.huaxi.hailuo.model.bean.SignUpAgreementBean
import com.huaxi.hailuo.presenter.contract.LoginConract
import com.huaxi.hailuo.presenter.impl.LoginPresenter
import com.huaxi.hailuo.ui.fragment.HomeFragment
import com.huaxi.hailuo.util.*
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity


/**
 * 登录界面
 */

class LoginActivity : BaseActivity<LoginConract.View, LoginConract.Presenter>(), LoginConract.View {

    var mRegIdQueryTimes: Int = 0
    var mLastHeightDifferece: Int = 0
    var loginType: Int = 2
    var isPwdLogin = false
    var mLoginBean: LoginBean? = null

    var agreementBean: SignUpAgreementBean? = null

    var isPushIdOK: Boolean = false
    var mHandler = object : Handler() {
        override fun handleMessage(msg: Message?) {
            super.handleMessage(msg)
            val push_id = JPushInterface.getRegistrationID(App.instance)
            LogUtil.d("pushid", "pushid=====$push_id")
            if (!TextUtils.isEmpty(push_id)) {
                isPushIdOK = true
            } else {
                if (mRegIdQueryTimes == 7) {
                    isPushIdOK = true
                    mRegIdQueryTimes = 0
                    return
                }
                sendEmptyMessageDelayed(10, 500)
                mRegIdQueryTimes++
            }
        }
    }

    companion object {
        var BUNDLE_KEY_FROM_TYPE = "bundle_key_from_type"
        var BUNDLE_KEY_PUSH_USER_ID = "bundle_key_push_user_id"
        var BUNDLE_KEY_RISK_RESULT = "bundle_key_risk_result"
        var FROM_PUSH = "from_push"
        var FROM_NORMAL = "from_normal"
    }

    var from_type = FROM_NORMAL
    var push_user_id = ""
    var mobile_model = ""
    var mobile_memory = ""
    var risk_result = ""

    override fun showProgress() {
    }

    override fun hideProgress() {
    }

    override fun showError() {
        et_phone_number2.finishTimer()
    }

    override fun showErrorMsg(msg: String) {
        et_phone_number2.finishTimer()
    }

    override fun showEmpty() {
    }

    override var mPresenter: LoginConract.Presenter = LoginPresenter()

    override fun getLayout(): Int = R.layout.activity_login

    override fun initData() {
//        throw  Exception("手动抛异常")
        val extras = intent.extras
        if (extras != null) {
            from_type = extras.getString(BUNDLE_KEY_FROM_TYPE, FROM_NORMAL)
            push_user_id = extras.getString(BUNDLE_KEY_PUSH_USER_ID, "")
            mobile_model = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, "")
            mobile_memory = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, "")
            risk_result = extras.getString(BUNDLE_KEY_RISK_RESULT, "")

        }
        mPresenter.signUpAgreement()
    }

    override fun initView() {
        et_mobile_num.setEditTextMaxLength(11)
        et_phone_number2.setEditTextMaxLength(18)
        et_password.setEditTextMaxLength(18)

        val window = window
        var systemUiVisibility = window.decorView.systemUiVisibility
        systemUiVisibility = systemUiVisibility xor View.SYSTEM_UI_FLAG_FULLSCREEN
        window.decorView.systemUiVisibility = systemUiVisibility
        StatusBarUtil.setPaddingSmart(this, tb_lgoin)

        mHandler.sendEmptyMessage(10)
        tv_home_title_login.paint.isFakeBoldText = true

        val text = "登录即同意《用户服务协议》"
        SpannableUtils.setSpannableStringColor(tv_agreement, text, "《", "》", resources.getColor(R.color.colorPrimary))

        tv_login_type.setOnClickListener {
            isPwdLogin = !isPwdLogin

            if (isPwdLogin) {
                // 密码登录
                loginType = 1
                tv_login_type.text = "验证码登录"
                tv_forget_pwd.visibility = View.VISIBLE
                tv_agreement.visibility = View.GONE

                et_password.visibility = View.VISIBLE
                et_phone_number2.visibility = View.GONE
                et_mobile_num.setHint("请输入您的注册手机号")

            } else {
                // 验证码登录
                loginType = 2
                tv_login_type.text = "密码登录"

                tv_forget_pwd.visibility = View.GONE
                tv_agreement.visibility = View.VISIBLE

                et_password.visibility = View.GONE
                et_phone_number2.visibility = View.VISIBLE
                et_mobile_num.setHint("请输入您的手机号")
            }
        }

        tv_agreement.setOnClickListener {
            if (agreementBean != null && agreementBean?.agreement_url != null) {
                startActivity<WebActivity>(
                        WebActivity.WEB_URL_KEY to agreementBean?.agreement_url!!,
                        WebActivity.WEB_URL_TITLE to "用户服务协议"
                )
            }
        }

        et_phone_number2.setListener { getVeryCode() }

        register.setOnClickListener({
            startActivity<RegestActivity>()
        })

        iv_login_return.setOnClickListener({
            backActivity()
        })
        et_password.setOnClickListener {
        }

        tv_login.setOnClickListener({
            val userName = et_mobile_num.text.toString().trim()
            val verryCode = et_phone_number2.text.toString().trim()
            val pwd = et_password.text.toString().trim()
            if (TextUtils.isEmpty(userName)) {
                ToastUtil.showToast(mActivity, "登录手机号码不能为空")
                return@setOnClickListener
            }


            if (loginType == 1) {
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.showToast(mActivity, "密码不能为空")
                    return@setOnClickListener
                }
                if (!isPushIdOK) {
                    ToastUtil.showToast(mActivity, resources.getString(R.string.initialization_please_wait))
                    return@setOnClickListener
                }
                //密码登录
                mPresenter.login(userName, et_password.text.toString().trim(), "", loginType)
            } else {
                if (TextUtils.isEmpty(verryCode)) {
                    ToastUtil.showToast(mActivity, "验证码不能为空")
                    return@setOnClickListener
                }
                if (!isPushIdOK) {
                    ToastUtil.showToast(mActivity, resources.getString(R.string.initialization_please_wait))
                    return@setOnClickListener
                }
                //验证码登录
                mPresenter.login(userName, "", et_phone_number2.text.toString().trim(), loginType)
            }
            UserUtil.saveMobile(mActivity, userName)
        })
        et_mobile_num.setListener { getVeryCode() }
        iv_login_return.setOnClickListener({
            backActivity()
        })
        tv_forget_pwd.setOnClickListener { gotoActivity(mActivity, ForgetPwdActivity::class.java, null) }

        /*---------------------start适配软键盘弹起 引发的布局变化  只是一个合适的适配方案 不是完美适配的方案------------------------------------*/
        /* lgogin.getViewTreeObserver().addOnGlobalLayoutListener(
                 {
                     val r = Rect()
                     lgogin.getWindowVisibleDisplayFrame(r)
                     val screenHeight = lgogin.getRootView().getHeight()
                     val statusBarHeight = StatusBarUtil.getStatusBarHeight()
                     val navigationBarHeight = StatusBarUtil.getNavigationBarHeight()
                     val heightDifference = screenHeight - (r.bottom - r.top) - statusBarHeight - navigationBarHeight
                     if (heightDifference > screenHeight / 4 && heightDifference != mLastHeightDifferece) {
                         mLastHeightDifferece = heightDifference
                         rl_activity.animate().translationY(-resources.displayMetrics.density * 90).setDuration(200).start()
                     } else if (heightDifference <= 0 && heightDifference != mLastHeightDifferece) {
                         mLastHeightDifferece = heightDifference
                         rl_activity.animate().translationY(1F).setDuration(200).start()
                     }
                 }
         )*/
        /*---------------------end适配软键盘弹起 引发的布局变化  只是一个合适的适配方案 不是完美适配的方案--------------------------------------*/
    }

    private fun getVeryCode(): Boolean {
        val userName = et_mobile_num.text.toString().trim()


        if (TextUtils.isEmpty(userName) || !RegexUtil.IsTelephone(userName)) {
            ToastUtil.showToast(mActivity, "手机号格式不正确")
            return false
        }
        //获取验证码
        et_phone_number2.startTimer()
        mPresenter.getVeryCode(userName, "2")

        return true
    }

    override fun loginCompelete(data: LoginBean) {
        CrashReport.setUserId(UserUtil.getUserId(App.instance))
        mLoginBean = data
        val instance = LocationUtil.getInstance()
        instance.setIsCallBack(true)
        instance.startLocation(this)
        //保存数据
        UserUtil.saveUserId(mActivity, data.user_id)
        UserUtil.saveToken(mActivity, data.token)
        UserUtil.saveMobile(mActivity, et_mobile_num.text.toString().trim())
        EventBus.getDefault().post(LoginEvent())
        if (loginType == 2) {
            CreditXAgent.onUserLoginSuccess(LoginMethod.VERIFICATION_CODE, UserUtil.getUserId(mActivity))
        } else {
            CreditXAgent.onUserLoginSuccess(LoginMethod.PASSWORD, UserUtil.getUserId(mActivity))
        }
        if ("1".equals(mLoginBean?.is_new)) {
            if (from_type == FROM_PUSH && TextUtils.equals(UserUtil.getUserId(App.instance), push_user_id)) {
                //是正确的推送 则 跳转到指定页面
                var bundle = Bundle()
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, mobile_model)
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, mobile_memory)
                if (risk_result == "1") {
                    gotoActivity(mActivity, AssessSuccessActivity::class.java, bundle)
                } else if (risk_result == "2") {
                    gotoActivity(mActivity, AssessFailureActivity::class.java, bundle)
                }
            } else {
                // 如果是首页自动跳转进来的，就不需要走下面的流程了
                val isAutoJump = intent.getBooleanExtra(HomeFragment.HOME_AUTO_JUMP, false)
                if (isAutoJump) {
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    gotoActivity(mActivity, MainActivity::class.java, null)
                }
            }

        } else if ("2".equals(mLoginBean?.is_new)) {
            //跳转到主页面
            gotoActivity(mActivity, MainActivity::class.java, null)

        } else {
            ToastUtil.showToast(mActivity, "服务器数据异常")
            return
        }
    }

    @Subscribe
    fun onSetPwdEvent(event: SetPwdEvent) {
        if (from_type == FROM_PUSH && TextUtils.equals(UserUtil.getUserId(App.instance), push_user_id)) {
            //是正确的推送 则 跳转到指定页面
            var bundle = Bundle()
            bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, mobile_model)
            bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, mobile_memory)
            if (risk_result == "1") {
                gotoActivity(mActivity, AssessSuccessActivity::class.java, bundle)
            } else if (risk_result == "2") {
                gotoActivity(mActivity, AssessFailureActivity::class.java, bundle)
            }
        } else {
            gotoActivity(mActivity, MainActivity::class.java, null)

        }
        finish()
    }

    override fun finishActivity() {
        finish()
    }

    override fun getVeryCodeResult(data: Any?) {
        ToastUtil.showToast(mActivity, "验证码发送成功")
    }

    override fun changeComplete() {
    }

    override fun signUpAgreementResult(data: SignUpAgreementBean?) {
        //注册协议
        agreementBean = data
    }

    override fun registComplete(data: RegistLoginBean?) {
//        这里用不到
    }

    override fun onDestroy() {
        super.onDestroy()
        mHandler.removeCallbacksAndMessages(null)
    }

}
