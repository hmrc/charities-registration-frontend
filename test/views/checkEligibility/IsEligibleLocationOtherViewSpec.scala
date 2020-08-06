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

  private val messageKeyPrefix = "isEligibleLocationOther"
  val form: Form[Boolean] = inject[IsEligibleLocationOtherFormProvider].apply()

    "IsEligibleLocationOtherView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[IsEligibleLocationOtherView](Some(emptyUserAnswers))
        view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithAdditionalGuidance(applyView(form), messageKeyPrefix, "details")

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsEligibleLocationOtherController.onSubmit().url)

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)

      "display EU countries list" in {
        assertContainsMessages(asDocument(applyView(form)),
          s"$messageKeyPrefix.austria",
          s"$messageKeyPrefix.belgium",
          s"$messageKeyPrefix.bulgaria",
          s"$messageKeyPrefix.croatia",
          s"$messageKeyPrefix.republicOfCyprus",
          s"$messageKeyPrefix.czechRepublic",
          s"$messageKeyPrefix.denmark",
          s"$messageKeyPrefix.estonia",
          s"$messageKeyPrefix.finland",
          s"$messageKeyPrefix.france",
          s"$messageKeyPrefix.germany",
          s"$messageKeyPrefix.greece",
          s"$messageKeyPrefix.hungary",
          s"$messageKeyPrefix.ireland",
          s"$messageKeyPrefix.italy",
          s"$messageKeyPrefix.latvia",
          s"$messageKeyPrefix.lithuania",
          s"$messageKeyPrefix.luxembourg",
          s"$messageKeyPrefix.malta",
          s"$messageKeyPrefix.netherlands",
          s"$messageKeyPrefix.poland",
          s"$messageKeyPrefix.portugal",
          s"$messageKeyPrefix.romania",
          s"$messageKeyPrefix.slovakia",
          s"$messageKeyPrefix.slovenia",
          s"$messageKeyPrefix.spain",
          s"$messageKeyPrefix.sweden")
      }
    }
  }
