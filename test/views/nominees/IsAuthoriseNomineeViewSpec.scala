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

package views.nominees

import assets.messages.BaseMessages
import controllers.nominees.routes
import forms.nominees.IsAuthoriseNomineeFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.nominees.IsAuthoriseNomineeView

class IsAuthoriseNomineeViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix = "isAuthoriseNominee"
  private val section: Option[String] = Some(messages("officialsAndNominees.section"))
  val form: Form[Boolean] = inject[IsAuthoriseNomineeFormProvider].apply()

  "IsAuthoriseNomineeView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[IsAuthoriseNomineeView](Some(emptyUserAnswers))
      view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, section = section)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsAuthoriseNomineeController.onSubmit(NormalMode).url, section = section)

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }
}
