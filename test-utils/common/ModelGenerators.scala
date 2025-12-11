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

package common

import org.scalacheck.Gen
import uk.gov.hmrc.domain.NinoGenerator
import com.google.i18n.phonenumbers.PhoneNumberUtil
import com.google.i18n.phonenumbers.PhoneNumberUtil.PhoneNumberFormat

trait ModelGenerators {
  private val ninoGenerator: NinoGenerator = NinoGenerator()

  def ninoGen: Gen[String] =
    Gen.const(ninoGenerator.nextNino.nino)

  def accountNameGen: Gen[String] =
    Gen.stringOfN(20, Gen.alphaNumChar)

  def sortCodeGen: Gen[String] =
    Gen.stringOfN(6, Gen.numChar)

  def accountNumberGen: Gen[String] =
    Gen.stringOfN(8, Gen.numChar)

  def rollNumberGen: Gen[String] =
    Gen.stringOfN(14, Gen.numChar)

  def passportGen: Gen[String] =
    Gen.stringOfN(9, Gen.numChar)

  def charityRegistrationGen: Gen[String] =
    Gen.stringOfN(6, Gen.numChar)

  def charityRegulatorRegistrationGen: Gen[String] =
    Gen.stringOfN(6, Gen.numChar)

  def acknowledgementRefGen: Gen[String] =
    Gen.stringOfN(7, Gen.numChar)

  private val phoneUtil: PhoneNumberUtil = PhoneNumberUtil.getInstance()
  private val region    = "GB"

  private def format(
      number: com.google.i18n.phonenumbers.Phonenumber.PhoneNumber,
      fmt: PhoneNumberFormat
    ): String =
    phoneUtil.format(number, fmt)


  def exampleFixedLineGen: Gen[String] =
    Gen.const {
      val num = phoneUtil.getExampleNumberForType(
        region,
        PhoneNumberUtil.PhoneNumberType.FIXED_LINE
      )
      format(num, PhoneNumberFormat.NATIONAL)
    }

  def exampleFixedLineIntGen: Gen[String] =
    exampleFixedLineGen.map { national =>
      val parsed = phoneUtil.parse(national, region)
      format(parsed, PhoneNumberFormat.E164)
    }

  def exampleMobileIntGen: Gen[String] =
    exampleMobileGen.map { national =>
      val parsed = phoneUtil.parse(national, region)
      format(parsed, PhoneNumberFormat.E164)
    }

  def exampleMobileGen: Gen[String] =
    Gen.const {
      val num = phoneUtil.getExampleNumberForType(
        region,
        PhoneNumberUtil.PhoneNumberType.MOBILE
      )
      format(num, PhoneNumberFormat.NATIONAL)
    }

//  def phoneNumbersGen: Gen[PhoneNumber] =
//    for {
//      daytime <- exampleFixedLineGen
//      mobile <- exampleMobileGen
//    } yield PhoneNumber(
//      daytimePhone = daytime,
//      mobilePhone = Some(mobile)
//    )
//
//  def phoneNumbersWithIntCodeGen: Gen[PhoneNumber] =
//    for {
//      daytime <- exampleFixedLineIntGen
//      mobile <- exampleMobileIntGen
//    } yield PhoneNumber(
//      daytimePhone = daytime,
//      mobilePhone = Some(mobile)
//    )

}
