package com.infomofo.oroku.shredder

import java.io.File
import java.net.URL

import com.infomofo.oroku.models
import com.infomofo.oroku.structure.StructuredInformation
import com.typesafe.scalalogging.LazyLogging
import org.joda.time.DateTime
import org.jsoup.nodes.Document
import org.jsoup.{Connection, Jsoup}

import scala.collection.JavaConverters._
import scala.io.Source

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
class PageShredder(document: Document, url: Option[URL] = None)
  extends OpenGraphMetadataShredder
  with TwitterCardMetadataShredder
  with SearchMetadataShredder
  with AppleItunesMetadataShredder
  with SchemaOrgMetadataShredder
  with LazyLogging {

  private lazy val headElement = document.head
  protected override lazy val bodyElement = document.body()
  protected override lazy val metaTags = headElement.select("meta")
  protected override lazy val headTags = headElement.select(":not(meta):not(script):not(link):not(head)")

  /**
   * a sequence of structured info objects shredded from this page, in order of their priority
   */
  private lazy val structuredInfos: Seq[StructuredInformation] = {
    val structuredOpenGraphMetadata = openGraphMetadata.toSeq.map(StructuredInformation(_))
    val structuredTwitterData = twitterCardMetadata.toSeq.map(StructuredInformation(_))
    val structuredSearchData = searchMetadata.toSeq.map(StructuredInformation(_))
    val structuredSchemaOrgMetadata = Some(StructuredInformation(schemaOrgItems))

    structuredOpenGraphMetadata ++ structuredTwitterData ++ structuredSearchData ++ structuredSchemaOrgMetadata
  }

  /**
   * All aggregate page information that can be shredded from a page
   */
  lazy val pageInfo = {
    val returnValue = models.PageInfo(
      titles = structuredInfos.flatMap(_.titles).distinct,
      images = structuredInfos.flatMap(_.images).distinct,
      descriptions = structuredInfos.flatMap(_.descriptions).distinct,
      urls = (structuredInfos.flatMap(_.urls) ++ url.map(_.toString)).distinct,
      keywords = structuredInfos.flatMap(_.keywords).distinct,
      categories = Nil,
      locations = Nil,
      prices = Nil,
      retrievedAt = new DateTime(),
      site = models.Site(
        name = structuredInfos.flatMap(_.siteNames).headOption.getOrElse(""),
        siteType = structuredInfos.flatMap(_.siteTypes).headOption
      ),
      openGraphMetadata = openGraphMetadata,
      appleItunesMetadata = appleItunesMetadata,
      twitterCardMetadata = twitterCardMetadata,
      schemaOrgItems = schemaOrgItems,
      searchMetadata = searchMetadata
    )

    val unhandledMetaTags = metaTags.iterator.asScala.filterNot(usedMetaTags.contains)
    if (unhandledMetaTags.nonEmpty) {
      logger.warn(s" Unhandled meta tags ${unhandledMetaTags.mkString(", ")}")
    }
    val unhandledHeadTags = headTags.iterator.asScala.filterNot(usedHeadTags.contains)
    if (unhandledHeadTags.nonEmpty) {
      logger.warn(s" Unhandled head tags ${unhandledHeadTags.mkString(", ")}")
    }
    returnValue
  }
}