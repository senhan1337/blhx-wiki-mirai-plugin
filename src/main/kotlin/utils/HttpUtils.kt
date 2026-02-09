package org.iris.wiki.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.time.Duration

object HttpUtils {

    private val cookie: String = ""

    private var client: OkHttpClient = OkHttpClient().newBuilder()
        .connectTimeout(Duration.ofMillis(20000))
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", ua.random())
                .header("Referer", "https://wiki.biligame.com/")
                .build()
            chain.proceed(request)
        }
        .build()

    private val ua = listOf(
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36 Edg/130.0.0.0",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:133.0) Gecko/20100101 Firefox/133.0",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
        "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/18.2 Safari/605.1.15",
        "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36",
        "Mozilla/5.0 (X11; Ubuntu; Linux x86_64; rv:133.0) Gecko/20100101 Firefox/133.0"
    )

    private fun sendRequest(request: Request): String {
        return try {
            val response = client.newCall(request).execute()
            response.body!!.string()
        } catch (_: Exception) {
            ""
        }
//        return json.parseToJsonElement(body)
    }


    suspend fun get(url: String): String = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url(url)
            .header("cookie", cookie)
            .header("Content-Type", "application/json; charset=utf-8")
            .header("user-agent", ua.random())
            // 修改 get/post 方法中的 header 部分
            .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8")
            .header("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
            .header("Cache-Control", "max-age=0")
            .header("Upgrade-Insecure-Requests", "1")
            .get()
            .build()
        try {
            val response = client.newCall(request).execute()
            response.body!!.string()
        } catch (_: Exception) {
            ""
        }
    }

    fun post(url: String, postBody: String): String {
        val media = "application/x-www-form-urlencoded; charset=utf-8"
        val request = Request.Builder().url(url)
            .header("Content-Type", media)
            .header("user-agent", ua.random())
            .post(postBody.toRequestBody(media.toMediaTypeOrNull())).build()
        return sendRequest(request)
    }


    fun getByteArray(url: String): ByteArrayOutputStream? {
        try {

            val request = Request.Builder().url(url)
                .header("cookie", cookie)
                .header("Content-Type", "application/json; charset=utf-8")
                .header("user-agent", ua.random())
                .get().build()
            val infoStream = ByteArrayOutputStream()
            val response = client.newCall(request).execute();

            val `in` = response.body?.byteStream()
            val buffer = ByteArray(2048)
            var len = 0
            val data = ""
            if (`in` != null) {
                while (`in`.read(buffer).also { len = it } > 0) {
                    infoStream.write(buffer, 0, len)
                }
            }
            infoStream.write((Math.random() * 100).toInt() + 1)
            infoStream.close()
            return infoStream
        } catch (e: Exception) {
            return null
        }
    }

}
