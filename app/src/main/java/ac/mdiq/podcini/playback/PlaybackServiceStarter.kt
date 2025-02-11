package ac.mdiq.podcini.playback

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import androidx.core.content.ContextCompat
import androidx.media3.common.util.UnstableApi
import ac.mdiq.podcini.playback.service.PlaybackService
import ac.mdiq.podcini.playback.service.PlaybackServiceConstants
import ac.mdiq.podcini.storage.model.playback.Playable

@UnstableApi
class PlaybackServiceStarter(private val context: Context, private val media: Playable) {

    private var shouldStreamThisTime = false
    private var callEvenIfRunning = false

    /**
     * Default value: false
     */
    fun callEvenIfRunning(callEvenIfRunning: Boolean): PlaybackServiceStarter {
        this.callEvenIfRunning = callEvenIfRunning
        return this
    }

    fun shouldStreamThisTime(shouldStreamThisTime: Boolean): PlaybackServiceStarter {
        this.shouldStreamThisTime = shouldStreamThisTime
        return this
    }

    val intent: Intent
        get() {
            val launchIntent = Intent(context, PlaybackService::class.java)
            launchIntent.putExtra(PlaybackServiceConstants.EXTRA_PLAYABLE, media as Parcelable)
            launchIntent.putExtra(PlaybackServiceConstants.EXTRA_ALLOW_STREAM_THIS_TIME, shouldStreamThisTime)
            return launchIntent
        }

    fun start() {
        if (PlaybackService.isRunning && !callEvenIfRunning) return
        ContextCompat.startForegroundService(context, intent)
    }
}
