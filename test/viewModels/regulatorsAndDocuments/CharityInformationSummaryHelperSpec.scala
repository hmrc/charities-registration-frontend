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

package viewModels.regulatorsAndDocuments

import assets.constants.ConfirmedAddressConstants
import assets.messages.BaseMessages
import base.SpecBase
import controllers.charityInformation.{routes => charityInfoRoutes}
import models.{CharityContactDetails, CharityName, CheckMode, UserAnswers}
import pages.addressLookup.CharityInformationAddressLookupPage
import pages.charityInformation.{CharityContactDetailsPage, CharityNamePage}
import utils.CurrencyFormatter
import viewmodels.SummaryListRowHelper
import viewmodels.charityInformation.CharityInformationSummaryHelper

class CharityInformationSummaryHelperSpec extends SpecBase with SummaryListRowHelper with CurrencyFormatter {


  val helper = new CharityInformationSummaryHelper(UserAnswers("id")
    .set(CharityNamePage, CharityName(fullName = "Believe",
                                      operatingName = Some("Original Charity"))).flatMap
     (_.set(CharityContactDetailsPage, CharityContactDetails(daytimePhone = "07700 900 982",
                                                             mobilePhone = Some("07700 900 982"),
                                                             emailAddress = "japan@china.com"))).flatMap
     (_.set(CharityInformationAddressLookupPage, ConfirmedAddressConstants.address)).success.value

  )


  "Check Your Answers Helper" must {

    "For the Charity names answers" must {

      "have a correctly formatted summary list rows" in {

        helper.charityNameRows mustBe Seq(summaryListRow(
          messages("charityName.fullName.checkYourAnswersLabel"),
          "Believe",
          Some(messages("charityName.fullName.checkYourAnswersLabel")),
          charityInfoRoutes.CharityNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        ),
        summaryListRow(
          messages("charityName.operatingName.checkYourAnswersLabel"),
          "Original Charity",
          Some(messages("charityName.operatingName.checkYourAnswersLabel")),
          charityInfoRoutes.CharityNameController.onPageLoad(CheckMode) -> BaseMessages.changeLink
        )
        )
      }
    }

    "For the Charity Contact Details answers" must {

      "have a correctly formatted summary list rows" in {

        helper.charityContactDetailsRows mustBe Seq(
          summaryListRow(
          messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel"),
          "07700 900 982",
          Some(messages("charityContactDetails.mainPhoneNumber.checkYourAnswersLabel")),
          charityInfoRoutes.CharityContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel"),
            "07700 900 982",
            Some(messages("charityContactDetails.alternativePhoneNumber.checkYourAnswersLabel")),
            charityInfoRoutes.CharityContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          ),
          summaryListRow(
            messages("charityContactDetails.emailAddress.checkYourAnswersLabel"),
            "japan@china.com",
            Some(messages("charityContactDetails.emailAddress.checkYourAnswersLabel")),
            charityInfoRoutes.CharityContactDetailsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Charity official address answer" must {

      "have a correctly formatted summary list row" in {

        helper.officialAddressRow mustBe Seq(
          summaryListRow(
            messages("charityInformation.addressLookup.checkYourAnswersLabel"),
            "Test 1, Test 2, AA00 0AA, United Kingdom",
            Some(messages("charityInformation.addressLookup.checkYourAnswersLabel")),
            controllers.addressLookup.routes.CharityInformationAddressLookupController.initializeJourney() -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
