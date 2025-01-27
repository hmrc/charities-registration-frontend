/*
 * Copyright 2025 HM Revenue & Customs
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
import forms.nominees.OrganisationNomineeContactDetailsFormProvider
import models.NormalMode
import models.nominees.OrganisationNomineeContactDetails
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.nominees.OrganisationNomineeContactDetailsView

class OrganisationNomineeContactDetailsViewSpec extends QuestionViewBehaviours[OrganisationNomineeContactDetails] {

  private val messageKeyPrefix: String = "organisationContactDetails"
  private val section: String          = "officialsAndNominees.section"

  val form: Form[OrganisationNomineeContactDetails] = inject[OrganisationNomineeContactDetailsFormProvider].apply()

  private val view: OrganisationNomineeContactDetailsView =
    viewFor[OrganisationNomineeContactDetailsView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable =
    view.apply(form, "test", NormalMode)(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(form, "test", NormalMode, fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable =
    view.f(form, "test", NormalMode)(fakeRequest, messages, frontendAppConfig)

  "OrganisationNomineeContactDetailsView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(
          view,
          messageKeyPrefix,
          Seq("test"),
          Some(messages(section))
        )

        behave like pageWithBackLink(view)

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
