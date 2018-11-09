package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * Created by admin on 2018/1/16.
 */
object CreditAssessmentContract {

    interface View : BaseView {
        fun getCreditAssessResult(data: CreditAssessBean?, isNeedJump: Boolean)

        fun processSubmitCreditResult()
        fun processLastSmsTime(t: LastSmsTimeBean?, isFirst: Boolean)
        fun processSubmitPhoneInfoResult()
    }

    interface Presenter : BasePresenter<View> {
        fun getCreditAssessData(isNeedJump: Boolean, dialog: Boolean = true)
        fun submitCredit(choose_mobile: String, choose_memory: String)
        fun getLastSmsTime(isFirst: Boolean)
        fun submitPhoneInfo(data: JSONObject, deviceId: String)
    }
}