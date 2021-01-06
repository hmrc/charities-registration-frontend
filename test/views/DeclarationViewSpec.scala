/*
 * Copyright 2021 HM Revenue & Customs
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
import views.behaviours.ViewBehaviours
import views.html.DeclarationView

class DeclarationViewSpec extends ViewBehaviours {

  private val messageKeyPrefix = "declaration"

    "declarationView" must {

      def applyView(): HtmlFormat.Appendable = {
        val view = viewFor[DeclarationView](Some(emptyUserAnswers))
        view.apply()(fakeRequest, messages, frontendAppConfig)
      }

      behave like normalPage(applyView(), messageKeyPrefix, section = Some(messages("declaration.section")))

      behave like pageWithAdditionalGuidance(applyView(), messageKeyPrefix,
        "p1")

      behave like pageWithBackLink(applyView())

      behave like pageWithWarningText(applyView(), messages("declaration.warning"))

      behave like pageWithSubmitButton(applyView(), messages("site.confirmAndSend"))

    }
  }
