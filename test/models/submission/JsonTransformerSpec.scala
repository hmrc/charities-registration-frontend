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

package models.submission

import base.SpecBase
import models.{Name, SelectTitle}
import models.addressLookup.{AddressModel, CountryModel}
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import play.api.libs.json.{Json, __}

class JsonTransformerSpec extends SpecBase {

  val jsonTransformer: JsonTransformer = new JsonTransformer{}

  "JsonTransformer" when {

    "Address" must {

      "convert the correct AddressModel" in {

        val userAnswers = emptyUserAnswers.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |   "common": {
            |    "addressDetails": {
            |      "officialAddress": {
            |        "postcode": "G58AN",
            |        "addressLine1": "7",
            |        "addressLine2": "Morrison street",
            |        "addressLine3": "line3",
            |        "addressLine4": "line4",
            |        "nonUKAddress": false,
            |        "nonUKCountry":"United Kingdom"
            |      }
            |     }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.getAddress(
          __ \ 'charityRegistration \ 'common \ 'addressDetails \ 'officialAddress,
          __ \ 'charityOfficialAddress)
        ).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressModel with mandatory fields only" in {

        val userAnswers = emptyUserAnswers.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street"), None, CountryModel("IN", "INDIA"))).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |   "common": {
            |    "addressDetails": {
            |      "officialAddress": {
            |        "addressLine1": "7",
            |        "addressLine2": "Morrison street",
            |        "nonUKAddress": true,
            |        "nonUKCountry":"INDIA"
            |      }
            |     }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.getAddress(
          __ \ 'charityRegistration \ 'common \ 'addressDetails \ 'officialAddress,
          __ \ 'charityOfficialAddress)
        ).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "getOptionalAddress" must {

      "convert the correct OptionalAddress" in {

        val userAnswers = emptyUserAnswers.set(CharityOfficialAddressLookupPage,
          AddressModel(Seq("7", "Morrison street", "line3", "line4"), Some("G58AN"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |   "common": {
            |    "addressDetails": {
            |      "charityCorrespondenceAddress": {
            |        "postcode": "G58AN",
            |        "addressLine1": "7",
            |        "addressLine2": "Morrison street",
            |        "addressLine3": "line3",
            |        "addressLine4": "line4",
            |        "nonUKAddress": false,
            |        "nonUKCountry":"United Kingdom"
            |      }
            |     }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.getOptionalAddress(
          __ \ 'charityRegistration \ 'common \ 'addressDetails \ 'charityCorrespondenceAddress,
          __ \ 'charityOfficialAddress)
        ).asOpt.value mustBe Json.parse(expectedJson)
      }

      "return none if no address is defined" in {

        emptyUserAnswers.data.transform(jsonTransformer.getOptionalAddress(
          __ \ 'charityRegistration \ 'common \ 'addressDetails \ 'charityCorrespondenceAddress,
          __ \ 'charityOfficialAddress)
        ).asOpt.value mustBe Json.parse("""{}""")
      }
    }

    "getName" must {

      "convert the correct Name object" in {

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0),
          Name(SelectTitle.Mr, "Jim", Some("John"), "Jones")).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "middleName": "John",
            |          "title": "0001"
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin

        userAnswers.data.transform(jsonTransformer.getName(
          __ \ 'charityRegistration \ 'common \ 'declarationInfo \ 'name,
          __ \ 'authorisedOfficials \ 0 \ 'officialsName)
        ).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct AddressModel with mandatory fields only" in {

        val userAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0),
          Name(SelectTitle.Mrs, "Jim", None, "Jones")).success.value

        val expectedJson =
          """{
            |  "charityRegistration": {
            |    "common": {
            |      "declarationInfo": {
            |        "name": {
            |          "firstName": "Jim",
            |          "lastName": "Jones",
            |          "title": "0002"
            |        }
            |      }
            |    }
            |  }
            |}""".stripMargin


        userAnswers.data.transform(jsonTransformer.getName(
          __ \ 'charityRegistration \ 'common \ 'declarationInfo \ 'name,
          __ \ 'authorisedOfficials \ 0 \ 'officialsName)
        ).asOpt.value mustBe Json.parse(expectedJson)

      }
    }
  }

}
