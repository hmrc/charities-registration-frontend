/*
 * Copyright 2023 HM Revenue & Customs
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

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}

class CharityTransformer extends JsonTransformer {
  //scalastyle:off magic.number

  private def getRegulator(reg: String): Reads[JsObject] =
    (__ \ Symbol("charityRegulator"))
      .read[JsArray]
      .map { x =>
        x.value.find(x => x == JsString(reg))
      }
      .flatMap {
        case Some(_) => (__ \ Symbol("regulator") \ reg).json.put(JsBoolean(true))
        case _       => doNothing
      }

  private def findNode(locationPath: JsPath, readPath: JsPath, index: String): Reads[JsObject] =
    locationPath.json.copyFrom(readPath.read[JsArray].map(x => JsBoolean(x.value.contains(JsString(index)))))

  def userAnswersToRegulator: Reads[JsObject] =
    (getRegulator("ccew") and
      ((__ \ Symbol("regulator") \ Symbol("ccewRegistrationNumber")).json.copyFrom(
        (__ \ Symbol("charityCommissionRegistrationNumber")).json.pick
      ) orElse doNothing) and
      getRegulator("oscr") and
      ((__ \ Symbol("regulator") \ Symbol("oscrRegistrationNumber")).json.copyFrom(
        (__ \ Symbol("scottishRegulatorRegNumber")).json.pick
      ) orElse doNothing) and
      getRegulator("ccni") and
      ((__ \ Symbol("regulator") \ Symbol("ccniRegistrationNumber")).json.copyFrom(
        (__ \ Symbol("nIRegulatorRegNumber")).json.pick
      ) orElse doNothing) and
      getRegulator("otherRegulator") and
      ((__ \ Symbol("regulator") \ Symbol("otherRegulatorName")).json.copyFrom(
        (__ \ Symbol("charityOtherRegulatorDetails") \ Symbol("regulatorName")).json.pick
      ) orElse doNothing) and
      ((__ \ Symbol("regulator") \ Symbol("otherRegulatorRegistrationNumber")).json.copyFrom(
        (__ \ Symbol("charityOtherRegulatorDetails") \ Symbol("registrationNumber")).json.pick
      ) orElse doNothing)).reduce

  def userAnswersToCharityOrganisation: Reads[JsObject] =
    (
      (__ \ Symbol("charityRegulator")).read[JsArray].flatMap { _ =>
        (__ \ Symbol("charityOrganisation")).json.copyFrom(userAnswersToRegulator)
      } orElse doNothing and
        (__ \ Symbol("charityOrganisation") \ Symbol("registeredRegulator")).json.copyFrom((__ \ Symbol("isCharityRegulator")).json.pick) and
        ((__ \ Symbol("charityOrganisation") \ Symbol("nonRegReason")).json.copyFrom(
          (__ \ Symbol("selectWhyNoRegulator")).json.pick
        ) orElse doNothing) and
        ((__ \ Symbol("charityOrganisation") \ Symbol("otherReason")).json.copyFrom(
          (__ \ Symbol("whyNotRegisteredWithCharity")).json.pick
        ) orElse doNothing)
    ).reduce

  def userAnswersToAboutOrganisationCommon: Reads[JsObject] =
    (
      ((__ \ Symbol("aboutOrgCommon") \ Symbol("otherDocument")).json.copyFrom(
        (__ \ Symbol("governingDocumentName")).json.pick
      ) orElse doNothing) and
        (__ \ Symbol("aboutOrgCommon") \ Symbol("effectiveDate")).json.copyFrom((__ \ Symbol("whenGoverningDocumentApproved")).json.pick)
    ).reduce

  def userAnswersToAboutOrganisation: Reads[JsObject] =
    (
      ((__ \ Symbol("aboutOrganisation")).json.copyFrom(userAnswersToAboutOrganisationCommon) orElse doNothing) and
        (__ \ Symbol("aboutOrganisation") \ Symbol("documentEnclosed")).json.copyFrom((__ \ Symbol("selectGoverningDocument")).json.pick) and
        (__ \ Symbol("aboutOrganisation") \ Symbol("governingApprovedDoc")).json.copyFrom(
          (__ \ Symbol("isApprovedGoverningDocument")).json.pick
        ) and
        (__ \ Symbol("hasCharityChangedPartsOfGoverningDocument")).readNullable[Boolean].flatMap {
          case Some(value) => (__ \ Symbol("aboutOrganisation") \ Symbol("governingApprovedWords")).json.put(JsBoolean(!value))
          case _           => (__ \ Symbol("aboutOrganisation") \ Symbol("governingApprovedWords")).json.put(JsBoolean(true))
        } and
        (__ \ Symbol("sectionsChangedGoverningDocument")).readNullable[String].flatMap {
          case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
            (__ \ Symbol("aboutOrganisation") \ Symbol("governingApprovedChanges")).json
              .put(JsString(replaceInvalidCharacters(changes).substring(0, 255)))
          case Some(changes)                                                   =>
            (__ \ Symbol("aboutOrganisation") \ Symbol("governingApprovedChanges")).json.put(JsString(replaceInvalidCharacters(changes)))
          case _                                                               => doNothing
        } and
        (__ \ Symbol("sectionsChangedGoverningDocument")).readNullable[String].flatMap {
          case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
            (__ \ Symbol("aboutOrganisation") \ Symbol("governingApprovedChangesB")).json
              .put(JsString(replaceInvalidCharacters(changes).substring(255)))
          case _                                                               => doNothing
        }
    ).reduce

  def userAnswersToOperationAndFundsCommon: Reads[JsObject] = {
    val hasFinancialAccounts = (__ \ Symbol("isFinancialAccounts")).readNullable[Boolean].map {
      case Some(bol) => JsBoolean(bol)
      case _         => JsBoolean(false)
    }
    (
      (__ \ Symbol("accountingPeriodEndDate"))
        .read[String]
        .flatMap(accountPeriod =>
          (__ \ Symbol("operationAndFundsCommon") \ Symbol("accountPeriodEnd")).json.put {
            JsString(("""\d+""".r findAllIn accountPeriod).toList.reverse.mkString)
          }
        ) and
        (__ \ Symbol("operationAndFundsCommon") \ Symbol("financialAccounts")).json.copyFrom(hasFinancialAccounts) and
        (__ \ Symbol("whyNoBankStatement")).readNullable[String].flatMap {
          case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
            (__ \ Symbol("operationAndFundsCommon") \ Symbol("noBankStatements")).json
              .put(JsString(replaceInvalidCharacters(changes).substring(0, 255)))
          case Some(changes)                                                   =>
            (__ \ Symbol("operationAndFundsCommon") \ Symbol("noBankStatements")).json.put(JsString(replaceInvalidCharacters(changes)))
          case _                                                               => doNothing
        } and
        (__ \ Symbol("whyNoBankStatement")).readNullable[String].flatMap {
          case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
            (__ \ Symbol("operationAndFundsCommon") \ Symbol("noBankStatementsB")).json
              .put(JsString(replaceInvalidCharacters(changes).substring(255)))
          case _                                                               => doNothing
        }
    ).reduce
  }

  def userAnswersToOtherCountriesOfOperation: Reads[JsObject] =
    (
      ((__ \ Symbol("otherCountriesOfOperation") \ Symbol("overseas1")).json
        .copyFrom((__ \ Symbol("whatCountryDoesTheCharityOperateIn") \ 0 \ Symbol("overseasCountry")).json.pick) orElse doNothing) and
        ((__ \ Symbol("otherCountriesOfOperation") \ Symbol("overseas2")).json
          .copyFrom((__ \ Symbol("whatCountryDoesTheCharityOperateIn") \ 1 \ Symbol("overseasCountry")).json.pick) orElse doNothing) and
        ((__ \ Symbol("otherCountriesOfOperation") \ Symbol("overseas3")).json
          .copyFrom((__ \ Symbol("whatCountryDoesTheCharityOperateIn") \ 2 \ Symbol("overseasCountry")).json.pick) orElse doNothing) and
        ((__ \ Symbol("otherCountriesOfOperation") \ Symbol("overseas4")).json
          .copyFrom((__ \ Symbol("whatCountryDoesTheCharityOperateIn") \ 3 \ Symbol("overseasCountry")).json.pick) orElse doNothing) and
        ((__ \ Symbol("otherCountriesOfOperation") \ Symbol("overseas5")).json
          .copyFrom((__ \ Symbol("whatCountryDoesTheCharityOperateIn") \ 4 \ Symbol("overseasCountry")).json.pick) orElse doNothing)
    ).reduce

  def userAnswersToOperationAndFunds: Reads[JsObject] =
    (
      ((__ \ Symbol("operationAndFunds")).json.copyFrom(userAnswersToOperationAndFundsCommon) orElse doNothing) and
        (__ \ Symbol("operationAndFunds") \ Symbol("estimatedGrossIncome")).json.copyFrom((__ \ Symbol("estimatedIncome")).json.pick) and
        (__ \ Symbol("operationAndFunds") \ Symbol("incomeReceivedToDate")).json.copyFrom((__ \ Symbol("actualIncome")).json.pick) and
        (__ \ Symbol("operationAndFunds") \ Symbol("futureFunds")).json.copyFrom(
          (__ \ Symbol("selectFundRaising"))
            .read[JsArray]
            .map(x => JsString(x.value.map(_.toString()).mkString(", ").replaceAll("\"", "")))
        ) and
        (__ \ Symbol("operationAndFunds") \ Symbol("otherAreaOperation")).json.put(JsBoolean(true)) and
        (__ \ Symbol("operationAndFunds") \ Symbol("englandAndWales")).json.copyFrom(
          (__ \ Symbol("operatingLocation"))
            .read[JsArray]
            .map(countriesOfOperation =>
              JsBoolean(
                countriesOfOperation.value.map(_.as[String]).exists(countries => countries == "1" || countries == "2")
              )
            )
        ) and
        findNode(__ \ Symbol("operationAndFunds") \ Symbol("scotland"), __ \ Symbol("operatingLocation"), "3") and
        findNode(__ \ Symbol("operationAndFunds") \ Symbol("northernIreland"), __ \ Symbol("operatingLocation"), "4") and
        (__ \ Symbol("operationAndFunds") \ Symbol("ukWide")).json.copyFrom(
          (__ \ Symbol("operatingLocation"))
            .read[JsArray]
            .map(x =>
              JsBoolean(
                Seq("3", "4")
                  .forall(country =>
                    x.value.map(_.as[String]) match {
                      case allOverUk
                          if allOverUk.contains(country) && allOverUk
                            .exists(country => country == "1" || country == "2") =>
                        true
                      case _ => false
                    }
                  )
              )
            )
        ) and
        findNode(__ \ Symbol("operationAndFunds") \ Symbol("overseas"), __ \ Symbol("operatingLocation"), "5") and
        ((__ \ Symbol("operationAndFunds")).json.copyFrom(userAnswersToOtherCountriesOfOperation) orElse doNothing)
    ).reduce

  def userAnswersToCharitableObjectives: Reads[JsObject] =
    (
      (__ \ Symbol("charitableObjectives")).readNullable[String].flatMap {
        case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
          (__ \ Symbol("charitableObjectives") \ Symbol("objectivesA")).json
            .put(JsString(replaceInvalidCharacters(changes).substring(0, 255)))
        case Some(changes)                                                   =>
          (__ \ Symbol("charitableObjectives") \ Symbol("objectivesA")).json.put(JsString(replaceInvalidCharacters(changes)))
        case _                                                               => doNothing
      } and
        (__ \ Symbol("charitableObjectives")).readNullable[String].flatMap {
          case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
            (__ \ Symbol("charitableObjectives") \ Symbol("objectivesB")).json
              .put(JsString(replaceInvalidCharacters(changes).substring(255)))
          case _                                                               => doNothing
        }
    ).reduce

  def userAnswersToCharitablePurposes: Reads[JsObject] =
    (
      findNode(__ \ Symbol("charitablePurposes") \ Symbol("reliefOfPoverty"), __ \ Symbol("charitablePurposes"), "reliefOfPoverty") and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("education"), __ \ Symbol("charitablePurposes"), "education") and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("religion"), __ \ Symbol("charitablePurposes"), "religion") and
        findNode(
          __ \ Symbol("charitablePurposes") \ Symbol("healthOrSavingOfLives"),
          __ \ Symbol("charitablePurposes"),
          "healthOrSavingOfLives"
        ) and
        findNode(
          __ \ Symbol("charitablePurposes") \ Symbol("citizenshipOrCommunityDevelopment"),
          __ \ Symbol("charitablePurposes"),
          "citizenshipOrCommunityDevelopment"
        ) and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("artsCultureOrScience"), __ \ Symbol("charitablePurposes"), "artsCultureOrScience") and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("amateurSport"), __ \ Symbol("charitablePurposes"), "amateurSport") and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("humanRights"), __ \ Symbol("charitablePurposes"), "humanRights") and
        findNode(
          __ \ Symbol("charitablePurposes") \ Symbol("environmentalProtection"),
          __ \ Symbol("charitablePurposes"),
          "environmentalProtection"
        ) and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("reliefOfYouthAge"), __ \ Symbol("charitablePurposes"), "reliefOfYouthAge") and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("animalWelfare"), __ \ Symbol("charitablePurposes"), "animalWelfare") and
        findNode(
          __ \ Symbol("charitablePurposes") \ Symbol("armedForcesOfTheCrown"),
          __ \ Symbol("charitablePurposes"),
          "armedForcesOfTheCrown"
        ) and
        findNode(__ \ Symbol("charitablePurposes") \ Symbol("other"), __ \ Symbol("charitablePurposes"), "other")
    ).reduce

  def userAnswersToPublicBenefit: Reads[JsObject] =
    (
      (__ \ Symbol("publicBenefits")).readNullable[String].flatMap {
        case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
          (__ \ Symbol("publicBenefit") \ Symbol("publicBenefitA")).json
            .put(JsString(replaceInvalidCharacters(changes).substring(0, 255)))
        case Some(changes)                                                   =>
          (__ \ Symbol("publicBenefit") \ Symbol("publicBenefitA")).json.put(JsString(replaceInvalidCharacters(changes)))
        case _                                                               => doNothing
      } and
        (__ \ Symbol("publicBenefits")).readNullable[String].flatMap {
          case Some(changes) if replaceInvalidCharacters(changes).length > 255 =>
            (__ \ Symbol("publicBenefit") \ Symbol("publicBenefitB")).json.put(JsString(replaceInvalidCharacters(changes).substring(255)))
          case _                                                               => doNothing
        }
    ).reduce

  def userAnswersToOrgPurpose: Reads[JsObject] = (__ \ Symbol("orgPurpose")).json.copyFrom(
    (userAnswersToCharitableObjectives and userAnswersToCharitablePurposes and userAnswersToPublicBenefit).reduce
  )

  def userAnswersToCharity: Reads[JsObject] = (__ \ Symbol("charityRegistration") \ Symbol("charity")).json.copyFrom(
    (
      userAnswersToCharityOrganisation and userAnswersToAboutOrganisation and
        userAnswersToOperationAndFunds and userAnswersToOrgPurpose
    ).reduce
  )
}
