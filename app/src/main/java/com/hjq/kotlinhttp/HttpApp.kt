package com.hjq.kotlinhttp

import android.app.Application
import com.blankj.utilcode.util.Utils

/**
 *
 * @Description:     HttpApp
 * @Author:         hjq
 * @CreateDate:     2020/12/14 16:40
 *
 */
class HttpApp :Application() {
    override fun onCreate() {
        super.onCreate()
        Utils.init(this)
    }
}