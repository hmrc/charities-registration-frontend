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

import play.api.data.{Form, FormError}
import play.twirl.api.HtmlFormat
import uk.gov.hmrc.govukfrontend.views.viewmodels.checkboxes.CheckboxItem

trait CheckboxViewBehaviours[A] extends ViewBehaviours {

  //noinspection ScalaStyle
  def checkboxPage(form: Form[Set[A]],
                   createView: Form[Set[A]] => HtmlFormat.Appendable,
                   messageKeyPrefix: String,
                   options: Seq[CheckboxItem],
                   fieldKey: String = "value",
                   legend: Option[String] = None): Unit = {

    "behave like a checkbox page" must {
      "contain a legend for the question" in {
        val doc = asDocument(createView(form))
        val legends = doc.getElementsByTag("legend")
        legends.size mustBe 1
        legends.text contains legend.getOrElse(messages(s"$messageKeyPrefix.heading"))
      }

      "contain an input for the value" in {
        val doc = asDocument(createView(form))
        for (option <- options) {
          assertRenderedById(doc, option.value)
        }
      }

      "contain a label for each input" in {
        val doc = asDocument(createView(form))
        for (option <- options) {
          doc.select(s"label[for=${option.value}]").text mustEqual option.content.text
        }
      }

      "rendered" must {

        "contain checkboxes for the values" in {
          val doc = asDocument(createView(form))
          for (option <- options) {
            assertContainsRadioButton(doc, option.id.get, "value[]", option.value, false)
          }
        }
      }

      for (option <- options) {

        s"rendered with a value of '${option.value}'" must {

          s"have the '${option.value}' radio button selected" in {

            val formWithData = form.bind(Map("value" -> s"${option.value}"))
            val doc = asDocument(createView(formWithData))

            assertContainsRadioButton(doc, option.id.get, "value[]", option.value, true)
          }
        }
      }

      "rendered with all values" must {

        val valuesMap: Map[String, String] = options.zipWithIndex.map {
          case (option, i) => s"value[$i]" -> option.value
        }.toMap

        val formWithData = form.bind(valuesMap)
        val doc = asDocument(createView(formWithData))

        for(option <- options) {
          s"have ${option.value} value selected" in {
            assertContainsRadioButton(doc, option.id.get, "value[]", option.value, true)
          }
        }
      }

      "not render an error summary" in {
        val doc = asDocument(createView(form))
        assertNotRenderedById(doc, "error-summary-title")
      }


      "show error in the title" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        doc.title.contains(messages("error.browser.title.prefix")) mustBe true
      }

      "show an error summary" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        assertRenderedById(doc, "error-summary-title")
      }

      "show an error associated with the value field" in {
        val doc = asDocument(createView(form.withError(FormError(fieldKey, "error.invalid"))))
        val errorSpan = doc.getElementsByClass("govuk-error-message").first
        errorSpan.text mustBe (messages("error.browser.title.prefix") + " " + messages("error.invalid"))
        doc.getElementsByTag("fieldset").first.attr("aria-describedby") contains errorSpan.attr("id")
      }
    }
  }
}
