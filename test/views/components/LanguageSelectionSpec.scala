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

package views.components

import helpers.TestHelper
import org.jsoup.Jsoup
import play.api.i18n.Lang
import play.api.mvc.Call
import views.html.components.languageSelection

class LanguageSelectionSpec extends TestHelper {

  def languageMap: Map[String, Lang] = Map(
    "english" -> Lang("en"),
    "cymraeg" -> Lang("cy")
  )

  def routeToSwitchLanguage: String => Call =
    (lang: String) => controllers.routes.LanguageSwitchController.switchToLanguage(lang)

  s"languageSelection component" must {

    "Render the correct markup, provided the current language is English" in {
      val html = languageSelection(languageMap, routeToSwitchLanguage)(messages.preferred(Seq(Lang("en"))))
      val document = Jsoup.parse(html.toString)

      document.select("a#cymraeg-switch").attr("href") shouldBe routeToSwitchLanguage("cymraeg").url
      document.select("p").text shouldBe "English | Cymraeg"
    }

    "Render the correct markup, provided the current language is Welsh" in {
      val html = languageSelection(languageMap, routeToSwitchLanguage)(messages.preferred(Seq(Lang("cy"))))
      val document = Jsoup.parse(html.toString)

      document.select("a#english-switch").attr("href") shouldBe routeToSwitchLanguage("english").url
      document.select("p").text shouldBe "English | Cymraeg"
    }
  }

}
