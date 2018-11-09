package com.huaxi.hailuo.ui.activity

import android.content.Intent
import android.net.Uri
import android.view.KeyEvent
import android.view.ViewGroup
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.log.LogUtil
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_web.*
import org.jetbrains.anko.startActivity

/**
 * H5页面
 * Created by wang on 2017/12/5.
 */
class WebActivity : SimpleActivity() {
    var mUrl: String? = null
    override fun getLayout(): Int = R.layout.activity_web

    companion object {
        var WEB_URL_KEY = "web_url_key"
        var WEB_URL_TITLE = "web_url_title"
    }

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_web)
        tv_web_back.setOnClickListener { checkCanGoback() }
        initWebView()
    }


    override fun initData() {
        mUrl = intent.getStringExtra(WEB_URL_KEY)
        var title = intent.getStringExtra(WEB_URL_TITLE)
        tv_web_title.text = title
        wv_web.loadUrl(mUrl)
    }

    private fun checkCanGoback() {
        if (wv_web.canGoBack()) {
            wv_web.goBack()
        } else {
            backActivity()
        }
    }

    override fun backActivity() {
        if (App.instance.isBackground()) {
            startActivity<LauncherActivity>()
        }
        super.backActivity()
    }

    private fun initWebView() {
        val webSettings = wv_web.settings
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

        wv_web.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            //使用系统自带的浏览器下载
            try {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
            } catch (e: Exception) {

            }
        }
        wv_web.webViewClient = object : WebViewClient() {
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


    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
            val canGoBack = wv_web.canGoBack()
            if (canGoBack) {
                wv_web.goBack()
            } else {
                backActivity()
            }
            return true
        }
        return super.onKeyDown(keyCode, event)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (wv_web != null) {
            val parent = wv_web.parent as ViewGroup
            parent.removeView(wv_web)
            wv_web.removeAllViews()
            wv_web.destroy()
        }
    }
}