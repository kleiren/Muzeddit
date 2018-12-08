package es.kleiren.muzeddit

import android.util.Log
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import java.io.IOException
import java.io.InputStream

class MuzedditArtProvider : MuzeiArtProvider() {

    companion object {
        private const val TAG = "MuzedditArtProvider"
    }

    override fun onLoadRequested(initial: Boolean) {
        RedditFetcherWorker.enqueueLoad()
    }

    override fun openFile(artwork: Artwork): InputStream {
        return super.openFile(artwork).also {
            artwork.token?.run {
                try {
                    RedditFetcherService.trackDownload(this)
                } catch (e: IOException) {
                    Log.w(TAG, "Error reporting download to Reddit", e)
                }
            }
        }
    }
}
