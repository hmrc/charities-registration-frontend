/*
 * Copyright 2026 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package audit

import config.FrontendAppConfig
import models.AuditTypes.{CharitiesRegistrationSubmission, NewUser, PartialUserTransfer}
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.*
import org.scalatest.*
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.http.connector.AuditResult.Success
import uk.gov.hmrc.play.audit.model.ExtendedDataEvent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditServiceSpec extends PlaySpec with GuiceOneAppPerSuite with Inside with BeforeAndAfterEach {

  private val mockAuditConnector = mock(classOf[AuditConnector])

  override lazy val app: Application = new GuiceApplicationBuilder()
    .overrides(
      bind[AuditConnector].toInstance(mockAuditConnector)
    )
    .build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditConnector)
  }

  private val auditService      = app.injector.instanceOf[AuditService]
  private val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  case class TestAuditEvent(eventData: String) extends AuditEvent {
    override def auditType: String = "TestAudit"

    override def transactionName: String = "testTransactionName"

    override def details: JsValue =
      Json.parse("""{"test-audit": { "foo": "test" }}""")
  }

  val testAuditEvent: TestAuditEvent = TestAuditEvent("test-audit")

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  "AuditService" must {

    "successfully sent audit event" in {

      val templateCaptor: ArgumentCaptor[ExtendedDataEvent] = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future(Success))

      auditService.sendEvent(testAuditEvent)

      verify(mockAuditConnector, times(1)).sendExtendedEvent(templateCaptor.capture())(any(), any())

      val event = templateCaptor.getValue
      event.auditSource mustBe frontendAppConfig.appName
      event.auditType mustBe "TestAudit"
      event.tags.get("transactionName") mustBe Some("testTransactionName")
      event.detail mustBe Json.parse("""{"test-audit": { "foo": "test" }}""")
    }

    "successfully sent SubmissionAuditEvent" in {
      val templateCaptor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future(Success))

      auditService.sendEvent(SubmissionAuditEvent(Json.parse("""{"this": { "is": "a test" }}""")))
      verify(mockAuditConnector, times(1)).sendExtendedEvent(templateCaptor.capture())(any(), any())

      val event = templateCaptor.getValue
      event.auditSource mustBe frontendAppConfig.appName
      event.auditType mustBe CharitiesRegistrationSubmission.toString
      event.tags.get("transactionName") mustBe Some("CharityRegistrationSubmission")
      event.detail mustBe Json.parse("""{"this": { "is": "a test" }}""")
    }

    "successfully sent SwitchOverAuditEvent" in {
      val templateCaptor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future(Success))

      auditService.sendEvent(
        SwitchOverAuditEvent(Json.parse("""{"this": { "is": "a test" }}"""), PartialUserTransfer.toString)
      )
      verify(mockAuditConnector, times(1)).sendExtendedEvent(templateCaptor.capture())(any(), any())

      val event = templateCaptor.getValue
      event.auditSource mustBe frontendAppConfig.appName
      event.tags.get("transactionName") mustBe Some("CharitiesSwitchOver")
      event.auditType mustBe PartialUserTransfer.toString
      event.detail mustBe Json.parse("""{"this": { "is": "a test" }}""")
    }

    "successfully sent NormalUserAuditEvent" in {
      val templateCaptor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when(mockAuditConnector.sendExtendedEvent(any())(any(), any())).thenReturn(Future(Success))

      auditService.sendEvent(NormalUserAuditEvent(Json.parse("""{"this": { "is": "a test" }}"""), NewUser.toString))
      verify(mockAuditConnector, times(1)).sendExtendedEvent(templateCaptor.capture())(any(), any())

      val event = templateCaptor.getValue
      event.auditSource mustBe frontendAppConfig.appName
      event.tags.get("transactionName") mustBe Some("CharitiesRewriteUser")
      event.auditType mustBe NewUser.toString
      event.detail mustBe Json.parse("""{"this": { "is": "a test" }}""")
    }

    "failed to sent audit event" in {

      val templateCaptor = ArgumentCaptor.forClass(classOf[ExtendedDataEvent])

      when(mockAuditConnector.sendExtendedEvent(any())(any(), any()))
        .thenReturn(Future.failed(new Exception("Failed test exception")))

      auditService.sendEvent(testAuditEvent)

      verify(mockAuditConnector, times(1)).sendExtendedEvent(templateCaptor.capture())(any(), any())

      val event = templateCaptor.getValue
      event.auditSource mustBe frontendAppConfig.appName
      event.auditType mustBe "TestAudit"
      event.detail mustBe Json.parse("""{"test-audit": { "foo": "test" }}""")
    }

  }
}
