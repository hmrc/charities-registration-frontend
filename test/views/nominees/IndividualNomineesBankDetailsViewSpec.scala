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

package views.nominees

import base.data.messages.BaseMessages
import forms.common.BankDetailsFormProvider
import models.{BankDetails, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.BankAccountDetailsView

class IndividualNomineesBankDetailsViewSpec extends QuestionViewBehaviours[BankDetails] {

  private val messageKeyPrefix    = "individualNomineesBankDetails"
  private val sectionName: String = "officialsAndNominees.section"
  val form: Form[BankDetails]     = inject[BankDetailsFormProvider].apply(messageKeyPrefix)

  "IndividualNomineesBankDetailsView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[BankAccountDetailsView](Some(emptyUserAnswers))
      view.apply(
        form,
        controllers.nominees.routes.IndividualNomineesBankDetailsController.onSubmit(NormalMode),
        messageKeyPrefix,
        sectionName,
        Some("Jim Jam")
      )(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(
      applyView(form),
      messageKeyPrefix,
      Seq("Jim Jam"),
      section = Some(messages("officialsAndNominees.section"))
    )

    behave like pageWithBackLink(applyView(form))

    behave like pageWithWarningText(applyView(form), messages("individualNomineesBankDetails.basc.warning"))

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

  }
}
