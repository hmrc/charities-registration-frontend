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

package views.contactDetails

import base.data.messages.BaseMessages
import controllers.contactDetails.routes
import forms.contactDetails.CanWeSendToThisAddressFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.contactDetails.CanWeSendToThisAddressView

class CanWeSendToThisAddressViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix      = "canWeSendLettersToThisAddress"
  private val section: Some[String] = Some(messages("contactDetail.section"))
  val form: Form[Boolean]           = inject[CanWeSendToThisAddressFormProvider].apply()

  "CanWeSendToThisAddressViewView" must {

    val charityInformationAddressLookup = List("12", "Banner Way", "ZZ1 1ZZ")

    def applyView(form: Form[_]): HtmlFormat.Appendable = {
      val view = viewFor[CanWeSendToThisAddressView](Some(emptyUserAnswers))
      view.apply(form, NormalMode, charityInformationAddressLookup)(fakeRequest, messages, frontendAppConfig)
    }

    behave like normalPage(applyView(form), messageKeyPrefix, charityInformationAddressLookup, section = section)

    behave like pageWithBackLink(applyView(form))

    behave like yesNoPage(form, applyView, messageKeyPrefix, charityInformationAddressLookup, section = section)

    behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }
}
