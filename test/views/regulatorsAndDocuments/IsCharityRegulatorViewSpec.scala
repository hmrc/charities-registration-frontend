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

package views.regulatorsAndDocuments

import assets.messages.BaseMessages
import controllers.regulatorsAndDocuments.routes
import forms.regulatorsAndDocuments.IsCharityRegulatorFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.regulatorsAndDocuments.IsCharityRegulatorView


class IsCharityRegulatorViewSpec extends YesNoViewBehaviours  {

  val messageKeyPrefix = "isCharityRegulator"
  val form = new IsCharityRegulatorFormProvider()()

    "IsCharityRegulatorView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[IsCharityRegulatorView](Some(emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(messages("charityRegulator.section")))

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsCharityRegulatorController.onSubmit(NormalMode).url, section = Some(messages("charityRegulator.section")))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)
  }}
