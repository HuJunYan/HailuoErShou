package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView

object MineverifyContract {

    interface View : BaseView {
//        fun showMine(t: MineBean?)
        fun processExitLoginResult()
//        fun processUserConfigResult(data : UserConfigBean?)
    }

    interface Presenter : BasePresenter<View> {
        fun loadMine()
        fun exitLogin()
        fun checkUserConfig()
    }

}