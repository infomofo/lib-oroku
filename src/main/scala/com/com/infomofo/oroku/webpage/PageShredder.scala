package com.com.infomofo.oroku.webpage

import java.io.File
import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import org.jsoup.nodes.{Document, Element}
import org.jsoup.select.Elements
import org.jsoup.{Jsoup, Connection}

import scala.collection.JavaConverters._
import scala.collection.mutable

import com.infomofo.oroku.models

import scala.io.Source
import scala.util.Try

object PageShredder {
  private val openGraphPropertyPattern = "og:(.*)".r
  private val facebookPropertyPattern = "fb:(.*)".r

  /**
   * Constructs a PageShredder object from a url, following redirects up to one time if necessary.
   * @param url a url to fetch the html source to shred
   * @return a PageShredder instance representing data scraped from the html data provided at the specified url
   */
  def apply(url: URL) = {
    val document = {
      val res: Connection.Response = {
        val response = Jsoup.connect(url.toString)
          .ignoreHttpErrors(true)
          .followRedirects(true)
          .ignoreContentType(true)
          .userAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:25.0) Gecko/20100101 Firefox/25.0")
          .referrer("http://www.google.com")
          .execute()
        response match {
          case x if x.statusCode == 307 =>
            // Follow redirects
            val newUrl = x.header("Location")
            if (newUrl != null && newUrl.length > 7) {
              Jsoup.connect(newUrl).execute()
            } else {
              throw new IllegalStateException(s"Invalid url specified by 307 redirect: $newUrl")
            }
          case x =>
            x
        }
      }
      res.parse
    }
    new PageShredder(document, Some(url))
  }

  /**
   * Constructs a PageShredder object from a raw source provided in HTML format, following redirects up to one time if
   * necessary.
   * @param source a source of html data
   * @return a PageShredder instance representing data scraped from the passed in html source
   */
  def apply(source: Source) = {
    val document = Jsoup.parse(source.getLines().mkString(""))
    new PageShredder(document)
  }

  def apply(file: File) = {
    val doc = Jsoup.parse(file, "UTF-8", "http://example.com/");
    new PageShredder(doc)
  }

  def apply(sourceString: String) = {
    val document = Jsoup.parse(sourceString)
    new PageShredder(document)
  }
}

/**
 * Represents a document that has been shredded for important information
 * @param document a document representing a webpage
 * @param url an optional additional url that indicates where the document was parsed
 */
class PageShredder(document: Document, url: Option[URL] = None) extends LazyLogging {

  private lazy val headElement = document.head
  private lazy val metaTags = headElement.select("meta")

  private val usedMetaTags = new mutable.HashSet[Element]()

  private def getMeta[MetaType](metaTypeConstructor: (Element) => MetaType)(implicit tagName: String, localMetaTags: Elements): Option[MetaType] = {
    val matchingTag = metaTags.select(s"meta[property=$tagName]").iterator().asScala.toList.headOption
    matchingTag map {
      element =>
        usedMetaTags += element
        metaTypeConstructor(element)
    }
  }

  private def getMetaString(implicit tagName: String, localMetaTags: Elements = metaTags): Option[models.MetaString] = {
    getMeta {
      case element =>
        models.MetaString(value = element.attr("content"), tag = element.toString)
    }
  }

  private def getMetaOpenGraphType(implicit tagName: String, localMetaTags: Elements = metaTags): Option[models.MetaOpenGraphType] = {
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

  private def getMetaInt(implicit tagName: String, localMetaTags: Elements = metaTags): Option[models.MetaInteger] = {
    getMeta {
      element =>
        models.MetaInteger(value = element.attr("content").toInt, tag = element.toString)
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
        image = models.OpenGraphMedia(
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

  /**
   * All aggregate page information that can be shredded from a page
   */
  lazy val pageInfo = {
    val returnValue = models.PageInfo(
      titles = openGraphMetadata.map(_.title.value).toSeq,
      urls = openGraphMetadata.map(_.url.value).toSeq ++ url.map(_.toString),
      keywords = Nil,
      locations = Nil,
      retrievedAt = new DateTime(),
      site = models.Site(
        name = openGraphMetadata.flatMap(_.siteName.map(_.value)).getOrElse(""),
        siteType = openGraphMetadata.map(_.openGraphType.value.toString)
      ),
      openGraphMetadata = openGraphMetadata
    )

    val unhandledMetaTags = metaTags.iterator.asScala.filterNot(usedMetaTags.contains)
    if (unhandledMetaTags.nonEmpty) {
      logger.warn(s" Unhandled meta tags ${unhandledMetaTags.mkString(", ")}")
    }
    returnValue
  }
}