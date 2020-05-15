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

package views.contact

import assets.messages.BaseMessages
import controllers.contact.routes
import forms.contact.IsCharityOfficialAddressInUKFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.contact.IsCharityOfficialAddressInUKView


class IsCharityOfficialAddressInUKViewSpec extends YesNoViewBehaviours  {

  val messageKeyPrefix = "isCharityOfficialAddressInUK"
  val form = new IsCharityOfficialAddressInUKFormProvider()()

    "IsCharityOfficialAddressInUKView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[IsCharityOfficialAddressInUKView](Some(emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsCharityOfficialAddressInUKController.onSubmit(NormalMode).url)

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)
  }}
