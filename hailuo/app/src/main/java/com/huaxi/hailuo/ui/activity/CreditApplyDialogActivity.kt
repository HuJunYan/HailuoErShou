package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.text.TextUtils
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.model.bean.CreditAssessBean
import com.huaxi.hailuo.model.bean.LastSmsTimeBean
import com.huaxi.hailuo.presenter.contract.CreditAssessmentContract
import com.huaxi.hailuo.presenter.impl.CreditAssessmentPresenter
import com.huaxi.hailuo.util.SharedPreferencesUtil
import com.huaxi.hailuo.util.ToastUtil
import kotlinx.android.synthetic.main.dialog_auth_error.*

/**
 * Created by zhangliuguang  on 2018/4/12.
 */
class CreditApplyDialogActivity : BaseActivity<CreditAssessmentContract.View, CreditAssessmentContract.Presenter>(), CreditAssessmentContract.View {

    private var choose_mobile: String = ""
    private var choose_memory: String = ""
    override var mPresenter: CreditAssessmentContract.Presenter = CreditAssessmentPresenter()
    override fun processSubmitCreditResult() {
        var bundle = Bundle()
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, choose_mobile)
        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, choose_memory)
        gotoActivity(mActivity, PhoneAssessActivity::class.java, bundle)
        //关闭本页面
        finish()
    }

    override fun processLastSmsTime(t: LastSmsTimeBean?, isFirst: Boolean) {
    }

    override fun processSubmitPhoneInfoResult() {
    }

    override fun getLayout(): Int = R.layout.dialog_auth_error

    override fun initView() {

    }

    override fun initData() {
        val mIntent = intent
        if (mIntent != null) {
            val eror_content = mIntent.getStringExtra("error_content")
            val have_a_try = mIntent.getStringExtra("have_a_try")
            tv_dialog_auth_error_tips.text = eror_content
            tv_dialog_auth_error_try.text = have_a_try
        } else {
            ToastUtil.showToast(mActivity, "数据错误")
        }
        choose_mobile = SharedPreferencesUtil.getInstance(mActivity).getString("choose_mobile", "")
        choose_memory = SharedPreferencesUtil.getInstance(mActivity).getString("choose_memory", "")
        tv_dialog_auth_error_try.setOnClickListener {
            if (TextUtils.isEmpty(choose_memory) && TextUtils.isEmpty(choose_mobile)) {
                ToastUtil.showToast(mActivity, "数据错误")
            } else {
                mPresenter.submitCredit(choose_mobile, choose_memory)
            }
        }

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

    override fun getCreditAssessResult(data: CreditAssessBean?, isNeedJump: Boolean) {
    }

}