package com.infomofo.oroku.shredder

import com.infomofo.oroku.models
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.select.Elements

import scala.util.Try

/**
 * This trait defines the behavior for shredding open graph metadata from meta tags
 */
private[shredder] trait OpenGraphMetadataShredder extends MetaShredder with LazyLogging {
  protected def getMetaOpenGraphType(implicit tagName: String, localMetaTags: Elements = metaTags) = {
    getMeta {
      element =>
        usedMetaTags += element
        val matchedOgType = models.OpenGraphType(element.attr("content"))
        matchedOgType match {
          case models.OpenGraphType.UNDEFINED(unknownType) =>
            logger.warn(s"Page found with an unknown open graph type $unknownType")
          case _ =>
        }
        models.MetaOpenGraphType(value = matchedOgType, tag = element.toString)
    }
  }

  /**
   * Data that can be parsed from the headers matching the open graph format
   */
  lazy val openGraphMetadata = {
    Try {
      val ogTitle = getMetaString("og:title")
      val ogType = getMetaOpenGraphType("og:type")
      val ogImage = getMetaString("og:image")
      val ogImageMimeType = getMetaString("og:image:type")
      val ogImageWidth = getMetaInt("og:image:width")
      val ogImageHeight = getMetaInt("og:image:height")
      val ogUrl = getMetaString("og:url")
      val ogSiteName = getMetaString("og:site_name")
      val ogDescription = getMetaString("og:description")

      val fbAppId = getMetaString("fb:app_id")

      Some(models.OpenGraphMetadata(
        title = ogTitle.get,
        openGraphType = ogType.get,
        image = models.MediaObject(
          url = ogImage.get,
          mimeType = ogImageMimeType,
          width = ogImageHeight,
          height = ogImageWidth
        ),
        url = ogUrl.get,
        siteName = ogSiteName,
        description = ogDescription,
        appId = fbAppId
      ))
    }.recover {
      case e =>
        logger.warn("Could not find expected metadata for open graph", e)
        None
    }.get
  }
}
