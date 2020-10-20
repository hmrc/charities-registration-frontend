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

package views.otherOfficials

import assets.messages.BaseMessages
import models.{Index, NormalMode}
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.otherOfficials.CharityOtherOfficialsView

class CharityOtherOfficialsViewSpec extends ViewBehaviours  {

  private val messageKeyPrefix = "charityOtherOfficials"

    "CharityOtherOfficialsView" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[CharityOtherOfficialsView](Some(emptyUserAnswers))
        view.apply(Index(0))(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix, section = Some(messages("officialsAndNominees.section")))

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1", "p2", "p3")

      behave like pageWithBackLink(applyView())

      behave like pageWithHyperLink(applyView(), "linkButton",
        controllers.otherOfficials.routes.OtherOfficialsNameController.onPageLoad(NormalMode, Index(0)).url,BaseMessages.continue)


    }
  }
