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

package views.common

import assets.messages.BaseMessages
import controllers.authorisedOfficials.routes
import forms.common.IsAddAnotherFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.common.IsAddAnotherView

class IsAddAnotherViewSpec extends YesNoViewBehaviours  {

  private val messageKeyPrefix = "isAddAnotherAuthorisedOfficial"
  private val section: Option[String] = Some(messages("officialsAndNominees.section"))
  private val firstOfficialsName = "Jane Johnson"
  private val secondOfficialsName = "Jeff Jackson"
  val form: Form[Boolean] = inject[IsAddAnotherFormProvider].apply(messageKeyPrefix)

    "IsAddAnotherAuthorisedOfficialView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[IsAddAnotherView](Some(emptyUserAnswers))
        view.apply(form, firstOfficialsName, Some(secondOfficialsName), messageKeyPrefix, onwardRoute)(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(form), messageKeyPrefix, section = section)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsAddAnotherAuthorisedOfficialController.onSubmit(NormalMode).url, section = section)

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

    }
  }
