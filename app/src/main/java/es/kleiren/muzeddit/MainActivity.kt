package es.kleiren.muzeddit

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import android.graphics.BitmapFactory
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.net.URL


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val workRequest = OneTimeWorkRequest.Builder(RedditFetcherWorker::class.java)
            .build()
        WorkManager
            .getInstance()
            .enqueue(workRequest)
        val photo = findViewById<ImageView>(R.id.imageView)

        val workManager = WorkManager.getInstance()
        WorkManager.getInstance()
            .getStatusById(workRequest.id)
            .observe(this@MainActivity, android.arch.lifecycle.Observer {
                it?.let {
                    // Get the output data from the worker.
                    val workerResult = it.outputData
                    // Check if the task is finished?
                    if (it.state.isFinished) {
                        Toast.makeText(this, "Work completed.", Toast.LENGTH_LONG)
                            .show()

                        val url = URL(
                            workerResult.getString("url")
                        )
                        doAsync {
                            val bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream())
                            uiThread {
                                // use result here if you want to update ui
                                photo.setImageBitmap(bmp)
                            }
                        }

                    } else {
                        Toast.makeText(this, "Work failed.", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            })
    }
}
