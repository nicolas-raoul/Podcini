package ac.mdiq.podcini.feed.parser.namespace

import android.util.Log
import androidx.core.text.HtmlCompat
import ac.mdiq.podcini.feed.parser.element.SyndElement
import ac.mdiq.podcini.feed.parser.util.DurationParser.inMillis
import org.xml.sax.Attributes

class Itunes : Namespace() {
    override fun handleElementStart(localName: String, state: ac.mdiq.podcini.feed.parser.HandlerState,
                                    attributes: Attributes): SyndElement {
        if (IMAGE == localName) {
            val url: String? = attributes.getValue(IMAGE_HREF)

            if (state.currentItem != null) {
                state.currentItem!!.imageUrl = url
            } else {
                // this is the feed image
                // prefer to all other images
                if (!url.isNullOrEmpty()) {
                    state.feed.imageUrl = url
                }
            }
        }
        return SyndElement(localName, this)
    }

    override fun handleElementEnd(localName: String, state: ac.mdiq.podcini.feed.parser.HandlerState) {
        if (state.contentBuf == null) {
            return
        }

        val content = state.contentBuf.toString()
        val contentFromHtml = HtmlCompat.fromHtml(content, HtmlCompat.FROM_HTML_MODE_COMPACT).toString()
        if (content.isEmpty()) {
            return
        }

        if (AUTHOR == localName && state.tagstack.size <= 3) {
            state.feed.author = contentFromHtml
        } else if (DURATION == localName) {
            try {
                val durationMs = inMillis(content)
                state.tempObjects[DURATION] = durationMs.toInt()
            } catch (e: NumberFormatException) {
                Log.e(NSTAG, String.format("Duration '%s' could not be parsed", content))
            }
        } else if (SUBTITLE == localName) {
            if (state.currentItem != null && state.currentItem?.description.isNullOrEmpty()) {
                state.currentItem!!.setDescriptionIfLonger(content)
            } else if (state.feed.description.isNullOrEmpty()) {
                state.feed.description = content
            }
        } else if (SUMMARY == localName) {
            if (state.currentItem != null) {
                state.currentItem!!.setDescriptionIfLonger(content)
            } else if (Rss20.CHANNEL == state.secondTag.name) {
                state.feed.description = content
            }
        } else if (NEW_FEED_URL == localName && content.trim { it <= ' ' }.startsWith("http")) {
            state.redirectUrl = content.trim { it <= ' ' }
        }
    }

    companion object {
        const val NSTAG: String = "itunes"
        const val NSURI: String = "http://www.itunes.com/dtds/podcast-1.0.dtd"

        private const val IMAGE = "image"
        private const val IMAGE_HREF = "href"

        private const val AUTHOR = "author"
        const val DURATION: String = "duration"
        private const val SUBTITLE = "subtitle"
        private const val SUMMARY = "summary"
        private const val NEW_FEED_URL = "new-feed-url"
    }
}
