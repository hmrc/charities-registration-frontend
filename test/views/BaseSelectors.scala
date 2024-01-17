/*
 * Copyright 2024 HM Revenue & Customs
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

package views

trait BaseSelectors {

  val panelHeading            = "main div div.govuk-panel.govuk-panel--confirmation h1"
  val panelBody               = "main div div.govuk-panel.govuk-panel--confirmation div.govuk-panel__body"
  val h1: String              = "h1"
  val h2ConfirmationPage      = "#main-content > div > div > div > h2"
  val p: Int => String        = i => s"main p:nth-of-type($i)"
  val legend: Int => String   = i => s"main div div div div:nth-of-type($i) fieldset legend"
  val link: Int => String     = i => s"main a:nth-of-type($i)"
  val indent                  = "div.govuk-inset-text"
  val hint                    = "main span.govuk-hint"
  val bullet: Int => String   = i => s"main ul.govuk-list.govuk-list--bullet li:nth-of-type($i)"
  val label                   = "main label.govuk-label"
  val nthLabel: Int => String = i => s"form > div > div:nth-child($i) > label"
  val warning                 = "main .govuk-warning-text__text"
  val whoCanClaim             = "#who-can-claim"
  val button                  = ".govuk-button"

  def checkAnswersHeading(id: Int, row: Int): String     = s"dl:nth-of-type($id) div:nth-of-type($row) > dt"
  def checkAnswersAnswerValue(id: Int, row: Int): String =
    s"dl:nth-of-type($id) div:nth-of-type($row) > dd:nth-of-type(1)"
  def reviewEmployeesRowLabel(row: Int): String          = s"table tr:nth-of-type($row) th"
  def reviewEmployeesRowAction(row: Int): String         = s"table tr:nth-of-type($row) td a"

}
