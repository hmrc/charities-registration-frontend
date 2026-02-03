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

package controllers

import base.SpecBase
import config.FrontendAppConfig
import play.api.test.Helpers.*

import java.net.URLEncoder

class SignOutControllerSpec extends SpecBase {

  private val controller: SignOutController = inject[SignOutController]

  "SignOut Controller" when {

    "calling the .signOut() method" must {

      "redirect to service home with new session" in {

        val result                = controller.signOut()(fakeRequest)
        val signOutNoSurveyAction = controller.signOut()
        val signOutResult         = signOutNoSurveyAction(fakeRequest)
        val expectedRedirectUrl   = redirectLocation(signOutResult).get
        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(expectedRedirectUrl)
      }
    }

    "calling the .signedYouOut() method" must {

      "display new page with new session" in {

        val result = controller.signedYouOut(fakeRequest)

        status(result) mustEqual OK
      }
    }

    "calling the .signOutNoSurvey() method" must {

      "display new page with new session" in {

        val result      = controller.signOutNoSurvey()(fakeRequest)
        val confApp     = FrontendAppConfig(servicesConfig = inject[FrontendAppConfig].servicesConfig)
        val continueUrl = URLEncoder.encode(s"${confApp.host}/register-charity-hmrc/sign-you-out", "UTF-8")
        val expectedUrl = s"${confApp.signOutUrl}?continue=$continueUrl"
        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(expectedUrl)
      }
    }

  }
}
