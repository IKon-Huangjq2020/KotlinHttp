package com.hjq.kotlinhttp.entity

data class NewResult(
    val `data`: List<NewData>,
    val stat: String
)