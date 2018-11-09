package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView

/**
 * Created by admin on 2018/2/3.
 */
class LoginOutContract{

    interface View:BaseView{

        fun loginOutComplete()
        fun finishActivity()

    }

    interface Presenter:BasePresenter<LoginOutContract.View>{

        fun loginOut()
    }
}