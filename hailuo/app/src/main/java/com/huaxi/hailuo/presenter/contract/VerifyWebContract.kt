package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView

/**
 * Created by admin on 2018/1/12.
 * \(^o^)/
 */

class VerifyWebContract{

    interface View:BaseView{

        fun collectionComplete()

        fun  unCollectionComplete()

    }

    interface Presenter:BasePresenter<VerifyWebContract.View>{

        fun collectionArt(article_id:String)

        fun unCollection(article_id:String)
    }
}
