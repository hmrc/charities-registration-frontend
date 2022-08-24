/*
 * Copyright 2022 HM Revenue & Customs
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

package viewModels.otherOfficials

import java.time.LocalDate
import base.SpecBase
import base.data.constants.ConfirmedAddressConstants
import models.authOfficials.OfficialsPosition
import models.{Name, Passport, PhoneNumber, SelectTitle, UserAnswers}
import pages.addressLookup.{OtherOfficialAddressLookupPage, OtherOfficialPreviousAddressLookupPage}
import pages.otherOfficials._
import viewmodels.otherOfficials.OtherOfficialStatusHelper

class OtherOfficialStatusHelperSpec extends SpecBase {

  implicit class OtherStatusPageSetter(userAnswers: UserAnswers) {

    def addAddress(index: Int): UserAnswers =
      userAnswers
        .set(IsOtherOfficialsPreviousAddressPage(index), true)
        .flatMap(_.set(OtherOfficialPreviousAddressLookupPage(index), ConfirmedAddressConstants.address))
        .success
        .value

    def addPassport(index: Int): UserAnswers =
      userAnswers
        .set(IsOtherOfficialNinoPage(index), false)
        .flatMap(
          _.set(OtherOfficialsPassportPage(index), Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth)))
        )
        .success
        .value

    def removeNino(index: Int): UserAnswers =
      userAnswers.remove(OtherOfficialsNinoPage(index)).success.value

  }

  private val helper = OtherOfficialStatusHelper

  private val year       = 2000
  private val month      = 1
  private val dayOfMonth = 2

  def common(index: Int, userAnswers: UserAnswers): UserAnswers = userAnswers
    .set(OtherOfficialsNamePage(index), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
    .flatMap(_.set(OtherOfficialsDOBPage(index), LocalDate.of(year, month, dayOfMonth)))
    .flatMap(
      _.set(
        OtherOfficialsPhoneNumberPage(index),
        PhoneNumber(daytimePhone = "07700 900 982", mobilePhone = Some("07700 900 982"))
      )
    )
    .flatMap(_.set(OtherOfficialsPositionPage(index), OfficialsPosition.values.head))
    .flatMap(_.set(OtherOfficialAddressLookupPage(index), ConfirmedAddressConstants.address))
    .success
    .value

  def completeFirstTwo: UserAnswers = common(1, common(0, emptyUserAnswers))
    .set(IsOtherOfficialNinoPage(0), true)
    .flatMap(_.set(OtherOfficialsNinoPage(0), "AA123123A"))
    .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(0), false))
    .flatMap(_.set(IsOtherOfficialNinoPage(1), true))
    .flatMap(_.set(OtherOfficialsNinoPage(1), "AA123123A"))
    .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(1), false))
    .flatMap(_.set(IsAddAnotherOtherOfficialPage, false))
    .success
    .value

  def completeAllThree: UserAnswers = common(2, completeFirstTwo)
    .set(IsOtherOfficialNinoPage(2), true)
    .flatMap(_.set(OtherOfficialsNinoPage(2), "AA123123A"))
    .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(2), false))
    .flatMap(_.set(IsAddAnotherOtherOfficialPage, true))
    .success
    .value

  "OtherOfficialsStatusHelper" must {

    "when verifying section 8 answers" must {

      "return false when user answers is empty" in {

        helper.checkComplete(emptyUserAnswers) mustBe false
      }

      "return false when not all required answer are defined" in {

        helper.checkComplete(common(0, emptyUserAnswers)) mustBe false
      }

      "return false when only one other official is provided" in {

        helper.checkComplete(
          common(0, emptyUserAnswers)
            .set(IsOtherOfficialNinoPage(0), true)
            .flatMap(_.set(OtherOfficialsNinoPage(0), "AA123123A"))
            .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(0), false))
            .success
            .value
        ) mustBe false
      }

      "return true with two other officials with Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(completeFirstTwo) mustBe true
      }

      "return false with two other officials with no required data for Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(completeFirstTwo.removeNino(0)) mustBe false
      }

      "return false with two other officials with additional data for Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(
          completeFirstTwo
            .addPassport(0)
            .addPassport(1)
            .set(OtherOfficialsNinoPage(0), "AA123123A")
            .success
            .value
        ) mustBe false
      }

      "return true with three other officials with Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(completeAllThree) mustBe true
      }

      "return true with three other officials with Nino, passport and no previous address (Scenario 2)" in {
        helper.checkComplete(completeAllThree.removeNino(0).removeNino(1).addPassport(0).addPassport(1)) mustBe true
      }

      "return false with three other officials with no required data for Nino, passport and no previous address (Scenario 2)" in {
        helper.checkComplete(
          completeAllThree
            .removeNino(1)
            .addPassport(1)
            .remove(IsOtherOfficialsPreviousAddressPage(0))
            .flatMap(_.remove(IsOtherOfficialsPreviousAddressPage(1)))
            .success
            .value
        ) mustBe false
      }

      "return true with two other officials with Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          completeFirstTwo
            .addAddress(0)
            .addAddress(1)
        ) mustBe true

      }

      "return false with two other officials with no required data for Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          completeFirstTwo
            .removeNino(0)
            .removeNino(1)
            .addAddress(0)
            .addAddress(1)
        ) mustBe false
      }

      "return false with two other officials with additional data for Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          completeFirstTwo
            .addPassport(0)
            .addPassport(1)
            .addAddress(0)
            .addAddress(1)
            .set(OtherOfficialsNinoPage(0), "AA123123A")
            .flatMap(_.set(OtherOfficialsNinoPage(1), "AA123123A"))
            .success
            .value
        ) mustBe false
      }

      "return true with three other officials with Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          completeAllThree
            .addAddress(0)
            .addAddress(1)
            .addAddress(2)
        ) mustBe true
      }

      "return true with three other officials with Nino, passport and previous address (Scenario 3)" in {
        helper.checkComplete(
          completeAllThree
            .addPassport(1)
            .removeNino(1)
            .addPassport(2)
            .removeNino(2)
            .addAddress(0)
            .addAddress(1)
        ) mustBe true
      }

      "return false with three other officials with no required data for Nino, passport and previous address (Scenario 3)" in {
        helper.checkComplete(
          completeAllThree
            .addPassport(1)
            .addPassport(2)
            .removeNino(1)
            .removeNino(2)
            .remove(IsOtherOfficialsPreviousAddressPage(0))
            .flatMap(_.remove(IsOtherOfficialsPreviousAddressPage(1)))
            .flatMap(_.remove(IsOtherOfficialsPreviousAddressPage(2)))
            .success
            .value
        ) mustBe false
      }

      "return true with two other officials with passport and no previous address (Scenario 4)" in {
        helper.checkComplete(
          completeFirstTwo
            .addPassport(0)
            .addPassport(1)
            .removeNino(0)
            .removeNino(1)
        ) mustBe true
      }

      "return false with two other officials with no required data for passport and no previous address (Scenario 4)" in {
        helper.checkComplete(
          completeFirstTwo
            .removeNino(0)
            .removeNino(1)
            .set(IsOtherOfficialNinoPage(0), false)
            .flatMap(_.set(IsOtherOfficialNinoPage(1), false))
            .success
            .value
        ) mustBe false
      }

      "return false with two other officials with additional data for passport and no previous address (Scenario 4)" in {
        helper.checkComplete(
          completeFirstTwo
            .addPassport(0)
            .addPassport(1)
            .set(OtherOfficialsNinoPage(0), "AA123123A")
            .flatMap(_.set(OtherOfficialsNinoPage(1), "AA123123A"))
            .success
            .value
        ) mustBe false
      }

      "return false with three other officials with no required data for passport, nino and no previous address (Scenario 4)" in {
        helper.checkComplete(
          completeAllThree
            .removeNino(1)
            .addPassport(2)
            .set(IsOtherOfficialNinoPage(0), false)
            .flatMap(_.set(IsOtherOfficialNinoPage(1), true))
            .flatMap(_.set(IsOtherOfficialNinoPage(2), true))
            .success
            .value
        ) mustBe false
      }

      "return true with two other officials with passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          completeFirstTwo
            .addPassport(0)
            .addPassport(1)
            .addAddress(0)
            .addAddress(1)
        ) mustBe true
      }

      "return false with two other officials with no required data for passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          completeFirstTwo
            .removeNino(0)
            .removeNino(1)
            .addAddress(0)
            .addAddress(1)
        ) mustBe false
      }

      "return false with two other officials with additional data for passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          completeFirstTwo
            .addPassport(0)
            .addAddress(0)
            .addPassport(1)
            .addAddress(1)
            .set(OtherOfficialsNinoPage(0), "AA123123A")
            .flatMap(_.set(OtherOfficialsNinoPage(1), "AA123123A"))
            .success
            .value
        ) mustBe false
      }

      "return true with three other officials with passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          completeAllThree
            .addPassport(0)
            .removeNino(0)
            .addAddress(0)
            .addPassport(1)
            .removeNino(1)
            .addAddress(1)
            .addPassport(2)
            .removeNino(2)
            .addAddress(2)
        ) mustBe true
      }

      "return true with three other officials with passport, Nino and previous address (Scenario 5)" in {
        helper.checkComplete(
          completeAllThree
            .addPassport(0)
            .removeNino(0)
            .addAddress(0)
            .addAddress(1)
        ) mustBe true
      }

      "return false with incorrect data for two other officials" in {
        helper.checkComplete(
          completeFirstTwo
            .removeNino(0)
            .addPassport(1)
            .remove(OtherOfficialsPassportPage(1))
            .success
            .value
        ) mustBe false
      }

      "return false with correct data for two other officials, but not correct data for the 3rd other official" in {
        helper.checkComplete(
          completeFirstTwo
            .set(IsAddAnotherOtherOfficialPage, true)
            .success
            .value
        ) mustBe false
      }

      "return false with incorrect data for three other officials" in {
        helper.checkComplete(
          completeAllThree
            .addPassport(0)
            .removeNino(0)
            .addAddress(0)
            .addAddress(1)
            .addAddress(2)
            .remove(IsOtherOfficialsPreviousAddressPage(1))
            .flatMap(_.remove(IsOtherOfficialsPreviousAddressPage(2)))
            .success
            .value
        ) mustBe false
      }

      "return false with correct data for first two other officials, but no answer to AddAnother page" in {
        helper.checkComplete(
          completeFirstTwo
            .remove(IsAddAnotherOtherOfficialPage)
            .success
            .value
        ) mustBe false
      }

    }

    "validate the data from the old service correctly" when {

      "one official with an unsupported title is pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, false)
            .flatMap(_.set(OtherOfficialsNamePage(0), Name(SelectTitle.UnsupportedTitle, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe false
      }

      "two officials with unsupported titles are pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, true)
            .flatMap(_.set(OtherOfficialsNamePage(0), Name(SelectTitle.UnsupportedTitle, "Joe", None, "Bloggs")))
            .flatMap(_.set(OtherOfficialsNamePage(1), Name(SelectTitle.UnsupportedTitle, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe false
      }

      "two officials with supported titles are pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, true)
            .flatMap(_.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Joe", None, "Bloggs")))
            .flatMap(_.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mrs, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe true
      }

      "one official with a supported title is pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherOtherOfficialPage, false)
            .flatMap(_.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe true
      }
    }
  }

  "otherOfficialCompleted" must {

    val userAnswers = common(0, emptyUserAnswers)
      .set(IsOtherOfficialNinoPage(0), true)
      .flatMap(_.set(OtherOfficialsNinoPage(0), "AA123123A"))
      .flatMap(_.set(IsOtherOfficialsPreviousAddressPage(0), false))
      .success
      .value

    "return false if other official data is empty" in {

      helper.otherOfficialCompleted(0, emptyUserAnswers) mustBe false
    }

    "return false if other official required data is not defined" in {

      helper.otherOfficialCompleted(0, common(0, emptyUserAnswers)) mustBe false
    }

    "return false if other official required data is defined with additional fields" in {

      helper.otherOfficialCompleted(
        0,
        common(
          0,
          userAnswers
            .addPassport(0)
            .set(OtherOfficialsNinoPage(0), "AA123123A")
            .success
            .value
        )
      ) mustBe false
    }

    "return true if other official required data is defined with additional fields" in {

      helper.otherOfficialCompleted(0, userAnswers) mustBe true
    }
  }
}
