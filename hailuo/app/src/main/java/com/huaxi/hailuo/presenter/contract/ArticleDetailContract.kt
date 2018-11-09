package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.ArticalDetailBean
import com.huaxi.hailuo.model.bean.DownRefreshBean
import com.huaxi.hailuo.model.bean.SetPasswordBean

/**
 * Created by admin on 2018/1/29.
 */
class ArticleDetailContract {

    interface View : BaseView {
        fun getArticalResult(data: ArticalDetailBean?)

    }

    interface Presenter : BasePresenter<ArticleDetailContract.View> {
        fun getArticalData(msg_id: String)

    }
}