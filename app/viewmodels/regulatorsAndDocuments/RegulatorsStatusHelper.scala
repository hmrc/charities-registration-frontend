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

package viewmodels.regulatorsAndDocuments

import models.UserAnswers
import models.regulators.CharityRegulator
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Scottish}
import models.regulators.SelectWhyNoRegulator.Other
import pages.QuestionPage
import pages.regulatorsAndDocuments._
import viewmodels.StatusHelper

import scala.collection.mutable.ListBuffer

object RegulatorsStatusHelper extends StatusHelper {

  override def checkComplete(userAnswers: UserAnswers): Boolean = {

    val allPages: Seq[QuestionPage[_]] = Seq(
      IsCharityRegulatorPage, CharityRegulatorPage,
      CharityCommissionRegistrationNumberPage,
      ScottishRegulatorRegNumberPage,
      NIRegulatorRegNumberPage,
      CharityOtherRegulatorDetailsPage,
      SelectWhyNoRegulatorPage, WhyNotRegisteredWithCharityPage
    )

    val requiredPairs: Map[CharityRegulator, QuestionPage[_]] = Map(
      (EnglandWales, CharityCommissionRegistrationNumberPage),
      (Scottish, ScottishRegulatorRegNumberPage),
      (NorthernIreland, NIRegulatorRegNumberPage),
      (models.regulators.CharityRegulator.Other, CharityOtherRegulatorDetailsPage)
    )

    lazy val pagesNeeded: ListBuffer[QuestionPage[_]] = ListBuffer(IsCharityRegulatorPage)

    val correctJourneyFound: Boolean = userAnswers.get(IsCharityRegulatorPage) match {
      case Some(true) =>
        pagesNeeded += CharityRegulatorPage
        userAnswers.get(CharityRegulatorPage) match {
          case Some(regulatorSet) if regulatorSet.nonEmpty =>
            regulatorSet.forall(regulator => {
              pagesNeeded += requiredPairs(regulator)
              userAnswers.arePagesDefined(Seq(requiredPairs(regulator)))
            })
          case _ => false
        }

      case Some(false) =>
        pagesNeeded += SelectWhyNoRegulatorPage
        userAnswers.get(SelectWhyNoRegulatorPage) match {
          case Some(value) if value.equals(Other) =>
            pagesNeeded += WhyNotRegisteredWithCharityPage
            userAnswers.arePagesDefined(Seq(WhyNotRegisteredWithCharityPage))
          case Some(_) =>
            true
          case _ => false
        }

      case _ => false
    }

    correctJourneyFound && userAnswers.unneededPagesNotPresent(pagesNeeded, allPages)
  }
}
