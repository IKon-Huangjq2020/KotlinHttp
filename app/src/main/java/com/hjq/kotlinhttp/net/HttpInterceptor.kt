package com.hjq.kotlinhttp.net


import com.blankj.utilcode.util.LogUtils
import com.google.gson.Gson
import com.hjq.kotlinhttp.entity.NewListBean
import okhttp3.Interceptor
import okhttp3.Response
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.Reader
import java.nio.charset.Charset


class HttpInterceptor : Interceptor {

    private var mGson = Gson()
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)
        LogUtils.d("intercept ${response.code} ${response.message}")
        val responseJsonStr = getResponseString(response)
        val errorCode = getErrorCode(responseJsonStr)
        if (!(response.code == 200 && errorCode == 0)) {
            val exception: HttpServerException?
            if (responseJsonStr.isNullOrEmpty()) {
                exception = HttpServerException(response.code, response.message)
            } else {
                var dataResult: NewListBean? = null
                try {
                    dataResult = mGson.fromJson(responseJsonStr, NewListBean::class.java)
                } catch (e: Exception) {

                }
                exception = if (dataResult != null) {
                    HttpServerException(dataResult.error_code, dataResult.reason)
                } else {
                    HttpServerException(response.code, response.message)
                }
            }
            throw exception
        }

        return response
    }

    private fun getResponseString(response: Response): String? {
        var returnJson: String? = null
        try {
            val charset = Charset.forName("UTF-8")
            val responseBody = response.peekBody(Long.MAX_VALUE)
            val jsonReader: Reader = InputStreamReader(responseBody.byteStream(), charset)
            val reader = BufferedReader(jsonReader)
            returnJson = reader.readLine()
            jsonReader.close()
        } catch (e: Exception) {
        }
        return returnJson
    }


    private fun getErrorCode(jsonStr: String?): Int {
        if (jsonStr.isNullOrEmpty()) return 0
        var statusCode = 0
        try {
            statusCode = JSONObject(jsonStr).getInt("error_code")
        } catch (e: Exception) {
        }
        return statusCode
    }
}