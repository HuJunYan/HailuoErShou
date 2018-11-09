package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.SpeekBean

/**
 * Created by hjy on 2018/1/30.
 */
class OpionContract {
    interface View : BaseView {

        fun upLoadComplete()

        fun getFeedBackResult(data: SpeekBean?)


    }

    interface Presenter : BasePresenter<OpionContract.View> {

        //得到我要吐槽的选项
        fun getFeedBack()

        /**
         * 上传意见
         */
        fun upLoadOpion(feed_type: String, feed_content: String, file: String)

        //上传图片不带图片
        fun upLoadOpionWithoutImage(feed_type: String, feed_content: String, file: String)

    }
}