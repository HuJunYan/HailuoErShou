package com.huaxi.hailuo.push

import android.app.ActivityManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import cn.jpush.android.api.JPushInterface
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.AuthErrorEvent
import com.huaxi.hailuo.event.BackHomeEvent
import com.huaxi.hailuo.event.RiskResultEvent
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.model.http.ApiManager
import com.huaxi.hailuo.ui.activity.*
import com.huaxi.hailuo.util.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.*


/**
 * Created by wang on 2017/12/21.
 */
class JpushReceiver : BroadcastReceiver() {
    private val TAG = JpushReceiver::class.java.simpleName

    override fun onReceive(context: Context, intent: Intent) {
        LogUtil.d(TAG, "JPushInterface onReceive")
        if (intent == null) {
            return
        }
        val bundle = intent.extras
        val result = bundle.getString(JPushInterface.EXTRA_EXTRA)
        val notificationId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID)
        try {
            LogUtil.d(TAG, "JPushInterface result = $result")
            val action = intent.action
            if (TextUtils.isEmpty(action)) {
                return
            }

            val msgType: String
            try {
                val jsonObject = GsonUtil.json2bean(result, JpushBaseBean::class.java)
                msgType = jsonObject.msg_type
                LogUtil.d(TAG, "JPushInterface msgType = $msgType")
            } catch (e: Exception) {
                e.printStackTrace()
                return
            }

            when (msgType) {
            // 审核
                "1" -> {
                    processMessageType111(context, action, result, notificationId)
                }
            // 消息中心
                "2" -> {
                    processMessageType222(context, action, result, notificationId)
                }
            // 还款失败
                "3" -> {
                    processMessageType333(context, action, result, notificationId)
                }
                else -> {
                    if (action == JPushInterface.ACTION_NOTIFICATION_OPENED) {
                        gotoMain(context)
                    }
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    private fun processMessageType111(context: Context, action: String?, result: String?, notificationId: Int) {
        val jPushAccessBean = GsonUtil.json2bean(result, JPushAssessBean::class.java)
        if (jPushAccessBean == null || jPushAccessBean.msg_content == null) {
            return
        }
        if (action == JPushInterface.ACTION_NOTIFICATION_RECEIVED) {
            LogUtil.d(TAG, "Jpush Received ACTION_NOTIFICATION_RECEIVED, notificationId = $notificationId")
            //用户已登录 且在前台
            if (UserUtil.isLogin(context) && App.isOnResume) {
                //数据解析正常情况下
                val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                val info = am.getRunningTasks(1).get(0)
                val shortClassName = info.topActivity.shortClassName
                LogUtil.d(TAG, "ACTION_NOTIFICATION_RECEIVED shortClassName = $shortClassName")
                if (shortClassName != null) {
                    if (shortClassName.contains(AssessSuccessActivity::class.java.simpleName)
                            || shortClassName.contains(AssessFailureActivity::class.java.simpleName)
                            || shortClassName.contains(PhoneAssessActivity::class.java.simpleName)) {
                        var nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                        nm.cancel(notificationId)
                    }
                }
                //申请刷新指定的页面
                EventBus.getDefault().postSticky(RiskResultEvent(notificationId))
                if (jPushAccessBean.msg_content.result == "5") {
                    EventBus.getDefault().post(AuthErrorEvent(jPushAccessBean.msg_content.content, jPushAccessBean.msg_content.btn_try))
                }
            }
            //不是同一个用户 且msg_type是风控审核结果类型的
            if (!TextUtils.equals(jPushAccessBean.msg_content.user_id, UserUtil.getUserId(context)) && "1" == jPushAccessBean.msg_type) {
                //取消这条推送
                val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                nm.cancel(notificationId)
                LogUtil.d(TAG, "notification clear id = $notificationId")
            }
        } else if (action == JPushInterface.ACTION_NOTIFICATION_OPENED) {
            processRiskResultMsg(jPushAccessBean, context)
        }
    }

    /**
     * 消息中心
     */
    private fun processMessageType222(context: Context, action: String?, result: String?, notificationId: Int) {
        val msg = GsonUtil.json2bean(result, JPushMessageCenterBean::class.java)
        if (msg == null || msg.msg_content == null) {
            return
        }
        if (action == JPushInterface.ACTION_NOTIFICATION_RECEIVED) {

            MessageNotificationUtil.addMessage(msg.msg_content.msg_id, notificationId)

        } else if (action == JPushInterface.ACTION_NOTIFICATION_OPENED) {
            processMessageCenterMsg(msg, context)
        }
    }

    /**
     * 还款失败
     */
    private fun processMessageType333(context: Context, action: String?, result: String?, notificationId: Int) {
        val bean = GsonUtil.json2bean(result, JPushRefundBean::class.java)
        if (bean == null || bean.msg_content == null) {
            return
        }
        if (action == JPushInterface.ACTION_NOTIFICATION_RECEIVED) {
            val userId = UserUtil.getUserId(context)
            // 是否未同用户，不是同一个用户，就清理消息
            if (userId != bean.msg_content.user_id) {
                JPushInterface.clearNotificationById(context, notificationId)
            }
        } else if (action == JPushInterface.ACTION_NOTIFICATION_OPENED) {

            // 用户未等登录跳到登录页面
            if (UserUtil.isLogin(context)) {
                val userId = UserUtil.getUserId(context)
                // 是否未同用户
                if (userId == bean.msg_content.user_id) {
                    val intent = Intent(context, OrderDetailActivity::class.java)
                    intent.putExtra(OrderDetailActivity.ORDERI_ID, bean.msg_content.order_id)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }
            } else {
                val intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        }
    }

    /**
     * 点击通知，打开消息中心的消息
     */
    private fun processMessageCenterMsg(msg: JPushMessageCenterBean, context: Context) {
        val msgContent = msg.msg_content ?: return

        // 用户未等登录跳到登录页面
        if (UserUtil.isLogin(context)) {
            try {
                readMessage(msgContent)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            if (!TextUtils.isEmpty(msgContent.url)) {
                // 地址不为空，才可跳转
                val intent = Intent(context, WebActivity::class.java)
                intent.putExtra(WebActivity.WEB_URL_KEY, msgContent.url)
                intent.putExtra(WebActivity.WEB_URL_TITLE, msgContent.title)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            } else {
                val intent = Intent(context, ArticalDetailActivity::class.java)
                intent.putExtra("msg_id", msgContent.msg_id)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                context.startActivity(intent)
            }
        } else {
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(intent)
        }
    }

    /**
     * 设置消息为已读
     */
    private fun readMessage(msg: JPushMessageCenterBean.MsgContent) {
        val calendar = Calendar.getInstance()
        // 当前月份
        val month = calendar.get(Calendar.MONTH)
        // push_time 为int类型 ，10位，需要乘1000转一下
        calendar.time = Date(msg.push_time.toLong() * 1000)
        // 消息的月份
        val msgMonth = calendar.get(Calendar.MONTH)

        val table_identifier: Int
        // 当前是1月份，消息是12月份的，table_identifier为1
        if (month == 0 && msgMonth == 11) {
            table_identifier = 1
        } else {
            table_identifier = month - msgMonth
        }

        if (table_identifier < 0) {
            return
        }
        val jsonObject = JSONObject()
        jsonObject.put("table_identifier", table_identifier.toString())
        jsonObject.put("msg_id", msg.msg_id)
        // 2为三个消息
        jsonObject.put("is_read_all", "2")

        val jsonObjectSigned = SignUtils.signJsonNotContainList(jsonObject)
        val body = RequsetUtil.getRequestBody(jsonObjectSigned)

        ApiManager.readMessage(body)
                .compose(RxUtil.rxSchedulerHelper<MyHttpResponse<Any>>())
                .compose(RxUtil.handleResult())
                .subscribe({}, { it.printStackTrace() })
    }

    private fun gotoMain(context: Context) {
        if (App.isOnResume) {
            return
        }
        val realIntent = Intent(context, LauncherActivity::class.java)
        realIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        toIntent(realIntent, context)
    }

    /**
     * 处理风控结果消息
     * @param jpushBaseBean 消息内容
     * @param context 上下文
     */
    fun processRiskResultMsg(jpushBaseBean: JPushAssessBean, context: Context) {


        if (jpushBaseBean == null || jpushBaseBean.msg_content == null) {
            return
        }
        var msg_content = jpushBaseBean.msg_content
        LogUtil.d(TAG, "JPushInterface msg_content = $msg_content")
        LogUtil.d(TAG, "JPushInterface msg_content = ${msg_content.result}")
        //msg_content 不能为空

        var isOnResume = App.isOnResume
        //用户点击了消息  需要去处理不同情况下的跳转
        val login = UserUtil.isLogin(App.instance)
        LogUtil.d("当前的登录状态是：" + login)
        var intent: Intent
        if (login) {
            //登录 直接跳转
            if (isOnResume) {
                //应用在前台,是同一个用户
                if (msg_content.user_id == UserUtil.getUserId(context)) {

                    if (msg_content.result == "1") {
                        //风控成功
                        intent = Intent(context, AssessSuccessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        var bundle = Bundle()
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, msg_content.mobile_model)
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, msg_content.mobile_memory)
                        intent.putExtras(bundle)
                        toIntent(intent, context)
                    } else if (msg_content.result == "2") {
                        //风控失败
                        intent = Intent(context, AssessFailureActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        var bundle = Bundle()
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, msg_content.mobile_model)
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, msg_content.mobile_memory)
                        intent.putExtras(bundle)
                        toIntent(intent, context)
                    } else if (msg_content.result == "5") {
                        EventBus.getDefault().post(AuthErrorEvent(msg_content.content, msg_content.btn_try))
                    }
                }
            } else {
                //应用没在前台
                //不是同一个用户 跳转到首页
                if (msg_content.user_id != UserUtil.getUserId(context)) {
                    gotoMain(context)
                } else {
                    //是同一个用户 风控成功
                    if (msg_content.result == "1") {
                        intent = Intent(context, AssessSuccessActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        var bundle = Bundle()
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, msg_content.mobile_model)
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, msg_content.mobile_memory)
                        intent.putExtras(bundle)
                        toMainAndOther(intent, context)
                    } else if (msg_content.result == "2") {
                        //风控失败
                        intent = Intent(context, AssessFailureActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        var bundle = Bundle()
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, msg_content.mobile_model)
                        bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, msg_content.mobile_memory)
                        intent.putExtras(bundle)
                        toMainAndOther(intent, context)
                    }
                }

            }
        } else {
            EventBus.getDefault().post(BackHomeEvent())
            //未登录  先去登录
            if (isOnResume) {
                //应用在前台
                intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                var bundle = Bundle()
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, msg_content.mobile_model)
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, msg_content.mobile_memory)
                bundle.putString(LoginActivity.BUNDLE_KEY_FROM_TYPE, LoginActivity.FROM_PUSH)
                bundle.putString(LoginActivity.BUNDLE_KEY_PUSH_USER_ID, msg_content.user_id)
                bundle.putString(LoginActivity.BUNDLE_KEY_RISK_RESULT, msg_content.result)
                intent.putExtras(bundle)
                toIntent(intent, context)
            } else {
                //应用没在前台
                intent = Intent(context, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                var bundle = Bundle()
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, msg_content.mobile_model)
                bundle.putString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, msg_content.mobile_memory)
                bundle.putString(LoginActivity.BUNDLE_KEY_FROM_TYPE, LoginActivity.FROM_PUSH)
                bundle.putString(LoginActivity.BUNDLE_KEY_RISK_RESULT, msg_content.result)
                bundle.putString(LoginActivity.BUNDLE_KEY_PUSH_USER_ID, msg_content.user_id)
                intent.putExtras(bundle)
                toMainAndOther(intent, context)
            }
        }
    }

    fun toIntent(intent: Intent, context: Context) {
        context.startActivity(intent)
    }

    /**
     * 先到首页 再到其他
     */
    fun toMainAndOther(intent: Intent, context: Context) {
        val mainActivityIntent = getMainActivityIntent(context)
        context.startActivities(arrayOf(mainActivityIntent, intent))
    }


    fun getMainActivityIntent(context: Context): Intent {
        val intent = Intent(context, MainActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_SINGLE_TOP
        } else {
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }
        return intent
    }
}