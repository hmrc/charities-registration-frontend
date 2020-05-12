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
import forms.checkEligibility.IsEligibleLocationOtherFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.checkEligibility.IsEligibleLocationOtherView

class IsEligibleLocationOtherViewSpec extends YesNoViewBehaviours  {

  val messageKeyPrefix = "isEligibleLocationOther"
  val form = new IsEligibleLocationOtherFormProvider()()

    "IsEligibleLocationOtherView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[IsEligibleLocationOtherView](Some(emptyUserAnswers))
        view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithSubHeading(applyView(form), "Check if you can register")

      behave like pageWithAdditionalGuidance(applyView(form), messageKeyPrefix, "help", "hint")

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsEligibleLocationOtherController.onSubmit().url)

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)

    }
  }
