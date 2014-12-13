package com.infomofo.oroku.shredder

import com.infomofo.oroku.models
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

import scala.collection.mutable

import scala.collection.JavaConverters._

/**
 * A trait for shredding information on a page contained in <meta> tags in the head of the document
 */
trait MetaShredder {

  protected def metaTags: Elements
  protected val usedMetaTags = new mutable.HashSet[Element]()

  private def convertNamespace(namespace: Option[String]): String = {
    namespace match {
      case None =>
        ""
      case Some(x) =>
        s"$x:"
    }
  }

  /**
   * Gets data from a meta tag
   * @param metaTypeConstructor A constructor that transforms a JSoup Element into the expected MetaType
   * @param tagName The name of the meta property, i.e. "og:title"
   * @param localMetaTags The metaTags to look at to parse this property. By default, uses all meta tags found in the document
   * @tparam MetaType The expected return type
   * @return An option of the expected return type
   */
  protected def getMeta[MetaType](tagName: String)(metaTypeConstructor: (Element) => MetaType)(implicit namespace: Option[String], attribute: String, localMetaTags: Elements) = {
    val matchingTag = localMetaTags.select(s"meta[$attribute=${convertNamespace(namespace)}$tagName]").iterator().asScala.toList.headOption
    matchingTag map {
      element =>
        usedMetaTags += element
        metaTypeConstructor(element)
    }
  }

  protected def getMetaString(tagName: String)(implicit namespace: Option[String], attribute: String, localMetaTags: Elements = metaTags): Option[models.MetaString] = {
    getMeta (tagName){
      case element =>
        models.MetaString(value = element.attr("content"), tag = element.toString)
    }
  }

 protected def getMetaInt(tagName: String)(implicit namespace: Option[String], attribute: String, localMetaTags: Elements = metaTags): Option[models.MetaInteger] = {
    getMeta (tagName){
      element =>
        models.MetaInteger(value = element.attr("content").toInt, tag = element.toString)
    }
  }

}

