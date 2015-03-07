package com.infomofo.oroku.structure

import com.infomofo.oroku.v0.models

/**
 * Structured Information is a trait that unifies how information from various sources can be unified
 */
trait StructuredInformation {

  def urls: Seq[String] = Nil

  def titles: Seq[String] = Nil

  def keywords: Seq[String] = Nil

  def locations: Seq[String] = Nil

  def descriptions: Seq[String] = Nil

  def siteNames: Seq[String] = Nil

  def siteTypes: Seq[String] = Nil

  def images: Seq[String] = Nil
}

object StructuredInformation {

  implicit class StructuredOpenGraphMetadata(val self: models.OpenGraphMetadata)
    extends StructuredInformation {

    override def titles: Seq[String] = self.title.toSeq.map(_.value)

    override def urls: Seq[String] = self.url.toSeq.map(_.value)

    override def descriptions: Seq[String] = self.description.toSeq.map(_.value)

    override def siteNames: Seq[String] = self.siteName.toSeq.map(_.value)

    override def siteTypes: Seq[String] = self.openGraphType.toSeq.map(_.value.toString)

    override def images: Seq[String] = self.image.toSeq.map(_.url.value)
  }

  def apply(meta: models.OpenGraphMetadata) = StructuredOpenGraphMetadata(meta)


  implicit class StructuredTwitterCardMetadata(val self: models.TwitterCardMetadata)
    extends StructuredInformation {

    override def titles: Seq[String] = self.title.toSeq.map(_.value)

    override def urls: Seq[String] = self.url.toSeq.map(_.value)

    override def descriptions: Seq[String] = self.description.toSeq.map(_.value)

    override def siteNames: Seq[String] = self.site.toSeq.flatMap(_.username.map(_.value))

    override def images: Seq[String] = self.images.map(_.url.value)
  }

  def apply(meta: models.TwitterCardMetadata) = StructuredTwitterCardMetadata(meta)

  implicit class StructuredSearchMetadata(val self: models.SearchMetadata)
    extends StructuredInformation {

    override def titles: Seq[String] = self.title.toSeq.map(_.value)

    override def keywords: Seq[String] = self.keywords.map(_.value)

    override def descriptions: Seq[String] = self.description.toSeq.map(_.value)

  }

  def apply(meta: models.SearchMetadata) = StructuredSearchMetadata(meta)

  implicit class StructuredSchemaOrgMetadata(val self: Seq[models.SchemaOrgItem])
    extends StructuredInformation {

    override def titles: Seq[String] = self.flatMap(_.name.map(_.value)) ++ self.flatMap(_.alternateName.map(_.value))

    override def urls: Seq[String] = Nil

    override def locations: Seq[String] = Nil

    override def keywords: Seq[String] = Nil

    override def descriptions: Seq[String] = self.flatMap(_.description.map(_.value))

    override def siteNames: Seq[String] = Nil

    override def siteTypes: Seq[String] = Nil

    override def images: Seq[String] = self.flatMap(_.image.map(_.value))
  }

  def apply(meta: Seq[models.SchemaOrgItem]) = StructuredSchemaOrgMetadata(meta)


  implicit class StructuredExtractedMetadata(val self: models.ExtractedMetadata)
    extends StructuredInformation {

    override def descriptions: Seq[String] = self.description.toSeq.map(_.value)

    override def images: Seq[String] = self.images.map(_.url.value)
  }

  def apply(meta: models.ExtractedMetadata) = StructuredExtractedMetadata(meta)

}

