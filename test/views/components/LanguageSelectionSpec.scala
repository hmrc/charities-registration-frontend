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

import base.SpecBase
import org.jsoup.Jsoup
import play.api.i18n.Lang
import views.html.components.languageSelection

class LanguageSelectionSpec extends SpecBase {

  lazy val languageSelectionView: languageSelection = app.injector.instanceOf[languageSelection]

  object Selectors {
    val welshLink = "a#cymraeg-switch"
    val englishLink = "a#english-switch"
    val content = "p"
  }

  s"languageSelection component" must {

    "Render the correct markup, provided the current language is English" in {
      val html = languageSelectionView(frontendAppConfig.languageMap, frontendAppConfig.routeToSwitchLanguage)(messagesApi.preferred(Seq(Lang("en"))))
      val document = Jsoup.parse(html.toString)

      document.select(Selectors.welshLink).attr("href") mustBe frontendAppConfig.routeToSwitchLanguage("cymraeg").url
      document.select(Selectors.content).text mustBe "English | Cymraeg"
    }

    "Render the correct markup, provided the current language is Welsh" in {
      val html = languageSelectionView(frontendAppConfig.languageMap, frontendAppConfig.routeToSwitchLanguage)(messagesApi.preferred(Seq(Lang("cy"))))
      val document = Jsoup.parse(html.toString)

      document.select(Selectors.englishLink).attr("href") mustBe frontendAppConfig.routeToSwitchLanguage("english").url
      document.select(Selectors.content).text mustBe "English | Cymraeg"
    }
  }
}