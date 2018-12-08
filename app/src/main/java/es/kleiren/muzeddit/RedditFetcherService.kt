package es.kleiren.muzeddit

import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

internal interface RedditFetcherService {

    companion object {

        private fun createService(): RedditFetcherService {
            val okHttpClient = OkHttpClient.Builder()
                .addInterceptor { chain ->
                    var request = chain.request()
                    val url = request.url().newBuilder().build()
                    request = request.newBuilder().url(url).build()
                    chain.proceed(request)
                }
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return retrofit.create<RedditFetcherService>(RedditFetcherService::class.java)
        }

        @Throws(IOException::class)
        internal fun popularPhotos(): RedditNewsResponse {
            return createService().popularPhotos.execute().body()
                ?: throw IOException("Response was null")
        }

        @Throws(IOException::class)
        internal fun trackDownload(photoId: String) {
            createService().trackDownload(photoId).execute()
        }
    }

    @get:GET("r/SpacePorn/.json?limit=30")
    val popularPhotos: Call<RedditNewsResponse>

    @GET("{url}")
    fun trackDownload(@Path("url") url: String): Call<Any>

    class RedditNewsResponse(val data: RedditDataResponse)

    class RedditDataResponse(
        val children: List<RedditChildrenResponse>,
        val after: String?,
        val before: String?
    )

    class RedditChildrenResponse(val data: RedditNewsDataResponse)

    class RedditNewsDataResponse(
        val author: String,
        val title: String,
        val num_comments: Int,
        val created: Long,
        val thumbnail: String,
        val url: String
    )

}
