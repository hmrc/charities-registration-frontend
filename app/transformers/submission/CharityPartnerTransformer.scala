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

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}

class CharityPartnerTransformer extends JsonTransformer {

  val localPath: JsPath = __ \ 'charityRegistration \ 'partner
  private val action = "1"
  private val relationOO = "1"
  private val relationAO = "2"
  private val relationNominee = "3"
  private val partnerTypeIndividual = "1"
  private val partnerTypeOrganisation = "2"

  def userAnswersToIndividualDetails(prefix: String): Reads[JsObject] = {

    val nino = (__ \ s"${prefix}Nino").read[String].map {
      value => JsString(value.filterNot(_.isWhitespace))
    }

    val position = prefix match {
      case "officials" => (__ \ 'individualDetails \ 'position).json.copyFrom((__ \ s"${prefix}Position").json.pick)
      case _ => (__ \ 'individualDetails \ 'position).json.put(JsString("01"))
    }

    val phoneNumbers: Reads[JsObject] = prefix match {
      case "organisationAuthorisedPerson" =>
        (__ \ 'individualDetails \ 'dayPhoneNumber).json.copyFrom((__ \ 'organisationContactDetails \ 'phoneNumber).json.pick)
      case _ =>
        ((__ \ 'individualDetails \ 'dayPhoneNumber).json.copyFrom((__ \ s"${prefix}PhoneNumber" \ 'daytimePhone).json.pick) and
          ((__ \ 'individualDetails \ 'mobilePhone).json.copyFrom((__ \ s"${prefix}PhoneNumber" \ 'mobilePhone).json.pick) orElse doNothing)).reduce

    }

    val remainingFields = (
      getName(__ \ 'individualDetails \ 'name, __ \ s"${prefix}Name") and
        (__ \ 'individualDetails \ 'dateOfBirth).json.copyFrom((__ \ s"${prefix}DOB").json.pick) and
        ((__ \ 'individualDetails \ 'nino).json.copyFrom(nino) orElse doNothing) and
        ((__ \ 'individualDetails \ 'nationalIdentityNumber).json
          .copyFrom((__ \ s"${prefix}Passport" \ 'passportNumber).json.pick) orElse doNothing) and
        ((__ \ 'individualDetails \ 'nationalIDCardIssuingCountry).json
          .copyFrom((__ \ s"${prefix}Passport" \ 'country).json.pick) orElse doNothing) and
        ((__ \ 'individualDetails \ 'nationalIDCardExpiryDate).json
          .copyFrom((__ \ s"${prefix}Passport" \ 'expiryDate).json.pick) orElse doNothing)
      ).reduce

    (position and phoneNumbers and remainingFields).reduce
  }

  def userAnswersToPartnerAddressDetails: Reads[JsObject] = {

    (getAddress(__ \ 'addressDetails \ 'currentAddress, __ \ "officialAddress") and
      getOptionalAddress(__ \ 'addressDetails \ 'previousAddress, __ \ "officialPreviousAddress")).reduce
  }

  def userAnswersToPartnerAddressDetailsOrganisation: Reads[JsObject] = {

    (getAddress(__ \ 'addressDetails \ 'currentAddress, __ \ "organisationAddress") and
      getOptionalAddress(__ \ 'addressDetails \ 'previousAddress, __ \ "organisationPreviousAddress")).reduce
  }

  def userAnswersToPartnerAddressDetailsIndividual: Reads[JsObject] = {

    (getAddress(__ \ 'addressDetails \ 'currentAddress, __ \ "individualAddress") and
      getOptionalAddress(__ \ 'addressDetails \ 'previousAddress, __ \ "individualPreviousAddress")).reduce
  }

  def userAnswersToResponsiblePerson(action: String, relation: String): Reads[JsObject] = {

    ((__ \ 'responsiblePerson \ 'action).json.put(JsString(action)) and
      (__ \ 'responsiblePerson \ 'relation).json.put(JsString(relation))).reduce
  }

  def userAnswersToAddPartner(partnerType: String): Reads[JsObject] = {

    ((__ \ 'type).json.put(JsString(partnerType)) and
      (__ \ 'addPartner \ 'effectiveDateOfChange).json.put(JsString(LocalDate.now().toString))).reduce
  }

  def userAnswersToOrgDetails: Reads[JsObject] = {

    ((__ \ 'orgDetails \ 'orgName).json.copyFrom((__ \ "organisationName").json.pick) and
      (__ \ 'orgDetails \ 'telephoneNumber).json.copyFrom((__ \ 'organisationContactDetails \ 'phoneNumber).json.pick) and
      (__ \ 'orgDetails \ 'emailAddress).json.copyFrom((__ \ 'organisationContactDetails \ 'email).json.pick)
      ).reduce
  }

  def userAnswersToBankDetails(pathKey: String): Reads[JsObject] = {
    (
      (__ \ 'bankDetails \ 'accountName).json.copyFrom((__ \ pathKey \ 'accountName).json.pick) and
        (__ \ pathKey \ 'sortCode).read[String].flatMap { n =>
          (__ \ 'bankDetails \ 'sortCode).json.put(JsNumber(n.toInt))
        } and
        (__ \ pathKey \ 'accountNumber).read[String].flatMap { n =>
          (__ \ 'bankDetails \ 'accountNumber).json.put(JsNumber(n.toInt))
        } and
        ((__ \ 'bankDetails \ 'rollNumber).json.copyFrom((__ \ pathKey \ 'rollNumber).json.pick) orElse doNothing)
      ).reduce
  }

  def userAnswersToPaymentDetails(pathKey: String, authorisedKey: String): Reads[JsObject] = {
    val paymentsAuthorised = (__ \ authorisedKey).read[Boolean].map {
      authorised => JsBoolean(authorised)
    }

    (
      (__ \ 'paymentDetails \ 'authorisedPayments).json.copyFrom(paymentsAuthorised) and
        (__ \ 'paymentDetails).json.copyFrom(paymentsAuthorised.flatMap {
          case JsTrue => userAnswersToBankDetails(pathKey)
          case _ => doNothing
        })
    ).reduce
  }

  def userAnswersToPartner: Reads[JsObject] = {

    val partners = for {
      authorisedOfficials <- (__ \ 'authorisedOfficials)
        .readNullable(Reads.seq(getOfficials(action, relationAO, partnerTypeIndividual))).map(x => x.fold(JsArray())(JsArray(_)))
      otherOfficials <- (__ \ 'otherOfficials)
        .readNullable(Reads.seq(getOfficials(action, relationOO, partnerTypeIndividual))).map(x => x.fold(JsArray())(JsArray(_)))
      organisation <- (__ \\ 'organisation).readNullable(getOrganisationNominee(action)).map(x => x.fold(JsArray())(Json.arr(_)))
      individual <- (__ \\ 'individual).readNullable(getIndividualNominee(action)).map(x => x.fold(JsArray())(Json.arr(_)))

    } yield authorisedOfficials ++ otherOfficials ++ organisation ++ individual

    localPath.json.copyFrom(partners)
  }

  def getOfficials(action: String, relation: String, partnerType: String): Reads[JsObject] =

    (userAnswersToResponsiblePerson(action, relation) and userAnswersToAddPartner(partnerType) and userAnswersToIndividualDetails("officials") and
      userAnswersToPartnerAddressDetails).reduce

  def getOrganisationNominee(action: String): Reads[JsObject] =

    (userAnswersToResponsiblePerson(action, relationNominee) and userAnswersToAddPartner(partnerTypeOrganisation) and
      userAnswersToIndividualDetails("organisationAuthorisedPerson") and userAnswersToPartnerAddressDetailsOrganisation and userAnswersToOrgDetails
      and userAnswersToPaymentDetails("organisationBankDetails", "isOrganisationNomineePayments")
      ).reduce

  def getIndividualNominee(action: String): Reads[JsObject] = {

    (userAnswersToResponsiblePerson(action, relationNominee) and userAnswersToAddPartner(partnerTypeIndividual) and
      userAnswersToIndividualDetails("individual") and userAnswersToPartnerAddressDetailsIndividual
      and userAnswersToPaymentDetails("individualBankDetails", "isIndividualNomineePayments")
      ).reduce
  }

}
