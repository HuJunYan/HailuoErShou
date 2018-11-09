package com.huaxi.hailuo.ui.activity

import android.content.Intent
import android.os.Bundle
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.presenter.contract.InviteFriendsContract
import com.huaxi.hailuo.presenter.impl.InviteFriendsPresenter
import kotlinx.android.synthetic.main.activity_invite_friends.*
import android.graphics.Bitmap
import android.net.Uri
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.model.bean.InviteFriendBean
import com.huaxi.hailuo.ui.view.InviteBottomDialog
import com.huaxi.hailuo.util.*


class InviteFriendsActivity : BaseActivity<InviteFriendsContract.View, InviteFriendsContract.Presenter>(), InviteFriendsContract.View {


    private var mQRBitmap: Bitmap? = null
    private var shareDescription: String? = null
    private var inviteBottomDialog: InviteBottomDialog? = null
    private var mInvitaionBean: InviteFriendBean? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override var mPresenter: InviteFriendsContract.Presenter = InviteFriendsPresenter()

    override fun getLayout(): Int = R.layout.activity_invite_friends

    override fun initView() {
        StatusBarUtil.setPaddingSmart(this, tb_invite_friends)
        //初始化webview
        initWebView()
        iv_invite_friends_return.setOnClickListener {
            backActivity()
        }


    }

    override fun inviteFriendsCompelete(data: InviteFriendBean?) {
        if (data != null) {
            mInvitaionBean = data
        }
        webview_invite_friends.loadUrl(data?.invitation_content_url)

        //好友分享
        tv_invite_my_friends.setOnClickListener {
            //邀请好友的埋点
            mPresenter.inviteFriendsBuridPoint("88", "-1")
            showShareDialog()
        }
        //跳转到现金红包页面
        tv_cash_red_packet.setOnClickListener {
            SharedPreferencesUtil.getInstance(mActivity).putString("share_link", data?.invitation_share_url)
            val intent = Intent(mActivity, CashRedPacketActivity::class.java)
            intent.putExtra("share_link", data?.invitation_share_url)
            intent.putExtra("share_desc", data?.share_desc)
            intent.putExtra("share_image", data?.share_image)
            intent.putExtra("share_title", data?.share_title)

            startActivity(intent)
        }
    }

    private fun showShareDialog() {
        if (mInvitaionBean?.invitation_share_url == null) {
            ToastUtil.showToast(this, "数据错误")
            return
        }
        mQRBitmap = QRCodeUtils.createQRCode(mInvitaionBean?.invitation_share_url)
        inviteBottomDialog = InviteBottomDialog(mActivity, RecycleShareUtils.getIUiListenerInstance(mActivity),
                mInvitaionBean?.share_title, mInvitaionBean?.share_desc, InviteBottomDialog.TYPE_NORMAL_SHARE,mInvitaionBean?.share_image)
                .setQRCodeBitmap(mQRBitmap).setShareUrl(mInvitaionBean?.invitation_share_url)
                .setShareIconResAndName(R.drawable.icon_wx_share, getString(R.string.app_name))
        inviteBottomDialog?.show()
    }

    override fun initData() {
        //邀请好友请求
        mPresenter.inviteFriends()


    }

    private fun initWebView() {
        val webSettings = webview_invite_friends.settings
        // 打开页面时， 自适应屏幕
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = true // 缩放至屏幕的大小
        // 便页面支持缩放
        webSettings.javaScriptEnabled = true //支持js
        webSettings.domStorageEnabled = true
        webSettings.setSupportZoom(true) //支持缩放
        // 设置出现缩放工具
        webSettings.builtInZoomControls = true;
//        扩大比例的缩放
        webSettings.useWideViewPort = true;
//        自适应屏幕
        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.SINGLE_COLUMN;
        webSettings.loadWithOverviewMode = true;
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE

        webview_invite_friends.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            //使用系统自带的浏览器下载
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {

            }
        }
        webview_invite_friends.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                LogUtil.d("abc", "mUrl--->" + url)
                if (url != null) {
                    if (url.startsWith("http:") || url.startsWith("https:")) {
                        view.loadUrl(url)
                    } else {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            startActivity(intent)
                        } catch (e: Exception) {
                        }
                    }
                }
                return true
            }
        }

    }

    override fun inviteFriendsBuridPointResult(data: Any?) {

    }

    override fun showProgress() {

    }

    override fun hideProgress() {

    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            val canGoBack = webview_invite_friends.canGoBack()
            if (canGoBack) {
                webview_invite_friends.goBack()
            } else {
                backActivity()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (webview_invite_friends != null) {
            val parent = webview_invite_friends.parent as ViewGroup
            parent.removeView(webview_invite_friends)
            webview_invite_friends.removeAllViews()
            webview_invite_friends.destroy()
        }
    }
}
