package com.huaxi.hailuo.util

import android.util.Log
import cn.jpush.android.api.JPushInterface
import com.huaxi.hailuo.base.App
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

/**
 * MessageNotificationUtil
 * @author liu wei
 * @date 2018/4/23
 */
class MessageNotificationUtil {
    companion object {
        // key 为 msgId ， value 为 消息通知 id
        // 记录推送过来的消息id 和对应的 通知id，方便阅读时关闭通知
        private val msgCenterMap = hashMapOf<String, Int>()

        fun addMessage(messageId: String, notification: Int) {
            Log.d("message", "message add")
            msgCenterMap[messageId] = notification
        }
    }

    fun register() {
        Log.d("message", "message register")
        EventBus.getDefault().register(this)
    }

    fun unregister() {
        Log.d("message", "message unregister")
        msgCenterMap.clear()
        EventBus.getDefault().unregister(this)
    }

    @Subscribe
    fun onMessageClick(event: MessageClickEvent) {
        remove(event.messageId)
    }

    fun remove(messageId: String) {
        Log.e("message", "message remove")
        try {
            val notification: Int = msgCenterMap[messageId] ?: return
            msgCenterMap.remove(messageId)

            JPushInterface.clearNotificationById(App.instance, notification)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    class MessageClickEvent(id: String) {
        var messageId = id
    }
}