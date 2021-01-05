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

import base.SpecBase
import models.UserAnswers
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.scalatest.Assertion
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{Content, HtmlContent, Text}

import scala.reflect.ClassTag

trait ViewSpecBase extends SpecBase with BaseSelectors {

  def viewFor[A](data: Option[UserAnswers] = None)(implicit tag: ClassTag[A]): A = app.injector.instanceOf[A]

  implicit class ContentExtension(x: Content) {
    def text: String = x match {
      case Text(text) => text
      case HtmlContent(html) => Jsoup.parse(html.toString).text
      case _ => ""
    }
  }

  def asDocument(html: Html): Document = Jsoup.parse(html.toString())

  def assertEqualsMessage(doc: Document, cssSelector: String, expectedMessageKey: String): Assertion =
    assertEqualsValue(doc, cssSelector, messages(expectedMessageKey))

  def assertEqualsValue(doc : Document, cssSelector : String, expectedValue: String): Assertion = {
    val elements = doc.select(cssSelector)

    if(elements.isEmpty) throw new IllegalArgumentException(s"CSS Selector $cssSelector wasn't rendered.")

    //<p> HTML elements are rendered out with a carriage return on some pages, so discount for comparison
    assert(elements.first().html().replace("\n", "") == expectedValue)
  }

  def assertPageTitleEqualsMessage(doc: Document, expectedMessageKey: String, args: Any*): Assertion = {
    val headers = doc.getElementsByTag("h1")
    headers.size mustBe 1
    headers.first.text.replaceAll("\u00a0", " ") mustBe messages(expectedMessageKey, args:_*).replaceAll("&nbsp;", " ")
  }

  def assertContainsText(doc:Document, text: String): Assertion = assert(doc.toString.contains(text), "\n\ntext " + text + " was not rendered on the page.\n")

  def assertContainsMessages(doc: Document, expectedMessageKeys: String*): Unit = {
    for (key <- expectedMessageKeys) assertContainsText(doc, messages(key))
  }

  def assertRenderedById(doc: Document, id: String): Assertion = {
    assert(doc.getElementById(id) != null, "\n\nElement " + id + " was not rendered on the page.\n")
  }

  def assertNotRenderedById(doc: Document, id: String): Assertion = {
    assert(doc.getElementById(id) == null, "\n\nElement " + id + " was rendered on the page.\n")
  }

  def assertRenderedByCssSelector(doc: Document, cssSelector: String): Assertion = {
    assert(!doc.select(cssSelector).isEmpty, "Element " + cssSelector + " was not rendered on the page.")
  }

  def assertNotRenderedByCssSelector(doc: Document, cssSelector: String): Assertion = {
    assert(doc.select(cssSelector).isEmpty, "\n\nElement " + cssSelector + " was rendered on the page.\n")
  }

  def assertNotContainsLabel(doc: Document, forElement: String): Assertion = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.isEmpty, "\n\nLabel for " + forElement + " was rendered on the page.\n")
  }

  def assertContainsLabel(doc: Document, forElement: String, expectedText: String, expectedHintText: Option[String] = None): Any = {
    val labels = doc.getElementsByAttributeValue("for", forElement)
    assert(labels.size == 1, s"\n\nLabel for $forElement was not rendered on the page.")
    val label = labels.first
    assert(label.text().contains(expectedText), s"\n\nLabel for $forElement was not $expectedText")

    if (expectedHintText.isDefined) {
      assert(label.getElementsByClass("form-hint").first.text == expectedHintText.get,
        s"\n\nLabel for $forElement did not contain hint text $expectedHintText")
    }
  }

  def assertElementHasClass(doc: Document, id: String, expectedClass: String): Assertion = {
    assert(doc.getElementById(id).hasClass(expectedClass), s"\n\nElement $id does not have class $expectedClass")
  }

  def assertContainsRadioButton(doc: Document, id: String, name: String, value: String, isChecked: Boolean): Assertion = {
    assertRenderedById(doc, id)
    val radio = doc.getElementById(id)
    assert(radio.attr("name") == name, s"\n\nElement $id does not have name $name")
    assert(radio.attr("value") == value, s"\n\nElement $id does not have value $value")
    if (isChecked) {
      assert(radio.hasAttr("checked"), s"\n\nElement $id is not checked")
    } else {
      assert(!radio.hasAttr("checked"), s"\n\nElement $id is checked")
    }
  }

  def checkYourAnswersRowChecks(id: Int, expectedRowData: (String, String)*)(implicit document: Document): Unit = {

    expectedRowData.zipWithIndex.foreach { case ((heading, value), i) =>

      val rowPosition = i + 1

      s"have the correct answer row at position $rowPosition" which {

        s"should have the correct heading of '$heading'" in {
          document.select(checkAnswersHeading(id,rowPosition)).first.text mustBe heading
        }

        s"should have the correct value of '$value'" in {
          document.select(checkAnswersAnswerValue(id, rowPosition)).first.text mustBe value
        }
      }
    }
  }

  def currency(amt: BigDecimal): String = f"£$amt%,1.2f".replace(".00","")

}
