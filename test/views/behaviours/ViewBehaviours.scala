/*
 * Copyright 2022 HM Revenue & Customs
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

import play.twirl.api.HtmlFormat
import views.ViewSpecBase

trait ViewBehaviours extends ViewSpecBase {

  def normalPage(view: HtmlFormat.Appendable,
                 messageKeyPrefix: String,
                 headingArgs: Seq[String] = Seq(),
                 section: Option[String] = None,
                 postHeadingString: String = ""): Unit = {

    "behave like a normal page" when {

      "rendered" must {

        "have the service name" in {

          val doc = asDocument(view)

          assert(doc.getElementsByClass("hmrc-header__service-name--linked").first.text == messages("service.name"),
            s"\n\nService name did not contain hint text ${messages("site.service_name")}")
        }

        "display the correct browser title" in {

          val doc = asDocument(view)
          assertEqualsMessage(doc, "title", title(messages(s"$messageKeyPrefix.title$postHeadingString", headingArgs:_*), section))
        }

        "display the correct page heading" in {

          val doc = asDocument(view)
          assertPageTitleEqualsMessage(doc, s"$messageKeyPrefix.heading$postHeadingString", headingArgs:_*)
        }

        if (frontendAppConfig.languageTranslationEnabled) {
          "display language toggles" in {
            val doc = asDocument(view)
            assertRenderedByCssSelector(doc, "nav.hmrc-language-select")
          }
        }
      }
    }
  }

  def pageWithBackLink(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with a back link" must {

      "have a back link" in {

        val doc = asDocument(view)
        assertRenderedById(doc, "back-link")
      }
    }
  }

  def pageWithSubHeading(view: HtmlFormat.Appendable, subheading: String): Unit = {

    "behave like a page with a Subheading" must {

      "display the correct subheading" in {
        assertEqualsMessage(asDocument(view), "span.govuk-caption-xl", subheading)
      }
    }
  }

  def pageWithHeading(view: HtmlFormat.Appendable, heading: String, level: Int = 1, occurence: Int = 1): Unit = {

    s"behave like a page with a Heading$level occurence instance $occurence" must {

      "display the correct Heading" in {
        assertEqualsMessage(asDocument(view), cssSelector = s"#main-content > div > div > div > h$level:nth-of-type($occurence)", heading)
      }
    }
  }

  def pageWithSignOutLink(view: HtmlFormat.Appendable): Unit = {

    "behave like a page with a Sign Out link" must {

      "have a Sign Out link" in {

        val doc = asDocument(view)
        assertRenderedByCssSelector(doc, "ul.govuk-header__navigation li:nth-of-type(1) a")
      }
    }
  }

  def pageWithSubmitButton(view: HtmlFormat.Appendable, msg: String): Unit = {

    "behave like a page with a submit button" must {

      s"have a button with message '$msg'" in {

        val doc = asDocument(view)
        assertEqualsMessage(doc, "#main-content > div > div > div > form > button", msg)
      }
    }
  }

  def pageWithParagraphMessage(view: HtmlFormat.Appendable, msg: String, selector: String): Unit = {

    s"behave like a page with paragraph $msg" must {

      s"have a button with message '$msg'" in {

        val doc = asDocument(view)
        assertEqualsMessage(doc, selector, msg)
      }
    }
  }

  def pageWithExpectedMessages(view: HtmlFormat.Appendable, checks: Seq[(String, String)]): Unit = checks.foreach {
    case (cssSelector, message) =>

      s"element with cssSelector '$cssSelector'" must {

        s"have message '$message'" in {
          val doc = asDocument(view)
          val elem = doc.select(cssSelector)
          doc.select(cssSelector).first.text() mustBe message
        }
      }
  }

  def pageWithBulletedPoint(view: HtmlFormat.Appendable, msg: String, bullet: Int): Unit = {

    s"behave like a page with bullet point$bullet" must {

      s"have a button with message '$msg'" in {
        val doc = asDocument(view)
        assertEqualsMessage(doc, cssSelector = s"#main-content > div > div > div > ul > li:nth-child($bullet)", expectedMessageKey = msg)
      }
    }
  }

  def pageWithHeading(view: HtmlFormat.Appendable, heading: String) = {

    "behave like a page with a Heading" must {

      "display the correct Heading" in {
        assertEqualsMessage(asDocument(view), "#main-content > div > div > div h1", heading)
      }
    }
  }

  def pageWithWarningText(view: HtmlFormat.Appendable, msg: String): Unit = {

    "behave like a page with a warning" must {

      s"have a warning message '$msg'" in {

        val doc = asDocument(view)

        assert(doc.getElementsByClass("govuk-warning-text__assistive").first.text == msg,
          s"\n Warning message did not contain text ${msg}")
      }
    }
  }

  def pageWithParagraphCustom(view: HtmlFormat.Appendable, msg: String, child: Int): Unit = {

    s"behave like a page with paragraph $msg" must {

      s"have a button with message '$msg'" in {

        val doc = asDocument(view)
        assertEqualsMessage(doc,  s"#main-content > div > div > div > form > p:nth-child($child)", msg)
      }
    }
  }

  def pageWithAdditionalGuidance(view: HtmlFormat.Appendable, messageKeyPrefix: String, expectedGuidanceKeys: String*): Unit = {

    "behave like a page with some Guidance" must {

      "display the correct guidance" in {

        val doc = asDocument(view)
        for (key <- expectedGuidanceKeys) assertContainsText(doc, messages(s"$messageKeyPrefix.$key"))
      }
    }
  }

  def pageWithGuidance(view: HtmlFormat.Appendable, msg: String): Unit = {

    "behave like a page with some Guidance" must {

      s"have a guidance message '$msg'" in {

        val doc = asDocument(view)
        assertEqualsMessage(doc, "#main-content > div > div > div > form > h1", msg)
      }
    }
  }

  def pageWithMultiFieldError(view: HtmlFormat.Appendable, key: String , errorMsg: String): Unit = {

    "behave like a page with a multiField error" in {

      val doc = asDocument(view)
      assertRenderedById(doc,s"$key-multiField-error-message")
    }
  }

  def pageWithHyperLink(view: => HtmlFormat.Appendable, linkId: String, url: String = "#", linkText: String) = {

    "behave like a page with a hyperlink" must {
      "have a hyperlink" in {
        val doc = asDocument(view)
        assertRenderedById(doc, linkId)
        doc.select(s"#$linkId").attr("href") mustBe url
        doc.select(s"#$linkId").text() mustBe linkText
      }
    }
  }

  def pageWithPrintOrDownloadLink(view: HtmlFormat.Appendable, linkId: String, url: String, linkText: String) = {
    "behave like a page with a Print or download link" must {
      "have a hyperlink" in {
        val doc = asDocument(view)
        doc.select(s"#$linkId").attr("href") mustBe url
        doc.select(s"#$linkId").text() mustBe linkText
      }
    }
  }
}
