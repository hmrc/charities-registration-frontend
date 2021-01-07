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

package views.checkEligibility

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.checkEligibility.IncorrectDetailsView

class IncorrectDetailsViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "incorrect-details"

  "IncorrectDetails view" must {

    def applyView(): HtmlFormat.Appendable = {
      val view = viewFor[IncorrectDetailsView](Some(emptyUserAnswers))
      view.apply()(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(), messageKeyPrefix)

    behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
      "notAnOrganisation.p1", "notAnOrganisation.p2", "notAnOrganisation.p1.link", "notAnOrganisation.p2.link")

    behave like pageWithBackLink(applyView())
  }
}
