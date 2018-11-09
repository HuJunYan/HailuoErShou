package com.huaxi.hailuo.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.huaxi.hailuo.R;
import com.huaxi.hailuo.base.GlobalParams;
import com.huaxi.hailuo.util.RecycleShareUtils;
import com.tencent.tauth.IUiListener;

/**
 * Created by zhangliuguang on 2018/5/18.
 */

public class InviteBottomDialog implements View.OnClickListener {
    public static final int TYPE_WEB = 1;
    public static final int TYPE_NORMAL_SHARE = 2;
    private Dialog bottomDialog;
    private ImageView mIvQRCode;
    private Activity mContext;
    private boolean mIsCheck;
    private IUiListener listener;
    private String mShareUrl;
    private String shareTitle;
    private String shareDescription;
    private int iconRes;
    private String iconName;
    private int type = TYPE_NORMAL_SHARE;
    private String share_image;

    /**
     * @param context
     * @param listener qq分享回调监听
     */
    public InviteBottomDialog(Activity context, IUiListener listener, String shareTitle, String shareDescription, int type,String share_image) {
        mContext = context;
        this.listener = listener;
        this.shareTitle = shareTitle;
        this.shareDescription = shareDescription;
        this.type = type;
        this.share_image = share_image;
        initDialog(context);

    }

    private void initDialog(Context context) {
        bottomDialog = new Dialog(context, R.style.invite_dialog_style);
        View contentView;
        contentView = LayoutInflater.from(context).inflate(R.layout.dialog_invite_share, null);
        mIvQRCode = (ImageView) contentView.findViewById(R.id.iv_qrcode);//二维码
        bottomDialog.setContentView(contentView);
        ViewGroup.LayoutParams layoutParams = contentView.getLayoutParams();
        layoutParams.width = context.getResources().getDisplayMetrics().widthPixels;
        contentView.setLayoutParams(layoutParams);
        bottomDialog.getWindow().setGravity(Gravity.BOTTOM);
        bottomDialog.getWindow().setWindowAnimations(R.style.invite_animation);
        contentView.findViewById(R.id.ll_invite_share_friends).setOnClickListener(this);
        contentView.findViewById(R.id.ll_invite_share_qq).setOnClickListener(this);
        contentView.findViewById(R.id.ll_invite_share_wechat).setOnClickListener(this);
        contentView.findViewById(R.id.ll_invite_share_qzone).setOnClickListener(this);
    }


    public void show() {
        bottomDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (mIsCheck) {
            return;
        }
        mIsCheck = true;
        switch (v.getId()) {
            case R.id.ll_invite_share_wechat://分享给微信好友
                shareToWeChatSession();
                break;

            case R.id.ll_invite_share_qzone://分享到QQ空间
                shareToQzone();
                break;
            case R.id.ll_invite_share_qq://分享到QQ
                shareToQQ();
                break;
            case R.id.ll_invite_share_friends://分享给朋友圈
                shareToWeChatTimeline();
                break;
        }
        bottomDialog.dismiss();
        mIsCheck = false;
    }




    private void shareToWeChatSession() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                RecycleShareUtils.shareToWx(mContext.getApplicationContext(), GlobalParams.SHARE_TO_WECHAT_SESSION, mShareUrl, shareTitle, shareDescription, iconRes,share_image);
            }
        }.start();
    }

    private void shareToWeChatTimeline() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                RecycleShareUtils.shareToWx(mContext.getApplicationContext(), GlobalParams.SHARE_TO_WECHAT_TIMELINE, mShareUrl, shareTitle, shareDescription, iconRes,share_image);
            }
        }.start();
    }

    private void shareToQQ() {
        RecycleShareUtils.shareToQQ(mContext, mShareUrl, RecycleShareUtils.getIUiListenerInstance(mContext), shareTitle, shareDescription, iconRes, iconName,share_image);
    }

    private void shareToQzone(){
        RecycleShareUtils.shareToQZone(mContext, mShareUrl, RecycleShareUtils.getIUiListenerInstance(mContext), shareTitle, shareDescription, iconRes, iconName,share_image);
    }

    /**
     * 设置二维码图片
     *
     * @param bitmap
     * @return
     */
    public InviteBottomDialog setQRCodeBitmap(Bitmap bitmap) {
        if (mIvQRCode == null || type == TYPE_WEB) {
            return this;
        }
        mIvQRCode.setImageBitmap(bitmap);
        return this;
    }

    public void cancel() {
        if (bottomDialog.isShowing()) {
            bottomDialog.cancel();
        }

    }

    public InviteBottomDialog setShareUrl(String mShareUrl) {
        this.mShareUrl = mShareUrl;
        return this;
    }


    public InviteBottomDialog setShareIconResAndName(@DrawableRes int iconRes, String name) {
        this.iconRes = iconRes;
        this.iconName = name;
        return this;
    }
}
