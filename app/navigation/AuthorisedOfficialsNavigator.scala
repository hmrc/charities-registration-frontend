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
import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.routes
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.addressLookup.AuthorisedOfficialAddressLookupPage
import pages.authorisedOfficials._
import play.api.mvc.Call

class AuthorisedOfficialsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case AuthorisedOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsDOBPage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPhoneNumberPage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPositionPage(index)) match {
      case Some(_) => authOfficialRoutes.IsAuthorisedOfficialNinoController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsAuthorisedOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsAuthorisedOfficialNinoPage(index)) match {
      case Some(true) => authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(NormalMode, index)
      case Some(false) => routes.DeadEndController.onPageLoad() // TODO redirect to next page once created
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNinoPage(index)) match {
      case Some(_) => addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialAddressLookupPage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialPreviousAddressController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialPreviousAddressPage(index) => userAnswers:UserAnswers  => userAnswers.get(AuthorisedOfficialPreviousAddressPage(index)) match {
      case Some(true) => routes.DeadEndController.onPageLoad() // TODO redirect to next page once created
      case Some(false) => authOfficialRoutes.AddedOneAuthorisedOfficialController.onPageLoad(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AddedOneAuthorisedOfficialPage(index) => _ => authOfficialRoutes.IsAddAnotherAuthorisedOfficialController.onPageLoad(NormalMode)

    case IsAddAnotherAuthorisedOfficialPage => userAnswers: UserAnswers => userAnswers.get(IsAddAnotherAuthorisedOfficialPage) match {
      case Some(true) => routes.DeadEndController.onPageLoad() // TODO redirect to next page once created
      case Some(false) => authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case AuthorisedOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsDOBPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPhoneNumberPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPositionPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsAuthorisedOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsAuthorisedOfficialNinoPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNinoPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialAddressLookupPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialPreviousAddressPage(index) => userAnswers:UserAnswers  => userAnswers.get(AuthorisedOfficialPreviousAddressPage(index)) match {
      case Some(true) => routes.DeadEndController.onPageLoad() // TODO redirect to next page once created
      case Some(false) => routes.DeadEndController.onPageLoad() // TODO redirect to next page once created
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsAddAnotherAuthorisedOfficialPage => userAnswers: UserAnswers => userAnswers.get(IsAddAnotherAuthorisedOfficialPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }
}
