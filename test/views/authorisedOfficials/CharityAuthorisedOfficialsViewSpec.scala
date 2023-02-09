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

package views.authorisedOfficials

import base.data.messages.BaseMessages
import models.{Index, NormalMode}
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.authorisedOfficials.CharityAuthorisedOfficialsView

class CharityAuthorisedOfficialsViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "charityAuthorisedOfficials"
  List(0, 1).foreach { index =>
    s"CharityAuthorisedOfficialsView for index $index" must {

      def applyView(index: Index): HtmlFormat.Appendable = {
        val view = viewFor[CharityAuthorisedOfficialsView](Some(emptyUserAnswers))
        view.apply(index)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(
        applyView(index),
        messageKeyPrefix,
        section = Some(messages("officialsAndNominees.section"))
      )

      behave like pageWithAdditionalGuidance(applyView(index), messageKeyPrefix, "p1", "p2")

      behave like pageWithBackLink(applyView(index))

      behave like pageWithHyperLink(
        applyView(index),
        "linkButton",
        controllers.authorisedOfficials.routes.AuthorisedOfficialsNameController.onPageLoad(NormalMode, index).url,
        BaseMessages.continue
      )

    }
  }
}
