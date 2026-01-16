/*
 * Copyright 2025 HM Revenue & Customs
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

package views

import forms.common.YesNoFormProvider
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.EmailOrPostView

class EmailOrPostViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix: String = "emailOrPost"
  private val section: Option[String]  = Some(messages("declaration.section"))
  val form: Form[Boolean]              = inject[YesNoFormProvider].apply(messageKeyPrefix)

  private val view: EmailOrPostView = viewFor[EmailOrPostView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[Boolean]): HtmlFormat.Appendable =
    view.apply(
      form,
      Seq("requiredDocuments.governingDocumentName.answerTrue"),
      Some(("requiredDocuments.foreignAddresses.answerTrue", "Firstname Lastname"))
    )(
      fakeRequest,
      messages,
      frontendAppConfig
    )

  private def viewViaRender(form: Form[Boolean]): HtmlFormat.Appendable = view.render(
    form,
    Seq("requiredDocuments.governingDocumentName.answerTrue"),
    Some(("requiredDocuments.foreignAddresses.answerTrue", "Firstname Lastname")),
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private def viewViaF(form: Form[Boolean]): HtmlFormat.Appendable = view.f(
    form,
    Seq("requiredDocuments.governingDocumentName.answerTrue"),
    Some(("requiredDocuments.foreignAddresses.answerTrue", "Firstname Lastname"))
  )(fakeRequest, messages, frontendAppConfig)

  "EmailOrPostView" when {
    def test(method: String, view: HtmlFormat.Appendable, createView: Form[Boolean] => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix, section = section)

        behave like pageWithBackLink(view)

        behave like pageWithHeading(
          view,
          "Would you prefer to send us the charity’s supporting documents by email or post?"
        )

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "p")

        behave like pageWithBulletedPoint(view, "requiredDocuments.governingDocumentName.answerTrue", 1)
        behave like pageWithBulletedPoint(
          view,
          "requiredDocuments.foreignAddresses.answerTrue",
          2,
          Some("Firstname Lastname")
        )

        behave like yesNoPage(form, createView, messageKeyPrefix, section = section, isEmailOrPost = true)

        behave like pageWithSubmitButton(view, messages("site.continue"))
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[Boolean] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => test.tupled(args))
  }
}
