package com.huaxi.hailuo.base

import com.huaxi.hailuo.util.DialogUtil
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


open class RxPresenter<T : BaseView> : BasePresenter<T> {

    var mView: T? = null
    var mCompositeDisposable: CompositeDisposable? = null

    override fun attachView(view: T) {
        mView = view
    }

    override fun detachView() {
        this.mView = null
        unDisposable()
    }

    fun addDisposable(disposable: Disposable) {
        if (mCompositeDisposable == null) {
            mCompositeDisposable = CompositeDisposable()
        }
        mCompositeDisposable?.add(disposable)
    }

    fun unDisposable() {
        mCompositeDisposable?.clear()
        DialogUtil.hideLoadingDialog()
    }
}