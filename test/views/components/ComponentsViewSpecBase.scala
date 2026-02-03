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

package views.components

import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ComponentsViewSpecBase extends ViewSpecBase {

  val viewViaApply: HtmlFormat.Appendable
  val viewViaRender: HtmlFormat.Appendable
  val viewViaF: HtmlFormat.Appendable

  def getViews: Seq[(String, HtmlFormat.Appendable)] = Seq(
    (".apply", viewViaApply),
    (".render", viewViaRender),
    (".f", viewViaF)
  )

  def renderViewTest(title: String, heading: String): Unit = {
    ".apply" should {
      "display the correct title" in {
        asDocument(
          viewViaApply
        ).title mustBe title
      }

      "display the correct heading" in {
        asDocument(
          viewViaApply
        ).select("h1").text mustBe heading
      }
    }

    ".render" should {
      "display the correct title" in {
        asDocument(
          viewViaRender
        ).title mustBe title
      }

      "display the correct heading" in {
        asDocument(
          viewViaRender
        ).select("h1").text mustBe heading
      }
    }

    ".f" should {
      "display the correct title" in {
        asDocument(
          viewViaF
        ).title mustBe title
      }

      "display the correct heading" in {
        asDocument(
          viewViaF
        ).select("h1").text mustBe heading
      }
    }
  }

  def renderComponentViewTest(title: String, heading: String): Unit = {
    ".apply" should {
      "display the correct title" in {
        asDocument(
          viewViaApply
        ).title mustBe title
      }

      "display the correct heading" in {
        asDocument(
          viewViaApply
        ).select("h1").text mustBe heading
      }
    }

    ".render" should {
      "display the correct title" in {
        asDocument(
          viewViaRender
        ).title mustBe title
      }

      "display the correct heading" in {
        asDocument(
          viewViaRender
        ).select("h1").text mustBe heading
      }
    }

    ".f" should {
      "display the correct title" in {
        asDocument(
          viewViaF
        ).title mustBe title
      }

      "display the correct heading" in {
        asDocument(
          viewViaF
        ).select("h1").text mustBe heading
      }
    }
  }

  def pageWithExpectedMessages(view: HtmlFormat.Appendable, checks: Seq[(String, String)]): Unit =
    checks.foreach { case (cssSelector, message) =>
      s"element with cssSelector '$cssSelector'" should {

        s"have message '$message'" in {
          val doc  = asDocument(view)
          val elem = doc.select(cssSelector)
          elem.first.text() mustBe message
        }
      }
    }

}
