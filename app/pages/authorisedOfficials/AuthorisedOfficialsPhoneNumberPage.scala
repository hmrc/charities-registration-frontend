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

package pages.authorisedOfficials

import models.AuthorisedOfficialsPhoneNumber
import pages.QuestionPage
import play.api.libs.json.JsPath

case class AuthorisedOfficialsPhoneNumberPage(index:Int) extends QuestionPage[AuthorisedOfficialsPhoneNumber] {

  override def path: JsPath =  AuthorisedOfficialsId(index).path \ AuthorisedOfficialsPhoneNumberPage.toString
}

object AuthorisedOfficialsPhoneNumberPage {

  override lazy val toString: String = "authorisedOfficialsPhoneNumber"
}
