package com.infomofo.oroku.shredder

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.select.Elements

import scala.util.Try

import com.infomofo.oroku.models

private[shredder] trait TwitterCardMetadataShredder extends OpenGraphMetadataShredder with LazyLogging {

  protected def getMetaTwitterCardType(implicit tagName: String, localMetaTags: Elements = metaTags) = {
    getMeta {
      element =>
        val matchedType = models.TwitterCardType(element.attr("content"))
        matchedType match {
          case models.TwitterCardType.UNDEFINED(unknownType) =>
            logger.warn(s"Page found with an unknown twitter card type $unknownType")
          case _ =>
        }
        models.MetaTwitterCardType(value = matchedType, tag = element.toString)
    }
  }
  /**
   * Data that can be parsed from the headers matching the twitter card format.  Certain tags will fall back to
   * open graph metadata if twitter data is unavailable.
   */
  lazy val twitterCardMetadata = {
    Try {
      val twitUrl = getMetaString("name=twitter:url")
      val twitCardType = getMetaTwitterCardType("name=twitter:card")
      val twitDomain = getMetaString("name=twitter:domain")
      val twitSite = getMetaString("name=twitter:site")
      val twitSiteId = getMetaInt("name=twitter:site:id")
      val twitCreator = getMetaString("name=twitter:creator")
      val twitCreatorId = getMetaInt("name=twitter:creator:id")
      val twitDescription = getMetaString("name=twitter:description") match {
        case None =>
          openGraphMetadata.flatMap(_.description)
        case x =>
          x
      }

      val twitTitle = getMetaString("name=twitter:title") match {
        case None =>
          openGraphMetadata.flatMap(_.title)
        case x =>
          x
      }

      val twitImageUrl = getMetaString("name=twitter:image:src") match {
        case None =>
          openGraphMetadata.flatMap(_.image.map(_.url))
        case x =>
          x
      }
      val twitImages = twitImageUrl.map {
        url =>
          val twitImageWidth = getMetaInt("name=twitter:image:width") match {
            case None =>
              openGraphMetadata.flatMap(_.image.flatMap(_.width))
            case x =>
              x
          }
          val twitImageHeight = getMetaInt("name=twitter:image:height") match {
            case None =>
              openGraphMetadata.flatMap(_.image.flatMap(_.height))
            case x =>
              x
          }
          models.MediaObject(url, width = twitImageWidth, height = twitImageHeight)
      }.toSeq ++ (1 until 4).flatMap {
        index =>
          getMetaString(s"name=twitter:image$index:src").map {
            url =>
              val twitImageWidth = getMetaInt(s"name=twitter:image$index:width")
              val twitImageHeight = getMetaInt(s"name=twitter:image$index:height")
              models.MediaObject(url, width = twitImageWidth, height = twitImageHeight)
          }
      }

      val twitCustomFields = (1 until 2).toSeq.flatMap {
        index =>
          getMetaString(s"name=twitter:data$index").map {
            customDataField =>
              val customDataLabel = getMetaString(s"name=twitter:label$index")
              models.TwitterCustomField(data = customDataField, label = customDataLabel)
          }
      }

      def appStoreInfo(platform: String) = {
          getMetaString(s"name=twitter:app:id:$platform").map {
            appId =>
              val appName = getMetaString(s"name=twitter:app:name:$platform")
              val appUrl = getMetaString(s"name=twitter:app:url:$platform")
              models.TwitterAppStoreInfo(appId, appName, appUrl)
          }
      }

      val twitterAppInfo = models.TwitterAppInfo (
        country = getMetaString("name=twitter:app:country"),
        iphone = appStoreInfo("iphone"),
        ipad = appStoreInfo("ipad"),
        googleplay = appStoreInfo("googleplay")
      )

      Some(models.TwitterCardMetadata(
        card = twitCardType,
        url = twitUrl,
        domain = twitDomain,
        site = Some(models.TwitterUser(
          username = twitSite,
          id = twitSiteId
        )),
        creator = Some(models.TwitterUser(
          username = twitCreator,
          id = twitCreatorId
        )),
        description = twitDescription,
        title = twitTitle,
        images = twitImages,
        app = Some(twitterAppInfo),
        customFields = twitCustomFields
      ))
    }.recover {
      case e =>
        logger.warn("Could not find expected metadata for open graph", e)
        None
    }.get
  }

}
