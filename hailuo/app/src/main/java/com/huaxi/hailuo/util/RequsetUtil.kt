package com.huaxi.hailuo.util

import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.GlobalParams
import okhttp3.RequestBody
import org.json.JSONObject

object RequsetUtil {

    fun getRequestBody(jsonObject: JSONObject): RequestBody {

        val rootJsonObject = JSONObject()

        val timestamp = System.currentTimeMillis().toString()
        val version = Utils.getVersion(App.instance)
        val token = UserUtil.getToken(App.instance)
        val user_id = UserUtil.getUserId(App.instance)
        val device_id = UserUtil.getDeviceId()
        val mobile = UserUtil.getMobile(App.instance)

        rootJsonObject.put("type", "1") //客户端类型0h5,1android,2ios,3winphone
        rootJsonObject.put("platform", GlobalParams.PLATFORM_FLAG)
        rootJsonObject.put("token", token) //token
        rootJsonObject.put("version", version)
        rootJsonObject.put("timestamp", timestamp)
        rootJsonObject.put(GlobalParams.USER_ID, user_id)
        rootJsonObject.put("mobile", mobile)
        rootJsonObject.put("device_id", device_id)
        rootJsonObject.put("data", jsonObject)
        val strEntity = rootJsonObject.toString()
        return RequestBody.create(okhttp3.MediaType.parse("application/json;charset=UTF-8"), strEntity)
    }

}