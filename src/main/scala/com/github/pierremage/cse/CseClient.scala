package com.github.pierremage.cse

import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.util.concurrent.Executor

import com.ning.http.client.{AsyncCompletionHandler, AsyncHttpClient, Response}

import scala.collection.immutable.Map
import scala.concurrent.{ExecutionContext, Future, Promise}
import scala.util.Try

/**
 * <a href="https://developers.google.com/custom-search/">CSE Documentation</a>
 */
class CseClient private(apiKey: String, cseId: String, httpClient: AsyncHttpClient)
  extends AutoCloseable {

  import CseClient._

  private val asyncCompletionHandler = new AsyncCompletionHandler[Either[CseError, CseSuccess]] {

    override def onCompleted(response: Response): Either[CseError, CseSuccess] =
      response.getStatusCode match {
        case 200 => Right(response.getResponseBodyAsStream)
        case _ => Left((response.getStatusCode, response.getResponseBody(StandardCharsets.UTF_8.name)))
      }
  }

  def search
    (searchTerms: String, params: Map[String, String] = Map.empty, prettyPrint: Boolean = false, alt: String = "json")
    (implicit ec: ExecutionContext)
    : Future[Either[CseError, CseSuccess]] = {

    //Wrap com.ning.http.client.ListenableFuture into scala.concurrent.Future
    //See https://github.com/dispatch/reboot/blob/master/core/src/main/scala/execution.scala
    //TODO Find a scala HTTP client that to avoid this wrapping.
    val lf = httpClient.prepareGet(url(apiKey, cseId, searchTerms, prettyPrint, alt, params).toString)
      .execute(asyncCompletionHandler)

    val p = Promise[Either[CseError, CseSuccess]]()
    lf.addListener(
      new Runnable {
        override def run() = p.complete(Try(lf.get()))
      },
      new Executor {
        override def execute(command: Runnable) = ec.execute(command)
      }
    )
    p.future
  }

  override def close() = httpClient.closeAsynchronously()

}

object CseClient {

  def apply(apiKey: String, cseId: String) = new CseClient(apiKey, cseId, new AsyncHttpClient)

  private def url
    (key: String, cx: String, q: String, prettyPrint: Boolean, alt: String, params: Map[String, String])
    : String = {

    val encodedQ: String = URLEncoder.encode(q, StandardCharsets.UTF_8.name)
    val additionalParams = params.view.map { case (name, value) => s"&$name=$value" }.mkString
    s"https://www.googleapis.com/customsearch/v1?key=$key&cx=$cx&q=$encodedQ&alt=$alt&prettyPrint=$prettyPrint$additionalParams"
  }

}