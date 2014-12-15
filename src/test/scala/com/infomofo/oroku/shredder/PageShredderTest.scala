package com.infomofo.oroku.shredder

import java.net.URL

import com.infomofo.oroku.models.OpenGraphType
import org.scalatest._

import scala.io.Source

class PageShredderTest extends FlatSpec with Matchers {
  "A Shredded Page With OpenGraph Data" should "have all the expected data" in {
    val source = Source.fromURL(getClass.getResource("/sampleopengraphpage.html"))
    val sourceString = source.getLines().mkString("")
    val shreddedPage = PageShredder(sourceString)
    shreddedPage.pageInfo.appleItunesMetadata should be (None)
    shreddedPage.pageInfo.openGraphMetadata.isDefined should be (true)
    shreddedPage.pageInfo.openGraphMetadata.get.description should be (None)
    shreddedPage.pageInfo.openGraphMetadata.get.title.get.value should be ("The Rock")

    shreddedPage.pageInfo.titles.contains(shreddedPage.pageInfo.openGraphMetadata.get.title.get.value) should be (true)
  }

  "A Shredded Page with Twitter and OpenGraph Data" should "have all the expected data" in {
    val source = Source.fromURL(getClass.getResource("/sampletwittercardpage.html"))
    val sourceString = source.getLines().mkString("")
    val shreddedPage = PageShredder(sourceString)
    shreddedPage.pageInfo.appleItunesMetadata should be (None)
    shreddedPage.pageInfo.openGraphMetadata.isDefined should be (true)
    shreddedPage.pageInfo.openGraphMetadata.get.description.isDefined should be (true)
    shreddedPage.pageInfo.openGraphMetadata.get.title.get.value should be ("A Twitter for My Sister")

    shreddedPage.pageInfo.titles.contains(shreddedPage.pageInfo.openGraphMetadata.get.title.get.value) should be (true)
    shreddedPage.pageInfo.twitterCardMetadata.isDefined should be (true)
    shreddedPage.pageInfo.twitterCardMetadata.get.creator.get.username.get.value should be ("@nickbilton")
    shreddedPage.pageInfo.twitterCardMetadata.get.title should be (shreddedPage.pageInfo.openGraphMetadata.get.title)

  }

  "A Shredded Page with common search metadata" should "have all the expected data" in {
    val source = Source.fromURL(getClass.getResource("/samplesearchmetapage.html"))
    val sourceString = source.getLines().mkString("")
    val shreddedPage = PageShredder(sourceString)
    shreddedPage.pageInfo.appleItunesMetadata should be (None)
    shreddedPage.pageInfo.searchMetadata.get.title.get.value should be ("Example Books - high-quality used books for children")
    shreddedPage.pageInfo.titles.contains(shreddedPage.pageInfo.searchMetadata.get.title.get.value) should be (true)
    shreddedPage.pageInfo.searchMetadata.get.description.get.value should be ("Author: A.N. Author, Illustrator: P. Picture, Category: Books, Price:  £9.24, Length: 784 pages")
    shreddedPage.pageInfo.descriptions.contains(shreddedPage.pageInfo.searchMetadata.get.description.get.value) should be (true)
  }

  "A Shredded Page with apple itunes metadata" should "have all the expected data" in {
    val source = Source.fromURL(getClass.getResource("/sampleitunesmetadatapage.html"))
    val sourceString = source.getLines().mkString("")
    val shreddedPage = PageShredder(sourceString)
    shreddedPage.pageInfo.appleItunesMetadata.isDefined should be (true)
    shreddedPage.pageInfo.appleItunesMetadata.get.appId.value should be (1234)
    shreddedPage.pageInfo.appleItunesMetadata.get.affiliateData.isDefined should be (true)
    shreddedPage.pageInfo.appleItunesMetadata.get.appArgument.isDefined should be (true)
  }

  "A Shredded Page with schema org metadata" should "have all the expected data" in {
    val source = Source.fromURL(getClass.getResource("/sampleschemaorgpage.html"))
    val sourceString = source.getLines().mkString("")
    val shreddedPage = PageShredder(sourceString)
    shreddedPage.pageInfo.schemaOrgItems.isEmpty should be (false)
  }

  "An actual live page" should "have all the expected data" in {
    val shreddedPage = PageShredder(new URL("http://ogp.me/"))
    shreddedPage.pageInfo.appleItunesMetadata should be (None)

    shreddedPage.pageInfo.openGraphMetadata.isDefined should be (true)
    val openGraphMetadata = shreddedPage.pageInfo.openGraphMetadata.get
    openGraphMetadata.description.isDefined should be (true)
    openGraphMetadata.title.get.value should be ("Open Graph protocol")
    openGraphMetadata.openGraphType.get.value should be (OpenGraphType.Website)
    openGraphMetadata.url.get.value should be ("http://ogp.me/")
    openGraphMetadata.image.get.url.value should be ("http://ogp.me/logo.png")
    openGraphMetadata.image.get.mimeType.get.value should be ("image/png")
    openGraphMetadata.image.get.width.get.value should be (300)
    openGraphMetadata.appId.get.value should be ("115190258555800")

    shreddedPage.pageInfo.titles.contains(openGraphMetadata.title.get.value) should be (true)
    shreddedPage.pageInfo.urls.contains(openGraphMetadata.url.get.value) should be (true)
    shreddedPage.pageInfo.descriptions.contains(openGraphMetadata.description.get.value) should be (true)
  }
}