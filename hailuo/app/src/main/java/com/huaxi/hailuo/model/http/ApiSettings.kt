package com.huaxi.hailuo.model.http

object ApiSettings {

    //检查版本更新
    const val CHECK_UPGRADE = "checkUpdate"
    //用户注册
    const val SIGN_UP = "signUp"
    //用户登录
    const val SIGN_IN = "signIn"
    //退出登录
    const val SIGN_OUT = "signOut"
    //重置密码
    const val RESET_PASSWORD = "resetPassword"
    //设置密码
    const val SET_PASSWORD = "setPassword"
    //注册协议
    const val SIGN_UP_AGREEMENT = "signUpAgreement"
    //发送验证码
    const val SEND_CODE = "sendCode"
    //首页
    const val HOME_PAGE = "homePage"
    //马上评估
    const val ASSESS = "assess"
    //信用评估
    const val CREDIT_ASSESS = "creditAssess"
    //图片上传
    const val UPLOAD_IMAGE = "uploadImage"
    //获取省份列表
    const val GET_PROVINCE = "getProvince"
    //获取城市列表
    const val GET_CITY = "getCity"
    //得到银行卡列表
    const val GET_BANK_LIST = "getBankList"
    //绑定银行卡
    const val BIND_BANK_CARD = "bindBankCard"
    //获取紧急联系人信息
    const val GET_EXTRO_CONTACTS = "getExtroContacts"
    //保存紧急联系人信息
    const val SAVE_EXTRO_CONTACTS = "saveExtroContacts"
    //获取用户个人信息
    const val GET_USER_INFO = "getUserInfo"
    //保存用户个人信息
    const val SAVE_USER_INFO = "saveUserInfo"
    //信用认证提交
    const val CREDIT_APPLY = "creditApply"
    //手机评估页面-下拉刷新
    const val DOWN_REFRESH = "downRefresh"
    //riskWaitingFeedback
    const val RISK_WAITING_FEED_BACK = "riskWaitingFeedback"

    //评估结果成功页面
    const val ASSESS_SUCCESS = "assessSuccess"
    //评估结果成功提交-生成订单
    const val GEN_ORDER = "genOrder"
    //获取订单详情
    const val GET_ORDER_INFO = "getOrderInfo"
    //获取退款信息
    const val GET_REFUND_INFO = "getRefundInfo"
    //获取确认退款
    const val CONFIRM_REFUND = "confirmRefund"
    //回收记录-订单记录
    const val ORDER_RECORD = "orderRecord"
    //得到用户已经绑定过的银行卡列表
    const val GET_USER_BANK_LIST = "getUserBankList"
    //删除银行卡
    const val DEL_BANK_CARD = "delBankCard"
    //帮助中心
    const val HELP_CENTER = "helpCenter"
    //意见反馈
    const val SUBMIT_FEEDBACK = "submitFeedback"
    //上传通讯录
    const val SAVE_CONTACTS = "saveContacts"
    //上传短信
    const val SAVE_PHONE_INFO = "savePhoneInfo"
    //获取最后一次短信上传时间
    const val GET_LAST_SMS_TIME = "getLastSmsTime"
    //获取区域列表
    const val GET_COUNTY = "getCounty"
    //上传用户地址
    const val USER_LOCATION = "userLocation"
    //得到手机型号列表
    const val GET_MOBILE_LIST = "getMobileList"
    //得到手机型号列表
    const val FORGET_PWD = "forgetPwd"
    //得到客服url
    const val MINE = "mine"
    //orc身份证
    const val OCR_IDCARD = "v1/ocridcard"
    //人脸对比
    const val VERIFY = "v2/verify"
    //优惠券列表
    const val GET_COUPON_LIST = "getCouponList"
    //检查是否能使用优惠券
    const val CHECK_USE_COUPON = "checkUseCoupon"
    //获取还款可用优惠券
    const val GET_REFUND_COUPON_LIST = "getRefundCouponList"
    //获取消息中心列表
    const val GET_MESSAGE_CENTER_LIST = "getMsgCenterList"
    //设置消息已读
    const val READ_MESSAGE = "readMsg"
    //埋点
    const val BURIED_POINT = "buriedPointHailuo"
    const val BURIED_POINT_UCENTER = "buriedPoint"
    //获取获取checkOldUserStatus
    const val CHECK_OLD_USER_STATUS = "checkOldUserStatus"
    //得到我要吐槽
    const val GET_FEED_BACK = "getFeedback"
    //首页认证优惠券弹框
    const val HOME_COUPON_DIALOG = "homeCouponDialog"
    //得到用户身份认证信息
    const val GET_IDNUM_INFO = "getIdNumInfo"
    //保存用户身份认证信息
    const val SAVE_IDNUM_INFO = "saveIdNumInfo"
    //校验身份一致性并保存
    const val CHECK_FACE = "checkFace"
    //消息详情页
    const val GET_MSG_INFO = "getMsgInfo"
    //邀请好友
    const val GET_INVITATION_FRIENDS = "getInvitationFriends"
    //我的现金红包数据展示
    const val MYREDPACKET = "myRedPacket"
    //我的现金红包收入明细/提现记录列表
    const val GET_RED_PACKET_MONEY_LIST = "getRedPacketMoneyList"
    //我的现金红包提现
    const val GET_WITHDRAW = "withdrawRedPacketMoney"

}