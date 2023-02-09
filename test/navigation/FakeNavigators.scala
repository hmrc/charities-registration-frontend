/*
 * Copyright 2023 HM Revenue & Customs
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

package navigation

import base.SpecBase
import config.FrontendAppConfig
import models.{Mode, UserAnswers}
import pages.Page
import play.api.mvc.Call

object FakeNavigators extends SpecBase {

  trait FakeMainNavigator extends BaseNavigator {
    override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = onwardRoute
  }

  object FakeEligibilityNavigator
      extends EligibilityNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeCharityInformationNavigator
      extends CharityInformationNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeRegulatorsAndDocumentsNavigator
      extends RegulatorsAndDocumentsNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeDocumentsNavigator
      extends DocumentsNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeObjectivesNavigator
      extends ObjectivesNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeFundRaisingNavigator
      extends FundRaisingNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeBankDetailsNavigator
      extends BankDetailsNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeAuthorisedOfficialsNavigator
      extends AuthorisedOfficialsNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeOtherOfficialsNavigator
      extends OtherOfficialsNavigator()(frontendAppConfig: FrontendAppConfig)
      with FakeMainNavigator

  object FakeNomineesNavigator extends NomineesNavigator()(frontendAppConfig: FrontendAppConfig) with FakeMainNavigator
}
