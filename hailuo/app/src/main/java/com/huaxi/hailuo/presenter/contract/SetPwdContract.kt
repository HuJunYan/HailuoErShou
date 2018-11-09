package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.SetPasswordBean

/**
 * Created by admin on 2018/1/29.
 */
class SetPwdContract{

    interface View:BaseView{

        fun setPwdComplete(data: SetPasswordBean?)

        fun finishActivity()
    }

    interface Presenter:BasePresenter<SetPwdContract.View>{

        fun  setPwd(pwd:String,confirmPwd:String)
    }
}