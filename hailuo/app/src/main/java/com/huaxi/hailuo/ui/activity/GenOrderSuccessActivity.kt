package com.huaxi.hailuo.ui.activity

import android.text.Html
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.presenter.contract.GenOrderSuccessContract
import com.huaxi.hailuo.presenter.impl.GenOrderSuccessPresenter
import com.huaxi.hailuo.util.ImageUtil
import com.huaxi.hailuo.util.StatusBarUtil
import kotlinx.android.synthetic.main.activity_gen_order_success.*
import org.jetbrains.anko.startActivity

/**
 * 生成订单成功页面
 */
class GenOrderSuccessActivity : BaseActivity<GenOrderSuccessContract.View, GenOrderSuccessContract.Presenter>(), GenOrderSuccessContract.View {
    var mOrderId = ""

    companion object {
        var GEN_ORDER_ID = "order_id"
        var GEN_ORDER_SUCCESS_ICON = "order_success_icon"
        var GEN_ORDER_SUCCESS_TITLE = "order_success_title"
        var GEN_ORDER_SUCCESS_CONTENT = "order_success_content"
    }

    override fun initData() {
        val extras = intent.extras
        if (extras != null) {
            mOrderId = extras.getString(GEN_ORDER_ID, "")
            ImageUtil.load(this, extras.getString(GEN_ORDER_SUCCESS_ICON, ""), iv_gen_order_success_icon)
            tv__gen_order_success_title.text = extras.getString(GEN_ORDER_SUCCESS_TITLE, "")
            tv_gen_order_success_content.text = extras.getString(GEN_ORDER_SUCCESS_CONTENT, "")
        }
    }


    override var mPresenter: GenOrderSuccessContract.Presenter = GenOrderSuccessPresenter()

    override fun getLayout(): Int = R.layout.activity_gen_order_success


    override fun showProgress() {}

    override fun hideProgress() {
    }

    override fun showError() {
    }

    override fun showErrorMsg(msg: String) {
    }

    override fun showEmpty() {
    }


    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_gen_order_success)
        tv_gen_order_success.setOnClickListener { backActivity() }
        tv_gen_order_success_go_home.setOnClickListener { backActivity() }
        tv_gen_order_success_next.setOnClickListener {
            startActivity<OrderDetailActivity>(OrderDetailActivity.ORDERI_ID to mOrderId)
            finish()
        }
        var str = "<a>放款成功后建议您及时还款，有助于<span style='color:#3281ff'>提升额度</span>，并且<span style='color:#3281ff'>降低费率！</span></a>"
        tv_tips.text = Html.fromHtml(str)
    }


}
