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

package viewmodels.operationsAndFunds

import models.UserAnswers
import pages.QuestionPage
import pages.operationsAndFunds.{CharitableObjectivesPage, CharitablePurposesPage, PublicBenefitsPage}
import viewmodels.StatusHelper

object CharityObjectivesStatusHelper extends StatusHelper {

    private val allPages: Seq[QuestionPage[_]] = Seq(
      CharitableObjectivesPage,
      CharitablePurposesPage,
      PublicBenefitsPage
    )

  override def checkComplete(userAnswers: UserAnswers): Boolean = {
    userAnswers.arePagesDefined(allPages)
  }
}
