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

package viewModels.operationsAndFunds

import controllers.operationsAndFunds.{routes => operations}
import models.{CheckMode, UserAnswers}
import pages.operationsAndFunds.{CharitableObjectivesPage, CharitablePurposesPage, PublicBenefitsPage}
import assets.messages.BaseMessages
import base.SpecBase
import models.operations.CharitablePurposes
import viewmodels.SummaryListRowHelper
import viewmodels.operationsAndFunds.CharityObjectivesSummaryHelper

class CharityObjectivesSummaryHelperSpec extends SpecBase with SummaryListRowHelper {


  private val helper = new CharityObjectivesSummaryHelper(UserAnswers("id")
    .set(CharitableObjectivesPage, "Charitable Objectives").flatMap
  (_.set(CharitablePurposesPage, CharitablePurposes.values.toSet)).flatMap
  (_.set(PublicBenefitsPage,"Public Benefits")).success.value
  )

  private val amateurSport = CharitablePurposes.AmateurSport
  private val animalWelfare = CharitablePurposes.AnimalWelfare
  private val artsCultureOrScience = CharitablePurposes.ArtsCultureHeritageScience
  private val citizenshipOrCommunityDevelopment = CharitablePurposes.CitizenshipCommunity
  private val education = CharitablePurposes.Education
  private val environmentalProtection = CharitablePurposes.EnvironmentalProtection
  private val healthOrSavingOfLives = CharitablePurposes.Health
  private val humanRights = CharitablePurposes.HumanRights
  private val armedForcesOfTheCrown = CharitablePurposes.PromotionOfEfficiency
  private val reliefOfPoverty = CharitablePurposes.ReliefOfPoverty
  private val reliefOfYouthAge = CharitablePurposes.ReliefOfThoseInNeed
  private val religion = CharitablePurposes.Religion
  private val other = CharitablePurposes.Other

  "Check your answers helper" must {

    "For the Charitable Objectives answer" must {

      "have a correctly formatted summary list row" in {

        helper.charitableObjectivesRow mustBe Some(
          summaryListRow(
            messages("charitableObjectives.checkYourAnswersLabel"),
            "Charitable Objectives",
            Some(messages("charitableObjectives.checkYourAnswersLabel")),
            operations.CharitableObjectivesController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    "For the Charitable Purposes answer" must {

      "have a correctly formatted summary list row" in {

        val purposeList =
          s"""<div>${messages(s"charitablePurposes.$other")}</div><div>${messages(
            s"charitablePurposes.$armedForcesOfTheCrown")}</div><div>${messages(s"charitablePurposes.$animalWelfare")}</div><div>${messages(
            s"charitablePurposes.$religion")}</div><div>${messages(s"charitablePurposes.$humanRights")}</div><div>${messages(
            s"charitablePurposes.$healthOrSavingOfLives")}</div><div>${messages(s"charitablePurposes.$citizenshipOrCommunityDevelopment")}</div><div>${messages(
            s"charitablePurposes.$environmentalProtection")}</div><div>${messages(s"charitablePurposes.$reliefOfPoverty")}</div><div>${messages(
            s"charitablePurposes.$artsCultureOrScience")}</div><div>${messages(s"charitablePurposes.$education")}</div><div>${messages(
            s"charitablePurposes.$reliefOfYouthAge")}</div><div>${messages(s"charitablePurposes.$amateurSport")}</div>"""
            .stripMargin

        helper.charitablePurposesRow mustBe Some(
          summaryListRow(
            messages("charitablePurposes.checkYourAnswersLabel"),
            purposeList,
            Some(messages("charitablePurposes.checkYourAnswersLabel")),
            operations.CharitablePurposesController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }

    //TODO Add other Charitable Purposes row once page is created

    "For the Public Benefits answer" must {

      "have a correctly formatted summary list row" in {

        helper.publicBenefitsRow mustBe Some(
          summaryListRow(
            messages("publicBenefits.checkYourAnswersLabel"),
            "Public Benefits",
            Some(messages("publicBenefits.checkYourAnswersLabel")),
            operations.PublicBenefitsController.onPageLoad(CheckMode) -> BaseMessages.changeLink
          )
        )
      }
    }
  }
}
