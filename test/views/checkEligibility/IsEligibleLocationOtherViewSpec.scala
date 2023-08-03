/*
 * Copyright 2023 HM Revenue & Customs
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

package views.checkEligibility

import base.data.messages.BaseMessages
import forms.checkEligibility.IsEligibleLocationOtherFormProvider
import models.NormalMode
import play.api.data.Form
import play.twirl.api.HtmlFormat
import views.behaviours.YesNoViewBehaviours
import views.html.checkEligibility.IsEligibleLocationOtherView

class IsEligibleLocationOtherViewSpec extends YesNoViewBehaviours {

  private val messageKeyPrefix: String = "isEligibleLocationOther"
  val form: Form[Boolean]              = inject[IsEligibleLocationOtherFormProvider].apply()

  private val view: IsEligibleLocationOtherView = viewFor[IsEligibleLocationOtherView](Some(emptyUserAnswers))

  private def viewViaApply(form: Form[Boolean]): HtmlFormat.Appendable =
    view.apply(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  private def viewViaRender(form: Form[Boolean]): HtmlFormat.Appendable =
    view.render(form, NormalMode, fakeRequest, messages, frontendAppConfig)

  private def viewViaF(form: Form[Boolean]): HtmlFormat.Appendable =
    view.f(form, NormalMode)(fakeRequest, messages, frontendAppConfig)

  "IsEligibleLocationOtherView" when {
    def test(method: String, view: HtmlFormat.Appendable, createView: Form[Boolean] => HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, messageKeyPrefix)

        behave like pageWithAdditionalGuidance(view, messageKeyPrefix, "details")

        behave like pageWithBackLink(view)

        behave like yesNoPage(form, createView, messageKeyPrefix)

        behave like pageWithSubmitButton(view, BaseMessages.continue)

        "display EU countries list" in {
          assertContainsMessages(
            asDocument(view),
            s"$messageKeyPrefix.austria",
            s"$messageKeyPrefix.belgium",
            s"$messageKeyPrefix.bulgaria",
            s"$messageKeyPrefix.croatia",
            s"$messageKeyPrefix.republicOfCyprus",
            s"$messageKeyPrefix.czechRepublic",
            s"$messageKeyPrefix.denmark",
            s"$messageKeyPrefix.estonia",
            s"$messageKeyPrefix.finland",
            s"$messageKeyPrefix.france",
            s"$messageKeyPrefix.germany",
            s"$messageKeyPrefix.greece",
            s"$messageKeyPrefix.hungary",
            s"$messageKeyPrefix.ireland",
            s"$messageKeyPrefix.italy",
            s"$messageKeyPrefix.latvia",
            s"$messageKeyPrefix.lithuania",
            s"$messageKeyPrefix.luxembourg",
            s"$messageKeyPrefix.malta",
            s"$messageKeyPrefix.netherlands",
            s"$messageKeyPrefix.poland",
            s"$messageKeyPrefix.portugal",
            s"$messageKeyPrefix.romania",
            s"$messageKeyPrefix.slovakia",
            s"$messageKeyPrefix.slovenia",
            s"$messageKeyPrefix.spain",
            s"$messageKeyPrefix.sweden"
          )
        }
      }

    val input: Seq[(String, HtmlFormat.Appendable, Form[Boolean] => HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply(form), viewViaApply),
      (".render", viewViaRender(form), viewViaRender),
      (".f", viewViaF(form), viewViaF)
    )

    input.foreach(args => (test _).tupled(args))
  }
}
