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

import assets.messages.BaseMessages
import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.checkEligibility.CheckIfCanRegisterView

class CheckIfCanRegisterViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "checkIfYouCanRegister"

  "CheckIfYouCanRegister view" must {

    def applyView(): HtmlFormat.Appendable = {
      val view = viewFor[CheckIfCanRegisterView](Some(emptyUserAnswers))
      view.apply(NormalMode)(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(), messageKeyPrefix)

    behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
      "p1")

    behave like pageWithBackLink(applyView())

    behave like pageWithHyperLink(applyView(),
      "linkButton", controllers.checkEligibility.routes.IsEligiblePurposeController.onPageLoad().url, BaseMessages.continue)

  }
}
