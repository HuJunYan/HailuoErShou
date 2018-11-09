package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.*
import java.util.HashMap

/**
 * Created by admin on 2018/1/16.
 */
object AuthExtroContactsContract {

    interface View : BaseView {
        fun processExtroData(data: ExtroContactsBean?)
        fun processUploadContactsResult()
        fun processUploadResult()
    }

    interface Presenter : BasePresenter<View> {
        fun getExtroContacts()
        fun saveExtroContacts(map: HashMap<String, ExtroContactsBean.EmeContacts>)
        fun uploadContacts(list: List<ContactsBean>)

    }
}