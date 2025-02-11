package ac.mdiq.podcini.feed.parser

import ac.mdiq.podcini.feed.parser.util.TypeGetter
import ac.mdiq.podcini.storage.model.feed.Feed
import ac.mdiq.podcini.util.Logd
import org.apache.commons.io.input.XmlStreamReader
import org.xml.sax.InputSource
import org.xml.sax.SAXException
import java.io.File
import java.io.IOException
import java.io.Reader
import javax.xml.parsers.ParserConfigurationException
import javax.xml.parsers.SAXParserFactory

class FeedHandler {
    @Throws(SAXException::class,
        IOException::class,
        ParserConfigurationException::class,
        UnsupportedFeedtypeException::class)
    fun parseFeed(feed: Feed): FeedHandlerResult {
        val tg = TypeGetter()
        val type = tg.getType(feed)
        val handler = SyndHandler(feed, type)

        if (feed.file_url != null) {
            val factory = SAXParserFactory.newInstance()
            factory.isNamespaceAware = true
            val saxParser = factory.newSAXParser()
//            saxParser.parse(File(feed.file_url!!), handler)

            val inputStreamReader: Reader = XmlStreamReader(File(feed.file_url!!))
            val inputSource = InputSource(inputStreamReader)
            Logd("FeedHandler", "starting saxParser.parse")
            saxParser.parse(inputSource, handler)
            inputStreamReader.close()
        }
        return FeedHandlerResult(handler.state.feed, handler.state.alternateUrls, handler.state.redirectUrl ?: "")
    }
}
