package com.huaxi.hailuo.base

/**
 * 常量类
 */
object GlobalParams {

    const val FACE_ADD_ADD_APPKEY = "79ZS4Ei9K0yihvS3aoJqTEFBGEkh5HnJ"
    const val FACE_ADD_ADD_APPSECRET = "KB8Dj_byOU_O8TcOjtszkWRKK_z8O4SY"

    const val USER_ID = "user_id" //用户id

    const val SERVICE_URL = "service_url"//客服的url

    const val INTO_IDCARDSCAN_FRONT_PAGE = 50 // 身份证正面
    const val INTO_IDCARDSCAN_BACK_PAGE = 49 // 身份证反面
    const val PAGE_INTO_LIVENESS = 51 //活体检测

    const val INTO_SELECT_GALLERY_FRONT = 52 // 用户现在图库（身份证正面）
    const val INTO_SELECT_GALLERY_BACK = 53 // 用户现在图库（身份证反面）

    const val USER_INFO_CALL_LIST = "call_list"//通话记录列表 key
    const val USER_INFO_APP_LIST = "app_list"//用户安装的app列表 key
    const val USER_INFO_MESSAGE_LIST = "message_list"//用户短信记录列表 key

    //很多页面需要 传同样的值 bundle的key
    const val BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL = "user_choose_mobile_model"
    const val BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY = "user_choose_mobile_memory"
    const val BUNDLE_KEY_USER_CHANNEL_TYPE = "channel_type"
    //氪信sdk key
    const val KEXIN_KEY = "7e9abee3b97de885eb72513b"

    //平台标识 海螺商城
    const val PLATFORM_FLAG = "hailuo"
    //友盟统计 SDK KEY
    const val YOUMENG_APP_KEY = "5b17c80df43e48594e000087"

    //腾讯bugly  APP ID
    const val BUGLY_APP_ID = "e49e50b3a5"
    const val APP_QQ_ID = "1106953520"//qq id
    const val APP_WX_ID = "wx37431d11ae7a36d3"//微信id
    const val SHARE_TO_WECHAT_SESSION = 0 //分享给朋友
    const val SHARE_TO_WECHAT_TIMELINE = 1 //分享到朋友圈
}