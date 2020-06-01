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

package models

import play.api.libs.json.Json

case class CharityName(fullName: String, operatingName: Option[String])

object CharityName {

  implicit val formats = Json.format[CharityName]

  override def toString: String = "charityNamesDetail"

}

case  class CharityContactDetails(daytimePhone:String,mobilePhone:Option[String],emailAddress:String)

object CharityContactDetails {

  implicit val formats = Json.format[CharityContactDetails]

  override def toString: String = "charityContactDetails"

}

case  class CharityUKAddress(addressLine1:String, addressLine2:Option[String], addressLine3:Option[String], townCity:String, postcode:String)

object CharityUKAddress {

  implicit val formats = Json.format[CharityUKAddress]

  override def toString: String = "charityUKAddress"

}