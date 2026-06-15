/*
 * Copyright 2026 HM Revenue & Customs
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

import play.twirl.api.HtmlFormat
import utils.{ImplicitDateFormatter, TimeMachine}
import views.behaviours.ViewBehaviours
import views.html.RegistrationSentView

class RegistrationSentViewSpec extends ViewBehaviours with ImplicitDateFormatter {

  private val messageKeyPrefix: String      = "registrationSent"
  private val section: Option[String]       = Some(messages("declaration.section"))
  private val registrationExpiryLimit: Long = 28
  private val view: RegistrationSentView    = viewFor[RegistrationSentView](Some(emptyUserAnswers))

  private def viewViaApply(): HtmlFormat.Appendable =
    view.apply(
      dayToString(inject[TimeMachine].now().plusDays(registrationExpiryLimit)),
      dayToString(inject[TimeMachine].now()),
      "080582080582",
      Seq.empty,
      None
    )(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable =
    view.render(
      dayToString(inject[TimeMachine].now().plusDays(registrationExpiryLimit)),
      dayToString(inject[TimeMachine].now()),
      "080582080582",
      Seq.empty,
      None,
      fakeRequest,
      messages,
      frontendAppConfig
    )

  "RegistrationSentView" when {
    ".apply" must {
      behave like normalPage(viewViaApply(), messageKeyPrefix, section = section)

      behave like pageWithAdditionalGuidance(
        viewViaRender,
        messageKeyPrefix,
        "h2",
        "post.step2"
      )
    }
  }
}
