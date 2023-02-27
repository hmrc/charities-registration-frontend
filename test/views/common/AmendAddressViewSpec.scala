/*
 * Copyright 2023 HM Revenue & Customs
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

package views.common

import base.data.messages.BaseMessages
import forms.common.{AmendAddressFormProvider, PassportFormProvider}
import models.Passport
import models.addressLookup.AmendAddressModel
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.common.{AmendAddressView, PassportView}

class AmendAddressViewSpec extends QuestionViewBehaviours[AmendAddressModel] {

  private val messageKeyPrefix               = "charityOfficialAmendAddress"
  override val form: Form[AmendAddressModel] = inject[AmendAddressFormProvider].apply(messageKeyPrefix)

  "AuthorisedOfficialsPassportView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[AmendAddressView](Some(emptyUserAnswers))
      view.apply(form, messageKeyPrefix, onwardRoute, countries = Seq(("GB", "United Kingdom")))(
        fakeRequest,
        messages,
        frontendAppConfig
      )
    }

    behave like normalPage(applyView(form), messageKeyPrefix)

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

  }
}