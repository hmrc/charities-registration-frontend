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

package viewmodels.contact

import models.{CharityName, CheckMode}
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import utils.{CurrencyFormatter, ImplicitDateFormatter}
import viewmodels.SummaryListRowHelper

class CharityNameCheckYourAnswersHelper() extends ImplicitDateFormatter
  with SummaryListRowHelper with CurrencyFormatter {

  def rows(charityName: CharityName)( implicit messages: Messages): Seq[SummaryListRow] = Seq(

    Some(
      summaryListRow(
        label = messages("charityName.fullName.checkYourAnswersLabel"),
        value = charityName.fullName,
        visuallyHiddenText = Some(messages("charityName.fullName.checkYourAnswersLabel")),
        controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode) -> messages("site.edit")
      )
    ),

    charityName.operatingName.map( name =>
      summaryListRow(
        label = messages("charityName.operatingName.checkYourAnswersLabel"),
        value = name,
        visuallyHiddenText = Some(messages("charityName.operatingName.checkYourAnswersLabel")),
        controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode) -> messages("site.edit")
      )
    )
  ).flatten

}
