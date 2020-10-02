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

package navigation

import config.FrontendAppConfig
import controllers.nominees.{routes => nomineeRoutes}
import controllers.routes
import javax.inject.Inject
import models._
import pages.Page
import pages.nominees._
import play.api.mvc.Call

class NomineesNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case IsAuthoriseNomineePage => userAnswers: UserAnswers => userAnswers.get(IsAuthoriseNomineePage) match {
      case Some(true) => nomineeRoutes.ChooseNomineeController.onPageLoad(NormalMode)
      case Some(false) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case ChooseNomineePage => userAnswers: UserAnswers => userAnswers.get(ChooseNomineePage) match {
      case Some(true) => nomineeRoutes.IndividualNomineeNameController.onPageLoad(NormalMode)
      case Some(false) => nomineeRoutes.WhatIsTheNameOfTheOrganisationController.onPageLoad(NormalMode)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IndividualNomineeNamePage => userAnswers: UserAnswers => userAnswers.get(IndividualNomineeNamePage) match {
      case Some(_) => nomineeRoutes.IndividualNomineeDOBController.onPageLoad(NormalMode)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IndividualNomineeDOBPage => userAnswers: UserAnswers => userAnswers.get(IndividualNomineeDOBPage) match {
      case Some(_) => nomineeRoutes.IndividualNomineesPhoneNumberController.onPageLoad(NormalMode)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IndividualNomineesPhoneNumberPage => userAnswers: UserAnswers => userAnswers.get(IndividualNomineesPhoneNumberPage) match {
      case Some(_) => nomineeRoutes.IsIndividualNomineeNinoController.onPageLoad(NormalMode)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case WhatIsTheNameOfOrganisationPage => userAnswers: UserAnswers => userAnswers.get(WhatIsTheNameOfOrganisationPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsIndividualNomineeNinoPage => userAnswers: UserAnswers => userAnswers.get(IsIndividualNomineeNinoPage) match {
      case Some(true) => routes.DeadEndController.onPageLoad() // TODO next page
      case Some(false) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case NomineeDetailsSummaryPage => _ => routes.IndexController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case IsAuthoriseNomineePage => userAnswers: UserAnswers => userAnswers.get(IsAuthoriseNomineePage) match {
      case Some(true) => routes.DeadEndController.onPageLoad() // TODO next page
      case Some(false) => nomineeRoutes.NomineeDetailsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case ChooseNomineePage => userAnswers: UserAnswers => userAnswers.get(ChooseNomineePage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IndividualNomineeNamePage => userAnswers: UserAnswers => userAnswers.get(IndividualNomineeNamePage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IndividualNomineeDOBPage => userAnswers: UserAnswers => userAnswers.get(IndividualNomineeDOBPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IndividualNomineesPhoneNumberPage => userAnswers: UserAnswers => userAnswers.get(IndividualNomineesPhoneNumberPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case WhatIsTheNameOfOrganisationPage => userAnswers: UserAnswers => userAnswers.get(WhatIsTheNameOfOrganisationPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsIndividualNomineeNinoPage => userAnswers: UserAnswers => userAnswers.get(IsIndividualNomineeNinoPage) match {
      case Some(true) => routes.DeadEndController.onPageLoad() // TODO next page
      case Some(false) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case NomineeDetailsSummaryPage => _ => routes.IndexController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case PlaybackMode =>
      routes.SessionExpiredController.onPageLoad() // TODO
  }
}
