/*
 * Copyright 2021 HM Revenue & Customs
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

package viewmodels.regulatorsAndDocuments

import models.UserAnswers
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Scottish}
import models.regulators.SelectWhyNoRegulator.Other
import models.regulators.{CharityRegulator, SelectWhyNoRegulator}
import pages.QuestionPage
import pages.regulatorsAndDocuments._
import viewmodels.StatusHelper

object RegulatorsStatusHelper extends StatusHelper {

  private val allPages: Seq[QuestionPage[_]] = Seq(
    IsCharityRegulatorPage, CharityRegulatorPage,
    CharityCommissionRegistrationNumberPage,
    ScottishRegulatorRegNumberPage,
    NIRegulatorRegNumberPage,
    CharityOtherRegulatorDetailsPage,
    SelectWhyNoRegulatorPage, WhyNotRegisteredWithCharityPage
  )

  implicit class RegulatorsStatus(list: Seq[QuestionPage[_]]) {

    private val requiredPairs: Map[CharityRegulator, QuestionPage[_]] = Map(
      (EnglandWales, CharityCommissionRegistrationNumberPage),
      (Scottish, ScottishRegulatorRegNumberPage),
      (NorthernIreland, NIRegulatorRegNumberPage),
      (models.regulators.CharityRegulator.Other, CharityOtherRegulatorDetailsPage)
    )

    def withCharityRegulator: Seq[QuestionPage[_]] = list ++ Seq(CharityRegulatorPage)

    def withSelectWhyNoRegulator: Seq[QuestionPage[_]] = list ++ Seq(SelectWhyNoRegulatorPage)

    def withNotRegisteredCause(cause: SelectWhyNoRegulator): Seq[QuestionPage[_]] = {
      if (cause.equals(Other)) {
        list ++ Seq(WhyNotRegisteredWithCharityPage)
      } else {
        list
      }
    }

    def withRegulators(regulators: Set[CharityRegulator]): Seq[QuestionPage[_]] =
      list ++ regulators.map(regulator => requiredPairs(regulator))

  }

  override def checkComplete(userAnswers: UserAnswers): Boolean = {


    lazy val common: Seq[QuestionPage[_]] = Seq(IsCharityRegulatorPage)

    userAnswers.get(IsCharityRegulatorPage) match {
      case Some(true) =>
        userAnswers.get(CharityRegulatorPage) match {
          case Some(regulatorSet) if regulatorSet.nonEmpty =>
            val pagesInJourney = common.withCharityRegulator.withRegulators(regulatorSet)
            userAnswers.arePagesDefined(pagesInJourney) && userAnswers.unneededPagesNotPresent(pagesInJourney, allPages)
          case _ => false
        }

      case Some(false) =>
        userAnswers.get(SelectWhyNoRegulatorPage) match {
          case Some(causeForNotRegistered) =>
            val pagesInJourney = common.withSelectWhyNoRegulator.withNotRegisteredCause(causeForNotRegistered)
            userAnswers.arePagesDefined(pagesInJourney) && userAnswers.unneededPagesNotPresent(pagesInJourney, allPages)
          case _ => false
        }

      case _ => false
    }

  }
}
