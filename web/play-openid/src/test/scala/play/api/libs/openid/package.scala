/*
 * Copyright (C) from 2022 The Play Framework Contributors <https://github.com/playframework>, 2011-2021 Lightbend Inc. <https://www.lightbend.com>
 */

package play.api.libs

import java.net.MalformedURLException
import java.net.URL

import scala.io.Source
import scala.jdk.CollectionConverters._
import scala.language.implicitConversions

import play.shaded.ahc.io.netty.handler.codec.http.QueryStringDecoder
import util.control.Exception._

package object openid {
  type Params = Map[String, Seq[String]]

  implicit def stringToSeq(s: String): Seq[String] = Seq(s)

  implicit def urlToRichUrl(url: URL): RichUrl[URL] = new RichUrl[URL] {
    def hostAndPath = new URL(url.getProtocol, url.getHost, url.getPort, url.getPath).toExternalForm
  }

  def readFixture(filePath: String): String = this.synchronized {
    Source.fromInputStream(this.getClass.getResourceAsStream(filePath)).mkString
  }

  def parseQueryString(url: String): Params = {
    catching(classOf[MalformedURLException])
      .opt(new URL(url))
      .map { url =>
        new QueryStringDecoder(url.toURI.getRawQuery, false).parameters().asScala.view.mapValues(_.asScala.toSeq).toMap
      }
      .getOrElse(Map())
  }

  // See 10.1 - Positive Assertions
  // http://openid.net/specs/openid-authentication-2_0.html#positive_assertions
  def createDefaultResponse(
      claimedId: String,
      identity: String,
      defaultSigned: String = "op_endpoint,claimed_id,identity,return_to,response_nonce,assoc_handle"
  ): Map[String, Seq[String]] = Map(
    "openid.ns"             -> "http://specs.openid.net/auth/2.0",
    "openid.mode"           -> "id_res",
    "openid.op_endpoint"    -> "https://www.google.com/a/example.com/o8/ud?be=o8",
    "openid.claimed_id"     -> claimedId,
    "openid.identity"       -> identity,
    "openid.return_to"      -> "https://example.com/openid?abc=false",
    "openid.response_nonce" -> "2012-05-25T06:47:55ZEJvRv76xQcWbTG",
    "openid.assoc_handle"   -> "AMlYA9VC8_UIj4-y4K_X2E_mdv-123-ABC",
    "openid.signed"         -> defaultSigned,
    "openid.sig"            -> "MWRsJZ/9AOMQt9gH6zTZIfIjk6g="
  )
}
