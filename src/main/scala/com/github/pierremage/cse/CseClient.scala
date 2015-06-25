package com.github.pierremage.cse

import java.net.URLEncoder
import java.nio.charset.StandardCharsets

import scala.collection.immutable.Map
import scalaj.http.{Http, HttpResponse}

/**
 * <a href="https://developers.google.com/custom-search/">CSE Documentation</a>
 */
class CseClient private(apiKey: String, cseId: String) {

  import CseClient._

  def search
    (searchTerms: String, params: Map[String, String] = Map.empty, prettyPrint: Boolean = false, alt: String = "json")
    : Either[(Int, String), String] = {

    Http(url(apiKey, cseId, searchTerms, prettyPrint, alt, params).toString).asString match {
      case HttpResponse(body, 200, _) =>
        Right(body)
      case HttpResponse(body, code, _) =>
        Left((code, body))
    }
  }

}

object CseClient {

  def apply(apiKey: String, cseId: String) = new CseClient(apiKey, cseId)

  private def url
    (key: String, cx: String, q: String, prettyPrint: Boolean, alt: String, params: Map[String, String])
    : String = {

    val encodedQ: String = URLEncoder.encode(q, StandardCharsets.UTF_8.name)
    val additionalParams = params.view.map { case (name, value) => s"&$name=$value" }.mkString
    s"https://www.googleapis.com/customsearch/v1?key=$key&cx=$cx&q=$encodedQ&alt=$alt&prettyPrint=$prettyPrint$additionalParams"
  }

}