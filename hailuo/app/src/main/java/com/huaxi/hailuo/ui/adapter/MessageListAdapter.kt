package com.huaxi.hailuo.ui.adapter

import android.support.v4.content.res.ResourcesCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.huaxi.hailuo.R
import com.huaxi.hailuo.model.bean.MessageCenterBean

/**
 * MessageListAdapter
 * @author liu wei
 * @date 2018/4/13
 */

class MessageListAdapter(list: List<MessageCenterBean.MessageBean>) :
        BaseQuickAdapter<MessageCenterBean.MessageBean, BaseViewHolder>(R.layout.item_meessage_list, list) {

    override fun convert(helper: BaseViewHolder, item: MessageCenterBean.MessageBean) {
        helper.setText(R.id.tv_message_title, item.title)
        helper.setText(R.id.tv_message_content, item.des)
        helper.setText(R.id.tv_message_time, item.dateStr)

        helper.addOnClickListener(R.id.ll_message_content)
        if (item.isRead == "1") {
            helper.setTextColor(R.id.tv_message_title, ResourcesCompat.getColor(mContext.resources,
                    R.color.message_title_read, mContext.theme))
            helper.setTextColor(R.id.tv_message_content, ResourcesCompat.getColor(mContext.resources,
                    R.color.message_desc_read, mContext.theme))

        } else {
            helper.setTextColor(R.id.tv_message_title, ResourcesCompat.getColor(mContext.resources,
                    R.color.message_title, mContext.theme))
            helper.setTextColor(R.id.tv_message_content, ResourcesCompat.getColor(mContext.resources,
                    R.color.message_desc, mContext.theme))
        }

    }
}