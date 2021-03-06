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

package views.contactDetails

import models.NormalMode
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.contactDetails.StartInformationView

class StartInformationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "startInformation"
  private val section: String = messages("contactDetail.section")

    "StartInformationView" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[StartInformationView](Some(emptyUserAnswers))
        view.apply()(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix, section = Some(section))

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1", "b1", "b2", "b3")

      behave like pageWithHyperLink(applyView(), "linkButton", controllers.contactDetails.routes.CharityNameController.onPageLoad(NormalMode).url, messages("site.continue"))

    }
  }
