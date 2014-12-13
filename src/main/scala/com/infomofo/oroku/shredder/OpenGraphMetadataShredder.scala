package com.infomofo.oroku.shredder

import com.infomofo.oroku.models
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.select.Elements

import scala.util.Try

/**
 * This trait defines the behavior for shredding open graph metadata from meta tags
 */
private[shredder] trait OpenGraphMetadataShredder extends MetaShredder with LazyLogging {

  protected def getMetaOpenGraphType(tagName: String)(implicit attribute: String, namespace: Option[String], localMetaTags: Elements = metaTags) = {
    getMeta(tagName){
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
      implicit val attribute = "property"
      implicit var namespace = Some("og")
      val ogTitle = getMetaString("title")
      val ogType = getMetaOpenGraphType("type")
      val ogImage = getMetaString("image")
      val ogImageMimeType = getMetaString("image:type")
      val ogImageWidth = getMetaInt("image:width")
      val ogImageHeight = getMetaInt("image:height")
      val ogUrl = getMetaString("url")
      val ogSiteName = getMetaString("site_name")
      val ogDescription = getMetaString("description")

      namespace = Some("fb")
      val fbAppId = getMetaString(tagName ="app_id")

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
