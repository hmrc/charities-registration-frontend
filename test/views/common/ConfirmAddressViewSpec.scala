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

package views.common

import play.twirl.api.HtmlFormat
import views.behaviours.ViewBehaviours
import views.html.common.ConfirmAddressView

class ConfirmAddressViewSpec extends ViewBehaviours {

  private val messageKeyPrefix: String                      = "charityOfficialAddress"
  private val charityInformationAddressLookup: List[String] = List(line1, line2, ukPostcode)

  private val view: ConfirmAddressView = viewFor[ConfirmAddressView](Some(emptyUserAnswers))

  private def viewViaApply(
    name: Option[String] = None,
    messageKeyPrefix: String = messageKeyPrefix
  ): HtmlFormat.Appendable = view.apply(
    charityInformationAddressLookup,
    messageKeyPrefix,
    onwardRoute,
    onwardRoute,
    name
  )(fakeRequest, messages, frontendAppConfig)

  private val viewViaRender: HtmlFormat.Appendable = view.render(
    charityInformationAddressLookup,
    messageKeyPrefix,
    onwardRoute,
    onwardRoute,
    None,
    fakeRequest,
    messages,
    frontendAppConfig
  )

  private val viewViaF: HtmlFormat.Appendable = view.f(
    charityInformationAddressLookup,
    messageKeyPrefix,
    onwardRoute,
    onwardRoute,
    None
  )(fakeRequest, messages, frontendAppConfig)

  "ConfirmAddressView" when {
    def test(method: String, view: HtmlFormat.Appendable): Unit =
      s"$method" must {
        behave like normalPage(view, s"$messageKeyPrefix.confirmPage")

        behave like pageWithHyperLink(view, "confirmAndContinue", onwardRoute.url, "Confirm and continue")

        behave like pageWithBackLink(view)
      }

    val input: Seq[(String, HtmlFormat.Appendable)] = Seq(
      (".apply", viewViaApply()),
      (".render", viewViaRender),
      (".f", viewViaF)
    )

    input.foreach(args => test.tupled(args))

    ".apply" when {
      "with name" must {
        behave like normalPage(
          viewViaApply(Some("Firstname Lastname"), "authorisedOfficialAddress"),
          "authorisedOfficialAddress.confirmPage",
          Seq("Firstname Lastname")
        )
      }

      "change link with no name" must {
        behave like pageWithHyperLink(viewViaApply(), "linkButton", onwardRoute.url, "Change charity’s address")
      }

      "change link with name" must {
        behave like pageWithHyperLink(
          viewViaApply(Some("Firstname Lastname"), "authorisedOfficialAddress"),
          "linkButton",
          onwardRoute.url,
          "Change authorised official’s home address"
        )
      }
    }
  }
}
