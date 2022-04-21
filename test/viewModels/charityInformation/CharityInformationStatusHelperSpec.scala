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

package viewModels.charityInformation

import base.SpecBase
import models.addressLookup.{AddressModel, CountryModel}
import models.{CharityContactDetails, CharityName}
import pages.addressLookup.{CharityOfficialAddressLookupPage, CharityPostalAddressLookupPage}
import pages.contactDetails.{CanWeSendToThisAddressPage, CharityContactDetailsPage, CharityNamePage}
import viewmodels.charityInformation.CharityInformationStatusHelper

class CharityInformationStatusHelperSpec extends SpecBase {

  "CharityInformationStatusHelper" must {

    "give the correct completion status" when {

      "no data is provided" in {
        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers)

        result mustBe false
      }

      "some but not all data is provided" in {
        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", Some("another name"))).success.value)

        result mustBe false
      }

      "unnecessary data is provided" in {

        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "abc@email.com")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, true))
          .flatMap(_.set(CharityPostalAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom")))).success.value
        )

        result mustBe false
      }

      "all data necessary is provided when postal address is the same as location" in {

        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "abc@email.com")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, true)).success.value
        )

        result mustBe true
      }

      "all data necessary is provided when postal address is the same as location and email is blank" in {

        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, true)).success.value
        )

        result mustBe false
      }

      "not all data necessary is provided when postal address is the same as location" in {

        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, true)).success.value
        )

        result mustBe false
      }

      "all data necessary is provided when postal address is different to location" in {

        val result = CharityInformationStatusHelper.checkComplete(emptyUserAnswers
          .set(CharityNamePage, CharityName("a charity", None))
          .flatMap(_.set(CharityContactDetailsPage, CharityContactDetails("0123123123", Some("07111111111"), "abc@email.com")))
          .flatMap(_.set(CharityOfficialAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(CanWeSendToThisAddressPage, false))
          .flatMap(_.set(CharityPostalAddressLookupPage, AddressModel(Seq("address"), Some("AA11AA"), CountryModel("GB", "United Kingdom")))).success.value
        )

        result mustBe true
      }

    }
  }
}
