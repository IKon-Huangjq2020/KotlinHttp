package com.hjq.kotlinhttp.net

import com.google.gson.Gson
import com.google.gson.TypeAdapter
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import java.io.IOException
import java.io.OutputStreamWriter
import java.nio.charset.Charset
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.X509TrustManager
import kotlin.jvm.Throws

/**
 *
 * @Description:     类作用描述
 * @Author:         hjq
 * @CreateDate:     2020/12/14 17:11
 *
 */

class ServiceBuilder constructor(private val baseUrl: String) {

    private var sslSocketFactory: SSLSocketFactory? = null
    private var x509TrustManager: X509TrustManager? = null
    private var interceptors = arrayListOf<Interceptor>()
    private var mLogLevel: HttpLoggingInterceptor.Level = HttpLoggingInterceptor.Level.NONE

    private val mTimeoutSeconds: Long = 15

    fun addInterceptor(interceptor: Interceptor): ServiceBuilder {
        this.interceptors.add(interceptor)
        return this
    }


    fun setLogLevel(level: HttpLoggingInterceptor.Level): ServiceBuilder {
        this.mLogLevel = level
        return this
    }

    /**
     * 生成API
     */
    fun <T> build(service: Class<T>): T {
        val clientBuilder = OkHttpClient.Builder()
            .connectTimeout(mTimeoutSeconds, TimeUnit.SECONDS)
            .writeTimeout(mTimeoutSeconds, TimeUnit.SECONDS)
            .readTimeout(mTimeoutSeconds, TimeUnit.SECONDS)
            .addNetworkInterceptor(HttpLoggingInterceptor().setLevel(mLogLevel))


        // 通用拦截器
        for (interceptor in interceptors) {
            clientBuilder.addInterceptor(interceptor)
        }


        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(CustomConverterFactory.create())
            .client(clientBuilder.build())
            .build()

        return retrofit.create(service)
    }


    /**
     * 自定义请求类 内容和原始一样 只修改CustomGsonResponseBodyConverter类即可
     */
    class CustomRequestBodyConverter<T>(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>
    ) :
        Converter<T, RequestBody> {

        @Throws(IOException::class)
        override fun convert(value: T): RequestBody {
            val buffer = Buffer()
            val writer = OutputStreamWriter(buffer.outputStream(), UTF_8)
            val jsonWriter = gson.newJsonWriter(writer)
            adapter.write(jsonWriter, value)
            jsonWriter.close()
            return RequestBody.create(MEDIA_TYPE, buffer.readByteString())
        }

        companion object {
            private val MEDIA_TYPE = "application/rawData; charset=UTF-8".toMediaTypeOrNull()
            private val UTF_8 = Charset.forName("UTF-8")
        }
    }


}
