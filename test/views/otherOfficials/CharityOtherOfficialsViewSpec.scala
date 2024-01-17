/*
 * Copyright 2024 HM Revenue & Customs
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

package views.otherOfficials

import base.data.messages.BaseMessages
import models.{Index, NormalMode}
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.otherOfficials.CharityOtherOfficialsView

class CharityOtherOfficialsViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "charityOtherOfficials"

  private val view: CharityOtherOfficialsView = viewFor[CharityOtherOfficialsView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply(Index(0))(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable = view.render(Index(0), fakeRequest, messages, frontendAppConfig)

  private val viewViaF: HtmlFormat.Appendable = view.f(Index(0))(fakeRequest, messages, frontendAppConfig)

  "CharityOtherOfficialsView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(messages("officialsAndNominees.section")))

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "p1", "p2", "p3")

        behave like pageWithBackLink(view)

        behave like pageWithHyperLink(
          view,
          "linkButton",
          controllers.otherOfficials.routes.OtherOfficialsNameController.onPageLoad(NormalMode, Index(0)).url,
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
