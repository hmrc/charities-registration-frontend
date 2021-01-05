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
import controllers.contactDetails.{routes => charityInfoRoutes}
import controllers.routes
import javax.inject.Inject
import models._
import pages.Page
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityInformationSummaryPage, CharityNamePage}
import play.api.mvc.Call


class CharityInformationNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  override val normalRoutes: Page => UserAnswers => Call = {
    case CharityNamePage => userAnswers: UserAnswers => userAnswers.get(CharityNamePage) match {
        case Some(_) => charityInfoRoutes.CharityContactDetailsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CharityContactDetailsPage => userAnswers: UserAnswers => userAnswers.get(CharityContactDetailsPage) match {
      case Some(_) => if(frontendAppConfig.isExternalTest){
        charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      } else {
        userAnswers.get(CharityOfficialAddressLookupPage) match {
          case Some(_) => charityInfoRoutes.ConfirmCharityOfficialAddressController.onPageLoad()
          case _ => controllers.addressLookup.routes.CharityOfficialAddressLookupController.initializeJourney()
        }
      }
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CharityOfficialAddressLookupPage => userAnswers: UserAnswers => userAnswers.get(CharityOfficialAddressLookupPage) match {
      case Some(_) => charityInfoRoutes.CanWeSendToThisAddressController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CanWeSendToThisAddressPage => userAnswers: UserAnswers => userAnswers.get(CanWeSendToThisAddressPage) match {
      case Some(true) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case Some(false) if userAnswers.get(CharityPostalAddressLookupPage).isDefined => charityInfoRoutes.ConfirmCharityPostalAddressController.onPageLoad()
      case Some(_) => controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CharityPostalAddressLookupPage => userAnswers: UserAnswers => userAnswers.get(CharityPostalAddressLookupPage) match {
      case Some(_) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharityInformationSummaryPage => _ => routes.IndexController.onPageLoad(None)

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {

    case CharityNamePage => userAnswers: UserAnswers => userAnswers.get(CharityNamePage) match {
      case Some(_) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CharityContactDetailsPage => userAnswers: UserAnswers => userAnswers.get(CharityContactDetailsPage) match {
      case Some(_) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CharityOfficialAddressLookupPage => userAnswers: UserAnswers => userAnswers.get(CharityOfficialAddressLookupPage) match {
      case Some(_) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CanWeSendToThisAddressPage => userAnswers: UserAnswers => userAnswers.get(CanWeSendToThisAddressPage) match {
      case Some(true) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case Some(false) if userAnswers.get(CharityPostalAddressLookupPage).isDefined => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case Some(false) => controllers.addressLookup.routes.CharityPostalAddressLookupController.initializeJourney()
      case _ => routes.SessionExpiredController.onPageLoad()
    }
    case CharityPostalAddressLookupPage => userAnswers: UserAnswers => userAnswers.get(CharityPostalAddressLookupPage) match {
      case Some(_) => charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val playbackRouteMap: Page => UserAnswers => Call = {

    case CharityNamePage => userAnswers: UserAnswers => userAnswers.get(CharityNamePage) match {
      case Some(_) => controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

}
