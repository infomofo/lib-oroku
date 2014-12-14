package com.infomofo.oroku.shredder

import com.infomofo.oroku.models
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import scala.collection.mutable

import scala.collection.JavaConverters._

/**
 * A trait for shredding information on a page contained in tags in the head of the document not already captured in
 * script, link, or meta tags
 */
trait HeadShredder {

  protected def headTags: Elements
  protected val usedHeadTags = new mutable.HashSet[Element]()

  /**
   * Gets data from a head tag
   */
  protected def getHead[HeadType](tagName: String)(headTypeConstructor: (Element) => HeadType)(implicit localHeadTags: Elements) = {
    val matchingTag = localHeadTags.select(tagName).iterator().asScala.toList.headOption
    matchingTag map {
      element =>
        usedHeadTags += element
        headTypeConstructor(element)
    }
  }

  protected def getHeadString(tagName: String)(implicit localMetaTags: Elements = headTags): Option[models.MetaString] = {
    getHead (tagName){
      case element =>
        models.MetaString(value = element.text(), tag = element.toString)
    }
  }
}

