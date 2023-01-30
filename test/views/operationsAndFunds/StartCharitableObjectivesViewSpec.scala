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

package views.operationsAndFunds

import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.operationsAndFunds.StartCharitableObjectivesView

class StartCharitableObjectivesViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "startCharitableObjectives"
  private val section: String  = messages("operationsAndFunds.section")

  "StartCharitableObjectivesView" must {

    def applyView(): HtmlFormat.Appendable = {
      val view = viewFor[StartCharitableObjectivesView](Some(emptyUserAnswers))
      view.apply()(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(), messageKeyPrefix, section = Some(section))

    behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix, "p1", "b1", "b2", "b3")

    behave like pageWithHyperLink(
      applyView(),
      "linkButton",
      controllers.operationsAndFunds.routes.CharitableObjectivesController.onPageLoad(NormalMode).url,
      messages("site.continue")
    )

  }
}
