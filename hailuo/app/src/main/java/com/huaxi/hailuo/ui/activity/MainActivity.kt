package com.huaxi.hailuo.ui.activity

import android.content.Intent
import android.text.TextUtils
import android.view.KeyEvent
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.event.HomeDiversionEvent
import com.huaxi.hailuo.event.LogOutEvent
import com.huaxi.hailuo.event.LoginEvent
import com.huaxi.hailuo.event.RefreshMainEvent
import com.huaxi.hailuo.toast
import com.huaxi.hailuo.ui.fragment.HomeFragment
import com.huaxi.hailuo.ui.fragment.MeFragment
import com.huaxi.hailuo.ui.fragment.VerifyHomeFragment
import com.huaxi.hailuo.ui.fragment.VerifyMeFragment
import com.huaxi.hailuo.util.ImageUtil
import com.huaxi.hailuo.util.MessageNotificationUtil
import kotlinx.android.synthetic.main.activity_main.*
import me.yokeyword.fragmentation.SupportFragment
import org.greenrobot.eventbus.Subscribe
import q.rorbin.badgeview.Badge
import q.rorbin.badgeview.QBadgeView

class MainActivity : SimpleActivity() {

    var mFragments: MutableList<SupportFragment>? = null
    var mTabPosition: Int = 0
    var mExitTime: Long = 0

    var mHomeFragment: HomeFragment? = null
    var mMeFragment: MeFragment? = null
    var mVerifyHomeFragment: VerifyHomeFragment? = null
    var mVerifyMeFragment: VerifyMeFragment? = null
    private val messageNotificationUtil = MessageNotificationUtil()

    override fun getLayout(): Int = R.layout.activity_main

    override fun initData() {
        messageNotificationUtil.register()
    }

    override fun initView() {
        initBottomNavigationView()
        initFragment()
    }

    /**
     * 初始化Tab个Fragment
     */
    private fun initFragment() {

        mFragments = mutableListOf<SupportFragment>()
        if (App.instance.mIsVerify) {
            //审核视图 App.instance.mIsVerify
            mVerifyHomeFragment = VerifyHomeFragment()
//            mVerifyMeFragment = VerifyMeFragment()
            mFragments?.add(mVerifyHomeFragment!!)
//            mFragments?.add(mVerifyMeFragment!!)
            mMeFragment = MeFragment()
            mFragments?.add(mMeFragment!!)
        } else {
            mHomeFragment = HomeFragment()
            mMeFragment = MeFragment()
            mFragments?.add(mHomeFragment!!)
            mFragments?.add(mMeFragment!!)
        }

        loadMultipleRootFragment(R.id.fl_container, 0,
                mFragments?.get(0),
                mFragments?.get(1))
    }

    /**
     * 初始化底部导航栏
     */
    private fun initBottomNavigationView() {

        val menu = bnve.menu
        if (App.instance.mIsVerify) {
            menu.removeItem(R.id.tab_home)
        } else {
            menu.removeItem(R.id.tab_verify_home)
        }

        bnve.enableAnimation(false)
        bnve.enableShiftingMode(false)
        bnve.enableItemShiftingMode(false)

        bnve.setOnNavigationItemSelectedListener { item ->
            var title = item.title
            var position = bnve.getMenuItemPosition(item)
            showHideFragment(mFragments?.get(position), mFragments?.get(mTabPosition))
            mTabPosition = position

            return@setOnNavigationItemSelectedListener true
        }
    }

    /**
     * 给底部导航栏增加气泡
     */
    private fun addBadgeAt(position: Int, number: Int): Badge {
        // add badge
        return QBadgeView(this)
                .setBadgeNumber(number)
                .setGravityOffset(12F, 2F, true)
                .bindTarget(bnve.getBottomNavigationItemView(position))
                .setOnDragStateChangedListener { dragState, badge, targetView ->
                    if (Badge.OnDragStateChangedListener.STATE_SUCCEED === dragState) {
                        //清除气泡的回调
                    }
                }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event?.repeatCount == 0) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                toast(getString(R.string.tips_global_again_exit))
                mExitTime = System.currentTimeMillis()
            } else {
                messageNotificationUtil.unregister()
                App.instance.exitApp()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    @Subscribe
    fun onLogoutEvent(event: LogOutEvent) {
    }


    @Subscribe
    fun onLoginEvent(event: LoginEvent) {
    }

    @Subscribe
    fun onRefreshMainEvent(event: RefreshMainEvent) {
    }

    /**
     * 导流按钮
     */
    @Subscribe
    fun onDiversionEvent(event: HomeDiversionEvent) {
        if (TextUtils.isEmpty(event.image)) {
            iv_diversion.setImageResource(R.drawable.iv_diversion)
        } else {
            ImageUtil.load(mActivity, event.image!!, R.drawable.iv_diversion, R.drawable.iv_diversion, iv_diversion)
        }

        if (!TextUtils.isEmpty(event.url)) {
            iv_diversion.visibility = View.VISIBLE
            iv_diversion.setOnClickListener {
                val intent = Intent(mActivity, WebActivity::class.java)
                intent.putExtra(WebActivity.WEB_URL_KEY, event.url)
                intent.putExtra(WebActivity.WEB_URL_TITLE,
                        if (TextUtils.isEmpty(event.title)) {
                            getString(R.string.app_name)
                        } else {
                            event.title
                        }
                )
                startActivity(intent)
            }
        } else {
            iv_diversion.visibility = View.GONE
            iv_diversion.setOnClickListener(null)
        }
    }
}
