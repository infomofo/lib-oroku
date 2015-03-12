package com.infomofo.oroku.shredder

import java.awt.Toolkit
import java.io.ByteArrayInputStream
import java.net.{URLConnection, HttpURLConnection, URL}
import javax.imageio.ImageIO
import javax.swing.ImageIcon

import com.infomofo.oroku.v0.models
import com.typesafe.scalalogging.LazyLogging
import sun.misc.BASE64Decoder

import scala.collection.JavaConverters._
import scala.util.Try

/**
 * This trait defines the behavior for shredding data from the body of the document
 */
private[shredder] trait ExtractedMetadataShredder extends BodyShredder with LazyLogging {

  private val base64Regex = """.*data\:(image\/[a-z]+);base64,(.*)""".r

  private val numRegex = """^(\d+)$""".r
  private val pxRegex = """^(\d+)px$""".r
  private val decoder = new BASE64Decoder()

  private val bannerThreshold = 3.0 // discard images that are less that 1:3 or 3:1 in height:Width ratio

  /**
   * Return the image urls from the body in descending order of expected display size
   */
  protected def extractImageMediaObjects = {
    val imageElements = bodyElement.getElementsByTag("img")
    logger.warn(s"Found ${imageElements.size} images in the body")
    // Go through all images and return all images larger than minimum size in descending order
    imageElements.iterator().asScala.toSeq
      .par
      .flatMap {
      imageElement =>
        try {
          var urlString = imageElement.absUrl("src")
          if (urlString.isEmpty) {
            logger.warn(s"checking for other src")
            urlString = imageElement.attr("src")
            if (urlString.isEmpty) {
            logger.warn(s"checking for srcset")
              val srcset = imageElement.attr("srcset").split(",").last.trim.split(" ").head.trim
              logger.warn(s"srcset: $srcset")
              urlString = srcset
            }
          }
          urlString = urlString.replaceAll("[\n\r]", "").trim
          val (img, mimeTypeOption) = {
            urlString match {
              case base64Regex(explicitMimeType, encodedImage) =>
                val imgBytes = decoder.decodeBuffer(encodedImage)
                val bufferedImage = ImageIO.read(new ByteArrayInputStream(imgBytes))
                bufferedImage -> Some(explicitMimeType)
              case _ =>
                if (urlString.startsWith("//")) {
                  urlString = "http:" + urlString
                }
                val url: URL = try {
                  new URL(urlString)
                } catch {
                  case e: java.net.MalformedURLException =>
                    logger.warn(s"could not find url $urlString due to $e", e)
                    throw e
                }
                //          val img = Toolkit.getDefaultToolkit().getImage(url)
                val image = ImageIO.read(url)
                val mimeTypeTry = try {
                  val connection = url.openConnection().asInstanceOf[HttpURLConnection]
                  connection.setRequestMethod("HEAD")
                  connection.connect()
                  Some(connection.getContentType)
//                  Some(URLConnection.guessContentTypeFromStream(url.openStream()))
                } catch {
                  case e: Exception =>
//                    logger.warn(s"Could not find mimeType for $imageElement", e)
                    None
                }
                image -> mimeTypeTry
            }
          }
//            lazy val img = new ImageIcon(url)
          val imgHeight = {
            val specifiedHeight = imageElement.attr("height")
            specifiedHeight match {
              case numRegex(height) =>
                height.toInt
              case pxRegex(height) =>
                height.toInt
              case _ =>
                img.getHeight
            }
          }

            val imgWidth  = {
              val specifiedWidth = imageElement.attr("width")
              specifiedWidth match {
                case numRegex(width) =>
                  width.toInt
                case pxRegex(width) =>
                  width.toInt
                case _ =>
                  img.getWidth
              }
            }

            val mediaObject = models.MediaObject (
              url = models.MetaString (value = urlString, tag = imageElement.toString),
              mimeType = mimeTypeOption.map{mimeTypeValue => models.MetaString (value = mimeTypeValue, tag = null)},
              width = Some(models.MetaInteger(value = imgWidth, tag = null)),
              height = Some(models.MetaInteger(value = imgHeight, tag = null))
            )
            logger.warn(s"media object found: $mediaObject")
            Some(
              mediaObject
            ).filterNot(
              _.url.value.contains("sprite")
            )
        } catch {
          case e: Exception =>
            logger.warn(s"Could not fetch image with element $imageElement due to ${e.getMessage}", e)
            None
        }
    }
      .seq
      .filterNot(
      mediaObject =>
        (mediaObject.width.get.value.toFloat / mediaObject.height.get.value.toFloat) > bannerThreshold
    )
      .filterNot(
      mediaObject =>
        (mediaObject.height.get.value.toFloat / mediaObject.width.get.value.toFloat) > bannerThreshold
    ).filter {
      mediaObject =>
        (mediaObject.width.get.value * mediaObject.height.get.value) > 5000
    }
    .sortBy {
      mediaObject =>
        mediaObject.width.get.value * mediaObject.height.get.value
    }.reverse
    .take(5)
  }
  /**
   * Data that can be parsed from the headers matching common search metadata format
   */
  lazy val extractedMetadata = {
    Try {
      logger.warn("extracting metadata")
      val description = Option.empty[models.MetaString]
      val text = Option.empty[models.MetaString]
      val images = extractImageMediaObjects
      logger.warn(s"found images: $images")
      Some(models.ExtractedMetadata(
        description = description,
        text = text,
        images = images
      ))
    }.recover {
      case e =>
        logger.warn("Could not find expected metadata for conventional article extraction metadata", e)
        None
    }.get
  }
}

