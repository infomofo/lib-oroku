package com.infomofo.oroku.webpage

import com.com.infomofo.oroku.webpage.PageShredder
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
}