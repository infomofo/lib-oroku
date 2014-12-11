package com.infomofo.oroku.structure

import com.infomofo.oroku.models

/**
 * Structured Information is a trait that unifies how information from various sources can be unified
 */
trait StructuredInformation {

  def urls: Seq[String]
  def titles: Seq[String]
  def keywords: Seq[String]
  def locations: Seq[String]
  def descriptions: Seq[String]
  def siteNames: Seq[String]
  def siteTypes: Seq[String]
  def images: Seq[String]
}

object StructuredInformation {
  implicit class StructuredOpenGraphMetadata(val self: models.OpenGraphMetadata)
    extends StructuredInformation {

    override def titles: Seq[String] = self.title.toSeq.map(_.value)

    override def urls: Seq[String] = self.url.toSeq.map(_.value)

    override def locations: Seq[String] = Nil

    override def keywords: Seq[String] = Nil

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

    override def locations: Seq[String] = Nil

    override def keywords: Seq[String] = Nil

    override def descriptions: Seq[String] = self.description.toSeq.map(_.value)

    override def siteNames: Seq[String] = self.site.toSeq.flatMap(_.username.map(_.value))

    override def siteTypes: Seq[String] = Nil

    override def images: Seq[String] = self.images.map(_.url.value)
  }

  def apply(meta: models.TwitterCardMetadata) = StructuredTwitterCardMetadata(meta)
}

