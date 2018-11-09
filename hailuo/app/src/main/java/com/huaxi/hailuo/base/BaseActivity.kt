package com.huaxi.hailuo.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.huaxi.hailuo.R
import com.huaxi.hailuo.event.AuthErrorEvent
import com.huaxi.hailuo.event.BackHomeEvent
import com.huaxi.hailuo.event.DummyEvent
import com.huaxi.hailuo.ui.activity.CreditApplyDialogActivity
import com.huaxi.hailuo.util.StatusBarUtil
import com.umeng.analytics.MobclickAgent
import me.yokeyword.fragmentation.SupportActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

@SuppressLint("RestrictedApi")
abstract class BaseActivity<in V : BaseView, T : BasePresenter<V>> : SupportActivity(), BaseView {

    protected val mActivity: Activity = this

    protected abstract var mPresenter: T

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        setStatusBar()
        App.instance.addActivity(mActivity)
        EventBus.getDefault().register(mActivity)
        mPresenter.attachView(this as V)
        initView()
        initData()
    }

    override fun onDestroy() {
        super.onDestroy()
        App.instance.removeActivity(mActivity)
        EventBus.getDefault().unregister(mActivity)
        mPresenter.detachView()

    }

    override fun onResume() {
        super.onResume()
        MobclickAgent.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        MobclickAgent.onPause(this)
    }

    override fun finish() {
        super.finish()
        if (isTaskRoot) {
            MobclickAgent.onKillProcess(applicationContext)
        }
    }

    /**
     * 设置状态栏
     */
    protected open fun setStatusBar() {
        StatusBarUtil.immersive(this, resources.getColor(R.color.colorPrimaryDark), 0F)
    }

    /**
     * 跳转到某个Activity
     */
    protected fun gotoActivity(mContext: Context, toActivityClass: Class<*>, bundle: Bundle?) {
        val intent = Intent(mContext, toActivityClass)
        if (bundle != null) {
            intent.putExtras(bundle)
        }
        mContext.startActivity(intent)
        (mContext as Activity).overridePendingTransition(R.anim.push_right_in, R.anim.not_exit_push_left_out)
    }

    /**
     * 退出Activity
     */
    protected open fun backActivity() {
        finish()
        overridePendingTransition(R.anim.not_exit_push_left_in, R.anim.push_right_out)
    }

    /**
     * 初始化子类布局
     */
    protected abstract fun getLayout(): Int

    /**
     * 初始化子类View
     */
    protected abstract fun initView()

    /**
     * 初始化子类一些数据
     */
    protected abstract fun initData()

    /**
     * 该方法不执行，只是让Event编译通过
     */
    @Subscribe
    fun dummy(event: DummyEvent) {
    }

    @Subscribe
    fun onTokenErrorEvent(event: com.huaxi.hailuo.event.TokenErrorEvent) {
        finish()
    }

    /**
     * 收到了风控异常的消息
     */
    @Subscribe
    fun onAuthErrorEvent(event: AuthErrorEvent) {
        val content = event.content
        val have_a_trye = event.btn_try
        val intent = Intent(mActivity, CreditApplyDialogActivity::class.java)
        intent.putExtra("error_content", content)
        intent.putExtra("have_a_try", have_a_trye)
        startActivity(intent)
//        startActivity<CreditApplyDialogActivity>()
    }

    @Subscribe
    fun onBackHomeEvent(event: BackHomeEvent) {
        finish()
    }


}