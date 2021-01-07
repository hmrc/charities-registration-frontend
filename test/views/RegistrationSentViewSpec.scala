/*
 * Copyright 2021 HM Revenue & Customs
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

  private val messageKeyPrefix = "registrationSent"
  private val section: Option[String] = Some(messages("declaration.section"))


  "RegistrationSentView for Email" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[RegistrationSentView](Some(emptyUserAnswers))
        view.apply(dayToString(
          inject[TimeMachine].now().plusDays(28)), dayToString(
          inject[TimeMachine].now()), "080582080582",
          emailOrPost = true, Seq.empty, None)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix, section = section)

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1", "submissionDate", "p3.beforeRefNo", "p3.afterRefNo", "p4.beforeRegistrations", "p4.keyWord", "p4.beforeRegNo", "p4.afterRegNo", "p9",
        "email.prefer.p", "whatHappensNext.p1", "whatHappensNext.p2", "whatHappensNext.p3", "changeSomething.p1")

      behave like pageWithHyperLink(applyView(), "link", frontendAppConfig.feedbackUrl(fakeRequest), messages("registrationSent.link"))

      "Contains the reference number" in{
       val doc = asDocument(applyView())
        assertContainsText(doc,"080582080582")
      }

    }

    "RegistrationSentView for Post" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[RegistrationSentView](Some(emptyUserAnswers))
        view.apply(dayToString(
          inject[TimeMachine].now().plusDays(28)), dayToString(
          inject[TimeMachine].now()),"080582080582",
          emailOrPost = false, Seq.empty, None)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix, section = section)

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1", "submissionDate", "p3.beforeRefNo", "p3.afterRefNo",
        "p4.receiveBy", "p4.applyAgain", "p4.postTo", "p9",
        "post.prefer.p", "whatHappensNext.p1", "whatHappensNext.p2", "whatHappensNext.p3", "changeSomething.p1")


      behave like pageWithHyperLink(applyView(), "link", frontendAppConfig.feedbackUrl(fakeRequest), messages("registrationSent.link"))

      "Contains the inset text" in {
        val doc = asDocument(applyView())
        assert(doc.getElementsByClass("govuk-inset-text").first.text == messages("registrationSent.warning"))
      }

      "Contains the address" in {
       val doc = asDocument(applyView())
        assertContainsText(doc,"Charities, Savings &amp; International 2")
        assertContainsText(doc,"HMRC")
        assertContainsText(doc,"BX9 1BU")
      }

      "Contains the reference number" in {
       val doc = asDocument(applyView())
        assertContainsText(doc,"080582080582")
      }

    }
  }
