package com.huaxi.hailuo.ui.activity

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.PagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import com.huaxi.hailuo.R
import com.huaxi.hailuo.base.App
import com.huaxi.hailuo.base.SimpleActivity
import com.huaxi.hailuo.util.UserUtil
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_guide.*
import kotlinx.android.synthetic.main.item_guide.view.*
import org.jetbrains.anko.startActivity

class GuideActivity : SimpleActivity() {
    override fun getLayout(): Int = R.layout.activity_guide

    override fun initView() {

    }

    private var isGoMain = false
    private var mImageList: MutableList<String>? = null

    companion object {

        var ISGOTOMAIN: String = "isGotoMain"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun initData() {

        CrashReport.setUserId(UserUtil.getUserId(App.instance))


        val viewList = java.util.ArrayList<View>()// 将要分页显示的View装入数组中
        val mLayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val pageView1 = mLayoutInflater.inflate(R.layout.item_guide, null)
        pageView1.iv_guide.setImageResource(R.drawable.first_one)
        val pageView2 = mLayoutInflater.inflate(R.layout.item_guide, null)
        pageView2.iv_guide.setImageResource(R.drawable.first_two)
        val pageView3 = mLayoutInflater.inflate(R.layout.item_guide, null)
        pageView3.iv_guide.setImageResource(R.drawable.first_three)
        viewList.add(pageView1)
        viewList.add(pageView2)
        viewList.add(pageView3)

        pageView3.setOnClickListener {
            //判断用户是否登录 没有登录 跳转到登录页面
            if (UserUtil.isLogin(App.instance)) {
                startActivity<MainActivity>()
            } else {
                startActivity<LoginActivity>()
            }

            finish()
        }


        val pagerAdapter = object : PagerAdapter() {

            override fun isViewFromObject(arg0: View, arg1: Any): Boolean = arg0 === arg1

            override fun getCount(): Int = viewList.size

            override fun destroyItem(container: ViewGroup, position: Int,
                                     `object`: Any) {
                container.removeView(viewList[position])
            }

            override fun instantiateItem(container: ViewGroup, position: Int): Any {
                container.addView(viewList[position])
                return viewList[position]
            }
        }

        vp_banner.adapter = pagerAdapter
        vp_banner.offscreenPageLimit = viewList.size



        vp_banner.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {

            }

            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

            }

            override fun onPageSelected(position: Int) {
                if (position == viewList.size - 1) {
                    UserUtil.setIsFirst(baseContext, true)
                }
            }

        })
        isGoMain = intent.getBooleanExtra(ISGOTOMAIN, false)

    }
}
