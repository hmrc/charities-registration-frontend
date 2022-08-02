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

import base.data.messages.BaseMessages
import controllers.authorisedOfficials.routes
import forms.common.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.common.YesNoView

class YesNoViewSpec extends YesNoViewBehaviours  {

  private val messageKeyPrefix = "removeAuthorisedOfficial"
  private val section: String = messages("officialsAndNominees.section")
  private val firstOfficialsName = "Jane Johnson"
  val form: Form[Boolean] = inject[YesNoFormProvider].apply(messageKeyPrefix)

    "YesNoView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
        val view = viewFor[YesNoView](Some(emptyUserAnswers))
        view.apply(form, firstOfficialsName, messageKeyPrefix, onwardRoute, "officialsAndNominees")(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(form), messageKeyPrefix, Seq(firstOfficialsName), Some(section))

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix,
        routes.RemoveAuthorisedOfficialsController.onSubmit(0).url,
        Seq(firstOfficialsName), section = Some(section))

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)

    }
  }
