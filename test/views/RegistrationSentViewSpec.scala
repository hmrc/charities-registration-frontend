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

package views

import play.twirl.api.HtmlFormat
import utils.{ImplicitDateFormatter, TimeMachine}
import views.behaviours.ViewBehaviours
import views.html.RegistrationSentView

class RegistrationSentViewSpec extends ViewBehaviours with ImplicitDateFormatter {

  private val messageKeyPrefix: String      = "registrationSent"
  private val section: Option[String]       = Some(messages("declaration.section"))
  private val firstLinkContent: String      = "#"
  private val registrationExpiryLimit: Long = 28
  private val view: RegistrationSentView    = viewFor[RegistrationSentView](Some(emptyUserAnswers))

  private def viewViaApply(emailOrPost: Boolean = true, noEmailOrPost: Boolean = false): HtmlFormat.Appendable =
    view.apply(
      dayToString(inject[TimeMachine].now().plusDays(registrationExpiryLimit)),
      dayToString(inject[TimeMachine].now()),
      "080582080582",
      emailOrPost = emailOrPost,
      noEmailOrPost = noEmailOrPost,
      Seq.empty,
      None
    )(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(
      dayToString(inject[TimeMachine].now().plusDays(registrationExpiryLimit)),
      dayToString(inject[TimeMachine].now()),
      "080582080582",
      emailOrPost = true,
      noEmailOrPost = false,
      Seq.empty,
      None,
      fakeRequest,
      messages,
      frontendAppConfig
    )

  private val viewViaF: HtmlFormat.Appendable =
    view.f(
      dayToString(inject[TimeMachine].now().plusDays(registrationExpiryLimit)),
      dayToString(inject[TimeMachine].now()),
      "080582080582",
      true,
      false,
      Seq.empty,
      None
    )(fakeRequest, messages, frontendAppConfig)

  "RegistrationSentView for Email" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = section)

        behave like pageWithPrintOrDownloadLink(
          view,
          "printOrDownloadlink",
          firstLinkContent,
          messages("registrationSent.printOrDownload")
        )

        behave like pageWithAdditionalGuidance(
          view,
          messageKeyPrefix,
          "p1",
          "submissionDate",
          "p3.beforeRefNo",
          "p3.afterRefNo",
          "p4.beforeRegistrations",
          "p4.keyWord",
          "p4.beforeRegNo",
          "p4.afterRegNo",
          "p9",
          "email.prefer.p",
          "whatHappensNext.p1",
          "whatHappensNext.p2",
          "whatHappensNext.p3",
          "changeSomething.p1"
        )

        behave like pageWithHyperLink(
          view,
          "link",
          frontendAppConfig.exitSurveyUrl,
          messages("registrationSent.link")
        )

        "contain the reference number" in {
          val doc = asDocument(view)
          assertContainsText(doc, "080582080582")
        }
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply()),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }

  "RegistrationSentView for Post" when {
    ".apply" must {
      behave like normalPage(viewViaApply(emailOrPost = false), messageKeyPrefix, section = section)

      behave like pageWithAdditionalGuidance(
        viewViaApply(emailOrPost = false),
        messageKeyPrefix,
        "p1",
        "submissionDate",
        "p3.beforeRefNo",
        "p3.afterRefNo",
        "p4.receiveBy",
        "p4.applyAgain",
        "p4.postTo",
        "p9",
        "post.prefer.p",
        "whatHappensNext.p1",
        "whatHappensNext.p2",
        "whatHappensNext.p3",
        "changeSomething.p1"
      )

      behave like pageWithHyperLink(
        viewViaApply(emailOrPost = false),
        "link",
        frontendAppConfig.exitSurveyUrl,
        messages("registrationSent.link")
      )

      "contain the inset text" in {
        val doc = asDocument(viewViaApply(emailOrPost = false))
        assert(doc.getElementsByClass("govuk-inset-text").first.text == messages("registrationSent.warning"))
      }

      "contain the address" in {
        val doc = asDocument(viewViaApply(emailOrPost = false))
        assertContainsText(doc, "Charities, Savings &amp; International 2")
        assertContainsText(doc, "HMRC")
        assertContainsText(doc, "BX9 1BU")
      }

      "contain the reference number" in {
        val doc = asDocument(viewViaApply(emailOrPost = false))
        assertContainsText(doc, "080582080582")
      }
    }
  }

  "RegistrationSentView with noEmailPost enabled" when {
    ".apply" must {
      behave like normalPage(viewViaApply(noEmailOrPost = true), messageKeyPrefix, section = section)

      behave like pageWithAdditionalGuidance(
        viewViaApply(noEmailOrPost = true),
        messageKeyPrefix,
        "no.email.h2",
        "no.email.p1"
      )
    }
  }
}
