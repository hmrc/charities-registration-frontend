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
import models.{CharityName, CheckMode}
import uk.gov.hmrc.govukfrontend.views.viewmodels.content.{HtmlContent, Text}
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist._
import viewmodels.SummaryListRowHelper

import scala.collection.mutable.ArrayBuffer

class CharityNameCheckYourAnswersHelperSpec extends SpecBase with SummaryListRowHelper{

  val helper: CharityNameCheckYourAnswersHelper = inject[CharityNameCheckYourAnswersHelper]

  val charityName = CharityName("Fname", Some("Lname"))

  " CharityNameCheckYourAnswersHelper" must {

    "return 2 summary rows when called with charity name" in {

      helper.rows(charityName) mustBe List(SummaryListRow(Key(Text("Full name of your charity"),"govuk-!-width-one-third"),
        Value(HtmlContent("Fname"),"govuk-!-width-one-third"), "",Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Full name of your charity")))))),
      SummaryListRow(Key(Text("Operating name of your charity (optional)"),"govuk-!-width-one-third"),
        Value(HtmlContent("Lname"),"govuk-!-width-one-third"), "", Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Operating name of your charity (optional)")))))))

    }

    "return 1 summary rows when called with all contact details" in {

      helper.rows(charityName.copy(operatingName = None)) mustBe List(SummaryListRow(
        Key(Text("Full name of your charity"),"govuk-!-width-one-third"),
        Value(HtmlContent("Fname"),"govuk-!-width-one-third"), "",Some(Actions("govuk-!-width-one-third",
          ArrayBuffer(ActionItem(controllers.charityInformation.routes.CharityNameController.onPageLoad(CheckMode).url,
            Text("Change"),Some("Full name of your charity")))))))

    }
  }
}
