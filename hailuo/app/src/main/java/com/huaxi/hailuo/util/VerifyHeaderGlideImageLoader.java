package com.huaxi.hailuo.util;

import android.content.Context;
import android.widget.ImageView;

import com.huaxi.hailuo.R;
import com.huaxi.hailuo.model.bean.HomeDataBean;
import com.huaxi.hailuo.model.bean.VerifyHomeDataBean;
import com.huaxi.hailuo.util.ImageUtil;
import com.youth.banner.loader.ImageLoader;


public class VerifyHeaderGlideImageLoader extends ImageLoader {
    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        //具体方法内容自己去选择，次方法是为了减少banner过多的依赖第三方包，所以将这个权限开放给使用者去选择
        VerifyHomeDataBean.VerifyBannerBean bannerBean = (VerifyHomeDataBean.VerifyBannerBean) path;
        ImageUtil.INSTANCE.loadCache(context, bannerBean.getImg_url(), R.drawable.ic_banner_placeholder, imageView);
    }

    @Override
    public ImageView createImageView(Context context) {
        return new ImageView(context);
    }
}
