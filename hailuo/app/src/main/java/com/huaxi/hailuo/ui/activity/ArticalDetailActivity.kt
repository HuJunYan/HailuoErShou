package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.text.Html
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.ArticalDetailBean
import com.huaxi.hailuo.presenter.contract.ArticleDetailContract
import com.huaxi.hailuo.presenter.impl.ArticalDetailPresenter
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.ToastUtil
import kotlinx.android.synthetic.main.activity_artical_detail.*

class ArticalDetailActivity : BaseActivity<ArticleDetailContract.View, ArticleDetailContract.Presenter>(),
        ArticleDetailContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override var mPresenter: ArticleDetailContract.Presenter = ArticalDetailPresenter()

    override fun getLayout(): Int = R.layout.activity_artical_detail

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_message_center2)
        iv_message_center_article_return.setOnClickListener {
            backActivity()
        }
        val extras = intent.extras
        if (extras != null) {
            var msg_info = extras.getString("msg_id", "")
            if (msg_info != null) {
                mPresenter.getArticalData(msg_info)
            } else {
                ToastUtil.showToast(mActivity, "数据错误")
            }
        } else {
            ToastUtil.showToast(mActivity, "数据错误")
        }

    }

    override fun initData() {
    }

    override fun getArticalResult(data: ArticalDetailBean?) {
        if (data != null) {
            tv_msg_title.text = data.msg_title
            tv_msg_time.text = data.msg_date
            tv_msg_content.text = Html.fromHtml(data.msg_content)
        }
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


}
