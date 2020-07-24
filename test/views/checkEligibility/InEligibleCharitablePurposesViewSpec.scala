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

package views.checkEligibility

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.checkEligibility.InEligibleCharitablePurposesView

class InEligibleCharitablePurposesViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "notEligible"
  private val messageLink= messages("notEligible.p3.link")
  private val messageTabOrWindow = messages("site.opensInNewWindowOrTab")


    "InEligibleCharitablePurposesView" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[InEligibleCharitablePurposesView](Some(emptyUserAnswers))
        view.apply()(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix)

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "charitablePurposes.p1", "p2", "p3", "p3.link")

      behave like pageWithHyperLink(applyView(), "link",frontendAppConfig.getRecognition, messages(s"$messageLink $messageTabOrWindow"))

    }
  }
