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

package viewmodels

import base.SpecBase
import models.requests.DataRequest
import models.{CharityContactDetails, UserAnswers}
import pages.checkEligibility.IsEligiblePurposePage
import play.api.i18n.Messages
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._

import scala.collection.mutable.ArrayBuffer

class CheckYourAnswersHelperSpec extends SpecBase with SummaryListRowHelper{

  implicit val localMessages: Messages = messages

  def helper(ua: UserAnswers = emptyUserAnswers.set(IsEligiblePurposePage, true).success.value): CheckYourAnswersHelper = new CheckYourAnswersHelper{
    override val request: DataRequest[_] = fakeDataRequest
    override implicit val messages: Messages = localMessages
    override val userAnswers: UserAnswers = ua
  }

  "CheckYourAnswersHelper" must {

    "return correct summary row when called with boolean Page Id" in {

      helper().answer[Boolean](IsEligiblePurposePage, controllers.checkEligibility.routes.IsEligiblePurposeController.onPageLoad())(
        implicitly, implicitly, (x : Boolean)=> x.toString) mustBe Some(SummaryListRow(
        Key(Text("Is your charity for charitable purposes only?"),"govuk-!-width-one-third"),
        Value(HtmlContent("true"),"govuk-!-width-one-third"), "",Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.checkEligibility.routes.IsEligiblePurposeController.onPageLoad().url,
            Text("Change"),Some("Is your charity for charitable purposes only?")))))))

    }

    "return None when called with boolean Page Id and empty user anser" in {

      helper(emptyUserAnswers).answer[Boolean](IsEligiblePurposePage, controllers.checkEligibility.routes.IsEligiblePurposeController.onPageLoad())(
        implicitly, implicitly, (x : Boolean)=> x.toString) mustBe None

    }
  }
}
