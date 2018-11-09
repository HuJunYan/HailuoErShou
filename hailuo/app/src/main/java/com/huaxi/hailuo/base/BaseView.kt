package com.huaxi.hailuo.base

/**
 * View基类
 */
interface BaseView {

    fun showProgress()

    fun hideProgress()

    fun showError()

    fun showErrorMsg(msg: String)

    fun showEmpty()

}