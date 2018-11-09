package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.UpgradeBean

object LauncherContract {

    interface View : BaseView {
        fun checkUpdateResult(data: UpgradeBean)
    }

    interface Presenter : BasePresenter<View> {
        fun checkUpdate()
    }
}