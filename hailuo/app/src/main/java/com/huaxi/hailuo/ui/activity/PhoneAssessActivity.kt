package com.huaxi.hailuo.ui.activity

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewTreeObserver
import android.view.animation.LinearInterpolator
import android.widget.RelativeLayout
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.RiskResultEvent
import com.huaxi.hailuo.model.bean.DownRefreshBean
import com.huaxi.hailuo.presenter.contract.PhoneAssessContract
import com.huaxi.hailuo.presenter.impl.PhoneAssessPresenter
import com.huaxi.hailuo.ui.view.ClassicCustomerHeader
import com.huaxi.hailuo.util.ImageUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.scwang.smartrefresh.layout.api.RefreshHeader
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.constant.RefreshState
import com.scwang.smartrefresh.layout.constant.SpinnerStyle
import com.scwang.smartrefresh.layout.listener.OnRefreshListener
import com.scwang.smartrefresh.layout.listener.SimpleMultiPurposeListener
import kotlinx.android.synthetic.main.activity_phone_assess.*
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.startActivity
import android.app.Dialog
import android.view.LayoutInflater
import com.huaxi.hailuo.log.LogUtil
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.dialog_auth_no_result.view.*
import java.util.concurrent.TimeUnit


class PhoneAssessActivity : BaseActivity<PhoneAssessContract.View, PhoneAssessContract.Presenter>(), PhoneAssessContract.View {

    var mobile_model = ""
    var mobile_memory = ""
    var mRefreshBean: DownRefreshBean? = null
    var noResultDialog: Dialog? = null
    //轮询时间间隔(时间单位分钟)
    private val mLoopInterval: Long = 1
    //轮询结束时间
    private val mLoopEndTime: Long = 30
    //是否显示轮询结束之后的弹框
    private var mIsShowDialog = false
    //是否正在轮询
    private var mIsLooping = false
    //任务队列
    private var disposables: CompositeDisposable? = null
    private var mUrl: String = ""

    override fun initData() {
        val extras = intent.extras
        if (extras != null) {
            mobile_model = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, "android")
            mobile_memory = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, "64G")
        }
        mPresenter.getRefreshData(mobile_model, mobile_memory)
    }

    private var isOnResume = false
    private var isFinish = false

    override var mPresenter: PhoneAssessContract.Presenter = PhoneAssessPresenter()

    override fun getLayout(): Int = R.layout.activity_phone_assess


    override fun showProgress() {}

    override fun hideProgress() {
    }

    override fun showError() {
        srl_phone_assess.finishRefresh()
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }


    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_phone_assess)
//        iv_phone_icon.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
//                iv_phone_icon.viewTreeObserver.removeOnGlobalLayoutListener(this)
//                val height = iv_phone_icon.height
//                val height1 = iv_phone_round.height
//                var delta = (height1 - height) / 2
//                val layoutParams = iv_phone_icon.layoutParams as FrameLayout.LayoutParams
//                layoutParams.topMargin = delta + 44 * resources.displayMetrics.density.toInt()
//                iv_phone_icon.layoutParams = layoutParams
//                iv_phone_icon.visibility = View.VISIBLE
//            }
//        })
        val spinnerStyle = ClassicCustomerHeader(this).setSpinnerStyle(SpinnerStyle.Translate)
        srl_phone_assess.isEnableLoadmore = false
        srl_phone_assess.isEnableRefresh = true
        srl_phone_assess.setEnableFooterTranslationContent(false)
        srl_phone_assess.refreshHeader = spinnerStyle
        srl_phone_assess.setOnRefreshListener(object : OnRefreshListener {
            override fun onRefresh(refreshlayout: RefreshLayout?) {
                mPresenter.getRefreshData(mobile_model, mobile_memory)
            }
        })
        initAnimateListener()
        initAnimate()

    }

    override fun riskWaitingFeedbackResult(t: Any?) {
        noResultDialog?.dismiss()
        //跳转到新的activity
        startActivity<QuestionFeedBackActivity>()
    }

    private fun initAnimateListener() {
        srl_phone_assess.setOnMultiPurposeListener(object : SimpleMultiPurposeListener() {
            override fun onHeaderPulling(header: RefreshHeader?, percent: Float, offset: Int, headerHeight: Int, extendHeight: Int) {
//                iv_drag_line.scaleY = 1 + percent
//                iv_drag_line.translationY = iv_drag_line.height * percent / 2
//                iv_drag_circle.translationY = iv_drag_line.height * percent
            }

            override fun onHeaderReleasing(header: RefreshHeader?, percent: Float, offset: Int, headerHeight: Int, extendHeight: Int) {
//                iv_drag_line.scaleY = 1 + percent
//                iv_drag_line.translationY = iv_drag_line.height * percent / 2

//                iv_drag_circle.translationY = iv_drag_line.height * percent
            }


            override fun onStateChanged(refreshLayout: RefreshLayout?, oldState: RefreshState?, newState: RefreshState?) {
                when (newState) {
                    RefreshState.None -> {
                        tv_phone_assess_tips.text = "下拉刷新,获取评估结果"
                    }
                    else -> {
                        tv_phone_assess_tips.text = ""
                    }
                }
            }
        })
    }

    private fun initAnimate() {
        val animate = ObjectAnimator.ofFloat(iv_phone_round, "rotation", 0f, 360f)
        animate.interpolator = LinearInterpolator()
        animate.repeatMode = ValueAnimator.RESTART
        animate.repeatCount = ValueAnimator.INFINITE
        animate.duration = 8000L
        animate.start()
    }

    override fun processSubmitCreditResult() {
        startLoop()
    }

    override fun processData(data: DownRefreshBean?) {
        srl_phone_assess.finishRefresh()
        if (data != null) {
            mRefreshBean = data
            if ("1" == data.risk_result) {
                //跳转到成功页面
                var bundle = Bundle()
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, data.mobile_model)
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, data.mobile_memory)
                gotoActivity(mActivity, AssessSuccessActivity::class.java, bundle)
                isFinish = true
            } else if ("2" == data.risk_result) {
                var bundle = Bundle()
                bundle.putString(AssessFailureActivity.ERROR_MSG, data.error_message)
                bundle.putString(AssessFailureActivity.ERROR_ICON, data.error_icon)
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, data.mobile_model)
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, data.mobile_memory)
                gotoActivity(mActivity, AssessFailureActivity::class.java, bundle)
                isFinish = true
                //风控结果是5的情况下，暂停轮训，请求风控的接口
            } else if ("5" == data.risk_result) {
                mPresenter.submitCredit(mobile_model, mobile_memory)
                disposables?.dispose()
            }

            if (!TextUtils.isEmpty(data.bubble_url)) {
                iv_daoliu_dudu.visibility = View.VISIBLE
                if (TextUtils.isEmpty(mUrl)) {
                    mUrl = data.daoliu_image
                    ImageUtil.load(mActivity, mUrl, iv_daoliu_dudu)
                }

                //嘟嘟白卡导流
                iv_daoliu_dudu.setOnClickListener {
                    mPresenter.daoliuBuriedPoint("89", "-1")
                    startActivity<WebActivity>(WebActivity.WEB_URL_KEY to data.bubble_url, WebActivity.WEB_URL_TITLE to data.daoliu_title)
                }
            } else {
                iv_daoliu_dudu.visibility = View.INVISIBLE
                iv_daoliu_dudu.isClickable = false
            }

            // 判断轮询器是否打开
            if (data.is_loop == "1" && !mIsLooping) {
                startLoop()
            }

        }
    }

    private fun startLoop() {
        disposables = CompositeDisposable()
        disposables?.add(getObservable()
                // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getObserver()))
    }


    private fun getObservable(): Observable<out Long> {
        return Observable.interval(0, mLoopInterval, TimeUnit.MINUTES).take(mLoopEndTime)
    }

    private fun getObserver(): DisposableObserver<Long> {
        return object : DisposableObserver<Long>() {

            override fun onStart() {
                super.onStart()
                mIsLooping = true
            }

            override fun onNext(t: Long) {
                val currentIndex = t + 1
                if (currentIndex < 5) { //前4次
                    // 调用接口
                    mPresenter.getRefreshData(mobile_model, mobile_memory)
                } else if (currentIndex.toInt() % 5 == 0) { //后6次
                    if (mLoopEndTime == currentIndex) {
                        mIsShowDialog = true
                    }
                    // 调用接口
                    mPresenter.getRefreshData(mobile_model, mobile_memory)
                }
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
                if (mIsShowDialog) {
                    // 显示弹框
                    riskControlTimeOut()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        isOnResume = true
    }

    override fun onPause() {
        super.onPause()
        isOnResume = false
    }

    override fun processSuccessData() {
        if (isFinish) finish()
    }

    @Subscribe
    fun onRiskResultEvent(event: RiskResultEvent) {
        val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.cancel(event.notify_id)
        if (isOnResume) {
            mPresenter.getRefreshData(mobile_model, mobile_memory)
        }
    }

    //30分钟轮询时间后，弹窗提示用户，可以再刷新和返回到首页，弹窗只出现一次
    private fun riskControlTimeOut() {
        if (noResultDialog == null) {
            noResultDialog = Dialog(mActivity, R.style.MyDialog)
        }
        if (noResultDialog!!.isShowing) {
            return
        }
        val mLayoutInflater = mActivity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view_dialog_not_login = mLayoutInflater.inflate(R.layout.dialog_auth_no_result, null, false)
        //设置布局
        noResultDialog?.setContentView(view_dialog_not_login)

        noResultDialog?.show()

        //反馈
        view_dialog_not_login.tv_dialog_try_again.setOnClickListener {
            //            noResultDialog?.dismiss()
            mPresenter.getRiskWaitingFeedback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        noResultDialog = null
        disposables?.dispose()
        mIsLooping = false
    }

}
