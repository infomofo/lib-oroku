package com.infomofo.oroku.shredder

import com.infomofo.oroku.v0.models
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.select.Elements

import scala.util.Try

/**
 * This trait defines the behavior for shredding common search metadata from meta and other head tags
 */
private[shredder] trait SearchMetadataShredder extends MetaShredder with HeadShredder with LazyLogging {

  protected def getMetaCharset(implicit localMetaTags: Elements = metaTags): Option[models.MetaString] = {
    getMeta ("charset"){
      case element =>
        models.MetaString(value = element.attr("charset"), tag = element.toString)
    }
  }

  /**
   * Data that can be parsed from the headers matching common search metadata format
   */
  lazy val searchMetadata = {
    Try {
      val title = getHeadString("title")

      implicit var attribute = "name"
      implicit val namespace = None
      val description = getMetaString("description")
      val robots = getMetaSeqString("robots")
      val googlebot = getMetaSeqString("googlebot")
      val googleSiteLinkSearchBox = getMetaBoolean("google", "nositelinksearchbox")
      val googleNotranslate = getMetaBoolean("google", "notranslate")
      val googleSiteVerification = getMetaString("google-site-verification")
      val keywords = getMetaSeqString("keywords")

      attribute = "http-equiv"
      val contentType = getMetaString("Content-Type")

      val charset = getMetaCharset

      Some(models.SearchMetadata(
        description = description,
        title = title,
        robots = robots,
        googlebot = googlebot,
        googleSiteLinkSearchBox = googleSiteLinkSearchBox,
        googleNotranslate = googleNotranslate,
        googleSiteVerification = googleSiteVerification.toSeq,
        charset = getMetaCharset,
        contentType = contentType,
        keywords = keywords
      ))
    }.recover {
      case e =>
        logger.warn("Could not find expected metadata for traditional search metadata", e)
        None
    }.get
  }
}

