/*
 * Copyright 2022 HM Revenue & Customs
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

package forms.common

import forms.behaviours.StringFieldBehaviours
import models.BankDetails
import org.scalacheck.Gen
import play.api.data.{Form, FormError}

class BankDetailsFormProviderSpec extends StringFieldBehaviours {

  val messagePrefix: String = "individualNomineesBankDetails"
  private val formProvider: BankDetailsFormProvider = inject[BankDetailsFormProvider]
  private val form: Form[BankDetails] = formProvider(messagePrefix)

  ".accountName" must {

    val fieldName = "accountName"
    val maxLength = 60
    val requiredKey = s"$messagePrefix.accountName.error.required"
    val lengthKey = s"$messagePrefix.accountName.error.length"
    val invalidKey = s"$messagePrefix.accountName.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
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

    behave like fieldWithRegex(
      form,
      fieldName,
      "()invalidName",
      FormError(fieldName, invalidKey, Seq(formProvider.validateFieldWithFullStop))
    )
  }

  ".sortCode" must {

    val fieldName = "sortCode"
    val requiredKey = s"$messagePrefix.sortCode.error.required"
    val invalidKey = s"$messagePrefix.sortCode.error.format"

    val validSortCodeGen = for {
      firstDigits     <- Gen.listOfN(2, Gen.numChar).map(_.mkString)
      firstSeparator <- Gen.oneOf(' ', '-').map(_.toString)
      secondDigits    <- Gen.listOfN(2, Gen.numChar).map(_.mkString)
      secondSeparator <- Gen.oneOf(' ', '-').map(_.toString)
      thirdDigits     <- Gen.listOfN(2, Gen.numChar).map(_.mkString)
    } yield s"$firstDigits$firstSeparator$secondDigits$secondSeparator$thirdDigits"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validSortCodeGen
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "123456a",
      FormError(fieldName, invalidKey, Seq(formProvider.sortCodePattern))
    )

    "bind sort codes in nnnnnn format" in {
      val result = form.bind(Map(fieldName -> "123456")).apply(fieldName)
      result.value.value mustBe "123456"
    }

    "bind sort codes in nn-nn-nn format" in {
      val result = form.bind(Map(fieldName -> "12-34-56")).apply(fieldName)
      result.value.value mustBe "12-34-56"
    }

    "bind sort codes in nn nn nn format" in {
      val result = form.bind(Map(fieldName -> "12 34 56")).apply(fieldName)
      result.value.value mustBe "12 34 56"
    }

    "bind sort codes in nn   nn    nn format" in {
      val result = form.bind(Map(fieldName -> "12   34   56")).apply(fieldName)
      result.value.value mustBe "12   34   56"
    }

    "not bind sort codes with characters" in {
      val result = form.bind(Map(fieldName -> "abcdef")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey, Seq(formProvider.sortCodePattern))
      result.errors.head.key mustEqual expectedError.key
    }

    "not bind sort codes with less than 6 digit" in {
      val result = form.bind(Map(fieldName -> "12   34  5")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey, Seq(formProvider.sortCodePattern))
      result.errors.head.key mustEqual expectedError.key
    }

    "not bind sort codes with more than 6 digit" in {
      val result = form.bind(Map(fieldName -> "12   34  5678")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey, Seq(formProvider.sortCodePattern))
      result.errors.head.key mustEqual expectedError.key
    }
  }

  ".accountNumber" must {

    val fieldName = "accountNumber"
    val requiredKey = s"$messagePrefix.accountNumber.error.required"
    val invalidKey = s"$messagePrefix.accountNumber.error.format"
    val minLength = 6
    val maxLength = 8

    val validAccountNumberGen = for {
      length <- Gen.choose(minLength, maxLength)
      digits <- Gen.listOfN(length, Gen.numChar)
    } yield digits.mkString

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      validAccountNumberGen
    )

    behave like mandatoryField(
      form,
      fieldName,
      requiredError = FormError(fieldName, requiredKey)
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "12345678?",
      FormError(fieldName, invalidKey, Seq(formProvider.accountNumberPattern))
    )

    "bind account number in format with any number of spaces nn   nn    nn format" in {
      val result = form.bind(Map(fieldName -> "12   34   56")).apply(fieldName)
      result.value.value mustBe "12   34   56"
    }

    "not bind strings with characters" in {
      val result = form.bind(Map(fieldName -> "abcdef")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey, Seq(formProvider.accountNumberPattern))
      result.errors.head.key mustEqual expectedError.key
    }

    "not bind strings with less than 6 digit" in {
      val result = form.bind(Map(fieldName -> "12 34   5")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey, Seq(formProvider.accountNumberPattern))
      result.errors.head.key mustEqual expectedError.key
    }

    "not bind strings with more than 8 digit" in {
      val result = form.bind(Map(fieldName -> "12 34 56 789")).apply(fieldName)
      val expectedError = FormError(fieldName, invalidKey, Seq(formProvider.accountNumberPattern))
      result.errors.head.key mustEqual expectedError.key
    }
  }

  ".rollNumber" must {

    val maxLength = 18
    val fieldName = "rollNumber"
    val lengthKey = s"$messagePrefix.rollNumber.error.length"
    val invalidKey = s"$messagePrefix.rollNumber.error.format"

    behave like fieldThatBindsValidData(
      form,
      fieldName,
      nonEmptyString
    )

    behave like fieldWithMaxLength(
      form,
      fieldName,
      maxLength = maxLength,
      lengthError = FormError(fieldName, lengthKey, Seq(maxLength))
    )

    behave like fieldWithRegex(
      form,
      fieldName,
      "roll-Number, .!?",
      FormError(fieldName, invalidKey, Seq(formProvider.rollNumberPattern))
    )

    "bind roll number for string haveing letters a to z, numbers, hyphens, spaces and full stops" in {
      val result = form.bind(Map(fieldName -> "roll-Number, .")).apply(fieldName)
      result.value.value mustBe "roll-Number, ."
    }

    "not bind strings with characters any other character apart from letters a to z, numbers, hyphens, spaces and full stops" in {
      val result = form.bind(Map(fieldName -> "roll-Number, .!?")).apply(fieldName)
      val expectedError = FormError(fieldName, s"$messagePrefix.error.accountNumber.invalid", Seq(formProvider.accountNumberPattern))
      result.errors.head.key mustEqual expectedError.key
    }

    "not bind strings with more than 18 characters" in {
      val result = form.bind(Map(fieldName -> "01234567890123456789")).apply(fieldName)
      val expectedError = FormError(fieldName, s"$messagePrefix.error.accountNumber.invalid", Seq(formProvider.accountNumberPattern))
      result.errors.head.key mustEqual expectedError.key
    }
  }

  "BankDetailsFormProvider" must {

    val bankDetails = BankDetails(
      accountName = "fullName",
      sortCode = "123456",
      accountNumber = "12345678",
      rollNumber = Some("rollNumber")
    )

    "apply BankDetails correctly" in {

      val details = form.bind(
        Map(
          "accountName" -> bankDetails.accountName,
          "sortCode" -> bankDetails.sortCode,
          "accountNumber" -> bankDetails.accountNumber,
          "rollNumber" -> bankDetails.rollNumber.getOrElse("")
        )
      ).get

      details.accountName mustBe bankDetails.accountName
      details.sortCode mustBe bankDetails.sortCode
      details.accountNumber mustBe bankDetails.accountNumber
      details.rollNumber mustBe bankDetails.rollNumber
    }

    "unapply BankDetails correctly" in {
      val filled = form.fill(bankDetails)
      filled("accountName").value.value mustBe bankDetails.accountName
      filled("sortCode").value.value mustBe bankDetails.sortCode
      filled("accountNumber").value.value mustBe bankDetails.accountNumber
      filled("rollNumber").value.value mustBe bankDetails.rollNumber.get
    }
  }

  "BankDetailsFormProvider with charity Name" must {

    val bankDetails = BankDetails(
      accountName = "fullName",
      sortCode = "123456",
      accountNumber = "12345678",
      rollNumber = Some("rollNumber")
    )

    val form: Form[BankDetails] = formProvider(messagePrefix, "fullName")

    "apply BankDetails correctly" in {

      val details = form.bind(
        Map(
          "sortCode" -> bankDetails.sortCode,
          "accountNumber" -> bankDetails.accountNumber,
          "rollNumber" -> bankDetails.rollNumber.getOrElse("")
        )
      ).get

      details.accountName mustBe bankDetails.accountName
      details.sortCode mustBe bankDetails.sortCode
      details.accountNumber mustBe bankDetails.accountNumber
      details.rollNumber mustBe bankDetails.rollNumber
    }

    "unapply BankDetails correctly" in {
      val filled = form.fill(bankDetails)
      filled("accountName").value.value mustBe bankDetails.accountName
      filled("sortCode").value.value mustBe bankDetails.sortCode
      filled("accountNumber").value.value mustBe bankDetails.accountNumber
      filled("rollNumber").value.value mustBe bankDetails.rollNumber.get
    }
  }

  "accountName" must {

    "valid for accountName" in {

      "accountName" must fullyMatch regex formProvider.validateField
    }

    "valid for accountName&" in {

      "accountName&" mustNot fullyMatch regex formProvider.validateField
    }
  }

  "sortCode" must {

    "valid for sortCode" in {

      "123456" must fullyMatch regex formProvider.sortCodePattern
    }

    "valid for 1234567" in {

      "1234567" mustNot fullyMatch regex formProvider.sortCodePattern
    }
  }

  "accountNumber" must {

    "valid for sortCode" in {

      "12345678" must fullyMatch regex formProvider.accountNumberPattern
    }

    "invalid for 12345678a" in {

      "12345678a" mustNot fullyMatch regex formProvider.accountNumberPattern
    }
  }

  "rollNumber" must {

    "valid for roll-Number, ." in {

      "roll-Number /" must fullyMatch regex formProvider.rollNumberPattern
    }

    "invalid for roll-Number, .!" in {

      "roll-Number, .!" mustNot fullyMatch regex formProvider.rollNumberPattern
    }
  }
}
