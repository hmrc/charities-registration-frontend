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

package transformers.submission

import base.SpecBase
import models.addressLookup.AddressModel
import models.authOfficials.OfficialsPosition
import models.nominees.OrganisationNomineeContactDetails
import models.{BankDetails, Name, Passport, PhoneNumber, UserAnswers}
import pages.addressLookup.*
import pages.authorisedOfficials.*
import pages.nominees.*
import pages.otherOfficials.*
import play.api.libs.json.*

import java.time.LocalDate

class CharityPartnerTransformerSpec extends SpecBase {

  val jsonTransformer: CharityPartnerTransformer = new CharityPartnerTransformer
  private val day: Int                           = 11
  private val month: Int                         = 12
  private val year: Int                          = 2000

  private val date: String = LocalDate.now().toString

  "CharityPartnerTransformer" when {

    "userAnswersToIndividualDetails for Organisation Nominee" must {

      "convert the correct individualDetails object" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false))
          .flatMap(
            _.set(
              OrganisationNomineeContactDetailsPage,
              nomineeOrganisationContactDetails
            )
          )
          .flatMap(_.set(OrganisationAuthorisedPersonNamePage, personNameWithoutMiddle))
          .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, nino))
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithoutMiddle.firstName}",
            |                    "lastName": "${personNameWithoutMiddle.lastName}"
            |                },
            |                "position": "01",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "nino": "$nino"
            |        }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result
          .transform(jsonTransformer.userAnswersToIndividualDetails("organisationAuthorisedPerson"))
          .asOpt
          .value mustBe Json.parse(expectedJson)

      }
    }

    "userAnswersToIndividualDetails for Individual Nominee" must {

      "convert the correct individualDetails object" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, true))
          .flatMap(_.set(IndividualNomineeNamePage, personNameWithoutMiddle))
          .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(IndividualNomineesPhoneNumberPage, phoneNumbers))
          .flatMap(_.set(IndividualNomineesNinoPage, nino))
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithoutMiddle.firstName}",
            |                    "lastName": "${personNameWithoutMiddle.lastName}"
            |                },
            |                "position": "01",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "mobilePhone": "$mobileNumber",
            |                "nino": "$nino"
            |        }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "individual").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("individual")).asOpt.value mustBe Json.parse(
          expectedJson
        )

      }
    }

    "userAnswersToIndividualDetails for Authorised Official" must {

      "convert the correct individualDetails object with all fields" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), personNameWithMiddle)
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(_.set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces))
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithMiddle.firstName}",
            |                    "middleName": "${personNameWithMiddle.middleName.get}",
            |                    "lastName": "${personNameWithMiddle.lastName}"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "mobilePhone": "$mobileNumber",
            |                "nino": "$nino"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct individualDetails object with passport" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), personNameWithMiddle)
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(
            _.set(
              AuthorisedOfficialsPassportPage(0),
              passport.copy(expiryDate = passport.expiryDate.plusYears(1))
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithMiddle.firstName}",
            |                    "middleName": "${personNameWithMiddle.middleName.get}",
            |                    "lastName": "${personNameWithMiddle.lastName}"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "mobilePhone": "$mobileNumber",
            |                "nationalIdentityNumber": "$passportNumber",
            |                "nationalIDCardIssuingCountry": "${passport.country}",
            |                "nationalIDCardExpiryDate": "${passport.expiryDate.plusYears(1)}"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct individualDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), personNameWithoutMiddle)
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(_.set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces))
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithoutMiddle.firstName}",
            |                    "lastName": "${personNameWithoutMiddle.lastName}"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "mobilePhone": "$mobileNumber",
            |                "nino": "$nino"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe
          Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerAddressDetails for Authorised Official" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            AuthorisedOfficialAddressLookupPage(0),
            addressAllLines
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with all fields including previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            AuthorisedOfficialAddressLookupPage(0),
            addressAllLines
          )
          .flatMap(
            _.set(
              AuthorisedOfficialPreviousAddressLookupPage(0),
              addressAllLines
            )
          )
          .success
          .value

        val expectedJson =
         s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               },
            |              "previousAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            AuthorisedOfficialAddressLookupPage(0),
            addressWithTown.copy(postcode = None, country = frCountryModel)
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "${town.get}",
            |                    "nonUKCountry": "$frCountryCode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            AuthorisedOfficialAddressLookupPage(0),
            address
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "authorisedOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerAddressDetails for Organisation Nominee" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OrganisationNomineeAddressLookupPage,
            addressAllLines
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct addressDetails object with all fields including previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OrganisationNomineeAddressLookupPage,
            addressAllLines
          )
          .flatMap(
            _.set(
              OrganisationNomineePreviousAddressLookupPage,
              addressAllLines
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               },
            |              "previousAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OrganisationNomineeAddressLookupPage,
            addressWithTown.copy(postcode = None, country = frCountryModel)
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "${town.get}",
            |                    "nonUKCountry": "$frCountryCode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OrganisationNomineeAddressLookupPage,
            address
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsOrganisation).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToPartnerAddressDetails for Individual Nominee" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            NomineeIndividualAddressLookupPage,
            addressAllLines
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "nominee" \ "individual").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct addressDetails object with all fields including previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            NomineeIndividualAddressLookupPage,
            addressAllLines
          )
          .flatMap(
            _.set(
              NomineeIndividualPreviousAddressLookupPage,
              addressAllLines
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               },
            |              "previousAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "nominee" \ "individual").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            NomineeIndividualAddressLookupPage,
            addressWithTown.copy(postcode = None, country = frCountryModel)
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "${town.get}",
            |                    "nonUKCountry": "$frCountryCode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "individual").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            NomineeIndividualAddressLookupPage,
            AddressModel(Seq(line1, line2), Some(ukPostcode), gbCountryModel)
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "individual").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetailsIndividual).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToPartnerResponsiblePerson for Authorized Official" must {

      "convert the correct responsiblePerson object" in {

        val expectedJson =
          """{
            |         "responsiblePerson": {
            |              "action": "1",
            |              "relation": "2"
            |        }
            |  }""".stripMargin

        emptyUserAnswers.data
          .transform(jsonTransformer.userAnswersToResponsiblePerson("1", "2"))
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToAddPartner for Authorized Official" must {

      "convert the correct addPartner object" in {

        val expectedJson =
          s"""{
             |        "type": "1",
             |         "addPartner": {
             |              "effectiveDateOfChange": "$date"
             |        }
             |  }""".stripMargin

        emptyUserAnswers.data.transform(jsonTransformer.userAnswersToAddPartner("1")).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToOrgDetails for Organisation Nominee" must {

      "convert the correct orgDetails object" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false))
          .flatMap(_.set(OrganisationNomineeNamePage, nomineeOrganisationName))
          .flatMap(
            _.set(
              OrganisationNomineeContactDetailsPage,
              nomineeOrganisationContactDetails
            )
          )
          .success
          .value

        val expectedJson =
          s"""{
             |        "orgDetails": {
             |              "orgName": "$nomineeOrganisationName",
             |              "telephoneNumber": "$daytimePhone",
             |              "emailAddress": "$organisationEmail"
             |        }
             |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToOrgDetails).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToBankDetails for Organisation Nominee" must {

      "convert the correct bankDetails object with bank roll number" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(OrganisationNomineesBankDetailsPage, bankDetails)
          .success
          .value

        val expectedJson =
          s"""{
             |        "bankDetails": {
             |              "accountName": "$accountName",
             |              "sortCode": ${sortCode.toInt},
             |              "accountNumber": ${accountNumber.toInt},
             |              "rollNumber": "$rollNumber"
             |        }
             |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToBankDetails("organisationBankDetails")).asOpt.value mustBe Json
          .parse(expectedJson)
      }

      "convert the correct bankDetails object without bank roll number" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(OrganisationNomineesBankDetailsPage, bankDetailsWithoutRollNumber)
          .success
          .value

        val expectedJson =
          s"""{
             |        "bankDetails": {
             |              "accountName": "$accountName",
             |              "sortCode": ${sortCode.toInt},
             |              "accountNumber": ${accountNumber.toInt}
             |        }
             |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToBankDetails("organisationBankDetails")).asOpt.value mustBe Json
          .parse(expectedJson)
      }
    }

    "userAnswersToPaymentDetails for Organisation Nominee" must {

      "convert the correct object if payments aren't authorised" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsOrganisationNomineePaymentsPage, false)
          .success
          .value

        val expectedJson =
          """{
            |   "paymentDetails": {
            |     "authorisedPayments": false
            |   }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result
          .transform(
            jsonTransformer.userAnswersToPaymentDetails("organisationBankDetails", "isOrganisationNomineePayments")
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }

      "convert the correct object if payments are authorised" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(IsOrganisationNomineePaymentsPage, true)
          .flatMap(_.set(OrganisationNomineesBankDetailsPage, bankDetailsWithoutRollNumber))
          .success
          .value

        val expectedJson =
          s"""{
            |   "paymentDetails": {
            |     "authorisedPayments": true,
            |     "bankDetails": {
            |              "accountName": "$accountName",
            |              "sortCode": ${sortCode.toInt},
            |              "accountNumber": ${accountNumber.toInt}
            |     }
            |   }
            |}""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "nominee" \ "organisation").json.pick).asOpt.get
        result
          .transform(
            jsonTransformer.userAnswersToPaymentDetails("organisationBankDetails", "isOrganisationNomineePayments")
          )
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswers for Authorized Official" must {

      "convert the correct object for one authorized official" in {

        val localUserAnswers = emptyUserAnswers
          .set(AuthorisedOfficialsNamePage(0), personNameWithMiddle)
          .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(_.set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces))
          .flatMap(
            _.set(
              AuthorisedOfficialAddressLookupPage(0),
              addressAllLines
            )
          )
          .success
          .value

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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers.data
          .transform(jsonTransformer.userAnswersToPartner)
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToIndividualDetails for Other Official" must {

      "convert the correct individualDetails object with all fields" in {

        val localUserAnswers: UserAnswers = emptyUserAnswers
          .set(OtherOfficialsNamePage(0), personNameWithMiddle)
          .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(_.set(OtherOfficialsNinoPage(0), ninoWithSpaces))
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithMiddle.firstName}",
            |                    "middleName": "${personNameWithMiddle.middleName.get}",
            |                    "lastName": "${personNameWithMiddle.lastName}"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "mobilePhone": "$mobileNumber",
            |                "nino": "$nino"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "otherOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct individualDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers
          .set(OtherOfficialsNamePage(0), personNameWithoutMiddle)
          .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(_.set(OtherOfficialsNinoPage(0), ninoWithSpaces))
          .success
          .value

        val expectedJson =
          s"""{
            |         "individualDetails": {
            |                "name": {
            |                    "title": "0001",
            |                    "firstName": "${personNameWithoutMiddle.firstName}",
            |                    "lastName": "${personNameWithoutMiddle.lastName}"
            |                },
            |                "position": "02",
            |                "dateOfBirth": "$officialsDOB",
            |                "dayPhoneNumber": "$daytimePhone",
            |                "mobilePhone": "$mobileNumber",
            |                "nino": "$nino"
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "otherOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToIndividualDetails("officials")).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswersToPartnerAddressDetails for Other Official" must {

      "convert the correct addressDetails object with all fields except previousAddress" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OtherOfficialAddressLookupPage(0),
            addressAllLines
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "$line3",
            |                    "addressLine4": "${town.get}",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin
        val result = localUserAnswers.data.transform((__ \ "otherOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object and nonUKAddress is true" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OtherOfficialAddressLookupPage(0),
            addressWithTown.copy(postcode = None, country = frCountryModel)
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": true,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "addressLine3": "${town.get}",
            |                    "nonUKCountry": "$frCountryCode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "otherOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }

      "convert the correct addressDetails object with mandatory fields only" in {

        val localUserAnswers = emptyUserAnswers
          .set(
            OtherOfficialAddressLookupPage(0),
            address
          )
          .success
          .value

        val expectedJson =
          s"""{
            |         "addressDetails": {
            |              "currentAddress": {
            |                    "nonUKAddress": false,
            |                    "addressLine1": "$line1",
            |                    "addressLine2": "$line2",
            |                    "postcode": "$ukPostcode"
            |               }
            |        }
            |  }""".stripMargin

        val result = localUserAnswers.data.transform((__ \ "otherOfficials" \ 0).json.pick).asOpt.get
        result.transform(jsonTransformer.userAnswersToPartnerAddressDetails).asOpt.value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswersToPartnerResponsiblePerson for Other Official" must {

      "convert the correct responsiblePerson object" in {

        val expectedJson =
          """{
            |         "responsiblePerson": {
            |              "action": "1",
            |              "relation": "1"
            |        }
            |  }""".stripMargin

        emptyUserAnswers.data
          .transform(jsonTransformer.userAnswersToResponsiblePerson("1", "1"))
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }
    }

    "userAnswers for Other Official" must {

      "convert the correct object for one other official" in {

        val localUserAnswers = emptyUserAnswers
          .set(OtherOfficialsNamePage(0), personNameWithMiddle)
          .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
          .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day)))
          .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), phoneNumbers))
          .flatMap(_.set(OtherOfficialsNinoPage(0), ninoWithSpaces))
          .flatMap(
            _.set(
              OtherOfficialAddressLookupPage(0),
              addressAllLines
            )
          )
          .success
          .value

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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers.data.transform(jsonTransformer.userAnswersToPartner).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }
    }

    "userAnswers for Official" must {

      val userAnswersTwoAuthOneOther: UserAnswers = emptyUserAnswers
        .set(AuthorisedOfficialsNamePage(0), personNameWithMiddle)
        .flatMap(_.set(AuthorisedOfficialsPositionPage(0), OfficialsPosition.Bursar))
        .flatMap(_.set(AuthorisedOfficialsDOBPage(0), LocalDate.of(year, month, day)))
        .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(0), phoneNumbers))
        .flatMap(_.set(AuthorisedOfficialsNinoPage(0), ninoWithSpaces))
        .flatMap(
          _.set(
            AuthorisedOfficialAddressLookupPage(0),
            AddressModel(
              Seq(line1, line2, line3, town.get),
              Some(ukPostcode),
              gbCountryModel
            )
          )
        )
        .flatMap(_.set(AuthorisedOfficialsNamePage(1), personNameWithoutMiddle))
        .flatMap(_.set(AuthorisedOfficialsPositionPage(1), OfficialsPosition.Director))
        .flatMap(_.set(AuthorisedOfficialsDOBPage(1), LocalDate.of(year, month, day)))
        .flatMap(_.set(AuthorisedOfficialsPhoneNumberPage(1), phoneNumbers))
        .flatMap(_.set(AuthorisedOfficialsNinoPage(1), nino2WithSpaces))
        .flatMap(
          _.set(
            AuthorisedOfficialAddressLookupPage(1),
            AddressModel(Seq(line1, line2, line3), None, inCountryModel)
          )
        )
        .flatMap(_.set(OtherOfficialsNamePage(0), personNameWithMiddle))
        .flatMap(_.set(OtherOfficialsPositionPage(0), OfficialsPosition.Bursar))
        .flatMap(_.set(OtherOfficialsDOBPage(0), LocalDate.of(year, month, day)))
        .flatMap(_.set(OtherOfficialsPhoneNumberPage(0), phoneNumbers))
        .flatMap(_.set(OtherOfficialsNinoPage(0), ninoWithSpaces))
        .flatMap(
          _.set(
            OtherOfficialAddressLookupPage(0),
            AddressModel(
              Seq(line1, line2, line3, town.get),
              Some(ukPostcode),
              gbCountryModel
            )
          )
        )
        .flatMap(_.set(OtherOfficialsNamePage(1), personNameWithoutMiddle))
        .flatMap(_.set(OtherOfficialsPositionPage(1), OfficialsPosition.Director))
        .flatMap(_.set(OtherOfficialsDOBPage(1), LocalDate.of(year, month, day)))
        .flatMap(_.set(OtherOfficialsPhoneNumberPage(1), phoneNumbers))
        .flatMap(_.set(OtherOfficialsNinoPage(1), nino2WithSpaces))
        .flatMap(
          _.set(
            OtherOfficialAddressLookupPage(1),
            AddressModel(Seq(line1, line2, line3), None, inCountryModel)
          )
        )
        .success
        .value

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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino2"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKCountry": "$inCountryCode"
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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino2"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKCountry": "$inCountryCode"
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        userAnswersTwoAuthOneOther.data.transform(jsonTransformer.userAnswersToPartner).asOpt.value mustBe Json.parse(
          expectedJson
        )
      }

      "convert the correct object for two authorized and other officials with an organisation nominee" in {

        val localUserAnswers = userAnswersTwoAuthOneOther
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, false))
          .flatMap(_.set(OrganisationNomineeNamePage, charityOperatingName))
          .flatMap(
            _.set(
              OrganisationNomineeContactDetailsPage,
              nomineeOrganisationContactDetails
            )
          )
          .flatMap(
            _.set(
              OrganisationNomineeAddressLookupPage,
              AddressModel(Seq(line1, line2, line3), None, inCountryModel)
            )
          )
          .flatMap(_.set(IsOrganisationNomineePreviousAddressPage, false))
          .flatMap(_.set(IsOrganisationNomineePaymentsPage, true))
          .flatMap(_.set(OrganisationNomineesBankDetailsPage, bankDetailsWithoutRollNumber))
          .flatMap(_.set(OrganisationAuthorisedPersonNamePage, personNameWithoutMiddle))
          .flatMap(_.set(OrganisationAuthorisedPersonDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(OrganisationAuthorisedPersonNinoPage, nino3))
          .success
          .value

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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino2"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKCountry": "$inCountryCode"
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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino2"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKCountry":"$inCountryCode"
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
             |          "orgName": "$charityOperatingName",
             |          "telephoneNumber": "$daytimePhone",
             |          "emailAddress": "$organisationEmail"
             |        },
             |        "individualDetails": {
             |          "name": {
             |            "title": "0001",
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "01",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "nino": "$nino3"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKAddress": true,
             |            "nonUKCountry":"$inCountryCode"
             |          }
             |        },
             |        "paymentDetails": {
             |          "authorisedPayments": true,
             |          "bankDetails": {
             |            "accountName": "$accountName",
             |            "sortCode": ${sortCode.toInt},
             |            "accountNumber": ${accountNumber.toInt}
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers.data
          .transform(jsonTransformer.userAnswersToPartner)
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }

      "convert the correct object for two authorized and other officials with an individual nominee" in {

        val localUserAnswers = userAnswersTwoAuthOneOther
          .set(IsAuthoriseNomineePage, true)
          .flatMap(_.set(ChooseNomineePage, true))
          .flatMap(_.set(IndividualNomineeNamePage, personNameWithoutMiddle))
          .flatMap(_.set(IndividualNomineeDOBPage, LocalDate.of(year, month, day)))
          .flatMap(_.set(IndividualNomineesPhoneNumberPage, phoneNumbers))
          .flatMap(_.set(IndividualNomineesNinoPage, nino3))
          .flatMap(
            _.set(
              NomineeIndividualAddressLookupPage,
              AddressModel(Seq(line1, line2), Some(ukPostcode), gbCountryModel)
            )
          )
          .flatMap(_.set(IsIndividualNomineePreviousAddressPage, true))
          .flatMap(
            _.set(
              NomineeIndividualPreviousAddressLookupPage,
              AddressModel(Seq(line1, "Individual Drive"), None, inCountryModel)
            )
          )
          .flatMap(_.set(IsIndividualNomineePaymentsPage, true))
          .flatMap(_.set(IndividualNomineesBankDetailsPage, bankDetailsWithoutRollNumber))
          .success
          .value

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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino2"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKCountry":"$inCountryCode"
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
             |            "firstName": "${personNameWithMiddle.firstName}",
             |            "middleName": "${personNameWithMiddle.middleName.get}",
             |            "lastName": "${personNameWithMiddle.lastName}"
             |          },
             |          "position": "02",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": false,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "addressLine4": "${town.get}",
             |            "postcode": "$ukPostcode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "05",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino2"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "nonUKAddress": true,
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "addressLine3": "$line3",
             |            "nonUKCountry":"$inCountryCode"
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
             |            "firstName": "${personNameWithoutMiddle.firstName}",
             |            "lastName": "${personNameWithoutMiddle.lastName}"
             |          },
             |          "position": "01",
             |          "dateOfBirth": "$officialsDOB",
             |          "dayPhoneNumber": "$daytimePhone",
             |          "mobilePhone": "$mobileNumber",
             |          "nino": "$nino3"
             |        },
             |        "addressDetails": {
             |          "currentAddress": {
             |            "addressLine1": "$line1",
             |            "addressLine2": "$line2",
             |            "nonUKAddress": false,
             |            "postcode": "$ukPostcode"
             |          },
             |          "previousAddress": {
             |            "addressLine1": "$line1",
             |            "addressLine2": "Individual Drive",
             |            "nonUKAddress": true,
             |            "nonUKCountry": "$inCountryCode"
             |          }
             |        },
             |        "paymentDetails": {
             |          "authorisedPayments": true,
             |          "bankDetails": {
             |            "accountName": "$accountName",
             |            "sortCode": ${sortCode.toInt},
             |            "accountNumber": ${accountNumber.toInt}
             |          }
             |        }
             |      }
             |    ]
             |  }
             |}""".stripMargin

        localUserAnswers.data
          .transform(jsonTransformer.userAnswersToPartner)
          .asOpt
          .value mustBe Json.parse(expectedJson)
      }
    }
  }
}
