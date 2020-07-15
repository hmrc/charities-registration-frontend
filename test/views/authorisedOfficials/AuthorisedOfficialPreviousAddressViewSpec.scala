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

package views.authorisedOfficials

import assets.messages.BaseMessages
import controllers.authorisedOfficials.routes
import forms.authorisedOfficials.AuthorisedOfficialPreviousAddressFormProvider
import models.{Index, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.authorisedOfficials.AuthorisedOfficialPreviousAddressView

class AuthorisedOfficialPreviousAddressViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix = "authorisedOfficialPreviousAddress"
  private val section: Some[String] = Some(messages("officialsAndNominees.section"))
  val form: Form[Boolean] = inject[AuthorisedOfficialPreviousAddressFormProvider].apply()

  "AuthorisedOfficialPreviousAddressView" must {


    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[AuthorisedOfficialPreviousAddressView](Some(emptyUserAnswers))
      view.apply(form, NormalMode, Index(0), "Jim Jones")(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, Seq("Jim Jones"), section = section)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.AuthorisedOfficialPreviousAddressController
      .onSubmit(NormalMode, Index(0)).url, Seq("Jim Jones"), section = section)

    behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)
  }
}

