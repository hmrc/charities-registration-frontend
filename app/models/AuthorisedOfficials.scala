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


case class AuthorisedOfficialsName(firstName: String, middleName: Option[String], lastName: String) {

  def fullName: String = {
     Seq(Some(firstName), middleName, Some(lastName)).flatten.mkString(" ")
  }
}

object AuthorisedOfficialsName {

  implicit val formats = Json.format[AuthorisedOfficialsName]

  override def toString: String = "authorisedOfficialsName"
}

case class AuthorisedOfficialsPhoneNumber(daytimePhone: String, mobilePhone: Option[String])

object AuthorisedOfficialsPhoneNumber {

  implicit val formats = Json.format[AuthorisedOfficialsPhoneNumber]

  override def toString: String = "authorisedOfficialsPhoneNumber"
}

