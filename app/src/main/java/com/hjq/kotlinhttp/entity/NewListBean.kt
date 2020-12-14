package com.hjq.kotlinhttp.entity

data class NewListBean(
    val error_code: Int,
    val reason: String,
    val result: NewResult?,
    val resultcode: String?
)