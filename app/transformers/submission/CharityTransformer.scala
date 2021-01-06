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

package transformers.submission

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}

class CharityTransformer extends JsonTransformer {
  //scalastyle:off magic.number

  private def getRegulator(reg: String): Reads[JsObject] = {
    (__ \ 'charityRegulator).read[JsArray].map {
      x => x.value.find(x => x == JsString(reg))
    }.flatMap {
      case Some(_) => (__ \ 'regulator \ reg).json.put(JsBoolean(true))
      case _ => doNothing
    }
  }

  private def findNode(locationPath: JsPath, readPath: JsPath, index: String): Reads[JsObject] = {
    locationPath.json.copyFrom(readPath.read[JsArray].map(x => JsBoolean(x.value.contains(JsString(index)))))
  }

  def userAnswersToRegulator: Reads[JsObject] = {
    (getRegulator("ccew") and
      ((__ \ 'regulator \ 'ccewRegistrationNumber).json.copyFrom((__ \ 'charityCommissionRegistrationNumber).json.pick) orElse doNothing) and
      getRegulator("oscr") and
      ((__ \ 'regulator \ 'oscrRegistrationNumber).json.copyFrom((__ \ 'scottishRegulatorRegNumber).json.pick) orElse doNothing) and
      getRegulator("ccni") and
      ((__ \ 'regulator \ 'ccniRegistrationNumber).json.copyFrom((__ \ 'nIRegulatorRegNumber).json.pick) orElse doNothing) and
      getRegulator("otherRegulator") and
      ((__ \ 'regulator \ 'otherRegulatorName).json.copyFrom((__ \ 'charityOtherRegulatorDetails \ 'regulatorName).json.pick) orElse doNothing) and
      ((__ \ 'regulator \ 'otherRegulatorRegistrationNumber).json.copyFrom(
        (__ \ 'charityOtherRegulatorDetails \ 'registrationNumber).json.pick) orElse doNothing)
      ).reduce
  }

  def userAnswersToCharityOrganisation: Reads[JsObject] = {
    (
      (__ \ 'charityRegulator).read[JsArray].flatMap { _ =>
        (__ \ 'charityOrganisation).json.copyFrom(userAnswersToRegulator)
      } orElse doNothing and
        (__ \ 'charityOrganisation \ 'registeredRegulator).json.copyFrom((__ \ 'isCharityRegulator).json.pick) and
        ((__ \ 'charityOrganisation \ 'nonRegReason).json.copyFrom((__ \ 'selectWhyNoRegulator).json.pick) orElse doNothing) and
        ((__ \ 'charityOrganisation \ 'otherReason).json.copyFrom((__ \ 'whyNotRegisteredWithCharity).json.pick) orElse doNothing)
      ).reduce
  }

  def userAnswersToAboutOrganisationCommon: Reads[JsObject] = {
    (
      ((__ \ 'aboutOrgCommon \ 'otherDocument).json.copyFrom((__ \ 'governingDocumentName).json.pick) orElse doNothing) and
        (__ \ 'aboutOrgCommon \ 'effectiveDate).json.copyFrom((__ \ 'whenGoverningDocumentApproved).json.pick)
      ).reduce
  }

  def userAnswersToAboutOrganisation: Reads[JsObject] = {
    (
      ((__ \ 'aboutOrganisation).json.copyFrom(userAnswersToAboutOrganisationCommon) orElse doNothing) and
        (__ \ 'aboutOrganisation \ 'documentEnclosed).json.copyFrom((__ \ 'selectGoverningDocument).json.pick) and
        (__ \ 'aboutOrganisation \ 'governingApprovedDoc).json.copyFrom((__ \ 'isApprovedGoverningDocument).json.pick) and
        (__ \ 'hasCharityChangedPartsOfGoverningDocument).readNullable[Boolean].flatMap {
          case Some(value) => (__ \ 'aboutOrganisation \ 'governingApprovedWords).json.put(JsBoolean(value))
          case _ => (__ \ 'aboutOrganisation \ 'governingApprovedWords).json.put(JsBoolean(false))
        } and
        (__ \ 'sectionsChangedGoverningDocument).readNullable[String].flatMap {
          case Some(changes) if changes.length > 255 => (__ \ 'aboutOrganisation \ 'governingApprovedChanges).json.put(JsString(changes.substring(0,255)))
          case Some(changes) => (__ \ 'aboutOrganisation \ 'governingApprovedChanges).json.put(JsString(changes))
          case _ => doNothing
        } and
        (__ \ 'sectionsChangedGoverningDocument).readNullable[String].flatMap {
          case Some(changes) if changes.length > 255 => (__ \ 'aboutOrganisation \ 'governingApprovedChangesB).json.put(JsString(changes.substring(255)))
          case _ => doNothing
        }
      ).reduce
  }

  def userAnswersToOperationAndFundsCommon: Reads[JsObject] = {
    val hasFinancialAccounts = (__ \ 'isFinancialAccounts).readNullable[Boolean].map {
      case Some(bol) => JsBoolean(bol)
      case _ => JsBoolean(false)
    }
    (
      (__ \ 'accountingPeriodEndDate).read[String].flatMap(accountPeriod =>
        (__ \ 'operationAndFundsCommon \ 'accountPeriodEnd).json.put(JsString(accountPeriod.replaceAll("-", "")))) and
        (__ \ 'operationAndFundsCommon \ 'financialAccounts).json.copyFrom(hasFinancialAccounts) and
        (__ \ 'whyNoBankStatement).readNullable[String].flatMap {
          case Some(changes) if changes.length > 255 => (__ \ 'operationAndFundsCommon \ 'noBankStatements).json.put(JsString(changes.substring(0,255)))
          case Some(changes) => (__ \ 'operationAndFundsCommon \ 'noBankStatements).json.put(JsString(changes))
          case _ => doNothing
        } and
        (__ \ 'whyNoBankStatement).readNullable[String].flatMap {
          case Some(changes) if changes.length > 255 => (__ \ 'operationAndFundsCommon \ 'noBankStatementsB).json.put(JsString(changes.substring(255)))
          case _ => doNothing
        }
      ).reduce
  }

  def userAnswersToOtherCountriesOfOperation: Reads[JsObject] = {
    (
      ((__ \ 'otherCountriesOfOperation \ 'overseas1).json
        .copyFrom((__ \ 'whatCountryDoesTheCharityOperateIn \ 0 \ 'overseasCountry).json.pick) orElse doNothing) and
        ((__ \ 'otherCountriesOfOperation \ 'overseas2).json
          .copyFrom((__ \ 'whatCountryDoesTheCharityOperateIn \ 1 \ 'overseasCountry).json.pick) orElse doNothing) and
        ((__ \ 'otherCountriesOfOperation \ 'overseas3).json
          .copyFrom((__ \ 'whatCountryDoesTheCharityOperateIn \ 2 \ 'overseasCountry).json.pick) orElse doNothing) and
        ((__ \ 'otherCountriesOfOperation \ 'overseas4).json
          .copyFrom((__ \ 'whatCountryDoesTheCharityOperateIn \ 3 \ 'overseasCountry).json.pick) orElse doNothing) and
        ((__ \ 'otherCountriesOfOperation \ 'overseas5).json
          .copyFrom((__ \ 'whatCountryDoesTheCharityOperateIn \ 4 \ 'overseasCountry).json.pick) orElse doNothing)
      ).reduce
  }

  def userAnswersToOperationAndFunds: Reads[JsObject] = {
    (
      ((__ \ 'operationAndFunds).json.copyFrom(userAnswersToOperationAndFundsCommon) orElse doNothing) and
        (__ \ 'operationAndFunds \ 'estimatedGrossIncome).json.copyFrom((__ \ 'estimatedIncome).json.pick) and
        (__ \ 'operationAndFunds \ 'incomeReceivedToDate).json.copyFrom((__ \ 'actualIncome).json.pick) and
        (__ \ 'operationAndFunds \ 'futureFunds).json.copyFrom((__ \ 'selectFundRaising).read[JsArray].map(x =>
          JsString(x.value.map(_.toString()).mkString(", ").replaceAll("\"", "")))) and
        (__ \ 'operationAndFunds \ 'otherAreaOperation).json.put(JsBoolean(true)) and
        (__ \ 'operationAndFunds \ 'englandAndWales).json.copyFrom((__ \ 'operatingLocation).read[JsArray]
          .map(countriesOfOperation => JsBoolean(
            countriesOfOperation.value.map(_.as[String]).exists(countries => countries == "1" || countries == "2")
          ))) and
        findNode(__ \ 'operationAndFunds \ 'scotland, __ \ 'operatingLocation, "3") and
        findNode(__ \ 'operationAndFunds \ 'northernIreland, __ \ 'operatingLocation, "4") and
        (__ \ 'operationAndFunds \ 'ukWide).json.copyFrom((__ \ 'operatingLocation).read[JsArray]
          .map(x => JsBoolean(Seq("3", "4")
            .forall(country => x.value.map(_.as[String]) match {
              case allOverUk if allOverUk.contains(country) && allOverUk.exists(country => country == "1" || country == "2") => true
              case _ => false
            }
             )))) and
        findNode(__ \ 'operationAndFunds \ 'overseas, __ \ 'operatingLocation, "5") and
        ((__ \ 'operationAndFunds).json.copyFrom(userAnswersToOtherCountriesOfOperation) orElse doNothing)
      ).reduce
  }

  def userAnswersToCharitableObjectives: Reads[JsObject] = {
    (
      (__ \ 'charitableObjectives).readNullable[String].flatMap {
        case Some(changes) if changes.length > 255 => (__ \ 'charitableObjectives \ 'objectivesA).json.put(JsString(changes.substring(0,255)))
        case Some(changes) => (__ \ 'charitableObjectives \ 'objectivesA).json.put(JsString(changes))
        case _ => doNothing
      } and
        (__ \ 'charitableObjectives).readNullable[String].flatMap {
          case Some(changes) if changes.length > 255 => (__ \ 'charitableObjectives \ 'objectivesB).json.put(JsString(changes.substring(255)))
          case _ => doNothing
        }
      ).reduce
  }

  def userAnswersToCharitablePurposes: Reads[JsObject] = {
    (
      findNode(__ \ 'charitablePurposes \ 'reliefOfPoverty, __ \ 'charitablePurposes, "reliefOfPoverty") and
        findNode(__ \ 'charitablePurposes \ 'education, __ \ 'charitablePurposes, "education") and
        findNode(__ \ 'charitablePurposes \ 'religion, __ \ 'charitablePurposes, "religion") and
        findNode(__ \ 'charitablePurposes \ 'healthOrSavingOfLives, __ \ 'charitablePurposes, "healthOrSavingOfLives") and
        findNode(__ \ 'charitablePurposes \ 'citizenshipOrCommunityDevelopment, __ \ 'charitablePurposes, "citizenshipOrCommunityDevelopment") and
        findNode(__ \ 'charitablePurposes \ 'artsCultureOrScience, __ \ 'charitablePurposes, "artsCultureOrScience") and
        findNode(__ \ 'charitablePurposes \ 'amateurSport, __ \ 'charitablePurposes, "amateurSport") and
        findNode(__ \ 'charitablePurposes \ 'humanRights, __ \ 'charitablePurposes, "humanRights") and
        findNode(__ \ 'charitablePurposes \ 'environmentalProtection, __ \ 'charitablePurposes, "environmentalProtection") and
        findNode(__ \ 'charitablePurposes \ 'reliefOfYouthAge, __ \ 'charitablePurposes, "reliefOfYouthAge") and
        findNode(__ \ 'charitablePurposes \ 'animalWelfare, __ \ 'charitablePurposes, "animalWelfare") and
        findNode(__ \ 'charitablePurposes \ 'armedForcesOfTheCrown, __ \ 'charitablePurposes, "armedForcesOfTheCrown") and
        findNode(__ \ 'charitablePurposes \ 'other, __ \ 'charitablePurposes, "other")
      ).reduce
  }

  def userAnswersToPublicBenefit: Reads[JsObject] = {
    (
      (__ \ 'publicBenefits).readNullable[String].flatMap {
        case Some(changes) if changes.length > 255 => (__ \ 'publicBenefit \ 'publicBenefitA).json.put(JsString(changes.substring(0,255)))
        case Some(changes) => (__ \ 'publicBenefit \ 'publicBenefitA).json.put(JsString(changes))
        case _ => doNothing
      } and
        (__ \ 'publicBenefits).readNullable[String].flatMap {
          case Some(changes) if changes.length > 255 => (__ \ 'publicBenefit \ 'publicBenefitB).json.put(JsString(changes.substring(255)))
          case _ => doNothing
        }
      ).reduce
  }

  def userAnswersToOrgPurpose: Reads[JsObject] = (__ \ 'orgPurpose).json.copyFrom(
    (userAnswersToCharitableObjectives and userAnswersToCharitablePurposes and userAnswersToPublicBenefit).reduce
  )

  def userAnswersToCharity: Reads[JsObject] = (__ \ 'charityRegistration \ 'charity).json.copyFrom(
    (
      userAnswersToCharityOrganisation and userAnswersToAboutOrganisation and
        userAnswersToOperationAndFunds and userAnswersToOrgPurpose
      ).reduce
  )
}
