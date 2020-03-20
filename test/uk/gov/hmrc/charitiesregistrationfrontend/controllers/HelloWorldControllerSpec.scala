package uk.gov.hmrc.charitiesregistrationfrontend.controllers

import org.scalatest.{Matchers, WordSpec}
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.http.Status
import play.api.mvc.{AnyContent, DefaultActionBuilder, DefaultMessagesActionBuilderImpl, DefaultMessagesControllerComponents, MessagesActionBuilder, MessagesControllerComponents}
import play.api.test.FakeRequest
import play.api.test.Helpers._
import play.api.{Configuration, Environment, _}
import uk.gov.hmrc.play.bootstrap.config.{RunMode, ServicesConfig}
import play.api.test.Helpers.stubControllerComponents
import uk.gov.hmrc.charitiesregistrationfrontend.config.AppConfig

import scala.concurrent.ExecutionContext
import scala.concurrent.ExecutionContext.Implicits.global

class HelloWorldControllerSpec extends WordSpec with Matchers with GuiceOneAppPerSuite {
  private val fakeRequest = FakeRequest("GET", "/")

  private val env           = Environment.simple()
  private val configuration = Configuration.load(env)

  private val serviceConfig = new ServicesConfig(configuration, new RunMode(configuration, Mode.Dev))
  private val appConfig     = new AppConfig(configuration, serviceConfig)

	private val messagesActionBuilder: MessagesActionBuilder = new DefaultMessagesActionBuilderImpl(stubBodyParser[AnyContent](), stubMessagesApi())

	lazy val stubMessagesControllerComponents: MessagesControllerComponents = DefaultMessagesControllerComponents(
		messagesActionBuilder,
		DefaultActionBuilder(stubBodyParser[AnyContent]()),
		stubControllerComponents().parsers,
		stubMessagesApi(),
		stubControllerComponents().langs,
		stubControllerComponents().fileMimeTypes,
		ExecutionContext.global
	)

  private val controller = new HelloWorldController(appConfig, stubMessagesControllerComponents)

  "GET /" should {
    "return 200" in {
      val result = controller.helloWorld(fakeRequest)
      status(result) shouldBe Status.OK
    }

    "return HTML" in {
      val result = controller.helloWorld(fakeRequest)
      contentType(result) shouldBe Some("text/html")
      charset(result)     shouldBe Some("utf-8")
    }

  }
}
