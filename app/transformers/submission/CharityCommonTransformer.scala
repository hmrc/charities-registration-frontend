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

import models.requests.DataRequest
import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json._
import uk.gov.hmrc.http.SessionKeys

import java.util.UUID

class CharityCommonTransformer extends JsonTransformer {

  private val sessionIdLength = 50
  private def newUUID: String = UUID.randomUUID.toString // will be 36 chars long

  private def getSessionId(implicit request: DataRequest[?]): String = {
    val veryLongUuid = "-" + newUUID + "-" + newUUID + "-" + newUUID
    val reqSessionId = request.session.data.getOrElse(SessionKeys.sessionId, newUUID)
    val sessionId    = if (reqSessionId.length < sessionIdLength) {
      reqSessionId + veryLongUuid.take(sessionIdLength - reqSessionId.length)
    } else {
      reqSessionId.take(sessionIdLength)
    }
    sessionId
  }

  val localPath: JsPath = __ \ "charityRegistration" \ "common"

  def userAnswersToAdmin(implicit request: DataRequest[?]): Reads[JsObject] =
    ((localPath \ "admin" \ "applicationDate").json.put(JsString("1970-01-01")) and
      (localPath \ "admin" \ "welshIndicator").json.put(JsBoolean(false)) and
      (localPath \ "admin" \ "credentialID").json.put(JsString(s"/newauth/credentialId/${request.internalId}")) and
      (localPath \ "admin" \ "sessionID").json.put(JsString(getSessionId)) and
      (localPath \ "admin" \ "acknowledgmentReference").json.put(JsString("15 CHARACTERS S"))).reduce

  def userAnswersToOrganisation: Reads[JsObject] =
    ((localPath \ "organisation" \ "applicationType").json.put(JsString("0")) and
      (localPath \ "organisation" \ "orgName").json.copyFrom((__ \ "charityName" \ "fullName").json.pick) and
      ((localPath \ "organisation" \ "operatingName").json.copyFrom(
        (__ \ "charityName" \ "operatingName").json.pick
      ) orElse doNothing) and
      getPhone(localPath \ "organisation" \ "telephoneNumber", __ \ "charityContactDetails" \ "daytimePhone") and
      getOptionalPhone(localPath \ "organisation" \ "mobileNumber", __ \ "charityContactDetails" \ "mobilePhone") and
      ((localPath \ "organisation" \ "emailAddress").json.copyFrom(
        (__ \ "charityContactDetails" \ "emailAddress").json.pick
      ) orElse doNothing) and
      (localPath \ "organisation" \ "countryEstd").json.copyFrom(
        (__ \ "charityEstablishedIn").read[String].map(value => if (value == "0") JsString("1") else JsString(value))
      )).reduce

  def userAnswersToAddressDetailsCommon: Reads[JsObject] = {

    val differentCorrespondenceAddress = (__ \ "canWeSendLettersToThisAddress").read[Boolean].flatMap { isDiff =>
      getOptionalAddress(localPath \ "addressDetails" \ "correspondenceAddress", __ \ "charityPostalAddress").flatMap {
        correspondenceAddress =>
          getAddress(localPath \ "addressDetails" \ "officialAddress", __ \ "charityOfficialAddress").map {
            officialAddress =>
              val result = for {
                v1 <- officialAddress.transform((localPath \ "addressDetails" \ "officialAddress").json.pick).asOpt
                v2 <- correspondenceAddress
                        .transform((localPath \ "addressDetails" \ "correspondenceAddress").json.pick)
                        .asOpt
              } yield v1 != v2
              JsBoolean(result.fold(false)(r => r && !isDiff))
          }
      }
    }

    (getAddress(localPath \ "addressDetails" \ "officialAddress", __ \ "charityOfficialAddress") and
      (localPath \ "addressDetails" \ "differentCorrespondence").json.copyFrom(differentCorrespondenceAddress) and
      getOptionalAddress(localPath \ "addressDetails" \ "correspondenceAddress", __ \ "charityPostalAddress")).reduce
  }

  def userAnswersToBankDetails: Reads[JsObject] =
    (
      (localPath \ "bankDetails" \ "accountName").json.copyFrom((__ \ "bankDetails" \ "accountName").json.pick) and
        (__ \ "bankDetails" \ "sortCode").read[String].flatMap { n =>
          (localPath \ "bankDetails" \ "sortCode").json.put(JsString(n))
        } and
        (__ \ "bankDetails" \ "accountNumber").read[String].flatMap { n =>
          (localPath \ "bankDetails" \ "accountNumber").json.put(JsString(n))
        } and
        ((localPath \ "bankDetails" \ "rollNumber").json.copyFrom(
          (__ \ "bankDetails" \ "rollNumber").json.pick
        ) orElse doNothing)
    ).reduce

  def userAnswersToIndDeclarationInfo: Reads[JsObject] = {

    val isNonUK = (__ \ "authorisedOfficials" \ 0 \ "officialAddress" \ "country" \ "code").read[String].map { code =>
      JsBoolean(code != "GB")
    }

    (getName(localPath \ "declarationInfo" \ "name", __ \ "authorisedOfficials" \ 0 \ "officialsName") and
      (localPath \ "declarationInfo" \ "position").json.copyFrom(
        (__ \ "authorisedOfficials" \ 0 \ "officialsPosition").json.pick
      ) and
      ((localPath \ "declarationInfo" \ "postcode").json.copyFrom(
        (__ \ "charityOfficialAddress" \ "postcode").json.pick
      ) orElse doNothing) and
      getPhone(
        localPath \ "declarationInfo" \ "telephoneNumber",
        __ \ "authorisedOfficials" \ 0 \ "officialsPhoneNumber" \ "daytimePhone"
      ) and
      (localPath \ "declarationInfo" \ "overseas").json.copyFrom(isNonUK) and
      (localPath \ "declarationInfo" \ "declaration").json.put(JsBoolean(true))).reduce
  }

  def userAnswersToCommon(implicit request: DataRequest[?]): Reads[JsObject] =
    (userAnswersToAdmin and userAnswersToOrganisation and userAnswersToAddressDetailsCommon and
      userAnswersToBankDetails and userAnswersToIndDeclarationInfo).reduce

}
