package com.huaxi.hailuo.util

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.view.LayoutInflater
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.log.LogUtil
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.dialog_service_error.view.*
import kotlinx.android.synthetic.main.dialog_update.view.*
import me.drakeet.materialdialog.MaterialDialog


object DialogUtil {

    var mLoadingDialog: Dialog? = null

    /**
     * 显示加载中dialog
     */
    fun showLoadingDialog() {

        if (mLoadingDialog != null && mLoadingDialog!!.isShowing) {
            return
        }

        val activity = App.instance.getCurrentActivity()
        val mLayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.view_progress, null, false)
        mLoadingDialog = Dialog(activity, R.style.loading_dialog)
        mLoadingDialog?.ownerActivity = activity
        mLoadingDialog?.setContentView(view)
        mLoadingDialog?.setCanceledOnTouchOutside(false)
        mLoadingDialog?.setCancelable(false)
        mLoadingDialog?.show()

    }

    /**
     * 隐藏加载中dialog
     */
    fun hideLoadingDialog() {
       /* val ownerActivity = mLoadingDialog?.ownerActivity
        if (ownerActivity == null || ownerActivity.isFinishing) {
            return
        }*/
        try {
            mLoadingDialog?.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 显示服务器维护dialog
     */
    fun showServiceErrorDialog() {
        val activity = App.instance.getCurrentActivity()
        val mLayoutInflater = activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = mLayoutInflater.inflate(R.layout.dialog_service_error, null, false)
        val mDialog = Dialog(activity, R.style.ErrorDialog)
        mDialog.setContentView(view)
        mDialog.setCanceledOnTouchOutside(false)
        mDialog.setCancelable(false)
        view.iv_dialog_error_close.setOnClickListener {
            mDialog.dismiss()
            App.instance.exitApp()
        }
        mDialog.show()
    }

    /**
     * 显示提示更新APP的dialog
     */
    fun showUpdateDialog(download_url: String) {
        val activity = App.instance.getCurrentActivity()
        val alert = MaterialDialog(activity)
        alert.setTitle("版本更新")
                .setPositiveButton("下载", {
                    val rxPermissions = RxPermissions(activity)
                    rxPermissions.request(Manifest.permission.WRITE_EXTERNAL_STORAGE).subscribe { aBoolean ->
                        if (aBoolean) {
                            showDownLoadDialog(download_url)
                            alert.dismiss()
                        } else {
                            ToastUtil.showToast(activity, "请打开存储权限")
                        }
                    }
                })
                .setNegativeButton("忽略", {
                    alert.dismiss()
                    App.instance.exitApp()
                })
                .setCanceledOnTouchOutside(false)
                .show()
    }

    /**
     * 显示下载更新dialog
     */
    private fun showDownLoadDialog(download_url: String) {
        val activity = App.instance.getCurrentActivity()
        val path = Utils.getDownLoadAppPath()
        val layoutInflater = LayoutInflater.from(activity)
        val contentView = layoutInflater.inflate(R.layout.dialog_update, null)

        val alert = MaterialDialog(activity)
        alert.setTitle("正在下载")
        alert.setContentView(contentView)
                .setCanceledOnTouchOutside(false)
                .show()
        val progressBar = contentView.number_progress_bar

        FileDownloader.setup(activity)
        FileDownloader.getImpl().create(download_url)
                .setPath(path, true)
                .setForceReDownload(true)
                .setListener(object : FileDownloadListener() {
                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun connected(task: BaseDownloadTask?, etag: String?, isContinue: Boolean, soFarBytes: Int, totalBytes: Int) {
                        progressBar.max = totalBytes

                        LogUtil.d("abc", "connected-->")
                    }

                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                        progressBar.progress = soFarBytes

                        LogUtil.d("abc", "progress-->")
                    }

                    override fun blockComplete(task: BaseDownloadTask?) {

                        LogUtil.d("abc", "blockComplete-->")
                    }

                    override fun retry(task: BaseDownloadTask?, ex: Throwable?, retryingTimes: Int, soFarBytes: Int) {
                    }

                    override fun completed(task: BaseDownloadTask) {
                        val targetFilePath = task.targetFilePath
                        Utils.installApk(activity, targetFilePath)
                    }

                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {
                    }

                    override fun error(task: BaseDownloadTask, e: Throwable) {

                        LogUtil.d("abc", "error-->" + e.message)
                    }

                    override fun warn(task: BaseDownloadTask) {
                        LogUtil.d("abc", "warn-->")
                    }
                }).start()
    }

    fun cancleLoadingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog?.dismiss()
        }
    }


}