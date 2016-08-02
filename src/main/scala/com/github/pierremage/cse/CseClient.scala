package com.github.pierremage.cse

import java.net.URLEncoder
import java.nio.charset.StandardCharsets.UTF_8

import scala.collection.immutable.Map
import scalaj.http.{Http, HttpResponse}

/**
  * <a href="https://developers.google.com/custom-search/">CSE Documentation</a>
  */
class CseClient(apiKey: String, cseId: String, endpoint: String = CseClient.defaultEndpoint) {

  def search(
    searchTerms: String,
    params: Map[String, String] = Map.empty,
    prettyPrint: Boolean = false,
    alt: String = "json")
  : Either[(Int, String), String] = {

    val httpRequest = Http(endpoint).
      param("key", apiKey).
      param("cx", cseId).
      param("q", URLEncoder.encode(searchTerms, UTF_8.name)).
      params(params).
      param("prettyPrint", prettyPrint.toString).
      param("alt", alt)

    httpRequest.asString match {
      case HttpResponse(body, 200, _) =>
        Right(body)
      case HttpResponse(body, code, _) =>
        Left(code -> body)
    }
  }

}

object CseClient {
  val defaultEndpoint = "https://www.googleapis.com/customsearch/v1"
}