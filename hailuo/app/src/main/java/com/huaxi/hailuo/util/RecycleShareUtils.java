package com.huaxi.hailuo.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.DrawableRes;

import com.huaxi.hailuo.base.GlobalParams;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static com.tencent.connect.share.QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT;

/**
 * Created by zhangliuguang on 2018/5/18.
 */

public class RecycleShareUtils {

    private static IWXAPI wxapi;
    private static int THUMB_SIZE = 150;
    public Context mContext;

    private RecycleShareUtils(Context mContext) {
        this.mContext = mContext;
    }

    public static IWXAPI getWxapi() {
        return wxapi;
    }

    /**
     * 分享到qq
     *
     * @param mContext
     * @param url              分享的url
     * @param shareTitle
     * @param shareDescription
     */
    public static void shareToQQ(Activity mContext, String url, IUiListener listener, String shareTitle, String shareDescription, @DrawableRes int res, String iconName, String share_image) {
        if (!isQQClientAvailable(mContext)) {
            ToastUtil.showToast(mContext, "请先安装QQ");
            return;
        }

        Tencent mTencent = Tencent.createInstance(GlobalParams.APP_QQ_ID, mContext);
        final Bundle params = new Bundle();
        params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);
        params.putString(QQShare.SHARE_TO_QQ_TITLE, shareTitle);
        params.putString(QQShare.SHARE_TO_QQ_SUMMARY, shareDescription);
        params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, url);
        //本地url 或者网上的图片url
        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, share_image);
//        params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, getQQThumbPath(mContext, res, iconName));
        params.putString(QQShare.SHARE_TO_QQ_APP_NAME, iconName);
        mTencent.shareToQQ(mContext, params, listener);
    }

    public static void shareToQZone(final Activity mContext, String url, IUiListener listener, String shareTitle, String summary, @DrawableRes int res, String iconName, String share_image) {
        if (!isQQClientAvailable(mContext)) {
            ToastUtil.showToast(mContext, "请先安装QQ");
            return;
        }
        Tencent mTencent = Tencent.createInstance(GlobalParams.APP_QQ_ID, mContext);
        final Bundle params = new Bundle();
        params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
        params.putString(QzoneShare.SHARE_TO_QQ_TITLE, shareTitle);//必填
        params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, summary);//选填
        params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, url);//必填
        ArrayList<String> list = new ArrayList<>();
        list.add(share_image);
        params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, list);
        mTencent.shareToQzone(mContext, params, listener);
    }


    /**
     * 分享到微信 /朋友圈
     *
     * @param context
     * @param flag             分享到微信或者朋友圈 标志 {@link GlobalParams#SHARE_TO_WECHAT_SESSION}
     *                         {@link GlobalParams#SHARE_TO_WECHAT_TIMELINE}
     * @param shareTitle
     * @param shareDescription
     */
    public static void shareToWx(Context context, int flag, String mShareUrl, String shareTitle, String shareDescription, @DrawableRes int res, String share_image) {
        if (!isWeixinAvilible(context)) {
            ToastUtil.showToast(context, "请先安装微信");
            return;
        }
        wxapi = WXAPIFactory.createWXAPI(context, GlobalParams.APP_WX_ID);
        wxapi.registerApp(GlobalParams.APP_WX_ID);
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = mShareUrl;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = shareTitle;
        msg.description = shareDescription;
        //加载本地的图片
//        Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), res);
//        Bitmap thumbBmp = Bitmap.createScaledBitmap(bmp, THUMB_SIZE, THUMB_SIZE, true);
//        bmp.recycle();
//        msg.thumbData = bmpToByteArray(thumbBmp);

        //加载网络图片********
        try {
            Bitmap thumb = BitmapFactory.decodeStream(new URL(share_image).openStream());
            //一定要压缩，不然会分享失败
            Bitmap thumbBmp2 = Bitmap.createScaledBitmap(thumb, THUMB_SIZE, THUMB_SIZE, true);
            //Bitmap回收
            thumb.recycle();
            msg.thumbData = Tools.bmpToByteArray(thumbBmp2, true);
        } catch (IOException e) {
            e.printStackTrace();
        }

        //调起微信请求
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = buildTransaction("webpage");
        req.message = msg;
        req.scene = flag == GlobalParams.SHARE_TO_WECHAT_SESSION ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        wxapi.sendReq(req);

    }


    /***
     * 判断是否安装微信
     * @param context
     * @return
     */
    public static boolean isWeixinAvilible(Context context) {
        final PackageManager packageManager = context.getPackageManager();// 获取packagemanager
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);// 获取所有已安装程序的包信息
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mm")) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    public static boolean isQQClientAvailable(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }


    private static String buildTransaction(final String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }


    private static IUiListener sIUiListener;

    public static IUiListener getIUiListenerInstance(final Context mContext) {
        if (sIUiListener == null) {
            synchronized (RecycleShareUtils.class) {
                if (sIUiListener == null) {
                    sIUiListener = new IUiListener() {
                        @Override
                        public void onComplete(Object o) {
                            ToastUtil.showToast(mContext, "分享完成");
                        }

                        @Override
                        public void onError(UiError uiError) {
                            ToastUtil.showToast(mContext, "分享失败");
                        }

                        @Override
                        public void onCancel() {
                            ToastUtil.showToast(mContext, "您取消了分享");
                        }
                    };
                }
            }
        }
        return sIUiListener;
    }
}
