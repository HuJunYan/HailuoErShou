package com.huaxi.hailuo.ui.fragment

import android.text.TextUtils
import android.view.View
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.BaseFragment
import com.huaxi.hailuo.event.LogOutEvent
import com.huaxi.hailuo.event.LoginEvent
import com.huaxi.hailuo.model.bean.MineBean
import com.huaxi.hailuo.model.bean.UserConfigBean
import com.huaxi.hailuo.presenter.contract.MineContract
import com.huaxi.hailuo.presenter.impl.MinePresenter
import com.huaxi.hailuo.ui.activity.*
import com.huaxi.hailuo.util.StringUtil
import com.huaxi.hailuo.util.UserUtil
import kotlinx.android.synthetic.main.fragment_me.*
import org.greenrobot.eventbus.Subscribe
import org.jetbrains.anko.support.v4.startActivity

/**
 * 我的页面
 */
class MeFragment : BaseFragment<MineContract.View, MinePresenter>(), MineContract.View {

    private var mMineBean: MineBean? = null
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

    override fun showMine(t: MineBean?) {
        if (t == null) {
            return
        }

        mMineBean = t
        //根据返回的客服服务url 是否为空来显示客服模块

        if (TextUtils.isEmpty(t?.service_url)) {
            rl_people_contact.visibility = View.GONE
            view_line.visibility = View.GONE
        } else {
            rl_people_contact.visibility = View.VISIBLE
            view_line.visibility = View.VISIBLE

            //1有新消息，2没有新消息
            iv_message_center.setImageResource(
                    if (mMineBean?.is_new_msg == "1")
                        R.drawable.ic_me_message
                    else R.drawable.ic_me_message_unread
            )
        }

        if (mMineBean?.is_invitation == "1") {
            rl_invite_friends.visibility = View.VISIBLE
            view_line_invite.visibility = View.VISIBLE
            if (!TextUtils.isEmpty(mMineBean?.invitation_des)) {
                tv_invite_desc.text = mMineBean?.invitation_des
            }
        } else if (mMineBean?.is_invitation == "2"){
            rl_invite_friends.visibility = View.GONE
            view_line_invite.visibility = View.GONE
        }

        //1 消息中心开   2.消息中心关
        if(mMineBean?.message_pow == "1"){
            iv_message_center.visibility = View.VISIBLE
        }else if(mMineBean?.message_pow == "2"){
            iv_message_center.visibility = View.GONE
        }

    }

    override fun processExitLoginResult() {
    }

    override fun processUserConfigResult(data: UserConfigBean?) {
    }

    override var mPresenter: MinePresenter = MinePresenter()

    override fun getLayout(): Int = R.layout.fragment_me

    override fun initView() {

//        StatusBarUtil.setPaddingSmart(mContext, tb_me)
        //跳转到邀请好友的页面
        rl_invite_friends.setOnClickListener {
            if (mMineBean != null) {
                if (!UserUtil.isLogin(App.instance)) {
                    startActivity<LoginActivity>()
                    return@setOnClickListener
                }
                startActivity<InviteFriendsActivity>()
            }
        }

        rl_me_avatar.setOnClickListener({
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
            }
        })
        rl_recycle_history.setOnClickListener({

            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<MyOrderListActivity>()
        })
        rl_setting.setOnClickListener({
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<SettingActivity>()
        })
        rl_me_about_xiguaka.setOnClickListener({
            //意见反馈
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<UpOpionActivity>()
        })
        rl_me_server_online.setOnClickListener({
            //帮助中心
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<HelpCenterActivity>()
        })
        rl_me_exit_login.setOnClickListener({
            //我的银行卡
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<MyBankListActivity>()
        })
        rl_yhq.setOnClickListener({
            //我的优惠券
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<MyCouponActivity>()
        })
        rl_people_contact.setOnClickListener({
            //客服
            if (mMineBean != null) {
                startActivity<ServiceOnlineActivity>(
                        ServiceOnlineActivity.SERVICE_ONLINE_KEY to mMineBean!!.service_url)
            }
        })
        iv_message_center.setOnClickListener {
            if (!UserUtil.isLogin(App.instance)) {
                startActivity<LoginActivity>()
                return@setOnClickListener
            }
            startActivity<MessageCenterActivity>()
        }
    }

    /**
     * 第一次加载也面试 initData 和 onSupportVisible 会同时调用导致重复请求。
     * 当initData 调用后，onSupportVisible 再调用
     */
    private var firstLoadMineData = false

    override fun initData() {
        mPresenter.loadMine()
        refreshUI()
        firstLoadMineData = true
    }

    override fun onSupportVisible() {
        super.onSupportVisible()
        if (firstLoadMineData) {
            mPresenter.loadMine()
        }
    }

    /**
     * 刷新界面数据
     */
    private fun refreshUI() {
        if (UserUtil.isLogin(App.instance)) {
            //设置手机号
            val PhoneNum = UserUtil.getMobile(App.instance)
            val encryptPhoneNum = StringUtil.encryptPhoneNum(PhoneNum)
            tv_user_phone_number.text = encryptPhoneNum
        } else {
            //未登录
            tv_user_phone_number.text = "点击登录"
        }

    }

    @Subscribe
    fun LoginEventSucces(event: LoginEvent) {
        refreshUI()
    }

    @Subscribe
    fun LoginOutEventSucces(event: LogOutEvent) {
        tv_user_phone_number.text = "点击登录"
        refreshUI()
    }


}