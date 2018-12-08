package es.kleiren.muzeddit

import android.net.Uri
import android.util.Log
import androidx.work.*
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import es.kleiren.muzeddit.BuildConfig.MUZEDDIT_AUTHORITY
import java.io.IOException

class RedditFetcherWorker : Worker() {

    companion object {
        private const val TAG = "RedditFetcherWorker"

        internal fun enqueueLoad() {
            val workManager = WorkManager.getInstance()
            workManager.enqueue(
                OneTimeWorkRequestBuilder<RedditFetcherWorker>()
                    .setConstraints(
                        Constraints.Builder()
                            .setRequiredNetworkType(NetworkType.CONNECTED)
                            .build()
                    )
                    .build()
            )
        }
    }

    override fun doWork(): Result {
        val photos = try {
            RedditFetcherService.popularPhotos()
        } catch (e: IOException) {
            Log.w(TAG, "Error reading Reddit response", e)
            return Result.RETRY
        }

        Log.w(TAG, photos.data.children.last().data.url)

        outputData = createOutputData(photos.data.children.last().data.url)

        val providerClient = ProviderContract.getProviderClient(
            applicationContext, MUZEDDIT_AUTHORITY
        )
        providerClient.addArtwork(photos.data.children.map { photo ->
            Artwork().apply {
                token = photo.data.url
                title = photo.data.title
                byline = photo.data.author
                persistentUri = Uri.parse(photo.data.url)
                webUri = Uri.parse(photo.data.url)
            }
        })
        return Result.SUCCESS
    }

    private fun createOutputData(firstData: String): Data {
        return Data.Builder()
            .putString("url", firstData)
            .build()
    }
}
