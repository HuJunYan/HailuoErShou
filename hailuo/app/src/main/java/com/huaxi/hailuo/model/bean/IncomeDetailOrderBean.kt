package com.huaxi.hailuo.model.bean


data class IncomeDetailOrderBean(var income_list: List<Income>, var count: String) {
    data class Income(
            val income_money: String,
            val income_des: String,
            val income_date: String,
            val income_result: String

    )
}