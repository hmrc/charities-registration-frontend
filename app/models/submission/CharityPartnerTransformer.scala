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

package models.submission

import java.time.LocalDate

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.{JsObjectReducer, _}
import play.api.libs.json.{__, _}

class CharityPartnerTransformer extends JsonTransformer {

  val localPath: JsPath = __ \ 'charityRegistration \ 'partner
  private val action = "1"
  private val relationOO = "1"
  private val relationAO = "2"
  private val partnerTypeIndividual = "1"

  def userAnswersToIndividualDetails : Reads[JsObject] = {

    val nino = (__ \ "officialsNino").read[String].map{
      value => JsString(value.filterNot(_.isWhitespace))
    }
    (
      getName(__ \ 'individualDetails\ 'name, __ \ 'officialsName) and
      (__ \ 'individualDetails \ 'position).json.copyFrom((__ \ 'officialsPosition ).json.pick) and
      (__ \ 'individualDetails \ 'dateOfBirth).json.copyFrom((__ \ 'officialsDOB ).json.pick) and
      (__ \ 'individualDetails \ 'dayPhoneNumber).json.copyFrom((__ \ 'officialsPhoneNumber \ 'daytimePhone).json.pick) and
      ((__ \ 'individualDetails \ 'mobilePhone).json.copyFrom((__ \ 'officialsPhoneNumber \ 'mobilePhone).json.pick) orElse doNothing) and
      ((__ \ 'individualDetails \ 'nino).json.copyFrom(nino) orElse doNothing)
      ).reduce
  }

  def userAnswersToPartnerAddressDetails : Reads[JsObject] = {

    (getAddress(__ \ 'addressDetails\ 'currentAddress, __ \ "officialAddress") and
      getOptionalAddress(__ \ 'addressDetails\ 'previousAddress, __ \ "officialPreviousAddress")).reduce
  }

  def userAnswersToResponsiblePerson(action:String, relation: String) : Reads[JsObject] = {

    ((__ \ 'responsiblePerson \ 'action).json.put(JsString(action)) and
      (__ \ 'responsiblePerson \ 'relation).json.put(JsString(relation))).reduce
  }

  def userAnswersToAddPartner(partnerType: String): Reads[JsObject] = {

    ((__  \ 'type).json.put(JsString(partnerType)) and
      (__ \ 'addPartner \ 'effectiveDateOfChange).json.put(JsString(LocalDate.now().toString))).reduce
  }

  def userAnswersToPartner: Reads[JsObject] = {

    val partners = for {
      a <- (__ \ 'authorisedOfficials).readNullable(Reads.seq(getOfficials(action, relationAO, partnerTypeIndividual))).map(x => x.fold(JsArray())(JsArray(_)))
      b <- (__ \ 'otherOfficials).readNullable(Reads.seq(getOfficials(action, relationOO, partnerTypeIndividual))).map(x => x.fold(JsArray())(JsArray(_)))
    } yield a ++ b
    localPath.json.copyFrom(partners)
  }

  def getOfficials(action:String, relation: String, partnerType: String): Reads[JsObject] =

    (userAnswersToResponsiblePerson(action, relation) and userAnswersToAddPartner(partnerType) and userAnswersToIndividualDetails and
      userAnswersToPartnerAddressDetails).reduce

}
