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

package controllers

import base.SpecBase
import play.api.test.Helpers._
import uk.gov.hmrc.play.bootstrap.binders.SafeRedirectUrl

class SignOutControllerSpec extends SpecBase {

  val controller: SignOutController = inject[SignOutController]

  "SignOut Controller" when {

    "calling the .signOut() method" must {

      "redirect to /gg/sign-out with continue to the feedback survey" in {

        val result = controller.signOut(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(frontendAppConfig.signOutUrl + s"?continue=${SafeRedirectUrl(frontendAppConfig.exitSurveyUrl).encodedUrl}")
      }
    }

    "calling the .signOutNoSurvey() method" must {

      "redirect to /gg/sign-out with continue to the session timeout page" in {

        val result = controller.signOutNoSurvey(fakeRequest)

        status(result) mustEqual SEE_OTHER
        redirectLocation(result) mustBe Some(frontendAppConfig.signOutUrl +
          s"?continue=${SafeRedirectUrl(frontendAppConfig.host + controllers.routes.SessionExpiredController.onPageLoad().url).encodedUrl}")
      }
    }
  }
}
