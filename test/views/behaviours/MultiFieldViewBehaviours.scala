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

package views.behaviours

import play.api.data.Form
import play.twirl.api.HtmlFormat

trait MultiFieldViewBehaviours[T <: Form[_]] extends ViewBehaviours {

  def pageWithMultiFieldForm(view: HtmlFormat.Appendable, expectedLabels: Map[String, String]): Unit =
    "behave link a page with a multi field form" when {

      "rendered" should {

        "contain a label for each of the inputs" in {
          val doc      = asDocument(view)
          val labels   = doc.select("label")
          val forAttrs = labels.eachAttr("for")

          expectedLabels.map { case (id, _) =>
            assert(forAttrs.contains(id))
          }

          assert(forAttrs.size == expectedLabels.size)
        }

        "have the right content for each label" in {
          val doc = asDocument(view)

          expectedLabels.map { case (id, expectedText) =>
            assert(doc.select(s"label[for=$id]").text() == expectedText)
          }
        }

      }

    }

}
