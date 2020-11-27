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

package transformers

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}
import transformers.submission.JsonTransformer

class UserAnswerTransformer extends JsonTransformer {
  //scalastyle:off magic.number

  private def nodeBooleanData(jsonPath: JsPath, value: String): Reads[JsString] = {
    jsonPath.read[Boolean].map(flag => if (flag) JsString(value) else JsString(""))
  }

  private def nodeJsArrayData(jsArrayParam: Reads[JsArray], path: JsPath): Reads[JsObject] = {
    jsArrayParam.flatMap { arr =>
      if (arr.value.exists(_ != JsString(""))) {
        path.json.put(JsArray(arr.value.filter(_ != JsString(""))))
      } else {
        doNothing
      }
    }
  }

  private def commonAddress(basePath: JsPath, uaPath: JsPath) = {
    val lines = for {
      line1 <- (basePath \ 'addressLine1).read[String].map(JsString)
      line2 <- (basePath \ 'addressLine2).read[String].map(JsString)
      line3 <- (basePath \ 'addressLine3).read[String].map(JsString)
      line4 <- (basePath \ 'addressLine4).read[String].map(JsString)
    } yield JsArray(Seq(line1, line2, line3, line4))

    nodeJsArrayData(lines, uaPath \ 'lines) and
      (basePath \ 'postcode).read[String].flatMap { postcode =>
        if (postcode != "") {
          (uaPath \ 'postcode).json.put(JsString(postcode))
        }
        else {
          doNothing
        }
      } and
      (uaPath \ 'country \ 'code).json.copyFrom((basePath \ 'country).read[String].map {
        country => if (country == "") JsString("GB") else JsString(country)
      }) and
      (uaPath \ 'country \ 'name).json.copyFrom((basePath \ 'country).read[String].map {
        country => if (country == "") JsString("GB") else JsString(country)
      })
  }

  def toUserAnswerCharityContactDetails: Reads[JsObject] = {
    (
      (__ \ 'charityName \ 'fullName).json
        .copyFrom((__ \ 'charityContactDetails \ 'fullName).json.pick) and
        ((__ \ 'charityName \ 'operatingName).json
          .copyFrom((__ \ 'charityContactDetails \ 'operatingName).json.pick) orElse doNothing) and
        (__ \ 'charityContactDetails \ 'daytimePhone).json
          .copyFrom((__ \ 'charityContactDetails \ 'daytimePhone).json.pick) and
        ((__ \ 'charityContactDetails \ 'mobilePhone).json
          .copyFrom((__ \ 'charityContactDetails \ 'mobilePhone).json.pick) orElse doNothing) and
        (__ \ 'charityContactDetails \ 'emailAddress).json
          .copyFrom((__ \ 'charityContactDetails \ 'email).readNullable[String].map(value => value.fold(JsString(""))(JsString))) and
        (__ \ 'isSection1Completed).json.put(JsBoolean(false))
      ).reduce
  }

  def toUserAnswerCharityOfficialAddress: Reads[JsObject] = {

    commonAddress(__ \ 'charityOfficialAddress, __ \ 'charityOfficialAddress).reduce
  }

  def toUserAnswerCorrespondenceAddress: Reads[JsObject] = {
    (__ \ 'correspondenceAddress \ 'toggle).readNullable[String].flatMap { toggle =>
      if (toggle.contains("true")) {
        (commonAddress(__ \ 'correspondenceAddress \ 'address, __ \ 'charityPostalAddress) and
          (__ \ 'canWeSendLettersToThisAddress).json.put(JsBoolean(false))).reduce
      }
      else {
        (doNothing and (__ \ 'canWeSendLettersToThisAddress).json.put(JsBoolean(true))).reduce
      }
    }
  }

  private val regulator: Reads[JsArray] = for {
    ccew <- nodeBooleanData(__ \ 'charityRegulator \ 'ccew \ 'isCharityRegulatorSelected, "ccew")
    oscrv <- nodeBooleanData(__ \ 'charityRegulator \ 'oscr \ 'isCharityRegulatorSelected, "oscr")
    ccni <- nodeBooleanData(__ \ 'charityRegulator \ 'ccni \ 'isCharityRegulatorSelected, "ccni")
    other <- nodeBooleanData(__ \ 'charityRegulator \ 'other \ 'isCharityOtherRegulatorSelected, "otherRegulator")
  } yield JsArray(Seq(ccew, oscrv, ccni, other))

  def toUserAnswersCharityRegulator: Reads[JsObject] = {

    regulator.flatMap {
      arr =>
        if (arr.value.exists(_ != JsString(""))) {
          ((__ \ 'isCharityRegulator).json.put(JsBoolean(true)) and
            (__ \ 'charityRegulator).json.put(JsArray(arr.value.filter(_ != JsString("")))) and
            ((__ \ 'charityCommissionRegistrationNumber).json.copyFrom(
              (__ \ 'charityRegulator \ 'ccew \ 'charityRegistrationNumber).json.pick) orElse doNothing) and
            ((__ \ 'scottishRegulatorRegNumber).json.copyFrom(
              (__ \ 'charityRegulator \ 'oscr \ 'charityRegistrationNumber).json.pick) orElse doNothing) and
            ((__ \ 'nIRegulatorRegNumber).json.copyFrom(
              (__ \ 'charityRegulator \ 'ccni \ 'charityRegistrationNumber).json.pick) orElse doNothing) and
            ((__ \ 'charityOtherRegulatorDetails \ 'regulatorName).json.copyFrom(
              (__ \ 'charityRegulator \ 'other \ 'charityRegulatorName).json.pick) orElse doNothing) and
            ((__ \ 'charityOtherRegulatorDetails \ 'registrationNumber).json.copyFrom(
              (__ \ 'charityRegulator \ 'other \ 'charityOtherRegistrationNumber).json.pick) orElse doNothing) and
            (__ \ 'isSection2Completed).json.put(JsBoolean(false))).reduce
        } else {
          ((__ \ 'isCharityRegulator).json.put(JsBoolean(true)) and
            (__ \ 'selectWhyNoRegulator).json.copyFrom(
              (__ \ 'charityRegulator \ 'reasonForNotRegistering \ 'charityRegulator).json.pick) and
            ((__ \ 'whyNotRegisteredWithCharity).json.copyFrom(
              (__ \ 'charityRegulator \ 'reasonForNotRegistering \ 'notRegReasonOtherDescription).json.pick) orElse doNothing) and
            (__ \ 'isSection2Completed).json.put(JsBoolean(false))).reduce
        }
    }
  }

  private def selectGoverningDocument(path: JsPath): Reads[JsObject] = {
    (__ \ 'charityGoverningDocument \ 'docType).read[String].flatMap {
      case "1" => path.json.put(JsString("3"))
      case "2" => path.json.put(JsString("1"))
      case "3" => path.json.put(JsString("4"))
      case "4" => path.json.put(JsString("5"))
      case "6" => path.json.put(JsString("2"))
      case "7" => path.json.put(JsString("6"))
    }
  }

  def toUserAnswersCharityGoverningDocument: Reads[JsObject] = {
    (
      selectGoverningDocument(__ \ 'selectGoverningDocument) and
        (__ \ 'governingDocumentName).json.copyFrom((__ \ 'charityGoverningDocument \ 'nameOtherDoc).json.pick) and
        ((__ \ 'whenGoverningDocumentApproved).json.copyFrom((__ \ 'charityGoverningDocument \ 'effectiveDate).json.pick) orElse doNothing) and
        (__ \ 'sectionsChangedGoverningDocument).json.copyFrom((__ \ 'charityGoverningDocument \ 'govDocApprovedWording).json.pick) and
        (__ \ 'isApprovedGoverningDocument).json.copyFrom((__ \ 'charityGoverningDocument \ 'governingApprovedDoc).json.pick) and
        (__ \ 'isSection3Completed).json.put(JsBoolean(false))
      ).reduce
  }

  private val charitablePurposes: Reads[JsArray] = for {
    reliefOfPoverty <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'reliefOfPoverty, "reliefOfPoverty")
    education <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'education, "education")
    religion <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'religion, "religion")
    healthOrSavingOfLives <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'healthOrSavingOfLives, "healthOrSavingOfLives")
    citizenshipOrCommunityDevelopment <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'citizenshipOrCommunityDevelopment, "citizenshipOrCommunityDevelopment")
    artsCultureOrScience <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'artsCultureHeritageOrScience, "artsCultureOrScience")
    amateurSport <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'amateurSport, "amateurSport")
    humanRights <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'humanRights, "humanRights")
    environmentalProtection <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'environmentalProtectionOrImprovement, "environmentalProtection")
    reliefOfYouthAge <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'reliefOfThoseInNeed, "reliefOfYouthAge")
    animalWelfare <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'animalWelfare, "animalWelfare")
    armedForcesOfTheCrown <-
      nodeBooleanData(__ \ 'whatYourCharityDoes \ 'promotionOfEfficiencyInArmedForcesPoliceFireAndRescueService, "armedForcesOfTheCrown")
    other <- nodeBooleanData(__ \ 'whatYourCharityDoes \ 'whatYourCharityDoesOther, "other")
  } yield JsArray(Seq(reliefOfPoverty, education, religion, healthOrSavingOfLives, citizenshipOrCommunityDevelopment,
    artsCultureOrScience, amateurSport, humanRights, environmentalProtection, reliefOfYouthAge, animalWelfare,
    armedForcesOfTheCrown, other))

  def toUserAnswersWhatYourCharityDoes: Reads[JsObject] = {

    charitablePurposes.flatMap {
      arr =>
        if (arr.value.exists(_ != JsString(""))) {
          (
            (__ \ 'charitableObjectives).json.copyFrom((__ \ 'whatYourCharityDoes \ 'charitableObjectives).json.pick) and
              (__ \ 'charitablePurposes).json.put(JsArray(arr.value.filter(_ != JsString("")))) and
              (__ \ 'whatYourCharityDoesOtherReason).json.copyFrom((__ \ 'whatYourCharityDoes \ 'whatYourCharityDoesOtherReason).json.pick) and
              ((__ \ 'publicBenefits).json.copyFrom((__ \ 'whatYourCharityDoes \ 'charityThingsBenefitThePublic).json.pick) orElse doNothing) and
              (__ \ 'isSection4Completed).json.put(JsBoolean(false))
            ).reduce
        } else {
          doNothing
        }
    }
  }

  private val futureFunds: Reads[JsArray] = for {
    donations <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'donations, "donations")
    fundraising <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'fundraising, "fundraising")
    grants <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'grants, "grants")
    membershipSubscriptions <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'membershipSubscriptions, "membershipSubscriptions")
    tradingIncome <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'tradingIncome, "tradingIncome")
    tradingSubsidiaries <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'tradingSubsidiaries, "tradingSubsidiaries")
    investmentIncome <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'investmentIncome, "investmentIncome")
    other <- nodeBooleanData(__ \ 'operationAndFunds \ 'futureFunds \ 'other, "other")
  } yield JsArray(Seq(donations, fundraising, grants, membershipSubscriptions, tradingIncome,
    tradingSubsidiaries, investmentIncome, other))

  private val whereWillCharityOperate = for {
    overseas <- nodeBooleanData(__ \ 'operationAndFunds \ 'whereWillCharityOperate \ 'overseas, "5")
    england <- nodeBooleanData(__ \ 'operationAndFunds \ 'whereWillCharityOperate \ 'englandAndWales, "1")
    wales <- nodeBooleanData(__ \ 'operationAndFunds \ 'whereWillCharityOperate \ 'englandAndWales, "2")
    oscrv <- nodeBooleanData(__ \ 'operationAndFunds \ 'whereWillCharityOperate \ 'scotland, "3")
    ccni <- nodeBooleanData(__ \ 'operationAndFunds \ 'whereWillCharityOperate \ 'northernIreland, "4")
    ukwide <- nodeBooleanData(__ \ 'operationAndFunds \ 'whereWillCharityOperate \ 'ukWide, "6")
  } yield JsArray(Seq(england, wales, oscrv, ccni, overseas, ukwide))

  private val updatedWhereWillCharityOperate: Reads[JsObject] = whereWillCharityOperate.flatMap { arr =>

    def containsList(l1: Seq[JsString]) = arr.value.exists(_ != JsString("")) && l1.forall(arr.value.toList.contains)

    arr.value.toList match {
      case _ if containsList(Seq(JsString("5"), JsString("6"))) =>
        (__ \ 'operatingLocation).json.put(JsArray(Seq(JsString("1"), JsString("2"), JsString("3"), JsString("4"), JsString("5"))))
      case _ if containsList(Seq(JsString("6"))) =>
        (__ \ 'operatingLocation).json.put(JsArray(Seq(JsString("1"), JsString("2"), JsString("3"), JsString("4"))))
      case list if list.exists(_ != JsString("")) =>
        (__ \ 'operatingLocation).json.put(JsArray(arr.value.filter(_ != JsString(""))))
      case _ => doNothing
    }
  }

  def toUserAnswersOperationAndFunds: Reads[JsObject] = {

    (
      nodeJsArrayData(futureFunds, __ \ 'selectFundRaising) and updatedWhereWillCharityOperate and
        (__ \ 'accountingPeriodEndDate).json.copyFrom({
          (__ \ 'operationAndFunds \ 'operationAndFundsCommon \ 'accountPeriodEnd).json.pick
        }) and
        (__ \ 'isFinancialAccounts).json.copyFrom((__ \ 'operationAndFunds \ 'operationAndFundsCommon \ 'financialAccounts).json.pick) and
        (__ \ 'isBankStatements).json.copyFrom((__ \ 'operationAndFunds \ 'operationAndFundsCommon \ 'bankStatements).json.pick) and
        ((__ \ 'whyNoBankStatement).json.copyFrom((__ \ 'operationAndFunds \ 'operationAndFundsCommon \ 'noBankStatements).json.pick) orElse doNothing) and
        ((__ \ 'otherFundRaising).json.copyFrom((__ \ 'operationAndFunds \ 'futureFundsOther).json.pick) orElse doNothing) and
        ((__ \ 'estimatedIncome).json.copyFrom((__ \ 'operationAndFunds \ 'estimatedGrossIncome).json.pick) orElse doNothing) and
        ((__ \ 'actualIncome).json.copyFrom((__ \ 'operationAndFunds \ 'incomeReceivedToDate).json.pick) orElse doNothing) and
        (__ \ 'isSection5Completed).json.put(JsBoolean(false))
      ).reduce
  }

  def toUserAnswersCharityBankAccountDetails: Reads[JsObject] = {
    (
      (__ \ 'bankDetails \ 'accountName).json.copyFrom((__ \ 'charityBankAccountDetails \ 'accountName).json.pick) and
        (__ \ 'bankDetails \ 'accountNumber).json.copyFrom((__ \ 'charityBankAccountDetails \ 'accountNumber).json.pick) and
        (__ \ 'bankDetails \ 'sortCode).json.copyFrom((__ \ 'charityBankAccountDetails \ 'sortCode).json.pick) and
        ((__ \ 'bankDetails \ 'rollNumber).json.copyFrom((__ \ 'charityBankAccountDetails \ 'rollNumber).json.pick) orElse doNothing) and
        (__ \ 'isSection6Completed).json.put(JsBoolean(false))
      ).reduce
  }


  def toUserAnswersCharityHowManyAuthOfficials: Reads[JsObject] = {
    (__ \ 'charityHowManyAuthOfficials \ 'numberOfAuthOfficials).readNullable[Int].flatMap {
      case Some(number) if number > 11 =>
        ((__ \ 'isAddAnotherOfficial).json.put(JsBoolean(true)) and
          (__ \ 'isSection7Completed).json.put(JsBoolean(false))).reduce
      case Some(_) =>
        ((__ \ 'isAddAnotherOfficial).json.put(JsBoolean(false)) and
          (__ \ 'isSection7Completed).json.put(JsBoolean(false))).reduce
      case _ => doNothing
    }
  }

  def toUserAnswersCharityHowManyOtherOfficials: Reads[JsObject] = {
    (__ \ 'charityHowManyOtherOfficials \ 'numberOfOtherOfficials).readNullable[Int].flatMap {
      case Some(number) if number > 22 =>
        ((__ \ 'addAnotherOtherOfficial).json.put(JsBoolean(true)) and
          (__ \ 'isSection8Completed).json.put(JsBoolean(false))).reduce
      case Some(_) =>
        ((__ \ 'addAnotherOtherOfficial).json.put(JsBoolean(false)) and
          (__ \ 'isSection8Completed).json.put(JsBoolean(false))).reduce
      case _ => doNothing
    }
  }

  def titleFilter(index: Int, authOrOther: String): Reads[JsObject] = {
    (authorisedOfficialOriginalKey(index, authOrOther) \ 'title).readNullable[String].flatMap {
      case Some(value) if Seq("0001", "0002", "0003", "0004").contains(value) => (__ \ 'title).json.put(JsString(value))
      case Some(_) => (__ \ 'title).json.put(JsString("unsupported"))
      case _  => doNothing
    }
  }

  private def optionalAddress(oldPath: JsPath, rewriteIsAddAnotherPath: JsPath, previousAddressPath: JsPath, booleanFlag: Boolean = true): Reads[JsObject] = {
    (oldPath \ 'toggle).readNullable[String].flatMap { toggle =>
      if (toggle.contains("true")) {
        (commonAddress(oldPath \ 'address, previousAddressPath) and
          rewriteIsAddAnotherPath.json.put(JsBoolean(booleanFlag))).reduce
      }
      else {
        (doNothing and rewriteIsAddAnotherPath.json.put(JsBoolean(!booleanFlag))).reduce
      }
    }
  }

  private def ninoOrPassport(isIndividualNino: JsPath, individualNino: JsPath, individualPassport: JsPath, oldSchemaPath: JsPath): Reads[JsObject] = {
    val passportPath = oldSchemaPath \ 'officialIndividualIdentityCardDetails
    (oldSchemaPath \ 'nationalInsuranceNumberPossession).readNullable[String].flatMap{
      case Some("true") =>  ((isIndividualNino).json.put(JsBoolean(true)) and
        (individualNino).json.copyFrom((oldSchemaPath \ 'niNumberUK).json.pick)).reduce
      case _ => ((isIndividualNino).json.put(JsBoolean(false)) and
        (individualPassport \ 'passportNumber).json.copyFrom((passportPath \ 'identityCardNumber).json.pick) and
        (individualPassport \ 'country).json.copyFrom((passportPath \ 'countryOfIssue).json.pick) and
        (individualPassport \ 'expiryDate).json.copyFrom((passportPath \ 'expiryDate).json.pick)
        ).reduce
    }
  }

  private def authorisedOfficialOriginalKey(index: Int, authOrOther: String): JsPath = __ \ s"${authOrOther}OfficialIndividual${index + 1}"


// scalastyle:off method.length
  def toOneOfficial(index: Int, authOrOther: String): Reads[JsObject] = {
    (
      (__ \ 'officialsName).json
        .copyFrom(titleFilter(index, authOrOther)) and
        (__ \ 'officialsName \ 'firstName).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'firstName).json.pick) and
        ((__ \ 'officialsName \ 'middleName).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'middleName).json.pick) orElse doNothing) and
        (__ \ 'officialsName \ 'lastName).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'lastName).json.pick) and
        (__ \ 'officialsDOB).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'dateOfBirth).json.pick) and
        (__ \ 'officialsPhoneNumber \ 'daytimePhone).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'dayPhoneNumber).json.pick) and
        ((__ \ 'officialsPhoneNumber \ 'mobilePhone).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'telephoneNumber).json.pick) orElse doNothing) and
        (__ \ 'officialsPosition).json
          .copyFrom((authorisedOfficialOriginalKey(index, authOrOther) \ 'positionType).json.pick) and
        ninoOrPassport(__ \ 'isOfficialNino,
          __ \ 'officialsNino,
          __ \ 'officialsPassport,
          authorisedOfficialOriginalKey(index, authOrOther) \ 'charityAuthorisedOfficialIndividualIdentity) and
        commonAddress(
          authorisedOfficialOriginalKey(index, authOrOther) \ 'charityAuthorisedOfficialAddress,
          __ \ 'officialAddress).reduce and
        optionalAddress(authorisedOfficialOriginalKey(index, authOrOther) \ 'charityAuthorisedOfficialPreviousAddress,
          __ \ s"isOfficial${if (authOrOther == "other") "s" else ""}PreviousAddress",
          __ \ 'officialPreviousAddress
        )
      ).reduce

  }

  def toUserAnswersCharityAuthorisedOfficialIndividual(index: Int, authOrOther: String): Reads[JsObject] = {

    val auth = __.readNullable(toOneOfficial(index, authOrOther)).map(x => x.fold(JsArray())(el => JsArray.empty :+ el))

    (__ \ s"${authOrOther}Officials").json.copyFrom(auth)
  }


}
