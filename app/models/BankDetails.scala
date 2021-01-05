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

package models

import play.api.libs.json._

case class BankDetails(accountName: String, sortCode: String, accountNumber: String, rollNumber: Option[String])

object BankDetails {

  implicit lazy val writes: OWrites[BankDetails] = {

    import play.api.libs.functional.syntax._
    (
      (__ \ "accountName").write[String] and
      (__ \ "sortCode").write[String] and
      (__ \ "accountNumber").write[String] and
      (__ \ "rollNumber").writeNullable[String]
    )(
      bankDetails => (
        bankDetails.accountName,
        bankDetails.sortCode.filter(_.isDigit).mkString,
        bankDetails.accountNumber.filter(_.isDigit).mkString,
        bankDetails.rollNumber)
      )
  }

  implicit val formats: Reads[BankDetails] = Json.format[BankDetails]

  override def toString: String = "bankDetail"

}
