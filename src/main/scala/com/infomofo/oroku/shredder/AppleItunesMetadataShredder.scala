package com.infomofo.oroku.shredder

import com.infomofo.oroku.models
import com.typesafe.scalalogging.LazyLogging

private[shredder] trait AppleItunesMetadataShredder extends MetaShredder with LazyLogging {

  private val appContentPattern = """([^=]+)=([^=]+)""".r

  lazy val appleItunesMetadata: Option[models.AppleItunesMetadata] = {
    getMetaEquals ("apple-itunes-app"){
      case element =>
        val appContent = element.attr("content").split(Array(',',' ')).flatMap {
          case appContentPattern(key, value) =>
            Some(key -> value)
          case "" =>
            None
          case x =>
            logger.warn(s"couldn't find a pattern to match in [$x]")
            None
        }.toMap

        models.MetaString(value = element.attr("content"), tag = element.toString)
        models.AppleItunesMetadata(
          appId = models.MetaInteger(value = appContent("app-id").toInt, tag = element.toString),
          affiliateData = appContent.get("affiliate-data").map {
            data =>
              models.MetaString(value = data, tag = element.toString)
          },
          appArgument = appContent.get("app-argument").map {
            data =>
              models.MetaString(value = data, tag = element.toString)
          }
        )
    }
  }

}
