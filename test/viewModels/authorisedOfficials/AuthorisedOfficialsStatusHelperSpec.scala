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

package viewModels.authorisedOfficials

import java.time.LocalDate
import base.SpecBase
import base.data.constants.ConfirmedAddressConstants
import models.authOfficials.OfficialsPosition
import models.{Name, Passport, PhoneNumber, SelectTitle, UserAnswers}
import pages.addressLookup.{AuthorisedOfficialAddressLookupPage, AuthorisedOfficialPreviousAddressLookupPage}
import pages.authorisedOfficials._
import viewmodels.authorisedOfficials.AuthorisedOfficialsStatusHelper

class AuthorisedOfficialsStatusHelperSpec extends SpecBase {

  private val helper = AuthorisedOfficialsStatusHelper

  private val year       = 2000
  private val month      = 1
  private val dayOfMonth = 2

  def common(index: Int, userAnswers: UserAnswers): UserAnswers = userAnswers
    .set(AuthorisedOfficialsNamePage(index), Name(SelectTitle.Mr, firstName = "John", None, lastName = "Jones"))
    .flatMap(_.set(AuthorisedOfficialsDOBPage(index), LocalDate.of(year, month, dayOfMonth)))
    .flatMap(
      _.set(
        AuthorisedOfficialsPhoneNumberPage(index),
        PhoneNumber(daytimePhone = "07700 900 982", mobilePhone = Some("07700 900 982"))
      )
    )
    .flatMap(_.set(AuthorisedOfficialsPositionPage(index), OfficialsPosition.values.head))
    .flatMap(_.set(AuthorisedOfficialAddressLookupPage(index), ConfirmedAddressConstants.address))
    .success
    .value

  private val commonData = (index: Int, userAnswers: UserAnswers) => common(index, userAnswers)

  "AuthorisedOfficialsStatusHelper" must {

    "when verifying section 7 answers" must {

      "return false when user answers is empty" in {

        helper.checkComplete(emptyUserAnswers) mustBe false
      }

      "return false when not all required answer are defined" in {

        helper.checkComplete(commonData(0, emptyUserAnswers)) mustBe false
      }

      "return true when one authorised official with Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe true
      }

      "return false when one authorised official with no required data for Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return false when one authorised official with additional data for Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return true when two authorised official with Nino and no previous address (Scenario 2)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), true))
            .flatMap(_.set(AuthorisedOfficialsNinoPage(1), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe true
      }

      "return true when two authorised official with Nino, passport and no previous address (Scenario 2)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), false))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(1),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe true
      }

      "return false when two authorised official with no required data for Nino, passport and no previous address (Scenario 2)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), false))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(1),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe false
      }

      "return true when one authorised official with Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe true
      }

      "return false when one authorised official with no required data for Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return false when one authorised official with additional data for Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return true when two authorised official with Nino and previous address (Scenario 3)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), true))
            .flatMap(_.set(AuthorisedOfficialsNinoPage(1), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(1), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe true
      }

      "return true when two authorised official with Nino, passport and previous address (Scenario 3)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), false))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(1),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(1), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe true
      }

      "return false when two authorised official with no required data for Nino, passport and previous address (Scenario 3)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), false))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(1),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe false
      }

      "return true when one authorised official with passport and no previous address (Scenario 4)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe true
      }

      "return false when one authorised official with no required data for passport and no previous address (Scenario 4)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return false when one authorised official with additional data for passport and no previous address (Scenario 4)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return false when two authorised official with no required data for passport, nino and no previous address (Scenario 4)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), true))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe false
      }

      "return true when one authorised official with passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe true
      }

      "return false when one authorised official with no required data for passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return false when one authorised official with additional data for passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, false))
            .success
            .value
        ) mustBe false
      }

      "return true when two authorised official with passport and previous address (Scenario 5)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), false))
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(1),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(1), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe true
      }

      "return true when two authorised official with passport, Nino and previous address (Scenario 5)" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), true))
            .flatMap(_.set(AuthorisedOfficialsNinoPage(1), "AA123456A"))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(1), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(1), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe true
      }

      "return false when not correct data for one authorised official" in {
        helper.checkComplete(
          commonData(0, emptyUserAnswers)
            .set(IsAuthorisedOfficialNinoPage(0), true)
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
            .success
            .value
        ) mustBe false
      }

      "return false when not correct data for two authorised officials" in {
        helper.checkComplete(
          commonData(1, commonData(0, emptyUserAnswers))
            .set(IsAuthorisedOfficialNinoPage(0), false)
            .flatMap(
              _.set(
                AuthorisedOfficialsPassportPage(0),
                Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth))
              )
            )
            .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), true))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAuthorisedOfficialNinoPage(1), true))
            .flatMap(_.set(AuthorisedOfficialsNinoPage(1), "AA123456A"))
            .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(1), ConfirmedAddressConstants.address))
            .flatMap(_.set(IsAddAnotherAuthorisedOfficialPage, true))
            .success
            .value
        ) mustBe false
      }

    }

    "validate the data from the old service correctly" when {

      "one official with an unsupported title is pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherAuthorisedOfficialPage, false)
            .flatMap(_.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.UnsupportedTitle, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe false
      }

      "two officials with unsupported titles are pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherAuthorisedOfficialPage, true)
            .flatMap(_.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.UnsupportedTitle, "Joe", None, "Bloggs")))
            .flatMap(_.set(AuthorisedOfficialsNamePage(1), Name(SelectTitle.UnsupportedTitle, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe false
      }

      "two officials with supported titles are pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherAuthorisedOfficialPage, true)
            .flatMap(_.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Joe", None, "Bloggs")))
            .flatMap(_.set(AuthorisedOfficialsNamePage(1), Name(SelectTitle.Mrs, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe true
      }

      "one official with a supported title is pulled" in {
        helper.validateDataFromOldService(
          emptyUserAnswers
            .set(IsAddAnotherAuthorisedOfficialPage, false)
            .flatMap(_.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Joe", None, "Bloggs")))
            .success
            .value
        ) mustBe true
      }
    }
  }

  "authorisedOfficialCompleted" must {

    val userAnswers = common(0, emptyUserAnswers)
      .set(IsAuthorisedOfficialNinoPage(0), true)
      .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123123A"))
      .flatMap(_.set(IsAuthorisedOfficialPreviousAddressPage(0), false))
      .success
      .value

    "return false if Authorised official data is empty" in {

      helper.authorisedOfficialCompleted(0, emptyUserAnswers) mustBe false
    }

    "return false if Authorised official required data is not defined" in {

      helper.authorisedOfficialCompleted(0, common(0, emptyUserAnswers)) mustBe false
    }

    "return false if Authorised official required data is defined with additional fields" in {

      helper.authorisedOfficialCompleted(
        0,
        common(
          0,
          userAnswers
            .set(AuthorisedOfficialsPassportPage(0), Passport("GB12345", "GB", LocalDate.of(year, month, dayOfMonth)))
            .flatMap(_.set(AuthorisedOfficialsNinoPage(0), "AA123123A"))
            .success
            .value
        )
      ) mustBe false
    }

    "return true if Authorised official required data is defined with additional fields" in {

      helper.authorisedOfficialCompleted(0, userAnswers) mustBe true
    }
  }
}
