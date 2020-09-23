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

package forms.mappings

import models.Enumerable
import org.scalatest.{MustMatchers, OptionValues, WordSpec}
import play.api.data.{Form, FormError}

object MappingsSpec {

  sealed trait Foo
  case object Bar extends Foo
  case object Baz extends Foo

  object Foo {

    val values: Set[Foo] = Set(Bar, Baz)

    implicit val fooEnumerable: Enumerable[Foo] =
      Enumerable(values.toSeq.map(v => v.toString -> v): _*)
  }
}

class MappingsSpec extends WordSpec with MustMatchers with OptionValues with Mappings {

  import MappingsSpec._

  "text" must {

    val testForm: Form[String] =
      Form(
        "value" -> text()
      )

    "bind a valid string" in {
      val result = testForm.bind(Map("value" -> "foobar"))
      result.get mustEqual "foobar"
    }

    "not bind an empty string" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind a string with spaces only" in {
      val result = testForm.bind(Map("value" -> "        "))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "return a custom error message" in {
      val form = Form("value" -> text("custom.error"))
      val result = form.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "custom.error"))
    }

    "unbind a valid value" in {
      val result = testForm.fill("foobar")
      result.apply("value").value.value mustEqual "foobar"
    }
  }

  "boolean" must {

    val testForm: Form[Boolean] =
      Form(
        "value" -> boolean()
      )

    "bind true" in {
      val result = testForm.bind(Map("value" -> "true"))
      result.get mustEqual true
    }

    "bind false" in {
      val result = testForm.bind(Map("value" -> "false"))
      result.get mustEqual false
    }

    "not bind a non-boolean" in {
      val result = testForm.bind(Map("value" -> "not a boolean"))
      result.errors must contain(FormError("value", "error.boolean"))
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind" in {
      val result = testForm.fill(true)
      result.apply("value").value.value mustEqual "true"
    }
  }

  "int" must {

    val testForm: Form[Int] =
      Form(
        "value" -> int()
      )

    "bind a valid integer" in {
      val result = testForm.bind(Map("value" -> "1"))
      result.get mustEqual 1
    }

    "not bind an empty value" in {
      val result = testForm.bind(Map("value" -> ""))
      result.errors must contain(FormError("value", "error.required"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind a valid value" in {
      val result = testForm.fill(123)
      result.apply("value").value.value mustEqual "123"
    }
  }

  "enumerable" must {

    val testForm = Form(
      "value" -> enumerable[Foo]()
    )

    "bind a valid option" in {
      val result = testForm.bind(Map("value" -> "Bar"))
      result.get mustEqual Bar
    }

    "not bind an invalid option" in {
      val result = testForm.bind(Map("value" -> "Not Bar"))
      result.errors must contain(FormError("value", "error.invalid"))
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }
  }

  "currency" must {
    val testForm = Form(
      "value" -> currency()
    )

    "bind a valid option with no decimals" in {
    val result = testForm.bind(Map("value" -> "123"))
    result.get mustEqual BigDecimal.valueOf(123)
    }

    "bind a valid option with decimals" in {
    val result = testForm.bind(Map("value" -> "123.12"))
    result.get mustEqual BigDecimal.valueOf(123.12)
    }

    "bind a valid option with a pound sign at the start" in {
      val result = testForm.bind(Map("value" -> "£123.12"))
      result.get mustEqual BigDecimal.valueOf(123.12)
    }

    "bind a valid option with spaces" in {
      val result = testForm.bind(Map("value" -> "£123   12 2"))
      result.get mustEqual BigDecimal.valueOf(123122)
    }

    "bind a valid option with commas" in {
      val result = testForm.bind(Map("value" -> "£1,1,123.12"))
      result.get mustEqual BigDecimal.valueOf(11123.12)
    }

    "bind a valid option with a pound sign at the end" in {
      val result = testForm.bind(Map("value" -> "123.12£"))
      result.get mustEqual BigDecimal.valueOf(123.12)
    }

    "successfully sanitise input with commas" in {
      val result = testForm.bind(Map("value" -> "£,123,"))
      result.get mustEqual BigDecimal.valueOf(123)
    }

    "fail to bind invalid input with multiple decimal points" in {
      val result = testForm.bind(Map("value" -> "£123.12.12"))
      result.errors must contain(FormError("value", "error.invalidNumeric"))
    }
    "fail to bind invalid input with pound sign on both ends" in {
      val result = testForm.bind(Map("value" -> "£123.12£"))
      result.errors must contain(FormError("value", "error.invalidNumeric"))
    }
    "successfully bind the highest number" in {
      val result = testForm.bind(Map("value" -> "9999999.99"))
      result.get mustEqual BigDecimal.valueOf(9999999.99)
    }

    "not bind an empty map" in {
      val result = testForm.bind(Map.empty[String, String])
      result.errors must contain(FormError("value", "error.required"))
    }

    "unbind a valid value" in {
      val result = testForm.fill(BigDecimal.valueOf(12.12))
      result.apply("value").value.value mustEqual "12.12"
    }
  }
}
