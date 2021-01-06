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

package viewmodels

import models.{Name, UserAnswers}
import models.addressLookup.AddressModel
import models.regulators.SelectWhyNoRegulator
import pages.QuestionPage
import pages.addressLookup._
import pages.authorisedOfficials.AuthorisedOfficialsNamePage
import pages.nominees.{IndividualNomineeNamePage, OrganisationAuthorisedPersonNamePage}
import pages.operationsAndFunds.{IsBankStatementsPage, IsFinancialAccountsPage}
import pages.otherOfficials.OtherOfficialsNamePage
import pages.regulatorsAndDocuments.{IsCharityRegulatorPage, SelectWhyNoRegulatorPage}
import play.api.i18n.Messages
import play.api.libs.json.Reads

object RequiredDocumentsHelper {

  private val requiredDocumentsKey: String = "requiredDocuments."

  private val addressesList: Seq[QuestionPage[AddressModel]] = Seq[QuestionPage[AddressModel]](
    AuthorisedOfficialAddressLookupPage(0), OtherOfficialAddressLookupPage(0),
    AuthorisedOfficialAddressLookupPage(1), OtherOfficialAddressLookupPage(1),
    OtherOfficialAddressLookupPage(2),
    NomineeIndividualAddressLookupPage, OrganisationNomineeAddressLookupPage
  )

  def checkPageCondition[T](userAnswers: UserAnswers, page: QuestionPage[T], expectedValue: T, stringSuffix: String = ".answerTrue"
    )(implicit reads: Reads[T]): Option[String] = {
    userAnswers.get(page)
      .filter(_ == expectedValue)
      .map(_ => requiredDocumentsKey + page.toString + stringSuffix)
  }

  def getNamePageFromAddressPage(address: QuestionPage[AddressModel]): QuestionPage[Name] = address match {
    case AuthorisedOfficialAddressLookupPage(index) => AuthorisedOfficialsNamePage(index)
    case OtherOfficialAddressLookupPage(index) => OtherOfficialsNamePage(index)
    case NomineeIndividualAddressLookupPage => IndividualNomineeNamePage
    case OrganisationNomineeAddressLookupPage => OrganisationAuthorisedPersonNamePage
  }

  def getOfficialsAndNomineesNames(userAnswers: UserAnswers): Seq[Name] =
    addressesList.flatMap(page => userAnswers.get(page) match {
        case Some(address) if address.country.code != "GB" =>
          userAnswers.get(getNamePageFromAddressPage(page))
        case _ => None
      })

  def formatNames(names: Seq[Name])(implicit messages: Messages): String = {
    val listOfNames = names.map(_.getFullName)
    messages.lang.code match {
      case "cy" => listOfNames.mkString(", ")
      case _ => listOfNames.mkString(", ").replaceFirst(",(?=[^,]+$)", s" ${messages("service.separator.and")}")
      }
    }

    def getRequiredDocuments(userAnswers: UserAnswers): Seq[String] = {
      Seq(
        Some(s"${requiredDocumentsKey}governingDocumentName.answerTrue"),
        checkPageCondition(userAnswers, IsCharityRegulatorPage, true),
        checkPageCondition(userAnswers, SelectWhyNoRegulatorPage, SelectWhyNoRegulator.UniformedYouthGroup),
        checkPageCondition(userAnswers, IsCharityRegulatorPage, false, ".answerAlternative"),
        checkPageCondition(userAnswers, IsFinancialAccountsPage, true),
        checkPageCondition(userAnswers, IsBankStatementsPage, true),
        checkPageCondition(userAnswers, IsBankStatementsPage, false, ".answerAlternative")
      ).flatten
    }

    def getForeignOfficialsMessages(userAnswers: UserAnswers)(implicit messages: Messages): Option[(String, String)] = {
      getOfficialsAndNomineesNames(userAnswers) match {
        case nonZero if nonZero.nonEmpty => Some(("requiredDocuments.foreignAddresses.answerTrue", formatNames(nonZero)))
        case _ => None
      }
    }
  }
