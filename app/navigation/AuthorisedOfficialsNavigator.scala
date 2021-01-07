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

package navigation

import config.FrontendAppConfig
import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.routes
import javax.inject.Inject
import models.{CheckMode, Index, Mode, NormalMode, PlaybackMode, UserAnswers}
import pages.Page
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, AuthorisedOfficialPreviousAddressLookupPage}
import pages.authorisedOfficials._
import pages.sections.Section7Page
import play.api.mvc.Call

class AuthorisedOfficialsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  def redirectToPlaybackPage(index: Int): Call = index match {
    case x if x == 0 | x == 1 => authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(x))
    case _ => routes.SessionExpiredController.onPageLoad() // TODO redirect to page if user attempts to use non-0-or-1 index
  }

  override val normalRoutes: Page => UserAnswers => Call = {

    case AuthorisedOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsDOBController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsDOBPage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsPhoneNumberController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPhoneNumberPage(index)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsPositionController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPositionPage(index)) match {
      case Some(_) => authOfficialRoutes.IsAuthorisedOfficialNinoController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsAuthorisedOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsAuthorisedOfficialNinoPage(index)) match {
      case Some(true) => authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(NormalMode, index)
      case Some(false) => authOfficialRoutes.AuthorisedOfficialsPassportController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNinoPage(index)) match {
      case Some(_) => if(frontendAppConfig.isExternalTest){
        redirectToPlaybackPage(index)
      } else {
        addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(index, NormalMode)
      }
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPassportPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPassportPage(index)) match {
      case Some(_) => if(frontendAppConfig.isExternalTest){
        redirectToPlaybackPage(index)
      } else {
        addressLookupRoutes.AuthorisedOfficialsAddressLookupController.initializeJourney(index, NormalMode)
      }
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialAddressLookupPage(index)) match {
      case Some(_) => authOfficialRoutes.IsAuthorisedOfficialPreviousAddressController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsAuthorisedOfficialPreviousAddressPage(index) => userAnswers: UserAnswers => userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(index)) match {
      case Some(true) => addressLookupRoutes.AuthorisedOfficialsPreviousAddressLookupController.initializeJourney(index, NormalMode)
      case Some(false) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialPreviousAddressLookupPage(index) => userAnswers: UserAnswers =>
      userAnswers.get(AuthorisedOfficialPreviousAddressLookupPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AddedAuthorisedOfficialPage(_) => _ => authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad()

    case AuthorisedOfficialsSummaryPage => userAnswers: UserAnswers => userAnswers.get(IsAddAnotherAuthorisedOfficialPage) match {
      case Some(true) if userAnswers.get(Section7Page).contains(true) => routes.IndexController.onPageLoad(None)
      case Some(true) => authOfficialRoutes.AuthorisedOfficialsNameController.onPageLoad(NormalMode, 1)
      case Some(false) => routes.IndexController.onPageLoad(None)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case RemoveAuthorisedOfficialsPage => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsId(0)) match {
      case Some(_) => authOfficialRoutes.AuthorisedOfficialsSummaryController.onPageLoad()
      case _ if userAnswers.data.fields.nonEmpty => authOfficialRoutes.CharityAuthorisedOfficialsController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {
    case AuthorisedOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNamePage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsDOBPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPhoneNumberPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPositionPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsAuthorisedOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsAuthorisedOfficialNinoPage(index)) match {
      case Some(true) if userAnswers.get(AuthorisedOfficialsNinoPage(index)).isDefined => redirectToPlaybackPage(index)
      case Some(true) => authOfficialRoutes.AuthorisedOfficialsNinoController.onPageLoad(CheckMode, index)
      case Some(false) if userAnswers.get(AuthorisedOfficialsPassportPage(index)).isDefined => redirectToPlaybackPage(index)
      case Some(false) => authOfficialRoutes.AuthorisedOfficialsPassportController.onPageLoad(CheckMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsNinoPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialsPassportPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialsPassportPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(AuthorisedOfficialAddressLookupPage(index)) match {
      case Some(_) => authOfficialRoutes.IsAuthorisedOfficialPreviousAddressController.onPageLoad(CheckMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsAuthorisedOfficialPreviousAddressPage(index) => userAnswers: UserAnswers => userAnswers.get(IsAuthorisedOfficialPreviousAddressPage(index)) match {
      case Some(true) if userAnswers.get(AuthorisedOfficialPreviousAddressLookupPage(index)).isDefined => redirectToPlaybackPage(index)
      case Some(true) => addressLookupRoutes.AuthorisedOfficialsPreviousAddressLookupController.initializeJourney(index, CheckMode)
      case Some(false) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AuthorisedOfficialPreviousAddressLookupPage(index) => userAnswers: UserAnswers =>
      userAnswers.get(AuthorisedOfficialPreviousAddressLookupPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)

  }

}
