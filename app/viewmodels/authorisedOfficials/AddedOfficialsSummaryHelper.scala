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

package viewmodels.authorisedOfficials

import controllers.authorisedOfficials.{routes => authOfficialRoutes}
import controllers.otherOfficials.{routes => otherOfficialRoutes}
import models.{CheckMode, Index, Mode, UserAnswers}
import pages.authorisedOfficials._
import pages.otherOfficials._
import play.api.i18n.Messages
import service.CountryService
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.otherOfficials.AddedOneOtherOfficialHelper
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class AddedOfficialsSummaryHelper(index: Index, mode: Mode = CheckMode, countryService: CountryService)(override val userAnswers: UserAnswers)
                                 (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  val addedOneAuthorisedOfficial = new AddedOneAuthorisedOfficialHelper(index, CheckMode, countryService)(userAnswers)
  val addedOneOtherOfficial = new AddedOneOtherOfficialHelper(index, CheckMode)(userAnswers)

  def isAddAnotherAuthorisedOfficialRow: Option[SummaryListRow] =
    answerPrefix(IsAddAnotherAuthorisedOfficialPage,
                 authOfficialRoutes.IsAddAnotherAuthorisedOfficialController.onPageLoad(mode),
                 messagePrefix = "isAddAnotherAuthorisedOfficial")

  def addedAnotherOtherOfficialRow: Option[SummaryListRow] =
    answerPrefix(AddAnotherOtherOfficialPage,
                 otherOfficialRoutes.AddAnotherOtherOfficialController.onPageLoad(mode),
                 messagePrefix = "addAnotherOtherOfficial")

  val authorisedRows: Seq[SummaryListRow] = addedOneAuthorisedOfficial.rows ++ isAddAnotherAuthorisedOfficialRow
  val authorisedRowsAddAnother: Seq[SummaryListRow] = addedOneAuthorisedOfficial.rows
  val otherRows: Seq[SummaryListRow] = addedOneOtherOfficial.rows
  val otherRowsAddAnother: Seq[SummaryListRow] = addedOneOtherOfficial.rows ++ addedAnotherOtherOfficialRow
}
