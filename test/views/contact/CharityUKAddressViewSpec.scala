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
import forms.contact.CharityUKAddressFormProvider
import models.{CharityUKAddress, NormalMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.contact.{CharityNameView, CharityUKAddressView}


class CharityUKAddressViewSpec extends QuestionViewBehaviours[CharityUKAddress]  {

  val messageKeyPrefix = "charityUKAddress"
  val form = new CharityUKAddressFormProvider()()

    "CharityUKAddressView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[CharityUKAddressView](Some(emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(form), messageKeyPrefix, section = Some(messages(s"$messageKeyPrefix.section")))

      behave like pageWithBackLink(applyView(form))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.continue)

      behave like pageWithHyperLink(applyView(form), "address-outsideUK", "/hmrc-register-charity-registration", linkText = messages(s"$messageKeyPrefix.officialAddress.outsideUK.link"))

    }
  }
