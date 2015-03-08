package com.infomofo.oroku.shredder

import com.infomofo.oroku.v0.models
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.nodes.Element

import scala.collection.JavaConverters._

private[shredder] trait SchemaOrgMetadataShredder extends BodyShredder with LazyLogging {

  lazy val itemscopes: Seq[Element] = bodyElement.select("[itemscope]").iterator().asScala.toSeq

  private def itemPropValue(property: String)(implicit itemScope: Element): Option[models.MetaString] = {
    val matchingElement = itemScope.select(s"[itemprop=$property]").iterator().asScala.toList.headOption
    matchingElement.map {
      element =>
        models.MetaString(
          value = element.text(), tag = element.toString
        )
    }
  }

  private def itemType(implicit itemScope: Element): Option[models.MetaString] = {
    val matchingElement = itemScope.select(s"[itemtype").iterator().asScala.toList.headOption
    matchingElement.map {
      element =>
        models.MetaString(
          value = element.attr("itemtype"), tag = element.toString
        )
    }
  }

  private def itemPropAttribute(property: String, attribute: String)(implicit itemScope: Element): Option[models.MetaString] = {
    val matchingElement = itemScope.select(s"[itemprop=$property]").iterator().asScala.toList.headOption
    matchingElement.map {
      element =>
        models.MetaString(
          value = element.attr(attribute), tag = element.toString
        )
    }.filterNot(_.value == "")
  }

  private def parseSchemaOrgItem(inputElement: Element): models.SchemaOrgItem = {
    implicit val itemScope = inputElement
    models.SchemaOrgItem (
      itemType = itemType,
      name = itemPropValue("name"),
      alternateName = itemPropValue("alternateName"),
      description = itemPropValue("description"),
      image = itemPropAttribute("image", "src"),
      url = itemPropAttribute("url", "href"),
      sameAs = itemPropAttribute("sameAs", "href"),
      brand = None,
      color = itemPropValue("color"),
      productId = itemPropValue("productID"),
      sku = itemPropValue("sku"),
      model = itemPropValue("model"),
      gtin8 = itemPropValue("gtin8"),
      gtin13 = itemPropValue("gtin13"),
      gtin14 = itemPropValue("gtin14")
    )
  }

  lazy val schemaOrgItems: Seq[models.SchemaOrgItem] = {
//    logger.info(s"$itemscopes")
    itemscopes.map (parseSchemaOrgItem)
  }

}
