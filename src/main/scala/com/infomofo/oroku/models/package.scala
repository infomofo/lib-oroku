package com.infomofo.oroku.models {

  /**
   * Information provided to show a smart app banner to direct the user to an
   * equivalent app store listing when viewing this webpage on iOS.  See
   * https://developer.apple.com/library/ios/documentation/AppleApplications/Reference/SafariWebContent/PromotingAppswithAppBanners/PromotingAppswithAppBanners.html
   */
  case class AppleItunesMetadata(
    appId: com.infomofo.oroku.models.MetaInteger,
    affiliateData: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    appArgument: scala.Option[com.infomofo.oroku.models.MetaString] = None
  )

  /**
   * A brand is a name used by an organization or business person for labeling a
   * product, product group, or similar.
   */
  case class BrandObject(
    logo: scala.Option[String] = None,
    name: scala.Option[String] = None
  )

  /**
   * An html head meta tag that has been parsed for boolean content.
   */
  case class MetaBoolean(
    value: Boolean,
    tag: String
  )

  /**
   * An html head meta tag that has been parsed for date time content.
   */
  case class MetaDateTimeIso8601(
    value: _root_.org.joda.time.DateTime,
    tag: String
  )

  /**
   * An html head meta tag that has been parsed for integer content.
   */
  case class MetaInteger(
    value: Int,
    tag: String
  )

  /**
   * An html head meta tag that has been parsed for open_graph_type content.
   */
  case class MetaOpenGraphType(
    value: com.infomofo.oroku.models.OpenGraphType,
    tag: String
  )

  /**
   * An html head meta tag that has been parsed for string content.
   */
  case class MetaString(
    value: String,
    tag: String
  )

  /**
   * An html head meta tag that has been parsed for twitter_card_type content.
   */
  case class MetaTwitterCardType(
    value: com.infomofo.oroku.models.TwitterCardType,
    tag: String
  )

  /**
   * A media type specified by open graph. The og:image, og:audio, and og:video
   * property has some optional structured properties.
   */
  case class OpenGraphMedia(
    url: com.infomofo.oroku.models.MetaString,
    secureUrl: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    mimeType: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    width: scala.Option[com.infomofo.oroku.models.MetaInteger] = None,
    height: scala.Option[com.infomofo.oroku.models.MetaInteger] = None
  )

  /**
   * Information about the site presented in opengraph format (see ogp.me)
   */
  case class OpenGraphMetadata(
    title: com.infomofo.oroku.models.MetaString,
    openGraphType: com.infomofo.oroku.models.MetaOpenGraphType,
    image: com.infomofo.oroku.models.OpenGraphMedia,
    url: com.infomofo.oroku.models.MetaString,
    description: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    determiner: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    locale: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    localeAlternative: Seq[com.infomofo.oroku.models.MetaString] = Nil,
    siteName: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    video: scala.Option[com.infomofo.oroku.models.OpenGraphMedia] = None,
    audio: scala.Option[com.infomofo.oroku.models.OpenGraphMedia] = None,
    updatedTime: scala.Option[com.infomofo.oroku.models.MetaDateTimeIso8601] = None,
    seeAlso: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    appId: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    profileId: scala.Option[com.infomofo.oroku.models.MetaString] = None
  )

  /**
   * The basic information on this webpage, according to major metadata tagging
   * standards and conventions, as well as information derived from the content of
   * the page itself.
   */
  case class PageInfo(
    titles: Seq[String],
    images: Seq[String] = Nil,
    descriptions: Seq[String] = Nil,
    urls: Seq[String],
    keywords: Seq[String],
    categories: Seq[String] = Nil,
    locations: Seq[String],
    retrievedAt: _root_.org.joda.time.DateTime,
    site: com.infomofo.oroku.models.Site,
    openGraphMetadata: scala.Option[com.infomofo.oroku.models.OpenGraphMetadata] = None,
    appleItunesMetadata: scala.Option[com.infomofo.oroku.models.AppleItunesMetadata] = None,
    twitterCardMetadata: scala.Option[com.infomofo.oroku.models.TwitterCardMetadata] = None,
    schemaOrgItems: Seq[com.infomofo.oroku.models.SchemaOrgItem] = Nil,
    searchMetadata: scala.Option[com.infomofo.oroku.models.SearchMetadata] = None
  )

  /**
   * The representation of an item according to schema.org. schema.org is a
   * collaboration by Google, Microsoft, and Yahoo! to improve the web by creating a
   * structured data markup schema supported by major search engines. Multiple item
   * scopes can be included on one page.  Only the most common properties are
   * currently parsed. See: http://schema.org/docs/gs.html.
   */
  case class SchemaOrgItem(
    itemType: scala.Option[String] = None,
    name: scala.Option[String] = None,
    alternateName: scala.Option[String] = None,
    description: scala.Option[String] = None,
    image: scala.Option[String] = None,
    url: scala.Option[String] = None,
    sameAs: scala.Option[String] = None,
    brand: scala.Option[com.infomofo.oroku.models.BrandObject] = None,
    color: scala.Option[String] = None,
    productId: scala.Option[String] = None,
    sku: scala.Option[String] = None,
    model: scala.Option[String] = None,
    gtin8: scala.Option[String] = None,
    gtin13: scala.Option[String] = None,
    gtin14: scala.Option[String] = None
  )

  /**
   * Meta tags provide search engines with information about their sites. Meta tags
   * can be used to provide information to all sorts of clients, and each system
   * processes only the meta tags they understand and ignores the rest.  See
   * https://support.google.com/webmasters/answer/79812?hl=en.
   */
  case class SearchMetadata(
    description: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    title: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    robots: Seq[com.infomofo.oroku.models.MetaString] = Nil,
    googlebot: Seq[com.infomofo.oroku.models.MetaString] = Nil,
    googleSiteLinkSearchBox: scala.Option[com.infomofo.oroku.models.MetaBoolean] = None,
    googleNotranslate: scala.Option[com.infomofo.oroku.models.MetaBoolean] = None,
    googleSiteVerification: Seq[com.infomofo.oroku.models.MetaString] = Nil,
    charset: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    contentType: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    keywords: Seq[com.infomofo.oroku.models.MetaString] = Nil
  )

  /**
   * information about the domain that this site is hosted on.
   */
  case class Site(
    name: String,
    siteType: scala.Option[String] = None,
    domain: scala.Option[String] = None
  )

  /**
   * A twitter card to detail a mobile app with direct download.
   */
  case class TwitterAppInfo(
    country: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    iphone: scala.Option[com.infomofo.oroku.models.TwitterAppStoreInfo] = None,
    ipad: scala.Option[com.infomofo.oroku.models.TwitterAppStoreInfo] = None,
    googleplay: scala.Option[com.infomofo.oroku.models.TwitterAppStoreInfo] = None
  )

  /**
   * Information linking twitter card data to an app store. See
   * https://dev.twitter.com/cards/types/app
   */
  case class TwitterAppStoreInfo(
    name: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    id: com.infomofo.oroku.models.MetaString,
    url: scala.Option[com.infomofo.oroku.models.MetaString] = None
  )

  /**
   * Additinal information provided by a webpage to control how a link to that page
   * is rendered when posted on twitter. See also:
   * https://dev.twitter.com/cards/markup.
   */
  case class TwitterCardMetadata(
    card: com.infomofo.oroku.models.MetaTwitterCardType,
    domain: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    site: scala.Option[com.infomofo.oroku.models.TwitterUser] = None,
    creator: scala.Option[com.infomofo.oroku.models.TwitterUser] = None,
    description: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    title: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    images: Seq[com.infomofo.oroku.models.TwitterImage] = Nil,
    app: scala.Option[com.infomofo.oroku.models.TwitterAppInfo] = None,
    customFields: Seq[com.infomofo.oroku.models.TwitterCustomField] = Nil
  )

  /**
   * A customizable data field to display on a twitter card
   */
  case class TwitterCustomField(
    label: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    data: com.infomofo.oroku.models.MetaString
  )

  /**
   * An image represented on a twitter card for a page.
   */
  case class TwitterImage(
    src: com.infomofo.oroku.models.MetaString,
    width: scala.Option[com.infomofo.oroku.models.MetaInteger] = None,
    height: scala.Option[com.infomofo.oroku.models.MetaInteger] = None
  )

  /**
   * User info declared for parsing into a twitter card.  See:
   * https://dev.twitter.com/cards/markup
   */
  case class TwitterUser(
    username: scala.Option[com.infomofo.oroku.models.MetaString] = None,
    id: scala.Option[com.infomofo.oroku.models.MetaInteger] = None
  )

  /**
   * The type of your object according to the open graph protocol.  When the
   * community agrees on the schema for a type, it is added to the list of global
   * types. All other objects in the type system are CURIEs of the form.
   */
  sealed trait OpenGraphType

  object OpenGraphType {

    case object Article extends OpenGraphType { override def toString = "article" }
    case object Book extends OpenGraphType { override def toString = "book" }
    case object Profile extends OpenGraphType { override def toString = "profile" }
    case object Website extends OpenGraphType { override def toString = "website" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends OpenGraphType

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Article, Book, Profile, Website)

    private[this]
    val byName = all.map(x => x.toString -> x).toMap

    def apply(value: String): OpenGraphType = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): scala.Option[OpenGraphType] = byName.get(value)

  }

  /**
   * The card type specified for this page that will affect how a link to this page
   * will be rendered if posted on twitter. See: https://dev.twitter.com/cards/types.
   */
  sealed trait TwitterCardType

  object TwitterCardType {

    /**
     * Default Card, including a title, description, thumbnail, and Twitter account
     * attribution. See https://dev.twitter.com/cards/types/summary.
     */
    case object Summary extends TwitterCardType { override def toString = "summary" }
    /**
     * Similar to a Summary Card, but with a prominently featured image. See
     * https://dev.twitter.com/cards/types/summary-large-image
     */
    case object SummaryLargeImage extends TwitterCardType { override def toString = "summary_large_image" }
    /**
     * A Card with a photo only. See https://dev.twitter.com/cards/types/photo.
     */
    case object Photo extends TwitterCardType { override def toString = "photo" }
    /**
     * A Card highlighting a collection of four photos. See
     * https://dev.twitter.com/cards/types/gallery
     */
    case object Gallery extends TwitterCardType { override def toString = "gallery" }
    /**
     * A Card to detail a mobile app with direct download.  See
     * https://dev.twitter.com/cards/types/app
     */
    case object App extends TwitterCardType { override def toString = "app" }
    /**
     * A Card to provide video/audio/media. See
     * https://dev.twitter.com/cards/types/player
     */
    case object Player extends TwitterCardType { override def toString = "player" }
    /**
     * A Card optimized for product information. See
     * https://dev.twitter.com/cards/types/product.
     */
    case object Product extends TwitterCardType { override def toString = "product" }

    /**
     * UNDEFINED captures values that are sent either in error or
     * that were added by the server after this library was
     * generated. We want to make it easy and obvious for users of
     * this library to handle this case gracefully.
     *
     * We use all CAPS for the variable name to avoid collisions
     * with the camel cased values above.
     */
    case class UNDEFINED(override val toString: String) extends TwitterCardType

    /**
     * all returns a list of all the valid, known values. We use
     * lower case to avoid collisions with the camel cased values
     * above.
     */
    val all = Seq(Summary, SummaryLargeImage, Photo, Gallery, App, Player, Product)

    private[this]
    val byName = all.map(x => x.toString -> x).toMap

    def apply(value: String): TwitterCardType = fromString(value).getOrElse(UNDEFINED(value))

    def fromString(value: String): scala.Option[TwitterCardType] = byName.get(value)

  }

}