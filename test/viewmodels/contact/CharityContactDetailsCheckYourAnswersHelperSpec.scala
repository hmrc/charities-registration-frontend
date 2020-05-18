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

import base.SpecBase
import models.{CharityContactDetails, CheckMode}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.SummaryListRowHelper

import scala.collection.mutable.ArrayBuffer

class CharityContactDetailsCheckYourAnswersHelperSpec extends SpecBase with SummaryListRowHelper{

  val helper: CharityContactDetailsCheckYourAnswersHelper = inject[CharityContactDetailsCheckYourAnswersHelper]

  val charityContactDetails = CharityContactDetails("07700 900 982", Some("07700 900 982"), emailAddress = "abc@gmail.com")

  "CharityContactDetailsCheckYourAnswersHelper" must {

    "return 3 summary rows when called with charity contact details" in {

      helper.rows(charityContactDetails) mustBe List(SummaryListRow(Key(Text("Main phone number"),"govuk-!-width-one-third"),
        Value(HtmlContent("07700 900 982"),"govuk-!-width-one-third"), "",Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityContactDetailsController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Main phone number")))))),
      SummaryListRow(Key(Text("Alternative phone number (optional)"),"govuk-!-width-one-third"),
        Value(HtmlContent("07700 900 982"),"govuk-!-width-one-third"), "", Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityContactDetailsController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Alternative phone number (optional)")))))),
      SummaryListRow(Key(Text("Email address"),"govuk-!-width-one-third"),
        Value(HtmlContent("abc@gmail.com"),"govuk-!-width-one-third"), "", Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityContactDetailsController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Email address")))))))

    }

    "return 2 summary rows when called with all contact details" in {

      helper.rows(charityContactDetails.copy(mobilePhone = None)) mustBe List(SummaryListRow(
        Key(Text("Main phone number"),"govuk-!-width-one-third"),
        Value(HtmlContent("07700 900 982"),"govuk-!-width-one-third"), "",Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityContactDetailsController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Main phone number")))))),
        SummaryListRow(Key(Text("Email address"),"govuk-!-width-one-third"),
          Value(HtmlContent("abc@gmail.com"),"govuk-!-width-one-third"), "", Some(Actions("govuk-!-width-one-third",
            ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityContactDetailsController.onPageLoad(CheckMode).url,
              Text("Change"),Some("Email address")))))))

    }
  }
}
