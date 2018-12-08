package es.kleiren.muzeddit

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.work.*
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.ProviderContract
import es.kleiren.muzeddit.BuildConfig.MUZEDDIT_AUTHORITY
import es.kleiren.muzeddit.RedditFetcherService.Companion.subReddit
import java.io.IOException

class RedditFetcherWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

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
            RedditFetcherService.popularPhotos(subReddit)
        } catch (e: IOException) {
            Log.w(TAG, "Error reading Reddit response", e)
            return Result.retry()
        }

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
        return Result.success()
    }
}
