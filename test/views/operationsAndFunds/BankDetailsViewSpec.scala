/*
 * Copyright 2022 HM Revenue & Customs
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

package views.operationsAndFunds

import assets.messages.BaseMessages
import forms.common.BankDetailsFormProvider
import models.{BankDetails, NormalMode, PlaybackMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.operationsAndFunds.BankDetailsView


class BankDetailsViewSpec extends QuestionViewBehaviours[BankDetails] {

  private val messageKeyPrefix = "bankDetails"
  private val sectionName: String = "operationsAndFunds.section"
  val form: Form[BankDetails] = inject[BankDetailsFormProvider].apply(messageKeyPrefix, "charityName")

    "BankDetailsView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[BankDetailsView](Some(emptyUserAnswers))
          view.apply(form, "charityName", controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(NormalMode),
            messageKeyPrefix, sectionName, None)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(messages("operationsAndFunds.section")))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithWarningText(applyView(form), messages("bankDetails.basc.warning"))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

      behave like pageWithAdditionalGuidance(applyView(form), messageKeyPrefix,
        "p1", "accountName", "accountName.hint")

      behave like pageWithHyperLink(applyView(form), "changeLink",
        controllers.contactDetails.routes.CharityNameController.onPageLoad(PlaybackMode).url,
        messages("site.edit") + messages("bankDetails.accountName"))

    }
  }
