package com.hjq.kotlinhttp.net

import okhttp3.logging.HttpLoggingInterceptor

/**
 *
 * @Description:     类作用描述
 * @Author:         hjq
 * @CreateDate:     2020/12/14 17:05
 *
 */
object NewsService {

    private var newsUrl = "http://v.juhe.cn/"

    private var mNewsApi: NewsApi? = null

    fun getNewsService(): NewsApi {
        if (mNewsApi == null) {
            mNewsApi = ServiceBuilder(newsUrl)
                .addInterceptor(HttpInterceptor())
                .setLogLevel(HttpLoggingInterceptor.Level.BODY)
                .build(NewsApi::class.java)
        }
        return mNewsApi!!
    }

}