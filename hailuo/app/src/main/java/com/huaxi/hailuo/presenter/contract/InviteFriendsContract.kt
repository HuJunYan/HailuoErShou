package com.huaxi.hailuo.presenter.contract

import com.huaxi.hailuo.base.BasePresenter
import com.huaxi.hailuo.base.BaseView
import com.huaxi.hailuo.model.bean.InviteFriendBean
import com.huaxi.hailuo.model.bean.LoginBean
import com.huaxi.hailuo.model.bean.SetPasswordBean

/**
 * Created by zhangliuguang on 2018/5/18.
 */
class InviteFriendsContract {

    interface View : BaseView {
        fun inviteFriendsCompelete(data: InviteFriendBean?)
        fun inviteFriendsBuridPointResult(data: Any?)
    }

    interface Presenter : BasePresenter<InviteFriendsContract.View> {
        fun inviteFriends()
        fun inviteFriendsBuridPoint(flag: String, result: String)
    }
}