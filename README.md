# lib-oroku

oroku is a scala library for shredding useful information from webpages

## Build status

[![Build Status](https://travis-ci.org/infomofo/lib-oroku.svg)](https://travis-ci.org/infomofo/lib-oroku)

## Installation

TODO: Publish lib-oroku so it can be imported by other projects

## Usage

Construct a PageShredder object

    val page = PageShredder(source)

Source can be of type java.net.URL, indicating a webpage address, scala.io.Source or String indicating the source of
a webpage, or org.jsoup.nodes.Document.

PageShredder provides methods to get specific types of information that can be shredded from the webpage document.

    page.pageInfo

That pageInfo object contains subObjects from various raw sources

    openGraphMetadata: scala.Option[com.infomofo.oroku.models.OpenGraphMetadata]
    appleItunesMetadata: scala.Option[com.infomofo.oroku.models.AppleItunesMetadata]
    twitterCardMetadata: scala.Option[com.infomofo.oroku.models.TwitterCardMetadata]
    schemaOrgItems: Seq[com.infomofo.oroku.models.SchemaOrgItem]
    searchMetadata: scala.Option[com.infomofo.oroku.models.SearchMetadata]

In addition, it provides aggregate values that have been aggregated from all sources with common data types.

    titles: Seq[String]
    images: Seq[String]
    descriptions: Seq[String]
    urls: Seq[String]
    keywords: Seq[String]
    categories: Seq[String]
    locations: Seq[String]
    retrievedAt: org.joda.time.DateTime

## Contributing

1. Fork it!
2. Create your feature branch: `git checkout -b my-new-feature`
3. Commit your changes: `git commit -am 'Add some feature'`
4. Push to the branch: `git push origin my-new-feature`
5. Submit a pull request :D

## History

TODO: Write History

## Credits

TODO: Write credits

## License

TODO: Write license
