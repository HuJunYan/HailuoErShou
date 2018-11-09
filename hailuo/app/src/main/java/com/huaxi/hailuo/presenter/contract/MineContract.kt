package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.MineBean
import com.huaxi.hailuo.model.bean.UserConfigBean

object MineContract {

    interface View : BaseView {
        fun showMine(t: MineBean?)
        fun processExitLoginResult()
        fun processUserConfigResult(data : UserConfigBean?)
    }

    interface Presenter : BasePresenter<View> {
        fun loadMine()
        fun exitLogin()
        fun checkUserConfig()
    }

}