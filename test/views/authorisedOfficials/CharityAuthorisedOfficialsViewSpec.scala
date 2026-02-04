/*
 * Copyright 2026 HM Revenue & Customs
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

  private val messageKeyPrefix: String = "charityAuthorisedOfficials"

  private val view: CharityAuthorisedOfficialsView = viewFor[CharityAuthorisedOfficialsView](Some(emptyUserAnswers))

  private def viewViaApply(index: Index): HtmlFormat.Appendable =
    view.apply(index)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(index: Index): HtmlFormat.Appendable =
    view.render(index, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(index: Index): HtmlFormat.Appendable = view.f(index)(fakeRequest, messages, frontendAppConfig)

  private def viewTest(method: String, view: HtmlFormat.Appendable, index: Index): Unit =
    s"$method" must {
      behave like normalPage(
        view,
        messageKeyPrefix,
        section = Some(messages("officialsAndNominees.section"))
      )

      behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "p1", "p2")

      behave like pageWithBackLink(view)

      behave like pageWithHyperLink(
        view,
        "linkButton",
        controllers.authorisedOfficials.routes.AuthorisedOfficialsNameController.onPageLoad(NormalMode, index).url,
        BaseMessages.continue
      )
    }

  List(0, 1).foreach { index =>
    s"CharityAuthorisedOfficialsView for index $index" when {
      viewTest(".apply", viewViaApply(index), index)
      viewTest(".render", viewViaRender(index), index)
      viewTest(".f", viewViaF(index), index)
    }
  }
}
