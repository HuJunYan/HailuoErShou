package com.huaxi.hailuo.ui.activity

import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.text.TextUtils
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.base.GlobalParams
import com.huaxi.hailuo.event.BackHomeEvent
import com.huaxi.hailuo.event.BindBankEvent
import com.huaxi.hailuo.event.RefreshCreditStatusEvent
import com.huaxi.hailuo.model.bean.*
import com.huaxi.hailuo.presenter.contract.BindbankCardContract
import com.huaxi.hailuo.presenter.impl.BindBankCardPresenter
import com.huaxi.hailuo.util.RegexUtil
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.TimeCount
import com.huaxi.hailuo.util.ToastUtil
import com.jakewharton.rxbinding2.view.RxView
import kotlinx.android.synthetic.main.activity_bind_bank_card.*
import org.greenrobot.eventbus.EventBus
import java.util.*
import java.util.concurrent.TimeUnit

class BindBankCardActivity : BaseActivity<BindbankCardContract.View, BindbankCardContract.Presenter>(), BindbankCardContract.View {

    private var mProvincePosition: Int = 0
    private var mProvinceMomentPosition: Int = 0
    private var mMProvinceData: ArrayList<String> = ArrayList<String>()
    private var mCityData: ArrayList<String> = ArrayList<String>()
    private var mBankList: ArrayList<String> = ArrayList<String>()
    private var mTitle: String? = null
    private var mTimer: TimeCount? = null
    private var mProvinceBean: ProvinceBean? = null
    private var mBankData: BankListBean? = null
    var mLastHeightDifferece: Int = 0;
    private var mCity: String? = null
    private var mCityId: String? = null // 城市id
    private var mCityBean: CityBean? = null
    private var mProvinceId: String? = null // 身份id
    private var mProvince: String? = null //省份
    private var mBankId: String? = null //银行id
    private var mCityPosition: Int = 0
    private var mBankName: String? = null  // 银行名称
    private var mobile_memory: String? = ""
    private var mobile_model: String? = ""
    private var channel_type: String? = ""
    private var mMobile_num: String? = ""
    private var mRedPacketType: String? = ""
    private var mRedPacketTitle: String? = ""
    private var mWithdrawMoney: String? = ""
    private var handler: Handler? = null
    private var mMoney: Double? = 0.00
    override fun processGenOrderResult(t: GenOrderBean?) {
        if (t != null) {
            var bundle = Bundle()
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_ID, t.order_id)
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_SUCCESS_CONTENT, t.order_success_content)
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_SUCCESS_ICON, t.order_success_icon)
            bundle.putString(GenOrderSuccessActivity.GEN_ORDER_SUCCESS_TITLE, t.order_success_title)
            gotoActivity(mActivity, GenOrderSuccessActivity::class.java, bundle)
        }
    }

    override fun processGenOrderFinish() {
        EventBus.getDefault().post(BackHomeEvent())
        finish()
    }

    override var mPresenter: BindbankCardContract.Presenter = BindBankCardPresenter()

    override fun getLayout(): Int = R.layout.activity_bind_bank_card

    companion object {
        public val TITLE: String = "title"
        public val Money: String = "money"
    }

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
        StatusBarUtil.setPaddingSmart(mActivity, tb_bind_my_card)
        val intent = intent
        if (intent != null) {
            mTitle = intent.getStringExtra(TITLE)
            mWithdrawMoney = intent.getStringExtra(Money)
        }

        val extras = intent.extras
        if (extras != null) {
            mobile_model = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MODEL, "android")
            mobile_memory = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHOOSE_MOBILE_MEMORY, "64G")
            channel_type = extras.getString(GlobalParams.BUNDLE_KEY_USER_CHANNEL_TYPE, "")
        }
        if (!TextUtils.isEmpty(mTitle)) {
            tv_bank_card_title.text = mTitle
        }
        tv_bank_card_back.setOnClickListener({
            backActivity()
        })
        tv_severity_code.setOnClickListener({
            val mobile_number = et_bank_card_phone_num.text.toString().trim()
            if (!RegexUtil.IsTelephone(mobile_number)) {
                ToastUtil.showToast(mActivity, "请输入正确的手机号")
                return@setOnClickListener
            }

            mTimer = TimeCount(tv_severity_code, 60000, 1000, "重新获取", true, R.drawable.shape_bind_card)
            mTimer!!.start()

            mPresenter.getVefiryCode(mobile_number);
        })
        if (!TextUtils.isEmpty(mWithdrawMoney)) {
            now_bind.text = "提现"
        }
        rl_bank_card.setOnClickListener { mPresenter.getBankListData() }
        rl_address.setOnClickListener { mPresenter.getProvinceData() }
        RxView.clicks(now_bind).throttleFirst(5, TimeUnit.SECONDS).subscribe {
            checkConditionToBindBankCard()
        }
//        now_bind.setOnClickListener { checkConditionToBindBankCard() }
//
////        /*---------------------start适配软键盘弹起 引发的布局变化  只是一个合适的适配方案 不是完美适配的方案------------------------------------*/
//        ll_bind_bank_root.getViewTreeObserver().addOnGlobalLayoutListener(
//                {
//                    val r = Rect()
//                    ll_bind_bank_root.getWindowVisibleDisplayFrame(r)
//                    val screenHeight = ll_bind_bank_root.getRootView().getHeight()
//                    val statusBarHeight = StatusBarUtil.getStatusBarHeight(mActivity)
//                    val navigationBarHeight = StatusBarUtil.getNavigationBarHeight()
//                    val heightDifference = screenHeight - (r.bottom - r.top) - statusBarHeight - navigationBarHeight
//                    if (heightDifference > screenHeight / 4 && heightDifference != mLastHeightDifferece) {
//                        mLastHeightDifferece = heightDifference
//                        ll_activity.animate().translationY(-resources.displayMetrics.density * 30).setDuration(200).start()
//                    } else if (heightDifference <= 0 && heightDifference != mLastHeightDifferece) {
//                        mLastHeightDifferece = heightDifference
//                        ll_activity.animate().translationY(0F).setDuration(200).start()
//                    }
//                }
//        )
//        /*---------------------end适配软键盘弹起 引发的布局变化  只是一个合适的适配方案 不是完美适配的方案--------------------------------------*/
    }


    override fun initData() {
        mPresenter.getUserInfo()
    }

    override fun getUserInfoResult(data: UserInfoBean?) {
        val user_name = data?.user_name
        val user_mobile = data?.user_mobile
        et_card_user_name.setText(user_name)
        et_bank_card_phone_num.setText(user_mobile)
    }

    private fun checkConditionToBindBankCard() {

        if (TextUtils.isEmpty(mBankId) || TextUtils.isEmpty(mBankName)) {
            ToastUtil.showToast(mActivity, "请选择银行卡所属银行")
            return
        }
        if (TextUtils.isEmpty(mProvinceId) || TextUtils.isEmpty(mCityId)) {
            ToastUtil.showToast(mActivity, "请选择开户行所在省市")
            return
        }
        val card_num = et_card_num.text.toString().trim()
        if (TextUtils.isEmpty(card_num)) {
            ToastUtil.showToast(mActivity, "请输入您的收款银行卡号")
            return
        }

        val verify_code = et_severity_code.text.toString().trim()
        if (TextUtils.isEmpty(verify_code)) {
            ToastUtil.showToast(mActivity, "请输入验证码")
            return
        }

        val card_user_name = et_card_user_name.text.toString().trim()
        val phone_num = et_bank_card_phone_num.text.toString().trim()
        //绑定银行卡  同时调用绑卡和提现的接口
        mPresenter.bindBankCard(card_user_name, mBankId!!, mBankName!!, mProvinceId!!, mCityId!!, card_num, phone_num, verify_code)
        //用户提现的时候没有绑定银行卡时候
        if (!TextUtils.isEmpty(mWithdrawMoney)) {
            mMoney = mWithdrawMoney?.toDouble()
            mPresenter.myCashWithdraw((mMoney?.toInt()!! * 100).toString(), "")
        }


    }

    //提现成功  三秒后返回我的金红包页面
    override fun myCashWithdrawResult(data: Any?) {
        handler = Handler()
        ToastUtil.showToast(mActivity, "提现成功")
//        var timer = Timer()
//        var task = timerTask {
//            run {
//                finish()
////                startActivity<CashRedPacketActivity>()
//            }
//        }
//        timer.schedule(task, 3000)
        EventBus.getDefault().post(Any())

        handler!!.postDelayed({ finish() }, 3000)
    }

    //处理银行卡列表信息
    override fun getBankListDataResult(data: BankListBean?) {
        if (data == null) {
            return
        }
        mBankData = data
        parserBankListData(data)
        showBankDialog(mBankList!!)

    }

    private fun parserBankListData(data: BankListBean) {
        val datas = mBankData!!.bank_list
        mBankList.clear()
        for (i in datas.indices) {
            val data = datas.get(i)
            val bank_name = data.bank_name
            mBankList.add(bank_name)
        }
    }

    /**
     * 显示银行弹窗
     */
    fun showBankDialog(list: List<String>) {


        val dialogView = View.inflate(mActivity, R.layout.dialog_listview, null)
        val arrayAdapter = ArrayAdapter<String>(mActivity, android.R.layout.simple_list_item_1)
        if (list.size > 0) {
            for (i in list.indices) {
                arrayAdapter.add(list[i])
            }
        }
        val mListView = dialogView.findViewById<ListView>(R.id.listview)
        mListView.setDividerHeight(0)
        mListView.setAdapter(arrayAdapter)


        val builder = AlertDialog.Builder(mActivity)
                .setView(dialogView)
                .setCancelable(true)

        val alertDialog = builder.create()

        alertDialog.setTitle("请选择银行")
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)

        mListView.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ ->
            mBankId = mBankData?.bank_list?.get(i)?.bank_id
            mBankName = mBankData?.bank_list?.get(i)?.bank_name
            tv_bank_card.text = mBankName
            alertDialog.dismiss()
        }
        alertDialog.show()
    }


    override fun bindBankCardComplete() {
        EventBus.getDefault().post(RefreshCreditStatusEvent())
        EventBus.getDefault().post(BindBankEvent())

        if (tv_bank_card_title.text.toString() == "更换银行卡") {
            ToastUtil.showToast(mActivity, "更换银行卡成功")
            finish()
        } else if (tv_bank_card_title.text.toString() == "收款银行卡") {
            //绑定银行卡成功之后下单
            mMobile_num = et_bank_card_phone_num.text.toString().trim()
            mPresenter.genOrder(mobile_model!!, mobile_memory!!, channel_type!!, mMobile_num!!)
        } else if (tv_bank_card_title.text.toString() == "添加银行卡") {
            ToastUtil.showToast(mActivity, "绑卡成功")
            finish()
        }
        var mIntent = intent
        if (mIntent != null) {
            mRedPacketType = mIntent.getStringExtra("bind_bank_type")
            mRedPacketTitle = mIntent.getStringExtra("bind_bank_title")
        }
    }

    override fun getProviceResult(data: ProvinceBean?) {
        if (data == null) {
            ToastUtil.showToast(this, "数据错误")
            return
        }
        mProvinceBean = data
        parserProvinceListData(data)
    }

    override fun getCityResult(data: CityBean?) {
        if (data == null) {
            ToastUtil.showToast(this, "数据错误")
            return
        }
        mCityBean = data
        parserCityListData(mCityBean!!)
        showCityDialog(mCityData)
    }

    /**
     * 解析城市数据
     *
     * @param mCityBean 城市数据源
     */
    private fun parserCityListData(mCityBean: CityBean) {
        val datas = mCityBean.cityList
        mCityData.clear()
        for (i in datas.indices) {
            val data = datas[i]
            val city_name = data.city_name
            mCityData.add(city_name)
        }
    }

    fun showCityDialog(list: List<String>) {
        val dialogView = View.inflate(this, R.layout.dialog_listview, null)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        if (list.size > 0) {
            for (i in list.indices) {
                arrayAdapter.add(list[i])
            }
        }

        val builder = AlertDialog.Builder(mActivity)
                .setView(dialogView)
                .setCancelable(true)
        val alertDialog = builder.create()

        alertDialog.setTitle("请选择城市")
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        var mListView = dialogView.findViewById<ListView>(R.id.listview)
        mListView.setDividerHeight(0)
        mListView.setAdapter(arrayAdapter)
        mListView.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, i, l ->
            alertDialog.dismiss()
            //只有当用户城市的数据也选择完毕后 再设置省的信息 防止出现 省的id  和市的id 不对应的问题
            mProvincePosition = mProvinceMomentPosition
            mProvince = mMProvinceData[mProvincePosition]
            mProvinceId = mProvinceBean!!.provinceList[mProvincePosition].province_id
            mCityPosition = i
            mCity = mCityData[mCityPosition]
            mCityId = mCityBean!!.cityList[mCityPosition].city_id
            tv_bank_address.text = ("$mProvince-$mCity")
        }
        alertDialog.show()


    }

    //验证码是否发送成功
    override fun getVefiryCodeResult(isSuccess: Boolean) {
        if (isSuccess) {
            ToastUtil.showToast(mActivity, "验证码发送成功")
        } else {
            mTimer?.finish()
        }
    }

    /**
     * 解析省份数据
     *
     * @param mProvinceBean 省份数据源
     */
    private fun parserProvinceListData(mProvinceBean: ProvinceBean) {
        if (mProvinceBean == null || mProvinceBean.provinceList == null) {
            ToastUtil.showToast(mActivity, "数据错误")
            return
        }
        val datas = mProvinceBean.provinceList
        mMProvinceData.clear()
        for (i in datas.indices) {
            val data = datas[i]
            val provice_name = data.province_name
            mMProvinceData.add(provice_name)
        }
        showProvineDialog(mMProvinceData)
    }

    fun showProvineDialog(list: List<String>) {
        val dialogView = View.inflate(this, R.layout.dialog_listview, null)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1)
        if (list.size > 0) {
            for (i in list.indices) {
                arrayAdapter.add(list[i])
            }
        }
        var mListView = dialogView.findViewById<ListView>(R.id.listview)
        mListView.setDividerHeight(0)
        mListView.setAdapter(arrayAdapter)

        val builder = AlertDialog.Builder(mActivity)
                .setView(dialogView)
                .setCancelable(true)
        val alertDialog = builder.create()

        alertDialog.setTitle("请选择省份")
        alertDialog.setView(dialogView)
        alertDialog.setCancelable(true)
        mListView.setOnItemClickListener({ _, _, i, l ->
            alertDialog.dismiss()
            mProvinceMomentPosition = i
            var mProvinceMomentPosition = mProvinceBean!!.provinceList[mProvinceMomentPosition].province_id
            mPresenter.getCityData(mProvinceMomentPosition)
        })
        alertDialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
        handler?.removeCallbacksAndMessages(null)
    }
}
