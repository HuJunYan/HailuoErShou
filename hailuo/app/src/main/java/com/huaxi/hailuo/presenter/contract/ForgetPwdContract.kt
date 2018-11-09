package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.SetPasswordBean

/**
 * Created by admin on 2018/1/29.
 */
class ForgetPwdContract {

    interface View : BaseView {
        fun getVeryCodeResult(data: Any)
        fun changeComplete()

    }

    interface Presenter : BasePresenter<ForgetPwdContract.View> {

        fun changePwd(mobile: String, code: String, new_password: String)
        fun getVeryCode(mobile: String, type: String)
    }
}