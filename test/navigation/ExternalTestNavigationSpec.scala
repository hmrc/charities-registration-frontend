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

package navigation

import java.time.LocalDate

import base.SpecBase
import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.contactDetails.{routes => charityInfoRoutes}
import controllers.nominees.{routes => nomineeRoutes}
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import controllers.routes
import models.nominees.OrganisationNomineeContactDetails
import models.{CharityContactDetails, CheckMode, Index, NormalMode, Passport}
import pages.authorisedOfficials.{AuthorisedOfficialsNinoPage, AuthorisedOfficialsPassportPage}
import pages.contactDetails.CharityContactDetailsPage
import pages.nominees.{IndividualNomineesNinoPage, IndividualNomineesPassportPage, OrganisationNomineeContactDetailsPage}
import pages.otherOfficials.{OtherOfficialsNinoPage, OtherOfficialsPassportPage}
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.mvc.Call

class ExternalTestNavigationSpec extends SpecBase {

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder().configure("features.isExternalTest" -> "true")

  def goToPlaybackPage(index: Int): Call                     = index match {
    case 0 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(0))
    case 1 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(1))
    case 2 => otherOfficialRoutes.AddedOtherOfficialController.onPageLoad(Index(2))
    case _ => routes.PageNotFoundController.onPageLoad()
  }

  def previousOrSameIndex(index: Int): Int = index match {
    case nonZero: Int if nonZero > 0 => nonZero - 1
    case _                           => 0
  }

  private val charityInformationNavigator: CharityInformationNavigator   = inject[CharityInformationNavigator]
  private val authorisedOfficialsNavigator: AuthorisedOfficialsNavigator = inject[AuthorisedOfficialsNavigator]
  private val otherOfficialsNavigator: OtherOfficialsNavigator           = inject[OtherOfficialsNavigator]
  private val nomineesNavigator: NomineesNavigator                       = inject[NomineesNavigator]

  "Navigator.nextPage(page, mode, userAnswers)" when {

    "in Normal mode" when {

      "from the CharityContactDetailsPage" must {

        "go to the Charity Details Summary page when clicked Confirm and continue button" in {

          charityInformationNavigator.nextPage(
            CharityContactDetailsPage,
            NormalMode,
            emptyUserAnswers
              .set(
                CharityContactDetailsPage,
                CharityContactDetails("07700 900 982", Some("07700 900 982"), "abc@gmail.com")
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the AuthorisedOfficialsNINOPage" must {
        "go to the You have added one authorised official page" in {
          authorisedOfficialsNavigator.nextPage(
            AuthorisedOfficialsNinoPage(0),
            NormalMode,
            emptyUserAnswers.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C").success.value
          ) mustBe
            authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
        }
      }

      "from the AuthorisedOfficialsPassportPage" must {
        "go to the AddressLookup page when clicked continue button" in {
          authorisedOfficialsNavigator.nextPage(
            AuthorisedOfficialsPassportPage(0),
            NormalMode,
            emptyUserAnswers
              .set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
              .success
              .value
          ) mustBe
            authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
        }
      }

      List(0, 1, 2).foreach { index =>
        s"from the OtherOfficialsPassportPage $index" must {
          "go to the What is [Full name]’s home address? when clicked continue button" in {
            otherOfficialsNavigator.nextPage(
              OtherOfficialsPassportPage(index),
              NormalMode,
              emptyUserAnswers
                .set(OtherOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
                .flatMap(
                  _.set(OtherOfficialsPassportPage(previousOrSameIndex(index)), Passport("1223", "gb", LocalDate.now()))
                )
                .flatMap(_.set(OtherOfficialsPassportPage(index), Passport("1223", "gb", LocalDate.now())))
                .success
                .value
            ) mustBe
              goToPlaybackPage(index)
          }
        }

        s"from the OtherOfficialsNinoPage $index"     must {
          "go to the You have added one/second/third other official page when no is selected" in {
            otherOfficialsNavigator.nextPage(
              OtherOfficialsNinoPage(index),
              NormalMode,
              emptyUserAnswers
                .set(OtherOfficialsNinoPage(0), "QQ 12 34 56 C")
                .flatMap(_.set(OtherOfficialsNinoPage(previousOrSameIndex(index)), "QQ 12 34 56 C"))
                .flatMap(_.set(OtherOfficialsNinoPage(index), "QQ 12 34 56 C"))
                .success
                .value
            ) mustBe
              goToPlaybackPage(index)
          }
        }
      }

      "from the IndividualNomineeNinoPage" must {

        "go to the IsIndividualNomineePayments page when clicked continue button" in {
          nomineesNavigator.nextPage(
            IndividualNomineesNinoPage,
            NormalMode,
            emptyUserAnswers.set(IndividualNomineesNinoPage, "QQ 12 34 56 C").success.value
          ) mustBe
            nomineeRoutes.IsIndividualNomineePaymentsController.onPageLoad(NormalMode)
        }
      }

      "from the IndividualNomineesPassportPage" must {

        "go to the IsIndividualNomineePayments page when clicked continue button" in {
          nomineesNavigator.nextPage(
            IndividualNomineesPassportPage,
            NormalMode,
            emptyUserAnswers.set(IndividualNomineesPassportPage, Passport("123", "gb", LocalDate.now())).success.value
          ) mustBe
            nomineeRoutes.IsIndividualNomineePaymentsController.onPageLoad(NormalMode)
        }
      }

      "from the OrganisationNomineeContactDetails page" must {
        "go to IsOrganisationNomineePayments page when clicked continue button" in {
          nomineesNavigator.nextPage(
            OrganisationNomineeContactDetailsPage,
            NormalMode,
            emptyUserAnswers
              .set(
                OrganisationNomineeContactDetailsPage,
                OrganisationNomineeContactDetails("0123123123", "test@email.com")
              )
              .success
              .value
          ) mustBe
            nomineeRoutes.IsOrganisationNomineePaymentsController.onPageLoad(NormalMode)
        }
      }
    }

    "in Check mode" when {

      "from the CharityContactDetailsPage" must {

        "go to the Charity Details Summary page when an answer is given" in {

          charityInformationNavigator.nextPage(
            CharityContactDetailsPage,
            CheckMode,
            emptyUserAnswers
              .set(
                CharityContactDetailsPage,
                CharityContactDetails("07700 900 982", Some("07700 900 982"), "abc@gmail.com")
              )
              .success
              .value
          ) mustBe
            charityInfoRoutes.CharityInformationSummaryController.onPageLoad()
        }
      }

      "from the AuthorisedOfficialsNINOPage" must {
        "go to Summary page when clicked continue button" in {
          authorisedOfficialsNavigator.nextPage(
            AuthorisedOfficialsNinoPage(0),
            CheckMode,
            emptyUserAnswers.set(AuthorisedOfficialsNinoPage(0), "QQ 12 34 56 C").success.value
          ) mustBe
            authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
        }
      }

      "from the AuthorisedOfficialsPassportPage" must {
        "go to Summary page when clicked continue button" in {
          authorisedOfficialsNavigator.nextPage(
            AuthorisedOfficialsPassportPage(0),
            CheckMode,
            emptyUserAnswers
              .set(AuthorisedOfficialsPassportPage(0), Passport("123", "gb", LocalDate.now()))
              .success
              .value
          ) mustBe
            authOfficialRoutes.AddedAuthorisedOfficialController.onPageLoad(Index(0))
        }
      }
    }
  }
}
