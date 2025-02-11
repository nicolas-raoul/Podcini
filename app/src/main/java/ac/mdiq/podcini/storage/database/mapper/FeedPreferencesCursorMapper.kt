package ac.mdiq.podcini.storage.database.mapper

import android.database.Cursor
import ac.mdiq.podcini.storage.model.feed.FeedFilter
import ac.mdiq.podcini.storage.model.feed.FeedPreferences
import ac.mdiq.podcini.storage.model.feed.FeedPreferences.AutoDeleteAction
import ac.mdiq.podcini.storage.model.feed.FeedPreferences.NewEpisodesAction
import ac.mdiq.podcini.storage.model.feed.VolumeAdaptionSetting.Companion.fromInteger
import ac.mdiq.podcini.storage.database.PodDBAdapter
import java.util.*

/**
 * Converts a [Cursor] to a [FeedPreferences] object.
 */
object FeedPreferencesCursorMapper {
    /**
     * Create a [FeedPreferences] instance from a database row (cursor).
     */
    @JvmStatic
    fun convert(cursor: Cursor): FeedPreferences {
        val indexId = cursor.getColumnIndexOrThrow(PodDBAdapter.SELECT_KEY_FEED_ID)
        val indexAutoDownload = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_AUTO_DOWNLOAD_ENABLED)
        val indexAutoRefresh = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_KEEP_UPDATED)
        val indexAutoDeleteAction = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_AUTO_DELETE_ACTION)
        val indexVolumeAdaption = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_FEED_VOLUME_ADAPTION)
        val indexUsername = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_USERNAME)
        val indexPassword = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_PASSWORD)
        val indexIncludeFilter = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_INCLUDE_FILTER)
        val indexExcludeFilter = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_EXCLUDE_FILTER)
        val indexMinimalDurationFilter = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_MINIMAL_DURATION_FILTER)
        val indexFeedPlaybackSpeed = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_FEED_PLAYBACK_SPEED)
        val indexAutoSkipIntro = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_FEED_SKIP_INTRO)
        val indexAutoSkipEnding = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_FEED_SKIP_ENDING)
        val indexEpisodeNotification = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_EPISODE_NOTIFICATION)
        val indexNewEpisodesAction = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_NEW_EPISODES_ACTION)
        val indexTags = cursor.getColumnIndexOrThrow(PodDBAdapter.KEY_FEED_TAGS)

        val feedId = cursor.getLong(indexId)
        val autoDownload = cursor.getInt(indexAutoDownload) > 0
        val autoRefresh = cursor.getInt(indexAutoRefresh) > 0
        val autoDeleteAction = AutoDeleteAction.fromCode(cursor.getInt(indexAutoDeleteAction))
        val volumeAdaptionValue = cursor.getInt(indexVolumeAdaption)
        val volumeAdaptionSetting = fromInteger(volumeAdaptionValue)
        val username = cursor.getString(indexUsername)
        val password = cursor.getString(indexPassword)
        val includeFilter = cursor.getString(indexIncludeFilter)
        val excludeFilter = cursor.getString(indexExcludeFilter)
        val minimalDurationFilter = cursor.getInt(indexMinimalDurationFilter)
        val feedPlaybackSpeed = cursor.getFloat(indexFeedPlaybackSpeed)
        val feedAutoSkipIntro = cursor.getInt(indexAutoSkipIntro)
        val feedAutoSkipEnding = cursor.getInt(indexAutoSkipEnding)
        val feedNewEpisodesAction = NewEpisodesAction.fromCode(cursor.getInt(indexNewEpisodesAction))
        val showNotification = cursor.getInt(indexEpisodeNotification) > 0
        var tagsString = cursor.getString(indexTags)
        if (tagsString.isNullOrEmpty()) tagsString = FeedPreferences.TAG_ROOT

        return FeedPreferences(feedId,
            autoDownload,
            autoRefresh,
            autoDeleteAction,
            volumeAdaptionSetting,
            username,
            password,
            FeedFilter(includeFilter, excludeFilter, minimalDurationFilter),
            feedPlaybackSpeed,
            feedAutoSkipIntro,
            feedAutoSkipEnding,
            showNotification,
            feedNewEpisodesAction,
            HashSet(listOf(*tagsString.split(FeedPreferences.TAG_SEPARATOR.toRegex())
                .dropLastWhile { it.isEmpty() }
                .toTypedArray())))
    }
}
