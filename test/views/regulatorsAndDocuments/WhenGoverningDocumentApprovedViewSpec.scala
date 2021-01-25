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

package views.regulatorsAndDocuments

import java.time.LocalDate

import assets.messages.BaseMessages
import forms.regulatorsAndDocuments.WhenGoverningDocumentApprovedFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.regulatorsAndDocuments.WhenGoverningDocumentApprovedView

class WhenGoverningDocumentApprovedViewSpec extends QuestionViewBehaviours[LocalDate] {

  private val messageKeyPrefix = "whenGoverningDocumentApproved"
  val form: Form[LocalDate] = inject[WhenGoverningDocumentApprovedFormProvider].apply()

  "WhenGoverningDocumentApprovedView view" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[WhenGoverningDocumentApprovedView](Some(emptyUserAnswers))
      view.apply(form, NormalMode, "Will")(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, Seq("Will"), section = Some(messages("charityRegulator.section")))

    behave like pageWithBackLink(applyView(form))

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }

}
