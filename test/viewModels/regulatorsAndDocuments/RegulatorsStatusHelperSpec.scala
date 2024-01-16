/*
 * Copyright 2024 HM Revenue & Customs
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

package viewModels.regulatorsAndDocuments

import base.SpecBase
import models.CharityOtherRegulatorDetails
import models.regulators.CharityRegulator
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Other, Scottish}
import models.regulators.SelectWhyNoRegulator.EnglandWalesUnderThreshold
import pages.regulatorsAndDocuments._
import viewmodels.regulatorsAndDocuments.RegulatorsStatusHelper

class RegulatorsStatusHelperSpec extends SpecBase {

  "RegulatorsStatusHelper" must {
    "go complete if form submitted with regulator, all but other, with valid data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland)))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "dummyEnglishCharity"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "dummyScottishCharity"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "dummyNICharity"))
          .success
          .value
      )

      result mustBe true
    }

    "go complete if form submitted with regulator, all with other, with valid data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other)))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "dummyEnglishCharity"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "dummyScottishCharity"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "dummyNICharity"))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("dummyOtherCharity", "dummyOtherCharity")
            )
          )
          .success
          .value
      )

      result mustBe true
    }

    "go complete if form submitted with regulator, only one without other, with valid data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "dummyScottishCharity"))
          .success
          .value
      )

      result mustBe true
    }

    "go complete if form submitted with regulator, only other, with valid data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("dummyOtherCharity", "dummyOtherCharity")
            )
          )
          .success
          .value
      )

      result mustBe true
    }

    "go complete if form submitted without regulator, no other, with valid data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, EnglandWalesUnderThreshold))
          .success
          .value
      )

      result mustBe true
    }

    "go complete if form submitted without regulator, with other, with valid data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, models.regulators.SelectWhyNoRegulator.Other))
          .flatMap(_.set(WhyNotRegisteredWithCharityPage, "dummy reason"))
          .success
          .value
      )

      result mustBe true
    }

    "go incomplete if form submitted with regulator, all but other, with missing data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland)))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "dummyEnglishCharity"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "dummyNICharity"))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted with regulator, all with other, with additional data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](EnglandWales, Scottish, NorthernIreland, Other)))
          .flatMap(_.set(CharityCommissionRegistrationNumberPage, "dummyEnglishCharity"))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "dummyScottishCharity"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "dummyNICharity"))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("dummyOtherCharity", "dummyOtherCharity")
            )
          )
          .flatMap(_.set(SelectWhyNoRegulatorPage, EnglandWalesUnderThreshold))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted with regulator, empty set, with missing data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator]()))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted with regulator, only one without other, with missing data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted with regulator, only one without other, with additional data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Scottish)))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "dummyScottishCharity"))
          .flatMap(_.set(NIRegulatorRegNumberPage, "dummyNICharity"))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted with regulator, only other, with missing data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted with regulator, only other, with additional data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, true)
          .flatMap(_.set(CharityRegulatorPage, Set[CharityRegulator](Other)))
          .flatMap(_.set(ScottishRegulatorRegNumberPage, "dummyScottishCharity"))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("dummyOtherCharity", "dummyOtherCharity")
            )
          )
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted without regulator, no other, with missing data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers.set(IsCharityRegulatorPage, false).success.value
      )

      result mustBe false
    }

    "go incomplete if form submitted without regulator, no other, with additional data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, EnglandWalesUnderThreshold))
          .flatMap(_.set(WhyNotRegisteredWithCharityPage, "dummy reason"))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted without regulator, with other, with missing data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, models.regulators.SelectWhyNoRegulator.Other))
          .success
          .value
      )

      result mustBe false
    }

    "go incomplete if form submitted without regulator, with other, with additional data" in {
      val result = RegulatorsStatusHelper.checkComplete(
        emptyUserAnswers
          .set(IsCharityRegulatorPage, false)
          .flatMap(_.set(SelectWhyNoRegulatorPage, models.regulators.SelectWhyNoRegulator.Other))
          .flatMap(_.set(WhyNotRegisteredWithCharityPage, "dummy reason"))
          .flatMap(
            _.set(
              CharityOtherRegulatorDetailsPage,
              CharityOtherRegulatorDetails("dummyOtherCharity", "dummyOtherCharity")
            )
          )
          .success
          .value
      )

      result mustBe false
    }
  }
}
