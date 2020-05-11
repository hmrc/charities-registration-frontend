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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait DecimalViewBehaviours extends QuestionViewBehaviours[BigDecimal] {

  val number = 123.12

  def decimalPage(form: Form[BigDecimal],
                  createView: Form[BigDecimal] => HtmlFormat.Appendable,
                  messageKeyPrefix: String,
                  expectedFormAction: String,
                  headingArgs: Seq[String] = Seq(),
                  section: Option[String] = None): Unit = {

    "behave like a page with a decimal value field" when {

      "rendered" must {

        "contain a label for the value" in {

          val doc = asDocument(createView(form))
          assertContainsLabel(doc, "value", messages(s"$messageKeyPrefix.label", headingArgs:_*))
        }

        "contain an input for the value" in {

          val doc = asDocument(createView(form))
          assertRenderedById(doc, "value")
        }
      }

      "rendered with a valid form" must {

        "include the form's value in the value input" in {

          val doc = asDocument(createView(form.fill(number)))
          doc.getElementById("value").attr("value") mustBe number.toString
        }
      }

      "rendered with an error" must {

        "show an error summary" in {

          val doc = asDocument(createView(form.withError(error)))
          assertRenderedById(doc, "error-summary-title")
        }

        "show an error associated with the value field" in {

          val doc = asDocument(createView(form.withError(error)))
          val errorSpan = doc.getElementsByClass("govuk-error-message").first
          errorSpan.text mustBe (messages("error.browser.title.prefix") + " " + messages(errorMessage))
        }

        "show an error prefix in the browser title" in {

          val doc = asDocument(createView(form.withError(error)))
          assertEqualsValue(doc, "title", s"""${messages("error.browser.title.prefix")} ${title(messages(s"$messageKeyPrefix.title", headingArgs:_*), section)}""")
        }
      }
    }
  }

  def currencyPage(createView: Form[BigDecimal] => HtmlFormat.Appendable) = {

    val doc = asDocument(createView(form))

    "behave like a currency page" which {

      "has the currency input class on the input field" in {
        doc.select("input").hasClass("govuk-currency-input__inner__input") mustBe true
      }

      "has a span for the currency unit with a pound sign" in {
        doc.select("span.govuk-currency-input__inner__unit").text mustBe "£"
      }
    }
  }

  def percentagePage(createView: Form[BigDecimal] => HtmlFormat.Appendable) = {

    val doc = asDocument(createView(form))

    "behave like a percentage page" which {

      "has the percentage input class on the input field" in {
        doc.select("input").hasClass("govuk-percentage-input__inner__input") mustBe true
      }

      "has a span for the percentage unit with a percent sign" in {
        doc.select("span.govuk-percentage-input__inner__unit").text mustBe "%"
      }
    }
  }
}