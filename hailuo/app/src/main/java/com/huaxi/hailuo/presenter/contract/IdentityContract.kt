package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*
import java.io.File

object IdentityContract {

    interface View : BaseView {
        fun showIdNumInfo(data: TianShenIdNumInfoBean)
        fun onSaveIdNumInfoResult()
        fun onCheckFaceResult()
        fun onOcrIdCardResult(data: IDCardBean)
    }

    interface Presenter : BasePresenter<View> {
        fun getIdNumInfo()
        fun saveIdNumInfo(saveBackIdCardDataBean: SaveBackIdCardDataBean)
        fun checkFace(type: String, real_name: String, id_num: String, delta: String, bestPath: String, envPath: String)
        fun ocrIdCard(idFile: File)
        fun ding(flag: String, result: String)
    }

}