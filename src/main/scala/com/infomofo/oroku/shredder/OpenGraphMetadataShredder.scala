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
      val ogTitle = getMetaString("property=og:title")
      val ogType = getMetaOpenGraphType("property=og:type")
      val ogImage = getMetaString("property=og:image")
      val ogImageMimeType = getMetaString("property=og:image:type")
      val ogImageWidth = getMetaInt("property=og:image:width")
      val ogImageHeight = getMetaInt("property=og:image:height")
      val ogUrl = getMetaString("property=og:url")
      val ogSiteName = getMetaString("property=og:site_name")
      val ogDescription = getMetaString("property=og:description")

      val fbAppId = getMetaString("property=fb:app_id")

      Some(models.OpenGraphMetadata(
        title = ogTitle,
        openGraphType = ogType,
        image = ogImage.map {
          url =>
            models.MediaObject(
              url = url,
              mimeType = ogImageMimeType,
              width = ogImageHeight,
              height = ogImageWidth
            )
        },
        url = ogUrl,
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
