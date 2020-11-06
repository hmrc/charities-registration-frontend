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
import models.{CheckMode, Index, Mode, NormalMode, PlaybackMode, UserAnswers}
import pages.Page
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.otherOfficials._
import play.api.mvc.Call

class OtherOfficialsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  def redirectToPlaybackPage(index: Int): Call = index match {
    case x if x >= 0 && x <= 2 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(x))
    case _ => routes.SessionExpiredController.onPageLoad() // TODO redirect to page if user attempts to use non-0-or-1 index
  }


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
      case Some(false) => otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(NormalMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPassportPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPassportPage(index)) match {
      case Some(_) => if(frontendAppConfig.isExternalTest){
        redirectToPlaybackPage(index)
      } else {
        addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(index, NormalMode)
      }
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNinoPage(index)) match {
      case Some(_) => if(frontendAppConfig.isExternalTest){
        redirectToPlaybackPage(index)
      } else {
        addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(index, NormalMode)
      }
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
      case Some(_) => otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(NormalMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialsPreviousAddressPage(index) => userAnswers: UserAnswers => userAnswers.get(IsOtherOfficialsPreviousAddressPage(index)) match {
      case Some(true) => addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, NormalMode)
      case Some(false) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialPreviousAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AddedOtherOfficialPage(0) => _ => otherOfficialRoutes.AddSecondOtherOfficialsController.onPageLoad()

    case AddedOtherOfficialPage(1) => _ => otherOfficialRoutes.AddAnotherOtherOfficialController.onPageLoad(NormalMode)

    case AddedOtherOfficialPage(2) => _ =>  otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()

    case AddAnotherOtherOfficialPage => userAnswers: UserAnswers => userAnswers.get(AddAnotherOtherOfficialPage) match {
      case Some(true) => otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(NormalMode,2)
      case Some(false) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case OtherOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsDOBPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPhoneNumberPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPositionPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsOtherOfficialNinoPage(index)) match {
      case Some(true) if userAnswers.get(OtherOfficialsNinoPage(index)).isDefined =>
        otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case Some(true) => otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(CheckMode, index)
      case Some(false) if userAnswers.get(OtherOfficialsPassportPage(index)).isDefined =>
        otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case Some(false) => otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(CheckMode, index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNinoPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPassportPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPassportPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
      case Some(_) => otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(CheckMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialsPreviousAddressPage(index) => userAnswers: UserAnswers => userAnswers.get(IsOtherOfficialsPreviousAddressPage(index)) match {
      case Some(true) if userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)).isDefined =>
        otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case Some(true) => addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, CheckMode)
      case Some(false) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialPreviousAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)) match {
      case Some(_) => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AddAnotherOtherOfficialPage => userAnswers: UserAnswers => userAnswers.get(AddAnotherOtherOfficialPage) match {
      case Some(_) => routes.DeadEndController.onPageLoad()// TODO summary page
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case AddedOtherOfficialPage(1)  => userAnswers: UserAnswers => userAnswers.get(AddedOtherOfficialPage(1)) match {
      case Some(_) => routes.DeadEndController.onPageLoad() // TODO next page
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val playbackRoute: Page => UserAnswers => Call = {
    case OtherOfficialsNamePage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNamePage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsDOBPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsDOBPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPhoneNumberPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPhoneNumberPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPositionPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPositionPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(IsOtherOfficialNinoPage(index)) match {
      case Some(true) if userAnswers.get(OtherOfficialsNinoPage(index)).isDefined => redirectToPlaybackPage(index)
      case Some(true) => otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(PlaybackMode,index)
      case Some(false) if userAnswers.get(OtherOfficialsPassportPage(index)).isDefined =>
        redirectToPlaybackPage(index)
      case Some(false) => otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(PlaybackMode,index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsNinoPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsNinoPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialsPassportPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialsPassportPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
      case Some(_) => otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(PlaybackMode, index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case IsOtherOfficialsPreviousAddressPage(index) => userAnswers:UserAnswers  => userAnswers.get(IsOtherOfficialsPreviousAddressPage(index)) match {
      case Some(true) if userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)).isDefined => redirectToPlaybackPage(index)
      case Some(true) => addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, PlaybackMode)
      case Some(false) => redirectToPlaybackPage(index)
      case _ =>  routes.SessionExpiredController.onPageLoad()
    }

    case OtherOfficialPreviousAddressLookupPage(index) => userAnswers: UserAnswers => userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)) match {
      case Some(_) => redirectToPlaybackPage(index)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()

  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case PlaybackMode =>
      playbackRoute(page)(userAnswers)
  }
}
