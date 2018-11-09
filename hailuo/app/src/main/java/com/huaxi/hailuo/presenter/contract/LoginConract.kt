package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.LoginBean
import com.huaxi.hailuo.model.bean.RegistLoginBean
import com.huaxi.hailuo.model.bean.SignUpAgreementBean

/**
 * Created by admin on 2017/12/28.
 */
object LoginConract{

    interface View:BaseView{

        /**
         * 登录完成
         */
        fun loginCompelete(data: LoginBean)

        /**
         * 获取验证码完成
         */
        fun getVeryCodeResult(data:Any?)

        /**
         * 结束activity
         */
        fun finishActivity()

        /**
         * 注册完成
         */
        fun registComplete(data: RegistLoginBean?)

        /**
         * 修改密码
         */

        fun changeComplete()

        /**
         * 得到协议地址
         */

        fun signUpAgreementResult(data: SignUpAgreementBean?)

    }

    interface Presenter:BasePresenter<LoginConract.View>{

        /**
         * 登录
         */
        fun login(phone:String,password:String,code:String,type:Int)

        /**
         * 获取验证码
         */
        fun getVeryCode(mobile:String,type:String)

        /**
         * 注册
         */
        fun regist(mobile:String,code:String,password:String)

        /**
         * 修改密码
         */
        fun changePwd(mobile:String,code:String,new_password:String)

        /**
         *注册协议
         */
        fun signUpAgreement()
    }
}