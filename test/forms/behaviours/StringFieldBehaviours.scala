/*
 * Copyright 2026 HM Revenue & Customs
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

import forms.mappings.Mappings
import play.api.data.{Form, FormError}

trait StringFieldBehaviours extends FieldBehaviours with Mappings {

  def fieldWithMaxLength(form: Form[?], fieldName: String, maxLength: Int, lengthError: FormError): Unit =
    forAll(stringsLongerThan(maxLength) -> "longString") { string =>
      s"not bind strings longer than $maxLength characters for $string" in {
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors.headOption mustBe scala.Some(lengthError)
      }
    }

  def fieldWithRegex(form: Form[?], fieldName: String, invalidString: String, error: FormError): Unit =
    s"not bind $invalidString invalidated by regex" in {
      val result = form.bind(Map(fieldName -> invalidString)).apply(fieldName)
      result.errors mustEqual Seq(error)
    }

  private def fieldWithRegexExcludingLigatures(
    form: Form[?],
    fieldName: String,
    invalidKey: String,
    regex: String
  ): Unit = {
    Seq(
      "abcdefghijklmnopqrstuvwxyz",
      "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
      "0123456789",
      " '’.",
      "àáâãäåāăą",
      "çćĉċč",
      "þďð",
      "èéêëēĕėęě",
      "ĝģğġ",
      "ĥħ",
      "ìíîïĩīĭį",
      "ĵ",
      "ķ",
      "ĺļľŀł",
      "ñńņňŋ",
      "òóôõöøōŏőǿ",
      "ŕŗř",
      "śŝşš",
      "ţťŧ",
      "ùúûüũūŭůűų",
      "ŵẁẃẅ",
      "ỳýŷÿ",
      "źżž",
      "ÀÁÂÃÄÅĀĂĄǺ",
      "ÇĆĈĊČ",
      "ÞĎÐ",
      "ÈÉÊËĒĔĖĘĚ",
      "ĜĞĠĢ",
      "ĤĦ",
      "ÌÍÎÏĨĪĬĮİ",
      "Ĵ",
      "Ķ",
      "ĹĻĽĿŁ",
      "ÑŃŅŇŊ",
      "ÒÓÔÕÖØŌŎŐǾ",
      "ŔŖŘ",
      "ŚŜŞŠ",
      "ŢŤŦ",
      "ÙÚÛÜŨŪŬŮŰŲ",
      "ŴẀẂẄ",
      "ỲÝŶŸ",
      "ŹŻŽ"
    ).foreach { string =>
      s"bind $string validated by foreign characters regex" in {
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Nil
      }
    }

    Seq(
      "æ",
      "ǽ",
      "œ",
      "Æ",
      "Ǽ",
      "Œ",
      "ß"
    ).foreach { string =>
      s"not bind $string invalidated by ligatures regex" in {
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(
          FormError(fieldName, invalidKey, Seq(validateFieldLigatures))
        )
      }
    }

    Seq(
      "$",
      "£",
      "^",
      "&",
      "(",
      "*"
    ).foreach { string =>
      s"not bind $string invalidated by foreign characters regex" in {
        val result = form.bind(Map(fieldName -> string)).apply(fieldName)
        result.errors mustEqual Seq(
          FormError(fieldName, invalidKey, Seq(regex))
        )
      }
    }
  }

  def fieldWithRegexForeignCharacters(form: Form[?], fieldName: String, invalidKey: String): Unit =
    fieldWithRegexExcludingLigatures(form, fieldName, invalidKey, validateFieldIncludingForeignCharacters)

  def fieldWithRegexForeignCharactersAndNewLine(form: Form[?], fieldName: String, invalidKey: String): Unit =
    fieldWithRegexExcludingLigatures(
      form,
      fieldName,
      invalidKey,
      validateFieldIncludingForeignCharactersAndNewLine
    )

  def bindValidValues(form: Form[String], fieldName: String)(values: String*): Unit =
    values.foreach { value =>
      s"bind valid value $value" in {
        form.bind(Map(fieldName -> value)).hasErrors mustBe false
      }
    }

  def notBindInvalidValues(form: Form[String], fieldName: String, error: String, args: Any*)(values: String*): Unit =
    values.foreach { value =>
      s"not bind invalid value $value" in {
        val result = form.bind(Map(fieldName -> value))
        result.errors.headOption mustBe scala.Some(FormError(fieldName, error, args))
      }
    }
}
