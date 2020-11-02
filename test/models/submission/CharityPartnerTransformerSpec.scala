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

import java.time.LocalDate

import base.SpecBase
import models.addressLookup.{AddressModel, CountryModel}
import models.authOfficials.OfficialsPosition
import models.nominees.OrganisationNomineeContactDetails
import models.{BankDetails, Name, Passport, PhoneNumber, SelectTitle, UserAnswers}
import pages.addressLookup._
import pages.authorisedOfficials._
import pages.nominees._
import pages.otherOfficials._
import play.api.libs.json.{Json, __}

class CharityPartnerTransformerSpec extends SpecBase {

  val jsonTransformer: CharityPartnerTransformer = new CharityPartnerTransformer
  private val day: Int = 11
  private val month: Int = 12
  private val year: Int = 2000

  private val date: String = LocalDate.now().toString

  "CharityPartnerTransformer" when {

    "userAnswersToIndividualDetails for Organisation Nominee" must {

      "convert the correct individualDetails object" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false))
          .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "abc@email.com")))
          .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, "Authorised", None, "Person")))
          .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, "AA123123A"))
          .success.value

        val expectedJson =
          """{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Authorised",
            |                    "lastName": "Person"
            |                },
            |                "position": "01",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "0123123123",
            |                "nino": "AA123123A"
            |        }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("organisationAuthorisedPerson")).asOpt.value mustBe Json.parse(expectedJson)

      }
    }

    "userAnswersToIndividualDetails for Individual Nominee" must {

      "convert the correct individualDetails object" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, true))
          .flatMap(_.set(IndividualNomineeNamePage, Name(SelectTitle.Mr, "Individual", None, "Nominee")))
          .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(IndividualNomineesPhoneNumberPage, PhoneNumber("0123123121", Some("0123123122"))))
          .flatMap(_.set(IndividualNomineesNinoPage, "AA123123A"))
          .success.value

        val expectedJson =
          """{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Individual",
            |                    "lastName": "Nominee"
            |                },
            |                "position": "01",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "0123123121",
            |                "mobilePhone": "0123123122",
            |                "nino": "AA123123A"
            |        }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'individual).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("individual")).asOpt.value mustBe Json.parse(expectedJson)

      }
    }

    "userAnswersToIndividualDetails for Authorised Official" must {

      "convert the correct individualDetails object with all fields" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")).flatMap(
          _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
          _.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C")).success.value

        val expectedJson =
          """{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Albert",
            |                    "middleName": "G",
            |                    "lastName": "Einstien"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "07700 900 982",
            |                "mobilePhone": "07700 900 981",
            |                "nino": "QQ123456C"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct individualDetails object with passport" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")).flatMap(
          _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981"))))
          .flatMap(_.set(AuthorisedOfficialsPassportPage(0), Passport("passportNumber", "gb", LocalDate.now.plusDays(1))))
          .success.value

        val expectedJson =
          s"""{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Albert",
            |                    "middleName": "G",
            |                    "lastName": "Einstien"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "07700 900 982",
            |                "mobilePhone": "07700 900 981",
            |                "nationalIdentityNumber": "passportNumber",
            |                "nationalIDCardIssuingCountry": "gb",
            |                "nationalIDCardExpiryDate": "${LocalDate.now.plusDays(1)}"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct individualDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", None, "Einstien")).flatMap(
          _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
          _.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C")).success.value

        val expectedJson =
          """{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Albert",
            |                    "lastName": "Einstien"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "07700 900 982",
            |                "mobilePhone": "07700 900 981",
            |                "nino": "QQ123456C"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerAddressDetails for Authorised Official" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with all fields including previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))
          .flatMap(_.set(AuthorisedOfficialPreviousAddressLookupPage(0),
            AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))))
          .success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               },
            |              "previousAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0),
          AddressModel(Seq("121", "Saint Mount Emilion", "Bercy Village"), None, CountryModel("FRA", "France"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "121",
            |                    "addressLine2": "Saint Mount Emilion",
            |                    "addressLine3": "Bercy Village",
            |                    "nonUKCountry": "France"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers.set(AuthorisedOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'authorisedOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerAddressDetails for Organisation Nominee" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(OrganisationNomineeAddressLookupPage,
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with all fields including previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(OrganisationNomineeAddressLookupPage,
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))
          .flatMap(_.set(OrganisationNomineePreviousAddressLookupPage,
            AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))))
          .success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               },
            |              "previousAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers.set(OrganisationNomineeAddressLookupPage,
          AddressModel(Seq("121", "Saint Mount Emilion", "Bercy Village"), None, CountryModel("FRA", "France"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "121",
            |                    "addressLine2": "Saint Mount Emilion",
            |                    "addressLine3": "Bercy Village",
            |                    "nonUKCountry": "France"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers.set(OrganisationNomineeAddressLookupPage,
          AddressModel(Seq("2", "Dubai Main Road"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerAddressDetails for Individual Nominee" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(NomineeIndividualAddressLookupPage,
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'individual).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with all fields including previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(NomineeIndividualAddressLookupPage,
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))
          .flatMap(_.set(NomineeIndividualPreviousAddressLookupPage,
            AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))))
          .success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               },
            |              "previousAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'individual).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers.set(NomineeIndividualAddressLookupPage,
          AddressModel(Seq("121", "Saint Mount Emilion", "Bercy Village"), None, CountryModel("FRA", "France"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "121",
            |                    "addressLine2": "Saint Mount Emilion",
            |                    "addressLine3": "Bercy Village",
            |                    "nonUKCountry": "France"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'individual).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers.set(NomineeIndividualAddressLookupPage,
          AddressModel(Seq("2", "Dubai Main Road"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'individual).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(expectedJson)
      }
    }


    "userAnswersToPartnerResponsiblePerson for Authorized Official" must {

      "convert the correct responsiblePerson object" in {

        val expectedJson =
          """{
            |			   "responsiblePerson": {
            |              "action": "1",
            |              "relation": "2"
            |        }
            |  }""".stripMargin

        emptyUserAnswers.data.transform(jsonTransformer.userAnswersToResponsiblePerson("1", "2")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToAddPartner for Authorized Official" must {

      "convert the correct addPartner object" in {

        val expectedJson =
          s"""{
             |        "type": "1",
             |			   "addPartner": {
             |              "effectiveDateOfChange": "$date"
             |        }
             |  }""".stripMargin

        emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAddPartner("1")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToOrgDetails for Organisation Nominee" must {

      "convert the correct orgDetails object" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false))
          .flatMap(_.set(OrganisationNomineeNamePage, "organisation"))
          .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "abc@email.com")))
          .success.value

        play.api.Logger.error("userAnswers is " + localUserAnswers)

        val expectedJson =
          """{
             |			  "orgDetails": {
             |              "orgName": "organisation",
             |              "telephoneNumber": "0123123123",
             |              "emailAddress": "abc@email.com"
             |        }
             |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToOrgDetails).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToBankDetails for Organisation Nominee" must {

      "convert the correct bankDetails object with bank roll number" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(OrganisationNomineesBankDetailsPage, BankDetails("bankAcc", "112233", "12341234", Some("bankRoll123")))
          .success.value

        val expectedJson =
          """{
             |			  "bankDetails": {
             |              "accountName": "bankAcc",
             |              "sortCode": 112233,
             |              "accountNumber": 12341234,
             |              "rollNumber": "bankRoll123"
             |        }
             |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        play.api.Logger.error("result here is " + result.toString())
        result.transform(jsonTransformer.userAnswersToBankDetails("organisationBankDetails")).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct bankDetails object without bank roll number" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(OrganisationNomineesBankDetailsPage, BankDetails("bankAcc", "112233", "12341234", None))
          .success.value

        val expectedJson =
          """{
             |			  "bankDetails": {
             |              "accountName": "bankAcc",
             |              "sortCode": 112233,
             |              "accountNumber": 12341234
             |        }
             |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToBankDetails("organisationBankDetails")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPaymentDetails for Organisation Nominee" must {

      "convert the correct object if payments aren't authorised" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsOrganisationNomineePaymentsPage, false).success.value

        val expectedJson =
          """{
            |   "paymentDetails": {
            |     "authorisedPayments": false
            |   }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPaymentDetails("organisationBankDetails", "isOrganisationNomineePayments")).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct object if payments are authorised" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsOrganisationNomineePaymentsPage, true)
          .flatMap(_.set(OrganisationNomineesBankDetailsPage, BankDetails("bankAcc", "112233", "12341234", None)))
          .success.value

        val expectedJson =
          """{
            |   "paymentDetails": {
            |     "authorisedPayments": true,
            |     "bankDetails": {
            |              "accountName": "bankAcc",
            |              "sortCode": 112233,
            |              "accountNumber": 12341234
            |     }
            |   }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'nominee \ 'organisation).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPaymentDetails("organisationBankDetails", "isOrganisationNomineePayments")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswers for Authorized Official" must {

      "convert the correct object for one authorized official" in {

        val localUserAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")).flatMap(
          _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
          _.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C")).flatMap(
          _.set(AuthorisedOfficialAddressLookupPage(0),
            AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))).success.value

        val expectedJson =
          s"""{
             |  "charityRegistration": {
             |    "partner": [
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers
          .data
          .transform(jsonTransformer.userAnswersToPartner)
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToIndividualDetails for Other Official" must {

      "convert the correct individualDetails object with all fields" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")).flatMap(
          _.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(OtherOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
          _.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")).success.value

        val expectedJson =
          """{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Albert",
            |                    "middleName": "G",
            |                    "lastName": "Einstien"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "07700 900 982",
            |                "mobilePhone": "07700 900 981",
            |                "nino": "QQ123456C"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'otherOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct individualDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", None, "Einstien")).flatMap(
          _.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(OtherOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
          _.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")).success.value

        val expectedJson =
          """{
            |			   "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "Albert",
            |                    "lastName": "Einstien"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "2000-12-11",
            |                "dayPhoneNumber": "07700 900 982",
            |                "mobilePhone": "07700 900 981",
            |                "nino": "QQ123456C"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'otherOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerAddressDetails for Other Official" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers.set(OtherOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "addressLine3": "line3",
            |                    "addressLine4": "line4",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ 'otherOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers.set(OtherOfficialAddressLookupPage(0),
          AddressModel(Seq("121", "Saint Mount Emilion", "Bercy Village"), None, CountryModel("FRA", "France"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "121",
            |                    "addressLine2": "Saint Mount Emilion",
            |                    "addressLine3": "Bercy Village",
            |                    "nonUKCountry": "France"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'otherOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers.set(OtherOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road"), Some("G27JD"), CountryModel("GB", "United Kingdom"))).success.value

        val expectedJson =
          """{
            |			   "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "2",
            |                    "addressLine2": "Dubai Main Road",
            |                    "postcode": "G27JD",
            |                    "nonUKCountry": "United Kingdom"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ 'otherOfficials \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }
    }


    "userAnswersToPartnerResponsiblePerson for Other Official" must {

      "convert the correct responsiblePerson object" in {

        val expectedJson =
          """{
            |			   "responsiblePerson": {
            |              "action": "1",
            |              "relation": "1"
            |        }
            |  }""".stripMargin

        emptyUserAnswers.data.transform(jsonTransformer.userAnswersToResponsiblePerson("1", "1")).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswers for Other Official" must {

      "convert the correct object for one other official" in {

        val localUserAnswers = emptyUserAnswers.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")).flatMap(
          _.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
          _.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
          _.set(OtherOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
          _.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")).flatMap(
          _.set(OtherOfficialAddressLookupPage(0),
            AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))).success.value

        val expectedJson =
          s"""{
             |  "charityRegistration": {
             |    "partner": [
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPartner).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswers for Official" must {

      val userAnswersTwoAuthOneOther: UserAnswers = emptyUserAnswers.set(AuthorisedOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien")).flatMap(
        _.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
        _.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
        _.set(AuthorisedOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
        _.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C")).flatMap(
        _.set(AuthorisedOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))).flatMap(
        _.set(AuthorisedOfficialsNamePage(1), Name(SelectTitle.Mr, "David", None, "Beckham"))).flatMap(
        _.set(AuthorisedOfficialsPositionPage(1), OfficialsPosition.Director)).flatMap(
        _.set(AuthorisedOfficialsDOBPage(1), LocalDate.of(year, month, day))).flatMap(
        _.set(AuthorisedOfficialsPhoneNumberPage(1), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
        _.set(AuthorisedOfficialsNinoPage(1), "QQ 12 34 56 A")).flatMap(
        _.set(AuthorisedOfficialAddressLookupPage(1),
          AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy")))).flatMap(
        _.set(OtherOfficialsNamePage(0), Name(SelectTitle.Mr, "Albert", Some("G"), "Einstien"))).flatMap(
        _.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar)).flatMap(
        _.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day))).flatMap(
        _.set(OtherOfficialsPhoneNumberPage(0), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
        _.set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")).flatMap(
        _.set(OtherOfficialAddressLookupPage(0),
          AddressModel(Seq("2", "Dubai Main Road", "line3", "line4"), Some("G27JD"), CountryModel("GB", "United Kingdom")))).flatMap(
        _.set(OtherOfficialsNamePage(1), Name(SelectTitle.Mr, "David", None, "Beckham"))).flatMap(
        _.set(OtherOfficialsPositionPage(1), OfficialsPosition.Director)).flatMap(
        _.set(OtherOfficialsDOBPage(1), LocalDate.of(year, month, day))).flatMap(
        _.set(OtherOfficialsPhoneNumberPage(1), PhoneNumber("07700 900 982", Some("07700 900 981")))).flatMap(
        _.set(OtherOfficialsNinoPage(1), "QQ 12 34 56 A")).flatMap(
        _.set(OtherOfficialAddressLookupPage(1),
          AddressModel(Seq("3", "Morrison Street", "Bill Tower"), None, CountryModel("IT", "Italy")))).success.value

      "convert the correct object for two authorized and other officials" in {

        val expectedJson =
          s"""{
             |  "charityRegistration": {
             |    "partner": [
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "David",
             |            "lastName": "Beckham"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "3",
             |            "addressLine2": "Morrison Street",
             |            "addressLine3": "Bill Tower",
             |            "nonUKCountry": "Italy"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "David",
             |            "lastName": "Beckham"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "3",
             |            "addressLine2": "Morrison Street",
             |            "addressLine3": "Bill Tower",
             |            "nonUKCountry": "Italy"
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        userAnswersTwoAuthOneOther.data.transform(jsonTransformer.userAnswersToPartner).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct object for two authorized and other officials with an organisation nominee" in {

        val localUserAnswers = userAnswersTwoAuthOneOther
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false))
          .flatMap(_.set(OrganisationNomineeNamePage, "organisationName"))
          .flatMap(_.set(OrganisationNomineeContactDetailsPage, OrganisationNomineeContactDetails("0123123123", "abc@email.com")))
          .flatMap(_.set(OrganisationNomineeAddressLookupPage,
            AddressModel(Seq("1", "Authorised Street", "Authorised Place"), None, CountryModel("IT", "Italy"))))
          .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, false))
          .flatMap(_.set(IsOrganisationNomineePaymentsPage, true))
          .flatMap(_.set(OrganisationNomineesBankDetailsPage, BankDetails("bankAcc", "112233", "12341234", None)))
          .flatMap(_.set(OrganisationAuthorisedPersonNamePage, Name(SelectTitle.Mr, "Authorised", None, "Person")))
          .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, "AA123123A"))
          .success.value

        val expectedJson =
          s"""{
             |  "charityRegistration": {
             |    "partner": [
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "David",
             |            "lastName": "Beckham"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "3",
             |            "addressLine2": "Morrison Street",
             |            "addressLine3": "Bill Tower",
             |            "nonUKCountry": "Italy"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "David",
             |            "lastName": "Beckham"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "3",
             |            "addressLine2": "Morrison Street",
             |            "addressLine3": "Bill Tower",
             |            "nonUKCountry": "Italy"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "3"
             |        },
             |        "type": "2",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "orgDetails": {
             |          "orgName": "organisationName",
             |          "telephoneNumber": "0123123123",
             |          "emailAddress": "abc@email.com"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Authorised",
             |            "lastName": "Person"
             |          },
             |          "position": "01",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "0123123123",
             |          "nino": "AA123123A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "addressLine1": "1",
             |            "addressLine2": "Authorised Street",
             |            "addressLine3": "Authorised Place",
             |            "nonUKAddress": true,
             |            "nonUKCountry": "Italy"
             |          }
             |        },
             |        "paymentDetails": {
             |          "authorisedPayments": true,
             |          "bankDetails": {
             |            "accountName": "bankAcc",
             |            "sortCode": 112233,
             |            "accountNumber": 12341234
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers
          .data
          .transform(jsonTransformer.userAnswersToPartner)
          .asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct object for two authorized and other officials with an individual nominee" in {

        val localUserAnswers = userAnswersTwoAuthOneOther
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, true))
          .flatMap(_.set(IndividualNomineeNamePage, Name(SelectTitle.Mr, "Individual", None, "Nominee")))
          .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(IndividualNomineesPhoneNumberPage, PhoneNumber("0123123121", Some("0123123122"))))
          .flatMap(_.set(IndividualNomineesNinoPage, "AA123123A"))
          .flatMap(_.set(NomineeIndividualAddressLookupPage,
            AddressModel(Seq("1", "Nominee Street"), Some("AA11AA"), CountryModel("GB", "United Kingdom"))))
          .flatMap(_.set(IsIndividualNomineePreviousAddressPage, true))
          .flatMap(_.set(NomineeIndividualPreviousAddressLookupPage,
            AddressModel(Seq("1", "Individual Drive"), None, CountryModel("IT", "Italy"))))
          .flatMap(_.set(IsIndividualNomineePaymentsPage, true))
          .flatMap(_.set(IndividualNomineesBankDetailsPage, BankDetails("bankAcc", "112233", "12341234", None)))
          .success.value

        val expectedJson =
          s"""{
             |  "charityRegistration": {
             |    "partner": [
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "2"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "David",
             |            "lastName": "Beckham"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "3",
             |            "addressLine2": "Morrison Street",
             |            "addressLine3": "Bill Tower",
             |            "nonUKCountry": "Italy"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Albert",
             |            "middleName": "G",
             |            "lastName": "Einstien"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456C"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "2",
             |            "addressLine2": "Dubai Main Road",
             |            "addressLine3": "line3",
             |            "addressLine4": "line4",
             |            "postcode": "G27JD",
             |            "nonUKCountry": "United Kingdom"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "1"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "David",
             |            "lastName": "Beckham"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "07700 900 982",
             |          "mobilePhone": "07700 900 981",
             |          "nino": "QQ123456A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "3",
             |            "addressLine2": "Morrison Street",
             |            "addressLine3": "Bill Tower",
             |            "nonUKCountry": "Italy"
             |          }
             |        }
             |      },
             |      {
             |        "responsiblePerson": {
             |          "action": "1",
             |          "relation": "3"
             |        },
             |        "type": "1",
             |        "addPartner": {
             |          "effectiveDateOfChange": "$date"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "Individual",
             |            "lastName": "Nominee"
             |          },
             |          "position": "01",
             |          "dateOfBirth": "2000-12-11",
             |          "dayPhoneNumber": "0123123121",
             |          "mobilePhone": "0123123122",
             |          "nino": "AA123123A"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "addressLine1": "1",
             |            "addressLine2": "Nominee Street",
             |            "nonUKAddress": false,
             |            "postcode": "AA11AA",
             |            "nonUKCountry": "United Kingdom"
             |          },
             |          "previousAddress": {
             |            "addressLine1": "1",
             |            "addressLine2": "Individual Drive",
             |            "nonUKAddress": true,
             |            "nonUKCountry": "Italy"
             |          }
             |        },
             |        "paymentDetails": {
             |          "authorisedPayments": true,
             |          "bankDetails": {
             |            "accountName": "bankAcc",
             |            "sortCode": 112233,
             |            "accountNumber": 12341234
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers
          .data
          .transform(jsonTransformer.userAnswersToPartner)
          .asOpt.value mustBe Json.parse(expectedJson)
      }
    }
  }

}
