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

import assets.messages.BaseMessages
import controllers.checkEligibility.routes
import forms.checkEligibility.IsEligiblePurposeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.checkEligibility.IsEligiblePurposeView

class IsEligiblePurposeViewSpec extends YesNoViewBehaviours  {

  private val messageKeyPrefix = "isEligiblePurpose"
  val form: Form[Boolean] = inject[IsEligiblePurposeFormProvider].apply()

    "IsEligiblePurposeView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[IsEligiblePurposeView](Some(emptyUserAnswers))
        view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithAdditionalGuidance(applyView(form), messageKeyPrefix, "p")

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsEligiblePurposeController.onSubmit().url, legendKey = Some("heading"))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)

    }
  }
