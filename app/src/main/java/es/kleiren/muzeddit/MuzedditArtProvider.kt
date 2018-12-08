package es.kleiren.muzeddit

import android.content.Intent
import android.util.Log
import com.google.android.apps.muzei.api.UserCommand
import com.google.android.apps.muzei.api.provider.Artwork
import com.google.android.apps.muzei.api.provider.MuzeiArtProvider
import java.io.IOException
import java.io.InputStream

class MuzedditArtProvider : MuzeiArtProvider() {

    companion object {
        private const val TAG = "MuzedditArtProvider"

        private const val COMMAND_ID= 1
    }

    override fun onLoadRequested(initial: Boolean) {
        RedditFetcherWorker.enqueueLoad()
    }

    override fun getCommands(artwork: Artwork) = context?.run {
        listOf(
            UserCommand(COMMAND_ID,"SubReddit source")
        )
    } ?: super.getCommands(artwork)

    override fun onCommand(artwork: Artwork, id: Int) {
        val context = context ?: return
        when (id) {
            COMMAND_ID -> {
                val intent = Intent(context, ConfigActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(intent)
            }
        }
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
