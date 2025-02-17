package org.ergoplatform.http.routes

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.{ScalatestRouteTest, RouteTestTimeout}
import akka.testkit.TestDuration
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.Json
import io.circe.syntax._
import org.ergoplatform.http.api.EmissionApiRoute
import org.ergoplatform.mining.emission.EmissionRules
import org.ergoplatform.settings.ErgoSettings
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers

import scala.concurrent.duration._

class EmissionApiRouteSpec extends AnyFlatSpec
  with Matchers
  with ScalatestRouteTest
  with FailFastCirceSupport {

  val prefix = "/emission/at"

  implicit val timeout: RouteTestTimeout = RouteTestTimeout(15.seconds.dilated)

  val ergoSettings: ErgoSettings = ErgoSettings.read()
  val coinEmission: EmissionRules = new EmissionRules(ergoSettings.chainSettings.monetary)

  val route: Route = EmissionApiRoute(ergoSettings).route

  it should "get correct emission values" in {
    Get(prefix + "/1") ~> route ~> check {
      status shouldBe StatusCodes.OK
      EmissionApiRoute.emissionInfoAtHeight(1L, coinEmission).asJson shouldEqual responseAs[Json]
    }

    Get(prefix + "/10000") ~> route ~> check {
      status shouldBe StatusCodes.OK
      EmissionApiRoute.emissionInfoAtHeight(10000L, coinEmission).asJson shouldEqual responseAs[Json]
    }

    Get(prefix + "/1000000") ~> route ~> check {
      status shouldBe StatusCodes.OK
      EmissionApiRoute.emissionInfoAtHeight(1000000L, coinEmission).asJson shouldEqual responseAs[Json]
    }
  }

}
