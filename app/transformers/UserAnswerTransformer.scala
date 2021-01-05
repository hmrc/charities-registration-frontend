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

package transformers

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}
import transformers.submission.JsonTransformer

class UserAnswerTransformer extends JsonTransformer {
  //scalastyle:off magic.number

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

  private val otherCountries: Reads[JsArray] = for {
    country1 <- (__ \ 'operationAndFunds \ 'otherCountriesOfOperation \ 'overseas1).readNullable[JsString]
    country2 <- (__ \ 'operationAndFunds \ 'otherCountriesOfOperation \ 'overseas2).readNullable[JsString]
    country3 <- (__ \ 'operationAndFunds \ 'otherCountriesOfOperation \ 'overseas3).readNullable[JsString]
    country4 <- (__ \ 'operationAndFunds \ 'otherCountriesOfOperation \ 'overseas4).readNullable[JsString]
    country5 <- (__ \ 'operationAndFunds \ 'otherCountriesOfOperation \ 'overseas5).readNullable[JsString]
  } yield JsArray(Seq(country1, country2, country3, country4, country5).flatten
    .map(countryName => Json.obj("overseasCountry" -> countryName)))

  private lazy val overseasLocations: Reads[JsObject] = otherCountries.flatMap { countries =>
    (__ \ 'whatCountryDoesTheCharityOperateIn).json.put(countries)
  }

  private val updatedWhereWillCharityOperate: Reads[JsObject] = whereWillCharityOperate.flatMap { arr =>

    def containsList(l1: Seq[JsString]) = arr.value.exists(_ != JsString("")) && l1.forall(arr.value.toList.contains)

    arr.value.toList match {
      case _ if containsList(Seq(JsString("5"), JsString("6"))) =>
        ((__ \ 'operatingLocation).json.put(JsArray(Seq(JsString("1"), JsString("2"), JsString("3"), JsString("4"), JsString("5")))) and
          overseasLocations).reduce
      case _ if containsList(Seq(JsString("5"))) =>
        ((__ \ 'operatingLocation).json.put(JsArray(Seq(JsString("5")))) and overseasLocations).reduce
      case _ if containsList(Seq(JsString("6"))) =>
        (__ \ 'operatingLocation).json.put(JsArray(Seq(JsString("1"), JsString("2"), JsString("3"), JsString("4"))))
      case list if list.exists(_ != JsString("")) =>
        (__ \ 'operatingLocation).json.put(JsArray(arr.value.filter(_ != JsString(""))))
      case _ => doNothing
    }
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

  private val regulator: Reads[JsArray] = for {
    ccew <- nodeBooleanData(__ \ 'charityRegulator \ 'ccew \ 'isCharityRegulatorSelected, "ccew")
    oscrv <- nodeBooleanData(__ \ 'charityRegulator \ 'oscr \ 'isCharityRegulatorSelected, "oscr")
    ccni <- nodeBooleanData(__ \ 'charityRegulator \ 'ccni \ 'isCharityRegulatorSelected, "ccni")
    other <- nodeBooleanData(__ \ 'charityRegulator \ 'other \ 'isCharityOtherRegulatorSelected, "otherRegulator")
  } yield JsArray(Seq(ccew, oscrv, ccni, other))

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

  private def commonAddress(oldPath: JsPath, uaPath: JsPath) = {
    val lines = for {
      line1 <- (oldPath \ 'addressLine1).read[String].map(JsString)
      line2 <- (oldPath \ 'addressLine2).read[String].map(JsString)
      line3 <- (oldPath \ 'addressLine3).read[String].map(JsString)
      line4 <- (oldPath \ 'addressLine4).read[String].map(JsString)
    } yield JsArray(Seq(line1, line2, line3, line4))

    nodeJsArrayData(lines, uaPath \ 'lines) and
      (oldPath \ 'postcode).read[String].flatMap { postcode =>
        if (postcode != "") {
          (uaPath \ 'postcode).json.put(JsString(postcode))
        }
        else {
          doNothing
        }
      } and
      (uaPath \ 'country \ 'code).json.copyFrom((oldPath \ 'country).read[String].map {
        country => if (country == "") JsString("GB") else JsString("XX")
      }) and
      (uaPath \ 'country \ 'name).json.copyFrom((oldPath \ 'country).read[String].map {
        country => if (country == "") JsString("United Kingdom") else JsString(country)
      })
  }

  private def optionalAddress(oldPath: JsPath, newFlagPath: JsPath, uaPath: JsPath, expectedFlag: Boolean = false): Reads[JsObject] = {
    val negativeFlag = !expectedFlag
    (oldPath \ 'toggle).readNullable[String].flatMap { toggle =>
      if (toggle.contains("true")) {
        (commonAddress(oldPath \ 'address, uaPath) and
          newFlagPath.json.put(JsBoolean(negativeFlag))).reduce
      }
      else {
        (doNothing and newFlagPath.json.put(JsBoolean(expectedFlag))).reduce
      }
    }
  }

  private def ninoOrPassport(uaPathNino: JsPath, uaPathPassport: JsPath, uaFlagPath: JsPath, oldSchemaPath: JsPath): Reads[JsObject] = {

    val passportPath = oldSchemaPath \ 'officialIndividualIdentityCardDetails

    (oldSchemaPath \ 'nationalInsuranceNumberPossession).readNullable[String].flatMap{
      case Some("true") => (uaFlagPath.json.put(JsBoolean(true)) and
        uaPathNino.json.copyFrom((oldSchemaPath \ 'niNumberUK).json.pick)).reduce
      case _ => (uaFlagPath.json.put(JsBoolean(false)) and
        (uaPathPassport \ 'passportNumber).json.copyFrom((passportPath \ 'identityCardNumber).json.pick) and
        (uaPathPassport \ 'country).json.copyFrom((passportPath \ 'countryOfIssue).json.pick) and
        (uaPathPassport \ 'expiryDate).json.copyFrom((passportPath \ 'expiryDate).json.pick)
        ).reduce
    }
  }

  private def getTitle(individual: JsPath, oldSchemaPath: JsPath): Reads[JsObject] = {
    oldSchemaPath.read[String].flatMap{
      case title if List("0001", "0002", "0003", "0004").contains(title) =>
        individual.json.copyFrom(oldSchemaPath.json.pick)
      case _ => individual.json.put(JsString("unsupported"))
    }
  }

  private def bankDetails(oldPath: JsPath, uaFlagPath: JsPath, uaPath: JsPath): Reads[JsObject] = {
    val commonBankPath = oldPath \ 'nomineePaymentDetails \ 'nomineeCommonPaymentDetails
    (oldPath \ 'authorisedToReceivePayments).read[String].flatMap { toggle =>
      if (toggle.contains("true")) {
        ((uaPath \ 'accountName).json.copyFrom((commonBankPath \ 'accountName).json.pick) and
          (uaPath \ 'accountNumber).json.copyFrom((commonBankPath \ 'accountNumber).json.pick) and
          (uaPath \ 'sortCode).json.copyFrom((commonBankPath \ 'sortCode).json.pick) and
          ((uaPath \ 'rollNumber).json.copyFrom((commonBankPath \ 'rollNumber).json.pick) orElse doNothing) and
          uaFlagPath.json.put(JsBoolean(true))).reduce
      }
      else {
        (doNothing and uaFlagPath.json.put(JsBoolean(false))).reduce
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

  private def selectWhyNoRegulator(path: JsPath): Reads[JsObject] = {
    (__ \ 'charityRegulator \ 'reasonForNotRegistering \ 'charityRegulator).read[String].flatMap {
      case "5" => path.json.put(JsString("1"))
      case "6" => path.json.put(JsString("2"))
      case "7" => path.json.put(JsString("3"))
      case "8" => path.json.put(JsString("4"))
      case "9" => path.json.put(JsString("5"))
      case "10" => path.json.put(JsString("7"))
    }
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

    optionalAddress(__ \ 'correspondenceAddress, __ \ 'canWeSendLettersToThisAddress, __ \ 'charityPostalAddress, expectedFlag = true)
  }

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
          ((__ \ 'isCharityRegulator).json.put(JsBoolean(false)) and
            selectWhyNoRegulator(__ \ 'selectWhyNoRegulator) and
            ((__ \ 'whyNotRegisteredWithCharity).json.copyFrom(
              (__ \ 'charityRegulator \ 'reasonForNotRegistering \ 'notRegReasonOtherDescription).json.pick) orElse doNothing) and
            (__ \ 'isSection2Completed).json.put(JsBoolean(false))).reduce
        }
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

  private def authorisedOfficialOriginalKey(index: Int, authOrOther: String): JsPath = __ \ s"${authOrOther}OfficialIndividual${index + 1}"

  // scalastyle:off method.length
  def toOneOfficial(index: Int, authOrOther: String): Reads[JsObject] = {
    (
      getTitle(__ \ 'officialsName \ 'title, authorisedOfficialOriginalKey(index, authOrOther) \ 'title) and
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
        ninoOrPassport(__ \ 'officialsNino, __ \ 'officialsPassport, __ \ 'isOfficialNino,
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

  def toUserAnswersCharityBankAccountDetails: Reads[JsObject] = {
    (
      (__ \ 'bankDetails \ 'accountName).json.copyFrom((__ \ 'charityBankAccountDetails \ 'accountName).json.pick) and
        (__ \ 'bankDetails \ 'accountNumber).json.copyFrom((__ \ 'charityBankAccountDetails \ 'accountNumber).json.pick) and
        (__ \ 'bankDetails \ 'sortCode).json.copyFrom((__ \ 'charityBankAccountDetails \ 'sortCode).json.pick) and
        ((__ \ 'bankDetails \ 'rollNumber).json.copyFrom((__ \ 'charityBankAccountDetails \ 'rollNumber).json.pick) orElse doNothing) and
        (__ \ 'isSection6Completed).json.put(JsBoolean(false))
      ).reduce
  }

  def toUserAnswersCharityAddNominee: Reads[JsObject] = {
    ((__ \ 'nominee \ 'isAuthoriseNominee).json.copyFrom((__ \ 'charityAddNominee \ 'nominee).readNullable[Boolean].map{
      case Some(x) => JsBoolean(x)
      case _ => JsBoolean(false)
    }) and
      (__ \ 'isSection9Completed).json.put(JsBoolean(false))
      ).reduce
  }

  def toUserAnswersCharityNomineeStatus: Reads[JsObject] = {
    (__ \ 'charityNomineeStatus \ 'nomineeStatus).readNullable[String].flatMap{
      case Some("individual") => ((__ \ 'nominee \ 'chooseNominee).json.put(JsBoolean(true)) and
        (__ \ 'nominee \ 'isAuthoriseNominee).json.put(JsBoolean(true))).reduce
      case Some(_) => ((__ \ 'nominee \ 'chooseNominee).json.put(JsBoolean(false)) and
        (__ \ 'nominee \ 'isAuthoriseNominee).json.put(JsBoolean(true))).reduce
      case _ => doNothing
    }
  }

  def toUserAnswersCharityNomineeIndividual: Reads[JsObject] = {
    val individual = __ \ 'nominee \ 'individual
    val individualOldPath = __ \ 'charityNomineeIndividual
    (
      (__ \ 'nominee \ 'isAuthoriseNominee).json.put(JsBoolean(true)) and
        (__ \ 'nominee \ 'chooseNominee).json.put(JsBoolean(true)) and
        getTitle(individual \ 'individualName \ 'title, individualOldPath \ 'title) and
        (individual \ 'individualName \ 'firstName).json.copyFrom((individualOldPath \ 'firstName).json.pick) and
        ((individual \ 'individualName \ 'middleName).json.copyFrom(
          (individualOldPath \ 'middleName).json.pick) orElse doNothing) and
        (individual \ 'individualName \ 'lastName).json.copyFrom((individualOldPath \ 'lastName).json.pick) and
        (individual \ 'individualDOB).json.copyFrom((individualOldPath \ 'dateOfBirth).json.pick) and
        (individual \ 'individualPhoneNumber \ 'daytimePhone).json.copyFrom(
          (individualOldPath \ 'dayTimePhoneNumber).json.pick) and
        ((individual \ 'individualPhoneNumber \ 'daytimePhone).json.copyFrom(
          (individualOldPath \ 'mobilePhoneNumber).json.pick) orElse doNothing) and
        ninoOrPassport(individual \ 'individualNino, individual \ 'individualPassport,
          individual \ 'isIndividualNino, individualOldPath \ 'nomineeIndividualIdentity) and
        commonAddress(individualOldPath \ 'nomineeIndividualHomeAddress, individual \ 'individualAddress).reduce and
        optionalAddress(individualOldPath \ 'nomineeIndividualPreviousAddress,
          individual \ 'isIndividualPreviousAddress,
          individual \ 'individualPreviousAddress) and
        bankDetails(individualOldPath \ 'nomineeIndividualBankDetails,
          individual \ 'isIndividualNomineePayments,
          individual \ 'individualBankDetails)
      ).reduce
  }

  def toUserAnswersCharityNomineeOrganisation: Reads[JsObject] = {
    val organisation = __ \ 'nominee \ 'organisation
    val organisationOldPath = __ \ 'charityNomineeOrganisation
    (
      (__ \ 'nominee \ 'isAuthoriseNominee).json.put(JsBoolean(true)) and
        (__ \ 'nominee \ 'chooseNominee).json.put(JsBoolean(false)) and
        (organisation \ 'organisationName).json.copyFrom((organisationOldPath \ 'orgFullName).json.pick) and
        (organisation \ 'organisationContactDetails \ 'phoneNumber).json.copyFrom(
          (organisationOldPath \ 'orgPhoneNumber).json.pick) and
        (organisation \ 'organisationContactDetails \ 'email).json.put(JsString("")) and
        commonAddress(organisationOldPath \ 'address, organisation \ 'organisationAddress).reduce and
        optionalAddress(organisationOldPath \ 'nomineeOrgPreviousAddress,
          organisation \ 'isOrganisationPreviousAddress,
          organisation \ 'organisationPreviousAddress) and
        getTitle(organisation \ 'organisationAuthorisedPersonName \ 'title,
          organisationOldPath \ 'nomineeOrgPersonalDetails \ 'title) and
        (organisation \ 'organisationAuthorisedPersonName \ 'firstName).json.copyFrom(
          (organisationOldPath \ 'nomineeOrgPersonalDetails \ 'firstName).json.pick) and
        ((organisation \ 'organisationAuthorisedPersonName \ 'middleName).json.copyFrom(
          (organisationOldPath \ 'nomineeOrgPersonalDetails \ 'middleName).json.pick) orElse doNothing) and
        (organisation \ 'organisationAuthorisedPersonName \ 'lastName).json.copyFrom(
          (organisationOldPath \ 'nomineeOrgPersonalDetails \ 'lastName).json.pick) and
        (organisation \ 'organisationAuthorisedPersonDOB).json.copyFrom(
          (organisationOldPath \ 'nomineeOrgPersonalDetails \ 'dateOfBirth).json.pick) and
        ninoOrPassport(organisation \ 'organisationAuthorisedPersonNino, organisation \ 'organisationAuthorisedPersonPassport,
          organisation \ 'isOrganisationNino, organisationOldPath \ 'nomineeOrgNIDetails) and
        bankDetails(organisationOldPath \ 'nomineeBankAccountDetails,
          organisation \ 'isOrganisationNomineePayments,
          organisation \ 'organisationBankDetails)
      ).reduce
  }

  def toUserAnswersOldAcknowledgement: Reads[JsObject] = {

    (
      (__ \ 'oldAcknowledgement \ 'refNumber).json.copyFrom((__ \ "acknowledgement-Reference" \ 'formBundle).json.pick) and
      (__ \ 'oldAcknowledgement \ 'submissionDate).json.copyFrom((__ \ "acknowledgement-Reference" \ 'timeStamp).json.pick)
      ).reduce
  }
}
