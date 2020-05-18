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

package forms.charityInformation

import forms.behaviours.StringFieldBehaviours
import models.CharityName
import play.api.data.FormError

class CharityNameFormProviderSpec extends StringFieldBehaviours {

  val maxLength = 160

//  val form = new CharityNameFormProvider()()
val formProvider = new CharityNameFormProvider()
  val form = formProvider()


  ".fullName" must {

    val requiredKey = "charityName.fullName.error.required"
    val lengthKey = "charityName.fullName.error.length"


    val fieldName = "fullName"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )
  }

  ".operatingName" must {

    val fieldName = "operatingName"

    val lengthKey = "charityName.operatingName.error.length"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      stringsWithMaxLength(maxLength)
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )
  }

  "CharityNameFormProvider" must {

    val charityName = CharityName("CName", Some("OpName"))

    "apply CharityName correctly" in {

      val details = form.bind(
        Map(
          "fullName" -> charityName.fullName,
          "operatingName" -> charityName.operatingName.getOrElse("")
        )
      ).get

      details.fullName mustBe charityName.fullName
      details.operatingName mustBe charityName.operatingName
    }

    "unapply CharityName correctly" in {
      val filled = form.fill(charityName)
      filled("fullName").value.value mustBe charityName.fullName
      filled("operatingName").value.value mustBe charityName.operatingName.get
    }
  }

  "fullName" must {

    "valid for CName" in {

      "CName" must fullyMatch regex formProvider.validateFields
    }

    "valid for CName&" in {

      "CName&" mustNot fullyMatch regex formProvider.validateFields
    }
  }

  "operatingName" must {

    "valid for OpName" in {

      "OpName" must fullyMatch regex formProvider.validateFields
    }

    "valid for OpName&" in {

      "OpName&" mustNot fullyMatch regex formProvider.validateFields
    }
  }
}
