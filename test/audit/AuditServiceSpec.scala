/*
 * Copyright 2020 HM Revenue & Customs
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
import org.mockito.ArgumentCaptor
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.{reset, times, verify, when}
import org.scalatest._
import org.scalatestplus.mockito.MockitoSugar
import org.scalatestplus.play.PlaySpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.play.audit.http.connector.AuditConnector
import uk.gov.hmrc.play.audit.http.connector.AuditResult.{Failure, Success}
import uk.gov.hmrc.play.audit.model.DataEvent

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class AuditServiceSpec extends PlaySpec with GuiceOneAppPerSuite with Inside with MockitoSugar with BeforeAndAfterEach {

  private val mockAuditConnector = mock[AuditConnector]

  override lazy val app: Application = new GuiceApplicationBuilder().overrides(
      bind[AuditConnector].toInstance(mockAuditConnector)
    ).build()

  override def beforeEach(): Unit = {
    super.beforeEach()
    reset(mockAuditConnector)
  }

  private val auditService = app.injector.instanceOf[AuditService]
  private val frontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  case class TestAuditEvent(eventData: String) extends AuditEvent {
    override def auditType: String = "TestAudit"

    override def transactionName: String = "testTransactionName"

    override def details: Map[String, String] = Map(
      "auditData" -> eventData
    )
  }

  val testAuditEvent: TestAuditEvent = TestAuditEvent("test-audit")

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  "AuditService" must {

    "successfully sent audit event" in {

      val templateCaptor = ArgumentCaptor.forClass(classOf[DataEvent])

      when(mockAuditConnector.sendEvent(any())(any(), any())).thenReturn(Future(Success))

      auditService.sendEvent(testAuditEvent)

      verify(mockAuditConnector, times(1)).sendEvent(templateCaptor.capture())

      inside(templateCaptor.getValue) {
        case DataEvent(auditSource, auditType, _, tag, detail, _) =>
          auditSource mustBe frontendAppConfig.appName
          auditType mustBe "TestAudit"
          tag.get("transactionName") mustBe Some("testTransactionName")
          detail mustBe Map("auditData" -> "test-audit")
      }
    }

    "successfully sent DeclarationAuditEvent" in {

      val templateCaptor = ArgumentCaptor.forClass(classOf[DataEvent])

      when(mockAuditConnector.sendEvent(any())(any(), any())).thenReturn(Future(Success))

      auditService.sendEvent(DeclarationAuditEvent(true))

      verify(mockAuditConnector, times(1)).sendEvent(templateCaptor.capture())

      inside(templateCaptor.getValue) {
        case DataEvent(auditSource, auditType, _, tag, detail, _) =>
          auditSource mustBe frontendAppConfig.appName
          tag.get("transactionName") mustBe Some("CharityDeclarationSubmission")
          auditType mustBe "Declaration"
          detail mustBe Map("declaration" -> "true")
      }
    }

    "failed to sent audit event" in {

      val templateCaptor = ArgumentCaptor.forClass(classOf[DataEvent])

      when(mockAuditConnector.sendEvent(any())(any(), any())).thenReturn(Future(Failure("Auditing Failed")))

      auditService.sendEvent(testAuditEvent)

      verify(mockAuditConnector, times(1)).sendEvent(templateCaptor.capture())

      inside(templateCaptor.getValue) {
        case DataEvent(auditSource, auditType, _, _, detail, _) =>
          auditSource mustBe frontendAppConfig.appName
          auditType mustBe "TestAudit"
          detail mustBe Map("auditData" -> "test-audit")
      }
    }

  }
}
