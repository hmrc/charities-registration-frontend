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
import controllers.nominees.routes
import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.nominees.OrganisationNomineeAuthorisedPersonView

class OrganisationNomineeAuthorisedPersonViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "organisationNomineeAuthorisedPerson"

  private val view: OrganisationNomineeAuthorisedPersonView =
    viewFor[OrganisationNomineeAuthorisedPersonView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable =
    view.apply("organisation name")(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render("organisation name", fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f("organisation name")(fakeRequest, messages, frontendAppConfig)

  "OrganisationNomineeAuthorisedPersonView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(
          view,
          messageKeyPrefix,
          Seq("organisation name"),
          section = Some(messages("officialsAndNominees.section"))
        )

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "p1")

        behave like pageWithBackLink(view)

        behave like pageWithHyperLink(
          view,
          "linkButton",
          routes.OrganisationAuthorisedPersonNameController.onSubmit(NormalMode).url,
          BaseMessages.continue
        )
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
