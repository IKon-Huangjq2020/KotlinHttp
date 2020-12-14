package com.hjq.kotlinhttp.net

import com.hjq.kotlinhttp.entity.NewListBean
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *
 * @Description:     类作用描述
 * @Author:         hjq
 * @CreateDate:     2020/12/14 17:05
 *
 */
interface NewsApi {

    /**
     * 获取新闻头条列表
     * @param mKey 聚合api的key
     * @param mType 获取新闻头条列表关键字
     */
    @GET("/toutiao/index")
    suspend fun getNewsList(
        @Query("key") mKey: String,
        @Query("type") mType: String? = null
    ): NewListBean
}