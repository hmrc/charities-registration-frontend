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

package models.requests

import org.apache.commons.lang3.StringUtils
import play.api.libs.json.{Json, OFormat}

final case class BarsBankAccount(
    sortCode:      String,
    accountNumber: String
)

object BarsBankAccount {

  implicit val format: OFormat[BarsBankAccount] = Json.format[BarsBankAccount]

  // TODO check if format is correct
  def normalise(sortCode: String, accountNumber: String): BarsBankAccount =
    BarsBankAccount(sortCode.filter(_.isDigit), leftPad(accountNumber))

  private def leftPad(accountNumber: String): String =
    StringUtils.leftPad(accountNumber, 8, "0")
}
