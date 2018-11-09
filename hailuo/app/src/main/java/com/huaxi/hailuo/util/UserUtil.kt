package com.huaxi.hailuo.util

import android.content.Context
import android.os.Build
import android.text.TextUtils
import com.huaxi.hailuo.base.App
import java.util.*

object UserUtil {

    private val DEVICE_ID: String = "device_id"
    private val TOKEN: String = "token"
    private val USER_ID: String = "user_id"
    private val MOBILE: String = "mobile"
    private val JPUSH_ID: String = "jpush_id"
    private val IS_MEMBER: String = "is_member"
    private val IS_WHITE: String = "is_white"
    private val USER_SERVICE_URL = "user_service_url"
    private val LOCATION = "user_location"
    private val PROVINCE = "user_province"
    private val CITY = "user_city"
    private val COUNTRY = "user_county"
    private val ADDRESS = "user_address"
    private val TYPE_HELP_CENTER: String = "1"
    private val TYPE_USE_RULE: String = "2"
    private val IS_EDIT_IDENTITY: String = "is_edit_identity"
    private val IS_FIRST: String = "is_first"

    /**
     * 得到设备ID
     */
    fun getDeviceId(): String {

        var uniquePsuedoID = SharedPreferencesUtil.getInstance(App.instance).getString(DEVICE_ID)
        if (!TextUtils.isEmpty(uniquePsuedoID)) {
            return uniquePsuedoID
        }
        /**
         * Return pseudo unique ID
         * @return ID
         */
        // If all else fails, if the user does have lower than API 9 (lower
        // than Gingerbread), has reset their device or 'Secure.ANDROID_ID'
        // returns 'null', then simply the ID returned will be solely based
        // off their Android device information. This is where the collisions
        // can happen.
        // Thanks http://www.pocketmagic.net/?p=1662!
        // Try not to use DISPLAY, HOST or ID - these items could change.
        // If there are collisions, there will be overlapping data
        val m_szDevIDShort = "35" + Build.BOARD.length % 10 +
                Build.BRAND.length % 10 +
                Build.CPU_ABI.length % 10 +
                Build.DEVICE.length % 10 +
                Build.MANUFACTURER.length % 10 +
                Build.MODEL.length % 10 +
                Build.PRODUCT.length % 10

        // Thanks to @Roman SL!
        // http://stackoverflow.com/a/4789483/950427
        // Only devices with API >= 9 have android.os.Build.SERIAL
        // http://developer.android.com/reference/android/os/Build.html#SERIAL
        // If a user upgrades software or roots their device, there will be a duplicate entry
        var serial: String? = null
        try {
            serial = android.os.Build::class.java.getField("SERIAL").get(null).toString()
            // Go ahead and return the serial for api => 9
            uniquePsuedoID = UUID(m_szDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        } catch (exception: Exception) {
            // String needs to be initialized
            serial = "serial" // some value
            uniquePsuedoID = UUID(m_szDevIDShort.hashCode().toLong(), serial!!.hashCode().toLong()).toString()
        }
        // Thanks @Joe!
        // http://stackoverflow.com/a/2853253/950427
        // Finally, combine the values we have found by using the UUID class to create a unique identifier
        uniquePsuedoID = uniquePsuedoID.replace("-", "")
        saveDeviceId(uniquePsuedoID)
        return uniquePsuedoID
    }

    /**
     *得到用户是否是第一次打开app
     */
    fun isFirst(context: Context): Boolean =
            SharedPreferencesUtil.getInstance(context).getBoolean(IS_FIRST)

    /**
     * 保存用户是是第一次打开app
     */
    fun setIsFirst(context: Context, isFirst: Boolean) {
        SharedPreferencesUtil.getInstance(context).putBoolean(IS_FIRST, isFirst)
    }

    /**
     * 保存设备ID
     */
    fun saveDeviceId(device_id: String) {
        SharedPreferencesUtil.getInstance(App.instance).putString(DEVICE_ID, device_id)
    }

    /**
     * 保存Token
     */
    fun saveToken(context: Context, token: String) {
        SharedPreferencesUtil.getInstance(context).putString(TOKEN, token)
    }

    /**
     * 得到Token
     */
    fun getToken(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(TOKEN)


    /**
     * 得到设备ID
     */
    fun getUserId(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(USER_ID)

    /**
     * 保存user_id
     */
    fun saveUserId(context: Context, user_id: String) {
        SharedPreferencesUtil.getInstance(context).putString(USER_ID, user_id)
    }

    /**
     * 得到手机号
     */
    fun getMobile(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(MOBILE)

    /**
     * 保存是否可编辑身份证好的状态
     */
    fun saveIsEditIdentity(context: Context, is_edit_identity: String) {
        SharedPreferencesUtil.getInstance(context).putString(IS_EDIT_IDENTITY, is_edit_identity)
    }


    /**
     * 得到是否可编辑身份证好的状态
     */
    fun getIsEditIdentity(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(IS_EDIT_IDENTITY)

    /**
     * 保存手机号
     */
    fun saveMobile(context: Context, mobile: String) {
        SharedPreferencesUtil.getInstance(context).putString(MOBILE, mobile)
    }


    /**
     * 获取用户jpush_id
     */
    fun getUserJPushId(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(JPUSH_ID)

    fun saveUserJPushId(context: Context, jpush_id: String) {
        SharedPreferencesUtil.getInstance(context).putString(JPUSH_ID, jpush_id)
    }

    /**
     *得到用户会员状态
     */
    fun getUserIsMember(context: Context): Boolean =
            SharedPreferencesUtil.getInstance(context).getBoolean(IS_MEMBER)

    /**
     * 保存用户是否是会员
     */
    fun saveUserIsMember(context: Context, isMember: Boolean) {
        SharedPreferencesUtil.getInstance(context).putBoolean(IS_MEMBER, isMember)
    }

    /**
     *得到用户白名单
     */
    fun getUserIsWhite(context: Context): Boolean =
            SharedPreferencesUtil.getInstance(context).getBoolean(IS_WHITE)

    /**
     * 保存用户是否是白名单
     */
    fun saveUserIsWhite(context: Context, isWhite: Boolean) {
        SharedPreferencesUtil.getInstance(context).putBoolean(IS_WHITE, isWhite)
    }

    /**
     *得到用户协议url
     */
    fun getUserServiceUrl(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(USER_SERVICE_URL)

    /**
     * 保存用户协议url
     */
    fun saveUserServiceUrl(context: Context, isWhite: String) {
        SharedPreferencesUtil.getInstance(context).putString(USER_SERVICE_URL, isWhite)
    }

    /**
     *得到用户Location
     */
    fun getLocation(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(LOCATION)

    /**
     * 保存用户Location
     */
    fun saveLocation(context: Context, location: String) {
        SharedPreferencesUtil.getInstance(context).putString(LOCATION, location)
    }

    /**
     *得到用户省份
     */
    fun getProvince(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(PROVINCE)

    /**
     * 保存用户省份
     */
    fun saveProvince(context: Context, province: String) {
        SharedPreferencesUtil.getInstance(context).putString(PROVINCE, province)
    }

    /**
     *得到用户城市
     */
    fun getCity(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(CITY)

    /**
     * 保存用户城市
     */
    fun saveCity(context: Context, city: String) {
        SharedPreferencesUtil.getInstance(context).putString(CITY, city)
    }

    /**
     *得到用户区县
     */
    fun getCountry(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(COUNTRY)

    /**
     * 保存用户区县
     */
    fun saveCountry(context: Context, country: String) {
        SharedPreferencesUtil.getInstance(context).putString(COUNTRY, country)
    }

    /**
     *得到用户地址
     */
    fun getAddress(context: Context): String =
            SharedPreferencesUtil.getInstance(context).getString(ADDRESS)

    /**
     * 保存用户地址
     */
    fun saveAddress(context: Context, address: String) {
        SharedPreferencesUtil.getInstance(context).putString(ADDRESS, address)
    }

    /**
     * 判断是否登录
     */
    fun isLogin(context: Context): Boolean = !TextUtils.isEmpty(getToken(context))

    /**
     * 清除所有的用户信息
     */
    fun clearUser(context: Context) {
        /*--------  用户协议 不清除-------------*/
        val userServiceUrl = getUserServiceUrl(context)
        SharedPreferencesUtil.getInstance(context).clearSp()
        saveUserServiceUrl(context, userServiceUrl)
        /*--------  用户协议 不清除-------------*/
    }


}