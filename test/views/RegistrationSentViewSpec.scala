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

package views

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.RegistrationSentView

class RegistrationSentViewSpec extends ViewBehaviours  {

  private val messageKeyPrefix = "registrationSent"

    "RegistrationSentView" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[RegistrationSentView](Some(emptyUserAnswers))
        view.apply()(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix)

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1", "p2" , "p3", "p4", "p8", "p9")

      behave like pageWithWarningText(applyView(), messages("registrationSent.warning"))

      behave like pageWithHyperLink(applyView(), "link",frontendAppConfig.feedbackUrl(fakeRequest), messages("registrationSent.link"))

      "Contains the address" in{
       val doc = asDocument(applyView())
        assertContainsText(doc,"Charities, Savings &amp; International 2")
        assertContainsText(doc,"HMRC")
        assertContainsText(doc,"BX9 1BU")
      }

    }
  }
