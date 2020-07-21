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
import models.{CheckMode, Index, UserAnswers}
import pages.authorisedOfficials._
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.ImplicitDateFormatter
import viewmodels.{CheckYourAnswersHelper, SummaryListRowHelper}

class AuthorisedOfficialsSummaryHelper(index: Index)(override val userAnswers: UserAnswers)
                                      (implicit val messages: Messages) extends ImplicitDateFormatter with CheckYourAnswersHelper
  with SummaryListRowHelper {

  val addedOneAuthorisedOfficial = new AddedOneAuthorisedOfficialHelper(index)(userAnswers)

  def isAddAnotherAuthorisedOfficialRows: List[SummaryListRow] = answer(IsAddAnotherAuthorisedOfficialPage,
    authOfficialRoutes.IsAddAnotherAuthorisedOfficialController.onPageLoad(CheckMode)).fold(List[SummaryListRow]())(List(_))

  val rows: Seq[SummaryListRow] = addedOneAuthorisedOfficial.rows ++ isAddAnotherAuthorisedOfficialRows

}
