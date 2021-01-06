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
import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.nominees.OrganisationNomineeAuthorisedPersonView

class OrganisationNomineeAuthorisedPersonViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "organisationNomineeAuthorisedPerson"

    "OrganisationNomineeAuthorisedPerson View" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[OrganisationNomineeAuthorisedPersonView](Some(emptyUserAnswers))
        view.apply("organisation name")(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix, Seq("organisation name"), section = Some(messages("officialsAndNominees.section")))

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1")

      behave like pageWithBackLink(applyView())

      behave like pageWithHyperLink(applyView(), "linkButton",routes.OrganisationAuthorisedPersonNameController.onSubmit(NormalMode).url,BaseMessages.continue)

    }
  }
