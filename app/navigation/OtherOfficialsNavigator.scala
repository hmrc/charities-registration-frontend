/*
 * Copyright 2025 HM Revenue & Customs
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

import controllers.addressLookup.{routes => addressLookupRoutes}
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import controllers.routes
import models.{CheckMode, Index, NormalMode, UserAnswers}
import pages.Page
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.otherOfficials._
import pages.sections.Section8Page
import play.api.mvc.Call

class OtherOfficialsNavigator extends BaseNavigator {

  def redirectToPlaybackPage(index: Int): Call = index match {
    case x if x >= 0 && x <= 2 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(x))
    case _                     => routes.PageNotFoundController.onPageLoad()
  }

  override val normalRoutes: Page => UserAnswers => Call = {

    case OtherOfficialsNamePage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsNamePage(index)) match {
          case Some(_) => otherOfficialRoutes.OtherOfficialsDOBController.onPageLoad(NormalMode, index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsDOBPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsDOBPage(index)) match {
          case Some(_) => otherOfficialRoutes.OtherOfficialsPhoneNumberController.onPageLoad(NormalMode, index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsPhoneNumberPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsPhoneNumberPage(index)) match {
          case Some(_) => otherOfficialRoutes.OtherOfficialsPositionController.onPageLoad(NormalMode, index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsPositionPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsPositionPage(index)) match {
          case Some(_) => otherOfficialRoutes.IsOtherOfficialNinoController.onPageLoad(NormalMode, index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsOtherOfficialNinoPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOtherOfficialNinoPage(index)) match {
          case Some(true)  => otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(NormalMode, index)
          case Some(false) => otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(NormalMode, index)
          case _           => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsPassportPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsPassportPage(index)) match {
          case Some(_) =>
            userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
              case Some(_) =>
                otherOfficialRoutes.ConfirmOtherOfficialsAddressController.onPageLoad(index)
              case _       =>
                addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(index, NormalMode)
            }
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsNinoPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsNinoPage(index)) match {
          case Some(_) =>
            userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
              case Some(_) => otherOfficialRoutes.ConfirmOtherOfficialsAddressController.onPageLoad(index)
              case _       => addressLookupRoutes.OtherOfficialsAddressLookupController.initializeJourney(index, NormalMode)
            }
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialAddressLookupPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
          case Some(address) if isNotValidAddress(address) =>
            otherOfficialRoutes.AmendOtherOfficialsAddressController.onPageLoad(NormalMode, index)
          case Some(_)                                     =>
            otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(NormalMode, index)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsOtherOfficialsPreviousAddressPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOtherOfficialsPreviousAddressPage(index)) match {
          case Some(true) if userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)).isDefined =>
            otherOfficialRoutes.ConfirmOtherOfficialsPreviousAddressController.onPageLoad(index)
          case Some(true)                                                                             =>
            addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, NormalMode)
          case Some(false)                                                                            =>
            redirectToPlaybackPage(index)
          case _                                                                                      =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialPreviousAddressLookupPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)) match {
          case Some(address) if isNotValidAddress(address) =>
            otherOfficialRoutes.AmendOtherOfficialsPreviousAddressController.onPageLoad(NormalMode, index)
          case Some(_)                                     => redirectToPlaybackPage(index)
          case _                                           => routes.PageNotFoundController.onPageLoad()
        }

    case AddedOtherOfficialPage(0) | AddedOtherOfficialPage(1) | AddedOtherOfficialPage(2) =>
      _ => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad

    case OtherOfficialsSummaryPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsAddAnotherOtherOfficialPage) match {
          case Some(_)
              if userAnswers.get(Section8Page).contains(true).||(userAnswers.get(OtherOfficialsId(2)).nonEmpty) =>
            routes.IndexController.onPageLoad(None)
          case Some(true)
              if userAnswers.get(OtherOfficialsId(0)).nonEmpty && userAnswers.get(OtherOfficialsId(1)).nonEmpty =>
            otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(NormalMode, 2)
          case _ if userAnswers.get(OtherOfficialsId(0)).nonEmpty =>
            otherOfficialRoutes.OtherOfficialsNameController.onPageLoad(NormalMode, 1)
          case _                                                  => routes.PageNotFoundController.onPageLoad()
        }

    case RemoveOtherOfficialsPage =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsId(0)) match {
          case Some(_)                               => otherOfficialRoutes.OtherOfficialsSummaryController.onPageLoad
          case _ if userAnswers.data.fields.nonEmpty => otherOfficialRoutes.CharityOtherOfficialsController.onPageLoad()
          case _                                     => routes.PageNotFoundController.onPageLoad()
        }

    case _ => _ => routes.IndexController.onPageLoad(None)
  }

  override val checkRouteMap: Page => UserAnswers => Call = {
    case OtherOfficialsNamePage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsNamePage(index)) match {
          case Some(_) => redirectToPlaybackPage(index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsDOBPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsDOBPage(index)) match {
          case Some(_) => redirectToPlaybackPage(index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsPhoneNumberPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsPhoneNumberPage(index)) match {
          case Some(_) => redirectToPlaybackPage(index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsPositionPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsPositionPage(index)) match {
          case Some(_) => redirectToPlaybackPage(index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case IsOtherOfficialNinoPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOtherOfficialNinoPage(index)) match {
          case Some(true) if userAnswers.get(OtherOfficialsNinoPage(index)).isDefined      =>
            redirectToPlaybackPage(index)
          case Some(true)                                                                  =>
            otherOfficialRoutes.OtherOfficialsNinoController.onPageLoad(CheckMode, index)
          case Some(false) if userAnswers.get(OtherOfficialsPassportPage(index)).isDefined =>
            redirectToPlaybackPage(index)
          case Some(false)                                                                 =>
            otherOfficialRoutes.OtherOfficialsPassportController.onPageLoad(CheckMode, index)
          case _                                                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsNinoPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsNinoPage(index)) match {
          case Some(_) => redirectToPlaybackPage(index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialsPassportPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialsPassportPage(index)) match {
          case Some(_) => redirectToPlaybackPage(index)
          case _       => routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialAddressLookupPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialAddressLookupPage(index)) match {
          case Some(address) if isNotValidAddress(address) =>
            otherOfficialRoutes.AmendOtherOfficialsAddressController.onPageLoad(CheckMode, index)
          case Some(_)                                     =>
            otherOfficialRoutes.IsOtherOfficialsPreviousAddressController.onPageLoad(CheckMode, index)
          case _                                           =>
            routes.PageNotFoundController.onPageLoad()
        }

    case IsOtherOfficialsPreviousAddressPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(IsOtherOfficialsPreviousAddressPage(index)) match {
          case Some(true) if userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)).isDefined =>
            redirectToPlaybackPage(index)
          case Some(true)                                                                             =>
            addressLookupRoutes.OtherOfficialsPreviousAddressLookupController.initializeJourney(index, CheckMode)
          case Some(false)                                                                            =>
            redirectToPlaybackPage(index)
          case _                                                                                      =>
            routes.PageNotFoundController.onPageLoad()
        }

    case OtherOfficialPreviousAddressLookupPage(index) =>
      (userAnswers: UserAnswers) =>
        userAnswers.get(OtherOfficialPreviousAddressLookupPage(index)) match {
          case Some(address) if isNotValidAddress(address) =>
            otherOfficialRoutes.AmendOtherOfficialsPreviousAddressController.onPageLoad(CheckMode, index)
          case Some(_)                                     => redirectToPlaybackPage(index)
          case _                                           => routes.PageNotFoundController.onPageLoad()
        }

    case _ => _ => routes.IndexController.onPageLoad(None)

  }
}
