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

package forms.behaviours

import play.api.data.{Form, FormError}

trait StringFieldBehaviours extends FieldBehaviours {

  def fieldWithMaxLength(form: Form[_],
                         fieldName: String,
                         maxLength: Int,
                         lengthError: FormError): Unit = {

    s"not bind strings longer than $maxLength characters" in {

      forAll(stringsLongerThan(maxLength) -> "longString") {
        string =>
          val result = form.bind(Map(fieldName -> string)).apply(fieldName)
          result.errors.headOption mustEqual Some(lengthError)
      }
    }
  }

  def bindValidValues(form: Form[String], fieldName: String)(values: String*) = {
    values.foreach { value =>
      s"bind valid value $value" in {
        form.bind(Map(fieldName -> value)).hasErrors mustBe false
      }
    }
  }

  def notBindInvalidValues(form: Form[String], fieldName: String, error: String, args: Any*)(values: String*) = {
    values.foreach { value =>
      s"not bind invalid value $value" in {
        val result = form.bind(Map(fieldName -> value))
        result.errors.headOption mustBe Some(FormError(fieldName, error, args))
      }
    }
  }
}