package com.infomofo.oroku.shredder

import org.jsoup.nodes.Element

/**
 * A trait for shredding information on a page contained in tags in the body of the document
 */
trait BodyShredder {

  protected def bodyElement: Element
//  protected val usedHeadTags = new mutable.HashSet[Element]()

}

