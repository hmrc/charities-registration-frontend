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

package views.components

import assets.messages.FooterLinksMessages
import base.SpecBase
import play.api.mvc.AnyContentAsEmpty
import play.api.test.FakeRequest
import uk.gov.hmrc.govukfrontend.views.viewmodels.footer.FooterItem

class FooterLinksSpec extends SpecBase {

  implicit val request: FakeRequest[AnyContentAsEmpty.type] = FakeRequest("", "")

  "FooterLinks.cookieLink(implicit messages: Messages, appConfig: FrontendAppConfig)" must {

    "Have the correct message and link for the cookies" in {

      FooterLinks.cookieLink mustBe FooterItem(
        Some(FooterLinksMessages.cookies),
        Some(frontendAppConfig.cookies)
      )
    }
  }

  "FooterLinks.accessibility(implicit messages: Messages, appConfig: FrontendAppConfig)" must {

    "Have the correct message and link for the privacy" in {

      FooterLinks.accessibilityLink mustBe FooterItem(
        Some(FooterLinksMessages.accessibility),
        Some(frontendAppConfig.accessibilityStatementFrontendUrl())
      )
    }
  }

  "FooterLinks.privacyLink(implicit messages: Messages, appConfig: FrontendAppConfig)" must {

    "Have the correct message and link for the privacy" in {

      FooterLinks.privacyLink mustBe FooterItem(
        Some(FooterLinksMessages.privacy),
        Some(frontendAppConfig.privacy)
      )
    }
  }

  "FooterLinks.termsConditionsLink(implicit messages: Messages, appConfig: FrontendAppConfig)" must {

    "Have the correct message and link for the termsConditions" in {

      FooterLinks.termsConditionsLink mustBe FooterItem(
        Some(FooterLinksMessages.termsConditions),
        Some(frontendAppConfig.termsConditions)
      )
    }
  }

  "FooterLinks.govukHelpLink(implicit messages: Messages, appConfig: FrontendAppConfig)" must {

    "Have the correct message and link for the help" in {

      FooterLinks.govukHelpLink mustBe FooterItem(
        Some(FooterLinksMessages.help),
        Some(frontendAppConfig.govUKHelp)
      )
    }
  }

  "FooterLinks.items(implicit messages: Messages, appConfig: FrontendAppConfig)" must {

    "Have the correct sequence of links" in {
      FooterLinks.items mustBe Seq(
        FooterItem(
          Some(FooterLinksMessages.cookies),
          Some(frontendAppConfig.cookies)
        ),
        FooterItem(
          Some(FooterLinksMessages.accessibility),
          Some(frontendAppConfig.accessibilityStatementFrontendUrl)
        ),
        FooterItem(
          Some(FooterLinksMessages.privacy),
          Some(frontendAppConfig.privacy)
        ),
        FooterItem(
          Some(FooterLinksMessages.termsConditions),
          Some(frontendAppConfig.termsConditions)
        ),
        FooterItem(
          Some(FooterLinksMessages.help),
          Some(frontendAppConfig.govUKHelp)
        )
      )
    }
  }
}
