/*
 * Copyright 2024 HM Revenue & Customs
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

import base.data.messages.BaseMessages
import forms.common.BankDetailsFormProvider
import models.{BankDetails, NormalMode, PlaybackMode}
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.QuestionViewBehaviours
import views.html.operationsAndFunds.BankDetailsView

class BankDetailsViewSpec extends QuestionViewBehaviours[BankDetails] {

  private val messageKeyPrefix: String = "bankDetails"
  private val sectionName: String      = "operationsAndFunds.section"
  val form: Form[BankDetails]          = inject[BankDetailsFormProvider].apply(messageKeyPrefix, "charityName")

  private val view: BankDetailsView = viewFor[BankDetailsView](Some(emptyUserAnswers))

  private val viewViaApply: HtmlFormat.Appendable = view.apply(
    form,
    "charityName",
    controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(NormalMode),
    messageKeyPrefix,
    sectionName,
    None
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaRender: HtmlFormat.Appendable = view.render(
    form,
    "charityName",
    controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(NormalMode),
    messageKeyPrefix,
    sectionName,
    None,
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaF: HtmlFormat.Appendable = view.f(
    form,
    "charityName",
    controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(NormalMode),
    messageKeyPrefix,
    sectionName,
    None
  )(
    fakeRequest,
    messages,
    frontendAppConfig
  )

  "BankDetailsView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = Some(messages("operationsAndFunds.section")))

        behave like pageWithBackLink(view)

        behave like pageWithWarningText(view, messages("bankDetails.basc.warning"))

        behave like pageWithSubmitButton(view, BaseMessages.saveAndContinue)

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "p1", "accountName", "accountName.hint")

        behave like pageWithHyperLink(
          view,
          "changeLink",
          controllers.contactDetails.routes.CharityNameController.onPageLoad(PlaybackMode).url,
          messages("site.edit") + messages("bankDetails.accountName")
        )
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
