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
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import controllers.routes
import javax.inject.Inject
import models.{CheckMode, Mode, NormalMode, UserAnswers}
import pages.Page
import pages.addressLookup.OtherOfficialAddressLookupPage
import pages.otherOfficials._
import play.api.mvc.Call

class OtherOfficialsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {

    case OtherOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsDOBController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsDOBPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPhoneNumberPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsPositionController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPositionPage(index)) match {
      case Some(_) => otherOfficialRoutes.IsOtherOfficialNinoController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsOtherOfficialNinoPage(index)) match {
      case Some(true) => otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(NormalMode, index)
      case Some(false) => routes.DeadEndController.onPageLoad() // TODO redirect to next page once created
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNinoPage(index)) match {
      case Some(_) => addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsPreviousAddressController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPreviousAddressPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPreviousAddressPage(index)) match {
      case Some(true) => routes.DeadEndController.onPageLoad()
      case Some(false) => index match {
        case 0 => otherOfficialRoutes.AddedOneOtherOfficialController.onPageLoad()
        case 1 => otherOfficialRoutes.AddedSecondOtherOfficialController.onPageLoad()
        case 2 => otherOfficialRoutes.AddedThirdOtherOfficialController.onPageLoad()
        case _ => routes.DeadEndController.onPageLoad() // TODO Summary page
      }
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AddedOneOtherOfficialPage => _ => otherOfficialRoutes.AddSecondOtherOfficialsController.onPageLoad()

    case AddedSecondOtherOfficialPage => _ => otherOfficialRoutes.AddAnotherOtherOfficialController.onPageLoad(NormalMode)

    case AddedThirdOtherOfficialPage => _ => routes.DeadEndController.onPageLoad()

    case AddAnotherOtherOfficialPage => userAnswers: UserAnswers => userAnswers.get(AddAnotherOtherOfficialPage) match {
      case Some(true) => otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(NormalMode,2)
      case Some(false) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad() // TODO redirect to next page once created
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case OtherOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsDOBPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPhoneNumberPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPositionPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsOtherOfficialNinoPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNinoPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPreviousAddressPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPreviousAddressPage(index)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO summary page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case AddAnotherOtherOfficialPage => userAnswers: UserAnswers => userAnswers.get(AddAnotherOtherOfficialPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad()// TODO summary page
      case _ => routes.SessionExpiredController.onPageLoad()
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
