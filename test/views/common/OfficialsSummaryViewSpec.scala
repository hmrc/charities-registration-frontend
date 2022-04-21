/*
 * Copyright 2022 HM Revenue & Customs
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

package views.common

import forms.common.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.common.OfficialsSummaryView

class OfficialsSummaryViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String = "authorisedOfficialsSummary.checkYourAnswers"
  private val section: String = messages("officialsAndNominees.section")
  val form: Form[Boolean] = inject[YesNoFormProvider].apply("authorisedOfficialsSummary")

  "OfficialsSummaryView" must {

    def applyView(form: Form[Boolean]): HtmlFormat.Appendable = {
      val view = viewFor[OfficialsSummaryView](Some(emptyUserAnswers))
      view.apply(form, "authorisedOfficialsSummary", 2,
        controllers.routes.IndexController.onPageLoad(None)
      )(
        fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, Seq(), section = Some(section), postHeadingString = ".addedOne")

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButton(applyView(form), messages("site.confirmAndContinue"))
  }
}
