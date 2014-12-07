package com.infomofo.oroku.shredder

import java.net.URL

import com.infomofo.oroku.models.OpenGraphType
import org.scalatest._

import scala.io.Source

class PageShredderTest extends FlatSpec with Matchers {
  "A Shredded Page" should "have all the expected data" in {
    val source = Source.fromURL(getClass.getResource("/sampleopengraphpage.html"))
    val sourceString = source.getLines().mkString("")
    val shreddedPage = PageShredder(sourceString)
    shreddedPage.pageInfo.appleItunesMetadata should be (None)
    shreddedPage.pageInfo.openGraphMetadata.isDefined should be (true)
    shreddedPage.pageInfo.openGraphMetadata.get.description should be (None)
    shreddedPage.pageInfo.openGraphMetadata.get.title.value should be ("The Rock")
  }

  "An actual live page" should "have all the expected data" in {
    val shreddedPage = PageShredder(new URL("http://ogp.me/"))
    shreddedPage.pageInfo.appleItunesMetadata should be (None)

    shreddedPage.pageInfo.openGraphMetadata.isDefined should be (true)
    val openGraphMetadata = shreddedPage.pageInfo.openGraphMetadata.get
    openGraphMetadata.description.isDefined should be (true)
    openGraphMetadata.title.value should be ("Open Graph protocol")
    openGraphMetadata.openGraphType.value should be (OpenGraphType.Website)
    openGraphMetadata.url.value should be ("http://ogp.me/")
    openGraphMetadata.image.url.value should be ("http://ogp.me/logo.png")
    openGraphMetadata.image.mimeType.get.value should be ("image/png")
    openGraphMetadata.image.width.get.value should be (300)
    openGraphMetadata.appId.get.value should be ("115190258555800")
  }
}