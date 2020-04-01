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

package common

import play.api.data.validation.{ValidationError, _}

object Validation {
  def constraintBuilder[A](key: String, args: String*)(condition: A => Boolean): Constraint[A] = {
    Constraint(input => if (condition(input)) {
      Valid
    } else {
      Invalid(ValidationError(key, args:_*))
    })
  }


  val mandatoryCheck: String => Boolean = input => input.trim != ""

  val optionalMandatoryCheck: Option[String] => Boolean = {
    case Some(input) => mandatoryCheck(input)
    case _ => false
  }


  val yesNoCheck: String => Boolean = {
    case "Yes" => true
    case "No" => true
    case "" => true
    case _ => false
  }

  val optionalYesNoCheck: Option[String] => Boolean = {
    case Some(input) => yesNoCheck(input)
    case _ => true
  }

}
