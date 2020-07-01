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

package views.charityInformation

import assets.messages.BaseMessages
import forms.charityInformation.CharityNameFormProvider
import models.{CharityName, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.charityInformation.CharityNameView


class CharityNameViewSpec extends QuestionViewBehaviours[CharityName]  {

  private val messageKeyPrefix = "charityName"
  val form: Form[CharityName] = inject[CharityNameFormProvider].apply()

    "CharityNameView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[CharityNameView](Some(emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(messages("contactDetail.section")))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)

    }
  }
