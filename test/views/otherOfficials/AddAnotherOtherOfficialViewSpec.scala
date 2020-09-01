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

package views.otherOfficials

import assets.messages.BaseMessages
import forms.common.IsAddAnotherFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.otherOfficials.AddAnotherOtherOfficialView


class AddAnotherOtherOfficialViewSpec extends YesNoViewBehaviours  {

  private val messageKeyPrefix = "addAnotherOtherOfficial"
  private val section: Option[String] = Some(messages("officialsAndNominees.section"))
  val form: Form[Boolean] = inject[IsAddAnotherFormProvider].apply(messageKeyPrefix)

  "AddAnotherOtherOfficialView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[AddAnotherOtherOfficialView](Some(emptyUserAnswers))
          view.apply(form, NormalMode,"Jim John Jones", "John Jim Jones")(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, Seq("Jim John Jones", "John Jim Jones"), section = section)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, controllers.otherOfficials.routes.AddAnotherOtherOfficialController.onSubmit(NormalMode).url,Seq("Jim John Jones", "John Jim Jones"),section = section)

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }}
