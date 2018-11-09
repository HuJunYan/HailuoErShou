package com.huaxi.hailuo.ui.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ImageView
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.BaseActivity
import com.huaxi.hailuo.model.bean.SpeekBean
import com.huaxi.hailuo.presenter.contract.OpionContract
import com.huaxi.hailuo.presenter.impl.UpLoadOpionPresenter
import com.huaxi.hailuo.ui.adapter.GridViewAdapter
import com.huaxi.hailuo.ui.adapter.OpinionBackAdapter
import com.huaxi.hailuo.util.BitmapUtils
import com.huaxi.hailuo.util.FileUtils
import com.huaxi.hailuo.util.StatusBarUtil
import com.huaxi.hailuo.util.ToastUtil
import com.jakewharton.rxbinding2.view.RxView
import com.tbruyelle.rxpermissions2.RxPermissions
import kotlinx.android.synthetic.main.activity_up_opion.*
import kotlinx.android.synthetic.main.dialog_feed_success.view.*
import me.drakeet.materialdialog.MaterialDialog
import java.util.concurrent.TimeUnit


/**
 * 意见反馈界面
 */
class UpOpionActivity : BaseActivity<OpionContract.View, OpionContract.Presenter>(), OpionContract.View, OpinionBackAdapter.OnItemClickListener {

    private var maxLength = 200
    private val IMAGE_CODE = 2
    private var mAdapter: GridViewAdapter? = null
    private var speek_type: String? = ""
    private var mSpeekBean: SpeekBean? = null
    private var mFeedList: ArrayList<SpeekBean.FeedbackItemBean>? = null
    private val mImageFullPath = arrayOfNulls<String>(1)
    private var choose_image_path: String? = null
    private var speek_content: String? = ""

    override var mPresenter: OpionContract.Presenter = UpLoadOpionPresenter()

    override fun getLayout(): Int = R.layout.activity_up_opion

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

    override fun initView() {
        StatusBarUtil.setPaddingSmart(mActivity, tb_opion)
        iv_regist_return.setOnClickListener({
            backActivity()
        })
        RxView.clicks(confirm)
                .throttleFirst(5, TimeUnit.SECONDS)
                .subscribe {
                    upload()
                }
        opion.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null) {
                    if (s.length > maxLength) {
                        val substring = s.substring(0, maxLength)
                        opion.setText(substring)
                        opion.setSelection(maxLength)
                    }
                }

                tv_content_count.text = "${s.toString().length}/200"
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }
        })
    }

    //得到我要吐槽的选项
    override fun getFeedBackResult(data: SpeekBean?) {
        if (data == null) {
            return
        }
        if (mFeedList == null) {
            mFeedList = ArrayList<SpeekBean.FeedbackItemBean>()
        }
        mFeedList?.addAll(data.feedback_item)
        initGridView()
        mSpeekBean = data
    }

    override fun initData() {
        //我要吐槽
        mPresenter.getFeedBack()

        //调用图库
        iv_feed_back.setOnClickListener {
            RxPermissions(mActivity).request(android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    android.Manifest.permission.READ_EXTERNAL_STORAGE)
                    .subscribe {
                        if (it) {
                            //调用相册
                            val intent = Intent(Intent.ACTION_PICK,
                                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                            intent.type = "image/*"
                            //打开相册
                            startActivityForResult(intent, IMAGE_CODE)
                        } else {
                            ToastUtil.showToast(mActivity, "请您授权")
                        }
                    }
        }
    }

    override fun onItemClick(view: View?, postion: Int) {
        ToastUtil.showToast(mActivity, "nihao" + "$postion")
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data == null || resultCode != RESULT_OK) {
            return
        }
        //获取图片路径
        if (requestCode == IMAGE_CODE && resultCode == Activity.RESULT_OK && data != null) {
            val selectedImage = data.data
            val bmp = BitmapFactory.decodeStream(contentResolver.openInputStream(selectedImage))
            val bitmap = BitmapUtils.resizeBitmap(bmp)
            val bytes = BitmapUtils.Bitmap2Bytes(bitmap)
            mImageFullPath[0] = FileUtils.authIdentitySaveJPGFile(mActivity, bytes, 20)
            showImage(mImageFullPath[0]!!, iv_feed_back)

        }
    }

    //加载图片
    private fun showImage(imaePath: String, image: ImageView) {
        val bm = BitmapFactory.decodeFile(imaePath)
        image.setImageBitmap(bm)
    }

    override fun upLoadComplete() {
        feedSuccessDialog()
    }

    /**
     * 上传反馈意见
     */
    private fun upload() {

        if (TextUtils.isEmpty(speek_type)) {
            ToastUtil.showToast(mActivity, "吐槽类型不能为空")
            return
        }

        choose_image_path = mImageFullPath[0]
        speek_content = opion.text.toString().trim()
        if (choose_image_path == null) {
            mPresenter.upLoadOpionWithoutImage(speek_type!!, speek_content!!, "")
        } else {
            //上传反馈意见
            mPresenter.upLoadOpion(speek_type!!, speek_content!!, choose_image_path!!)
        }
    }

    private fun initGridView() {
        mAdapter = GridViewAdapter(mActivity, mFeedList)
        if (mFeedList?.size!! > 0) {
            gridView?.adapter = mAdapter
        }

        //gridView的点击事件
        gridView?.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            //把点击的position传递到adapter里面去
//            mAdapter?.changeState(position)
            mAdapter?.changeState(mSpeekBean!!.feedback_item.get(position).feedback_type)
            speek_type = mSpeekBean!!.feedback_item.get(position).feedback_type
        }
    }


    //吐槽成功的dialog
    private fun feedSuccessDialog() {
        val mLayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        var mView = mLayoutInflater.inflate(R.layout.dialog_feed_success, null, false)
        var mMaterialDialog = MaterialDialog(this).setView(mView)
        mMaterialDialog?.setCanceledOnTouchOutside(true)
        mMaterialDialog.show()
        mView.tv_dialog_feed_success_know.setOnClickListener {
            mMaterialDialog.dismiss()
            finish()
        }
    }

}