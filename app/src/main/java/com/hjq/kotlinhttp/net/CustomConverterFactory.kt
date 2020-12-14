package com.hjq.kotlinhttp.net

import com.google.gson.Gson
import com.google.gson.JsonIOException
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonToken
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import retrofit2.Converter
import retrofit2.Retrofit
import java.io.IOException
import java.io.OutputStreamWriter
import java.lang.reflect.Type
import java.nio.charset.Charset
import kotlin.jvm.Throws


class CustomConverterFactory private constructor(private val gson: Gson) :
    Converter.Factory() {

    override fun responseBodyConverter(
        type: Type?,
        annotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): CustomResponseBodyConverter<out Any> {
        val adapter = gson.getAdapter(TypeToken.get(type!!))
        return CustomResponseBodyConverter(gson, adapter as TypeAdapter<*>)
    }

    override fun requestBodyConverter(
        type: Type?,
        parameterAnnotations: Array<Annotation>?,
        methodAnnotations: Array<Annotation>?,
        retrofit: Retrofit?
    ): Converter<*, RequestBody>? {
        val adapter = gson.getAdapter(TypeToken.get(type!!))
        return CustomRequestBodyConverter(gson, adapter as TypeAdapter<*>)
    }

    companion object {

        @JvmOverloads
        fun create(gson: Gson = Gson()): CustomConverterFactory {
            return CustomConverterFactory(gson)
        }
    }

    /**
     * 自定义请求类
     */
    internal class CustomRequestBodyConverter<T>(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>
    ) : Converter<T, RequestBody> {

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

    /**
     * 自定义响应类
     */
    class CustomResponseBodyConverter<T>(
        private val gson: Gson,
        private val adapter: TypeAdapter<T>
    ) : Converter<ResponseBody, T> {

        override fun convert(value: ResponseBody): T {
            val jsonReader = gson.newJsonReader(value.charStream())
            value.use {
                val result = adapter.read(jsonReader)
                if (jsonReader.peek() != JsonToken.END_DOCUMENT) {
                    throw JsonIOException("JSON document was not fully consumed.")
                }
                return result
            }
        }

    }
}