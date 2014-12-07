package com.infomofo.oroku.structure

import com.infomofo.oroku.models

/**
 * Structured Information is a trait that unifies how information from various sources can be unified
 */
trait StructuredInformation {
  def titles: Seq[String]
  def urls: Seq[String]
  def keywords: Seq[String]
  def locations: Seq[String]
}

object StructuredInformation {
  implicit class StructuredOpenGraphMetadata(val self: models.OpenGraphMetadata)
    extends StructuredInformation {

    override def titles: Seq[String] = Seq(self.title.value)

    override def urls: Seq[String] = Seq(self.url.value)

    override def locations: Seq[String] = Nil

    override def keywords: Seq[String] = Nil

  }

  def apply(meta: models.OpenGraphMetadata) = StructuredOpenGraphMetadata(meta)
}