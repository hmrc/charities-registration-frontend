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

import models.requests.DataRequest
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}

class CharityCommonTransformer extends JsonTransformer {

  val localPath: JsPath = __ \ 'charityRegistration \ 'common

  def userAnswersToAdmin(implicit request: DataRequest[_]): Reads[JsObject] = {
    ((localPath \ 'admin \ 'applicationDate).json.put(JsString("1970-01-01")) and
      (localPath \ 'admin \ 'welshIndicator).json.put(JsBoolean(false)) and
      (localPath \ 'admin \ 'credentialID).json.put(JsString(s"/newauth/credentialId/${request.internalId}")) and
      (localPath \ 'admin \ 'sessionID).json.put(JsString("50 CHARACTERS STRING 50 CHARACTERS " + "STRING 50 CHARA")) and
      (localPath \ 'admin \ 'acknowledgmentReference).json.put(JsString("15 CHARACTERS S"))).reduce
  }

  def userAnswersToOrganisation: Reads[JsObject] = {
    ((localPath \ 'organisation \ 'applicationType).json.put(JsString("0")) and
      (localPath \ 'organisation \ 'orgName).json.copyFrom((__ \ 'charityName \ 'fullName).json.pick) and
      ((localPath \ 'organisation \ 'operatingName).json.copyFrom((__ \ 'charityName \ 'operatingName).json.pick) orElse doNothing) and
      (localPath \ 'organisation \ 'telephoneNumber).json.copyFrom((__ \ 'charityContactDetails \ 'daytimePhone).json.pick) and
      ((localPath \ 'organisation \ 'mobileNumber).json.copyFrom((__ \ 'charityContactDetails \ 'mobilePhone).json.pick) orElse doNothing) and
      ((localPath \ 'organisation \ 'emailAddress).json.copyFrom((__ \ 'charityContactDetails \ 'emailAddress).json.pick) orElse doNothing) and
      (localPath \ 'organisation \ 'countryEstd).json.copyFrom(
        (__ \ 'charityEstablishedIn).read[String].map(value => if(value == "0") JsString("1") else JsString(value)))).reduce
  }

  def userAnswersToAddressDetailsCommon: Reads[JsObject] = {

    val differentCorrespondenceAddress = (__ \ 'canWeSendLettersToThisAddress).read[Boolean].flatMap { isDiff =>
      getOptionalAddress(localPath \ 'addressDetails \ 'correspondenceAddress, __ \ 'charityPostalAddress).flatMap {
        correspondenceAddress =>
          getAddress(localPath \ 'addressDetails \ 'officialAddress, __ \ 'charityOfficialAddress).map { officialAddress =>
            val result = for {
              v1 <- officialAddress.transform((localPath \ 'addressDetails \ 'officialAddress).json.pick).asOpt
              v2 <- correspondenceAddress.transform((localPath \ 'addressDetails \ 'correspondenceAddress).json.pick).asOpt
            } yield v1 != v2
            JsBoolean(result.fold(false)(r => r && !isDiff))
          }
      }
    }

    (getAddress(localPath \ 'addressDetails\ 'officialAddress, __ \ 'charityOfficialAddress) and
      (localPath \ 'addressDetails\ 'differentCorrespondence).json.copyFrom(differentCorrespondenceAddress) and
      getOptionalAddress(localPath \ 'addressDetails\ 'correspondenceAddress, __ \ 'charityPostalAddress)).reduce
  }

  def userAnswersToBankDetails: Reads[JsObject] = {
    (
      (localPath \ 'bankDetails \ 'accountName).json.copyFrom((__ \ 'bankDetails \ 'accountName).json.pick) and
      (__ \ 'bankDetails \ 'sortCode).read[String].flatMap { n =>
        (localPath \ 'bankDetails \ 'sortCode).json.put(JsNumber(n.toInt))
      } and
      (__ \ 'bankDetails \ 'accountNumber).read[String].flatMap { n =>
        (localPath \ 'bankDetails \ 'accountNumber).json.put(JsNumber(n.toInt))
      } and
      ((localPath \ 'bankDetails \ 'rollNumber).json.copyFrom((__ \ 'bankDetails \ 'rollNumber).json.pick) orElse doNothing)
    ).reduce
  }

  def userAnswersToIndDeclarationInfo: Reads[JsObject] = {

    val isNonUK = (__ \ 'authorisedOfficials \ 0 \ 'officialAddress \ 'country \ 'code).read[String].map{
      code => JsBoolean(code != "GB")
    }

    (getName(localPath \ 'declarationInfo \ 'name, __ \ 'authorisedOfficials \ 0 \ 'officialsName) and
      (localPath \ 'declarationInfo \ 'position).json.copyFrom((__ \ 'authorisedOfficials \ 0 \ 'officialsPosition).json.pick) and
      (localPath \ 'declarationInfo \ 'overseas).json.copyFrom(isNonUK) and
      (localPath \ 'declarationInfo \ 'declaration).json.put(JsBoolean(true))).reduce
  }

  def userAnswersToCommon(implicit request: DataRequest[_]): Reads[JsObject] = {

    (userAnswersToAdmin and userAnswersToOrganisation and userAnswersToAddressDetailsCommon and
      userAnswersToBankDetails and userAnswersToIndDeclarationInfo).reduce
  }

}
