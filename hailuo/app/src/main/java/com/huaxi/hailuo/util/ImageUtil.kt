package com.huaxi.hailuo.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.annotation.DrawableRes
import android.text.TextUtils
import android.widget.ImageView

import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.huaxi.hailuo.model.bean.GlideApp


object ImageUtil {

    /**
     * 加载图片并且缓存
     */
    fun loadCache(context: Context, imageUrl: String, @DrawableRes placeholder: Int, iv: ImageView) {
        GlideApp.with(context).load(imageUrl).placeholder(placeholder).into(iv)

    }

    /**
     * 加载图片并且缓存
     */
    fun loadCache(context: Context, res: Int, @DrawableRes placeholder: Int, iv: ImageView) {
        GlideApp.with(context).load(res).placeholder(placeholder).into(iv)
    }

    /**
     * 加载图片不缓存
     */
    fun load(context: Context, imageUrl: String, view: ImageView) {
        if (TextUtils.isEmpty(imageUrl)) {
            return
        }

        GlideApp.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(view)
    }

    /**
     * 加载图片不缓存
     */
    fun load(context: Context, imageUrl: String, @DrawableRes placeholder: Int, view: ImageView?) {
        if (TextUtils.isEmpty(imageUrl)) {
            return
        }

        GlideApp.with(context).load(imageUrl).placeholder(placeholder).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .transition(withCrossFade())
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view?.setImageDrawable(resource)
                    }
                })

    }

    /**
     * 加载图片不缓存
     */
    fun load(context: Context, imageUrl: String, @DrawableRes placeHolder: Int, @DrawableRes errorHolder: Int, view: ImageView?) {
        if (TextUtils.isEmpty(imageUrl)) {
            return
        }

        GlideApp.with(context).load(imageUrl)
                .placeholder(placeHolder)
                .error(errorHolder)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .transition(withCrossFade())
                .into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view?.setImageDrawable(resource)
                    }
                })

    }

    /**
     * 清除缓存的图片
     */
    fun clear(context: Context) {
        GlideApp.get(context).clearMemory()
    }

    /**
     * 加载图片不缓存
     */
    fun loadFitXY(context: Context, imageUrl: String, view: ImageView?) {
        if (TextUtils.isEmpty(imageUrl)) {
            return
        }

        GlideApp.with(context).load(imageUrl).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true)
                .transition(withCrossFade()).into(object : SimpleTarget<Drawable>() {
                    override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                        view?.setImageDrawable(resource)
                        view?.scaleType = ImageView.ScaleType.FIT_XY
                    }
                })

    }
}


