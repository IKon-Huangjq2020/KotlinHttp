package com.hjq.kotlinhttp.viewmodel

import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blankj.utilcode.util.NetworkUtils
import com.hjq.kotlinhttp.listener.HttpResultListener
import com.hjq.kotlinhttp.net.HttpServerException
import kotlinx.coroutines.*

/**
 *
 * @Description:     新闻ViewModel
 * @Author:         hjq
 * @CreateDate:     2020/12/14 16:41
 *
 */
class NewsViewModel : ViewModel(), LifecycleObserver {


    /**
     * 启动一个返回<T>的异步事务
     * @param listener 接口回调
     * @param block 要执行的网络请求
     */
    fun <T> netAsync(
        listener: HttpResultListener<T>,
        block: suspend CoroutineScope.() -> T
    ) {
        viewModelScope.launch(CoroutineExceptionHandler { _, e ->
            if (e is HttpServerException) {
                listener.onError(e.httpCode, e.errorMsg)
                return@CoroutineExceptionHandler
            }
            listener.onError(0, e.message ?: "获取数据错误")
        }) {
            //检查网络连接
            if (!NetworkUtils.isConnected()) {
                throw RuntimeException("没有网络连接")
            }
            //网络任务在子线程执行
            val result = withContext(Dispatchers.Default) { block.invoke(this) }
            listener.onResult(result)
        }

    }

}