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

package views.operationsAndFunds

import assets.messages.BaseMessages
import controllers.operationsAndFunds.routes
import forms.operationsAndFunds.IsBankStatementsFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.operationsAndFunds.IsBankStatementsView


class IsBankStatementsViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix = "isBankStatements"
  private val section: Option[String] = Some(messages("operationsAndFunds.section"))
  val form: Form[Boolean] = inject[IsBankStatementsFormProvider].apply()

    "IsBankStatementsView" must {

      def applyView(form: Form[_]): HtmlFormat.Appendable = {
          val view = viewFor[IsBankStatementsView](Some(emptyUserAnswers))
          view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)
        }

      behave like normalPage(applyView(form), messageKeyPrefix, section = section)

      behave like pageWithBackLink(applyView(form))

      behave like yesNoPage(form, applyView, messageKeyPrefix, routes.IsBankStatementsController.onSubmit(NormalMode).url, section = section)

      behave like pageWithSubmitButton(applyView(form), BaseMessages.saveAndContinue)
  }}
