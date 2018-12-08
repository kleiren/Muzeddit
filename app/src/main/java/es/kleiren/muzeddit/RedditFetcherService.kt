package es.kleiren.muzeddit

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.IOException

internal interface RedditFetcherService {

    companion object {
        var subReddit = "SpacePorn+EarthPorn+ExposurePorn"

        private fun createService(): RedditFetcherService {

            val retrofit = Retrofit.Builder()
                .baseUrl("https://www.reddit.com/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

            return retrofit.create<RedditFetcherService>(RedditFetcherService::class.java)
        }

        @Throws(IOException::class)
        internal fun popularPhotos(url: String): RedditNewsResponse {
            return createService().popularPhotos(url).execute().body()
                ?: throw IOException("Response was null")
        }

        @Throws(IOException::class)
        internal fun trackDownload(photoId: String) {
            createService().trackDownload(photoId).execute()
        }
    }

    @GET("r/{url}/.json?limit=30")
    fun popularPhotos(@Path("url") url: String): Call<RedditNewsResponse>

    @get:GET("r/SpacePorn/.json?limit=30")
    val popularPhotos: Call<RedditNewsResponse>

    @GET("{url}")
    fun trackDownload(@Path("url") url: String): Call<Any>

    class RedditNewsResponse(val data: RedditDataResponse)

    class RedditDataResponse(
        val children: List<RedditChildrenResponse>
    )

    class RedditChildrenResponse(val data: RedditNewsDataResponse)

    class RedditNewsDataResponse(
        val author: String,
        val title: String,
        val url: String
    )

}
