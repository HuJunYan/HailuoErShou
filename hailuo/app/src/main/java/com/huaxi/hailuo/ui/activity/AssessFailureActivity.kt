package com.huaxi.hailuo.ui.activity

import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.R.id.*
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.BackHomeEvent
import com.huaxi.hailuo.model.bean.DownRefreshBean
import com.huaxi.hailuo.presenter.contract.AssessFailureContract
import com.huaxi.hailuo.presenter.contract.AuthExtroContactsContract
import com.huaxi.hailuo.presenter.impl.AssessFailurePresenter
import com.huaxi.hailuo.util.ImageUtil
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_assess_failure.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.startActivity

class AssessFailureActivity : BaseActivity<AssessFailureContract.View, AssessFailureContract.Presenter>(), AssessFailureContract.View {


    companion object {
        var ERROR_MSG = "error_msg"
        var ERROR_ICON = "error_icon"
    }

    var mobile_model = ""
    var mobile_memory = ""
    override fun initData() {
        val extras = intent.extras
        if (extras != null) {
            tv_assess_failure_message.text = extras.getString(ERROR_MSG, "")
            ImageUtil.load(mActivity, extras.getString(ERROR_ICON, ""), iv_assess_failure)
            mobile_model = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, "")
            mobile_memory = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, "")
        }


        mPresenter.getData(mobile_model, mobile_memory)
        mPresenter.getRefreshData(mobile_model, mobile_memory)
    }


    override var mPresenter: AssessFailureContract.Presenter = AssessFailurePresenter()

    override fun getLayout(): Int = R.layout.activity_assess_failure


    override fun showProgress() {}

    override fun hideProgress() {
    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

    override fun processData(data: DownRefreshBean?) {
        if (data != null) {
//            ImageUtil.load(mActivity, data.error_icon, iv_assess_failure)
            tv_assess_failure_message.text = data.error_message
        }
    }

    override fun processSuccessResult(data: DownRefreshBean?) {
        if (!TextUtils.isEmpty(data?.bubble_url)) {
            iv_assess_failure.visibility = View.VISIBLE
            ImageUtil.load(mActivity, data!!.daoliu_image, iv_assess_failure_daoliu)
            iv_assess_failure_daoliu.setOnClickListener {
                mPresenter.daoliuBuriedPoint("89", "-1")
                startActivity<WebActivity>(WebActivity.WEB_URL_KEY to data.bubble_url, WebActivity.WEB_URL_TITLE to data.daoliu_title)
            }
        } else {
            iv_assess_failure_daoliu.visibility = View.INVISIBLE
            iv_assess_failure_daoliu.isClickable = false
        }
    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_assess_failure)
        tv_assess_failure_back.setOnClickListener {
            EventBus.getDefault().post(BackHomeEvent())
            backActivity()
        }
        tv_assess_failure_go_home.setOnClickListener {
            EventBus.getDefault().post(BackHomeEvent())
            backActivity()
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0) {
            EventBus.getDefault().post(BackHomeEvent())
            backActivity()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

}
