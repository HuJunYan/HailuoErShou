package com.huaxi.hailuo.util

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.view.LayoutInflater
import com.huaxi.hailuo.R
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.OrderDetailBean
import com.tbruyelle.rxpermissions2.RxPermissions
import com.umeng.commonsdk.stateless.UMSLEnvelopeBuild.mContext
import kotlinx.android.synthetic.main.dialog_calendar_remind.view.*
import kotlinx.android.synthetic.main.dialog_start_perssion.view.*
import java.util.*


object CalendarUtil {
    val CALENDER_NOTICE = "calender_notice_"

    @SuppressLint("StaticFieldLeak")
//    private var mDialog: AlertDialog? = null
    private var mDialog: Dialog? = null
    private var calendarDialog: Dialog? = null
    private var dialog_perssion: Dialog? = null

    /**
     * 显示日历的dialog
     */
    fun showCalendarDialog(activity: Activity, orderId: String, notice: OrderDetailBean.Notice) {
        val isInCalendar = checkRepayActionIsInCalendar(activity, notice)
        if (isInCalendar) {
//            mDialog?.dismiss()
            calendarDialog?.dismiss()
            return
        }
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }

        if (calendarDialog != null) {
            calendarDialog!!.dismiss()
        }
        calendarDialog(activity, notice)
    }


    /**
     * 检查还款提醒是否已经在日历里面
     */
    @SuppressLint("MissingPermission")
    private fun checkRepayActionIsInCalendar(activity: Activity, notice: OrderDetailBean.Notice): Boolean {
        var result = false

        //判断是否有日历授权
        val rxPermissions = RxPermissions(activity)
        val isGranted = rxPermissions.isGranted(android.Manifest.permission.READ_CALENDAR)
        if (!isGranted) {
            return result
        }

        val eventCursor = activity.contentResolver.query(CalendarContract.Events.CONTENT_URI, null, null, null, null)
        try {
            if (eventCursor == null) {
                return result
            }

            val calendar = Calendar.getInstance()
            calendar.timeInMillis = notice.notice_timestamp.toLong() * 1000
            val actionDay = calendar.get(Calendar.DAY_OF_MONTH)

            if (eventCursor.count > 0) {
                eventCursor.moveToFirst()
                while (!eventCursor.isAfterLast) {
                    //日历提示的标题
                    val title = eventCursor.getString(eventCursor.getColumnIndex(CalendarContract.Events.TITLE))
                    val dtstart = eventCursor.getString(eventCursor.getColumnIndex("dtstart"))
                    calendar.timeInMillis = dtstart.toLong()
                    val dbDay = calendar.get(Calendar.DAY_OF_MONTH)
                    LogUtil.d("abc", "title-->$title----dbDay--->$dbDay")
                    if (title.contains("海螺商城") && actionDay == dbDay) {
                        LogUtil.d("abc", "系统日历已经有还款事件")
                        result = true
                        break
                    }
                    eventCursor.moveToNext()
                }
            } else {

            }
        } catch (e: Exception) {
        } finally {
            if (eventCursor != null) {
                eventCursor.close();
            }
        }
        return result
    }


    /**
     * 将提醒事件加入到系统日历中
     */
    fun saveAction2Calendar(activity: Activity, notice: OrderDetailBean.Notice) {
        val notice_title = notice.notice_title
        val notice_content = notice.notice_title
        val notice_timestamp = notice.notice_timestamp

        //服务器返回来时间戳是秒，需要转成毫秒
        val start_time = notice_timestamp.toLong() * 1000
        val end_time = notice_timestamp.toLong() * 1000 + 60 * 60 * 1000

//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = start_time
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH) + 1
//        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val calendar = Calendar.getInstance()
        calendar.timeInMillis = start_time
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val calendarIntent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI)
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, start_time)
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end_time)
        calendarIntent.putExtra(CalendarContract.Events.TITLE, notice_title)
        calendarIntent.putExtra(CalendarContract.Events.DESCRIPTION, notice_content)
        calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, CalendarContract.EXTRA_EVENT_ALL_DAY)
        try {
            activity.startActivity(calendarIntent)
        } catch (e: Exception) {
            ToastUtil.showToast(activity, "您的手机不支持该功能")
//            EventBus.getDefault().post(CalendarExceptionEvent())
        }
        LogUtil.d("http", "插入时间--day->$day")
    }

    //日历提醒的弹框
    private fun calendarDialog(activity: Activity, notice: OrderDetailBean.Notice) {
        if (calendarDialog == null) {
            calendarDialog = Dialog(activity, R.style.MyDialog)
        }
        if (calendarDialog!!.isShowing) {
            return
        }
        val mLayoutInflater = activity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view_calendar = mLayoutInflater.inflate(R.layout.dialog_calendar_remind, null, false)
        calendarDialog?.setCanceledOnTouchOutside(false)
        //设置布局
        calendarDialog?.setContentView(view_calendar)
        view_calendar.tv_dialog_calendar_no.setOnClickListener {
            calendarDialog?.dismiss()
        }
        view_calendar.tv_dialog_calendar_yes.setOnClickListener {
            if (activity != null) {
                var rxPermissions = RxPermissions(activity)
                rxPermissions.request(android.Manifest.permission.READ_CALENDAR,
                        android.Manifest.permission.WRITE_CALENDAR
                ).subscribe {
                    if (it) {
                        saveAction2Calendar(activity, notice)
                    } else {
                        ToastUtil.showToast(activity, "您已关闭海螺商城日历的授权，请到设置中开启！", ToastUtil.SHOW)
//                    isStartCalendar(activity)
                    }
                }
            }

            calendarDialog?.dismiss()
        }
        try {
            calendarDialog?.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun isStartCalendar(mActivity: Activity) {
        if (dialog_perssion == null) {
            dialog_perssion = Dialog(mActivity, R.style.MyDialog)
        }
        if (dialog_perssion!!.isShowing) {
            return
        }
        val mLayoutInflater = mActivity!!.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var view_perssion = mLayoutInflater.inflate(R.layout.dialog_start_perssion, null, false)
        dialog_perssion?.setCanceledOnTouchOutside(false)
        //设置布局
        dialog_perssion?.setContentView(view_perssion)
        view_perssion.tv_dialog_start_perssion_confirm.setOnClickListener {
            //            Tools.getAppDetailSettingIntent(mContext)
            Tools.toSelfSetting(mContext)
        }

        dialog_perssion?.show()

    }

    fun cancel() {
        calendarDialog?.dismiss()
        calendarDialog = null
    }
}
