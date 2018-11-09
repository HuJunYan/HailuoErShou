package com.huaxi.hailuo.model.bean

data class MyHttpResponse<T>(val code: Int, val msg: String, val data: T)