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

import controllers.operationsAndFunds.{routes => operations}
import models.{CheckMode, UserAnswers}
import pages.operationsAndFunds._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class CharityObjectivesSummaryHelper(override val userAnswers: UserAnswers)
                                    (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
    with SummaryListRowHelper {

    def charitableObjectivesRow: Option[SummaryListRow] =
      answer(CharitableObjectivesPage, operations.CharitableObjectivesController.onPageLoad(CheckMode))

    def charitablePurposesRow: Option[SummaryListRow] =
      multiLineAnswer(CharitablePurposesPage, operations.CharitablePurposesController.onPageLoad(CheckMode))

    //TODO Add other Charitable Purposes row once page is created

    def publicBenefitsRow: Option[SummaryListRow] =
      answer(PublicBenefitsPage, operations.PublicBenefitsController.onPageLoad(CheckMode))

    val rows: Seq[SummaryListRow] = Seq(
      charitableObjectivesRow,
      charitablePurposesRow,
      publicBenefitsRow
    ).flatten

  }
