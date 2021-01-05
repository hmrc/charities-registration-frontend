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

package viewmodels.charityInformation

import models.UserAnswers
import pages.QuestionPage
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import viewmodels.StatusHelper

object CharityInformationStatusHelper extends StatusHelper {

  override def checkComplete(userAnswers: UserAnswers): Boolean = {
    val pagesAlwaysRequired: Seq[QuestionPage[_]] =
      Seq(CharityNamePage, CharityContactDetailsPage, CharityOfficialAddressLookupPage, CanWeSendToThisAddressPage)
    val charityPostalAddressIsDefined = userAnswers.arePagesDefined(Seq(CharityPostalAddressLookupPage))

    userAnswers.arePagesDefined(pagesAlwaysRequired) && userAnswers.get(CanWeSendToThisAddressPage).exists {
      case true => !charityPostalAddressIsDefined
      case false => charityPostalAddressIsDefined
    } && userAnswers.get(CharityContactDetailsPage).fold(false)(_.emailAddress.trim.nonEmpty)
  }
}
