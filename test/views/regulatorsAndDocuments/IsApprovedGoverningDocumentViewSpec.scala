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

package views.regulatorsAndDocuments

import base.data.messages.BaseMessages
import controllers.regulatorsAndDocuments.routes
import forms.regulatorsAndDocuments.IsApprovedGoverningDocumentFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.regulatorsAndDocuments.IsApprovedGoverningDocumentView

class IsApprovedGoverningDocumentViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix        = "isApprovedGoverningDocument.4"
  private val section: Option[String] = Some(messages("charityRegulator.section"))
  val form: Form[Boolean]             = inject[IsApprovedGoverningDocumentFormProvider].apply()

  "IsApprovedGoverningDocumentView" must {

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[IsApprovedGoverningDocumentView](Some(emptyUserAnswers))
      view.apply(form, NormalMode, "4")(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, section = section)

    behave like pageWithAdditionalGuidance(applyView(form), "isApprovedGoverningDocument", "p")

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, section = section)

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }
}
