/*
 * Copyright 2026 HM Revenue & Customs
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

package config

object Constants {
  val maxAddressLineLength: Int = 35
  val maxAccountNameLength: Int = 60
  val maxRollNumberLength: Int  = 18

  val minAgeYears: Int       = 16
  val earliestBirthYear: Int = 1900

  val maxNameLength: Int = 100

  val maxCountryLength: Int  = 50
  val maxPassportLength: Int = 30

  val maxEmailLength: Int = 160

  val maxOperatingNameLength: Int    = 160
  val maxOrganisationNameLength: Int = 160
  val maxCharityNameLength: Int      = 60

  val maxCharitableObjectivesLength: Int = 500

  val maxOtherFundraisingLength: Int = 95

  val maxPublicBenefitsLength: Int = 500

  val maxWhyNoBankStatementsReasonLength: Int = 350

  val maxRegulatorNameLength: Int      = 100
  val maxRegistrationNumberLength: Int = 20

  val earliestGoverningDocumentYear: Int = 1800

  val maxGoverningDocumentNameLength: Int            = 50
  val maxSectionsChangedGoverningDocumentLength: Int = 350
  val maxWhyNotRegisteredWithCharityReason: Int      = 100

  val maxOperatingCountries: Int = 5

  object Css {
    val bodyCssClass = "govuk-body"

    val linkCssClass = "govuk-link"

    val hintCssClass = "govuk-hint"

    val buttonCssClass = "govuk-button"

    val radiosCssClass       = "govuk-radios"
    val inlineRadiosCssClass = "govuk-radios--inline"

    val insetTextCssClass = "govuk-inset-text"

    val sectionBreakCssClass        = "govuk-section-break"
    val sectionBreakVisibleCssClass = "govuk-section-break--visible"

    val oneHalfCssClass       = "govuk-!-width-one-half"
    val threeQuartersCssClass = "govuk-!-width-three-quarters"

    val fontSize19Class = "govuk-!-font-size-19"

    val boldFontCssClass = "govuk-!-font-weight-bold"

    val hmrcTagCssClass = "hmrc-status-tag"
    val tagCssClass     = "govuk-tag"

    val yellowTagCssClass    = "govuk-tag--yellow"
    val turquoiseTagCssClass = "govuk-tag--turquoise"
    val greyTagCssClass      = "govuk-tag--grey"

    val headingXLCssClass = "govuk-heading-xl"
    val headingLCssClass  = "govuk-heading-l"
    val headingMCssClass  = "govuk-heading-m"

    val labelCssClass        = "govuk-label"
    val labelWrapperCssClass = "govuk-label-wrapper"
    val labelXLCssClass      = "govuk-label--xl"

    val fieldsetLegendClass = "govuk-fieldset__legend"

    val fieldsetLegendXLCssClass = "govuk-fieldset__legend--xl"
    val fieldsetLegendMCssClass  = "govuk-fieldset__legend--m"

    val textAreaCssClass       = "govuk-textArea"
    val characterCountCssClass = "govuk-js-character-count"

    val marginTop5CssClass = "govuk-!-margin-top-5"
    val marginTop6CssClass = "govuk-!-margin-top-6"

    val marginBottom3CssClass = "govuk-!-margin-bottom-3"
    val marginBottom4CssClass = "govuk-!-margin-bottom-4"
    val marginBottom6CssClass = "govuk-!-margin-bottom-6"
    val marginBottom7CssClass = "govuk-!-margin-bottom-7"

    val inputCssClass      = "govuk-input"
    val inputErrorCssClass = "govuk-input--error"

    val inputWidth2CssClass  = "govuk-input--width-2"
    val inputWidth4CssClass  = "govuk-input--width-4"
    val inputWidth5CssClass  = "govuk-input--width-5"
    val inputWidth10CssClass = "govuk-input--width-10"
    val inputWidth20CssClass = "govuk-input--width-20"

    val bulletListCssClass = "govuk-list govuk-list--bullet"

    val panelCssClass             = "govuk-panel"
    val panelConfirmationCssClass = "govuk-panel--confirmation"
    val panelTitleCssClass        = "govuk-panel__title"
    val panelBodyCssClass         = "govuk-panel__body"

    val visuallyHiddenCssClass = "govuk-visually-hidden"

    val formGroupCssClass      = "govuk-form-group"
    val formGroupErrorCssClass = "govuk-form-group--error"

    val addToAListCssClass                = "hmrc-add-to-a-list"
    val addToAListShortClass              = "hmrc-add-to-a-list--short"
    val addToAListContentsCssClass        = "hmrc-add-to-a-list__contents"
    val addToAListChangeCssClass          = "hmrc-add-to-a-list__change"
    val addToAListRemoveCssClass          = "hmrc-add-to-a-list__remove"
    val addToAListIdentifierCssClass      = "hmrc-add-to-a-list__identifier"
    val addToAListIdentifierLightCssClass = "hmrc-add-to-a-list__identifier--light"

    val currencyInputCssClass           = "govuk-currency-input"
    val currencyInputInnerCssClass      = "govuk-currency-input__inner"
    val currencyInputInnerInputCssClass = "govuk-currency-input__inner__input"
    val currencyInputUnitCssClass       = "hmrc-currency-input__unit"

    val taskListCssClass              = "app-task-list"
    val taskListSectionCssClass       = "app-task-list__section"
    val taskListSectionNumberCssClass = "app-task-list__section-number"
    val taskListItemCssClass          = "app-task-list__item"
    val taskListItemsCssClass         = "app-task-list__items"
    val taskListTaskNameCssClass      = "app-task-list__task-name"

    val autocompleteWrapperCssClass = "autocomplete-wrapper"
  }
}
