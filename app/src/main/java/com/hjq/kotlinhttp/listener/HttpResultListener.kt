package com.hjq.kotlinhttp.listener

interface HttpResultListener<T> {
    fun onResult(data: T)

    fun onError(errorCode: Int, errorMsg: String)
}