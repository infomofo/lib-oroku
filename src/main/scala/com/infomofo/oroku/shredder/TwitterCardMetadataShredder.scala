package com.infomofo.oroku.shredder

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.select.Elements

import scala.util.Try

import com.infomofo.oroku.models

private[shredder] trait TwitterCardMetadataShredder extends OpenGraphMetadataShredder with LazyLogging {

  protected def getMetaTwitterCardType(tagName: String)(implicit namespace: Option[String], attribute: String, localMetaTags: Elements = metaTags) = {
    getMetaEquals(tagName) {
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
      implicit val namespace = Some("twitter")
      implicit val attribute = "name"
      val twitUrl = getMetaString("url")
      val twitCardType = getMetaTwitterCardType("card")
      val twitDomain = getMetaString("domain")
      val twitSite = getMetaString("site")
      val twitSiteId = getMetaInt("site:id")
      val twitCreator = getMetaString("creator")
      val twitCreatorId = getMetaInt("creator:id")
      val twitDescription = getMetaString("description") match {
        case None =>
          openGraphMetadata.flatMap(_.description)
        case x =>
          x
      }

      val twitTitle = getMetaString("title") match {
        case None =>
          openGraphMetadata.flatMap(_.title)
        case x =>
          x
      }

      val twitImageUrl = getMetaString("image:src") match {
        case None =>
          openGraphMetadata.flatMap(_.image.map(_.url))
        case x =>
          x
      }
      val twitImages = twitImageUrl.map {
        url =>
          val twitImageWidth = getMetaInt("image:width") match {
            case None =>
              openGraphMetadata.flatMap(_.image.flatMap(_.width))
            case x =>
              x
          }
          val twitImageHeight = getMetaInt("image:height") match {
            case None =>
              openGraphMetadata.flatMap(_.image.flatMap(_.height))
            case x =>
              x
          }
          models.MediaObject(url, width = twitImageWidth, height = twitImageHeight)
      }.toSeq ++ (1 until 4).flatMap {
        index =>
          getMetaString(s"image$index:src").map {
            url =>
              val twitImageWidth = getMetaInt(s"image$index:width")
              val twitImageHeight = getMetaInt(s"image$index:height")
              models.MediaObject(url, width = twitImageWidth, height = twitImageHeight)
          }
      }

      val twitCustomFields = (1 until 2).toSeq.flatMap {
        index =>
          getMetaString(s"data$index").map {
            customDataField =>
              val customDataLabel = getMetaString(s"label$index")
              models.TwitterCustomField(data = customDataField, label = customDataLabel)
          }
      }

      def appStoreInfo(platform: String) = {
          getMetaString(s"app:id:$platform").map {
            appId =>
              val appName = getMetaString(s"app:name:$platform")
              val appUrl = getMetaString(s"app:url:$platform")
              models.TwitterAppStoreInfo(appId, appName, appUrl)
          }
      }

      val twitterAppInfo = models.TwitterAppInfo (
        country = getMetaString("app:country"),
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
        logger.warn("Could not find expected metadata for twitter card", e)
        None
    }.get
  }

}
