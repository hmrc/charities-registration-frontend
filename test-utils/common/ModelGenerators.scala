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
}
