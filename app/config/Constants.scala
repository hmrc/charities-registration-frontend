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
  inline val maxAddressLineLength = 35
  inline val maxAccountNameLength = 60
  inline val maxRollNumberLength  = 18

  inline val minAgeYears       = 16
  inline val earliestBirthYear = 1900

  inline val maxNameLength = 100

  inline val maxCountryLength  = 50
  inline val maxPassportLength = 30

  inline val maxEmailLength = 160

  inline val maxOperatingNameLength    = 160
  inline val maxOrganisationNameLength = 160
  inline val maxCharityNameLength      = 60

  inline val maxCharitableObjectivesLength = 500

  inline val maxOtherFundraisingLength = 95

  inline val maxPublicBenefitsLength = 500

  inline val maxWhyNoBankStatementsReasonLength = 350

  inline val maxRegulatorNameLength      = 100
  inline val maxRegistrationNumberLength = 20

  inline val earliestGoverningDocumentYear = 1800

  inline val maxGoverningDocumentNameLength            = 50
  inline val maxSectionsChangedGoverningDocumentLength = 350
  inline val maxWhyNotRegisteredWithCharityReason      = 100

  inline val maxOperatingCountries = 5

  object Css {
    inline val bodyCssClass = "govuk-body"

    inline val linkCssClass = "govuk-link"

    inline val hintCssClass = "govuk-hint"

    inline val buttonCssClass = "govuk-button"

    inline val radiosCssClass       = "govuk-radios"
    inline val inlineRadiosCssClass = "govuk-radios--inline"

    inline val insetTextCssClass = "govuk-inset-text"

    inline val sectionBreakCssClass        = "govuk-section-break"
    inline val sectionBreakVisibleCssClass = "govuk-section-break--visible"

    inline val oneHalfCssClass       = "govuk-!-width-one-half"
    inline val threeQuartersCssClass = "govuk-!-width-three-quarters"

    inline val fontSize19Class = "govuk-!-font-size-19"

    inline val boldFontCssClass = "govuk-!-font-weight-bold"

    inline val hmrcTagCssClass = "hmrc-status-tag"
    inline val tagCssClass     = "govuk-tag"

    inline val yellowTagCssClass    = "govuk-tag--yellow"
    inline val turquoiseTagCssClass = "govuk-tag--turquoise"
    inline val greyTagCssClass      = "govuk-tag--grey"

    inline val headingXLCssClass = "govuk-heading-xl"
    inline val headingLCssClass  = "govuk-heading-l"
    inline val headingMCssClass  = "govuk-heading-m"

    inline val labelCssClass        = "govuk-label"
    inline val labelWrapperCssClass = "govuk-label-wrapper"
    inline val labelXLCssClass      = "govuk-label--xl"

    inline val fieldsetLegendClass = "govuk-fieldset__legend"

    inline val fieldsetLegendXLCssClass = "govuk-fieldset__legend--xl"
    inline val fieldsetLegendMCssClass  = "govuk-fieldset__legend--m"

    inline val textAreaCssClass       = "govuk-textArea"
    inline val characterCountCssClass = "govuk-js-character-count"

    inline val marginTop5CssClass = "govuk-!-margin-top-5"
    inline val marginTop6CssClass = "govuk-!-margin-top-6"

    inline val marginBottom3CssClass = "govuk-!-margin-bottom-3"
    inline val marginBottom4CssClass = "govuk-!-margin-bottom-4"
    inline val marginBottom6CssClass = "govuk-!-margin-bottom-6"
    inline val marginBottom7CssClass = "govuk-!-margin-bottom-7"

    inline val inputCssClass      = "govuk-input"
    inline val inputErrorCssClass = "govuk-input--error"

    inline val inputWidth2CssClass  = "govuk-input--width-2"
    inline val inputWidth4CssClass  = "govuk-input--width-4"
    inline val inputWidth5CssClass  = "govuk-input--width-5"
    inline val inputWidth10CssClass = "govuk-input--width-10"
    inline val inputWidth20CssClass = "govuk-input--width-20"

    inline val bulletListCssClass = "govuk-list govuk-list--bullet"

    inline val panelCssClass             = "govuk-panel"
    inline val panelConfirmationCssClass = "govuk-panel--confirmation"
    inline val panelTitleCssClass        = "govuk-panel__title"
    inline val panelBodyCssClass         = "govuk-panel__body"

    inline val visuallyHiddenCssClass = "govuk-visually-hidden"

    inline val formGroupCssClass      = "govuk-form-group"
    inline val formGroupErrorCssClass = "govuk-form-group--error"

    inline val addToAListCssClass                = "hmrc-add-to-a-list"
    inline val addToAListShortClass              = "hmrc-add-to-a-list--short"
    inline val addToAListContentsCssClass        = "hmrc-add-to-a-list__contents"
    inline val addToAListChangeCssClass          = "hmrc-add-to-a-list__change"
    inline val addToAListRemoveCssClass          = "hmrc-add-to-a-list__remove"
    inline val addToAListIdentifierCssClass      = "hmrc-add-to-a-list__identifier"
    inline val addToAListIdentifierLightCssClass = "hmrc-add-to-a-list__identifier--light"

    inline val currencyInputCssClass           = "govuk-currency-input"
    inline val currencyInputInnerCssClass      = "govuk-currency-input__inner"
    inline val currencyInputInnerInputCssClass = "govuk-currency-input__inner__input"
    inline val currencyInputUnitCssClass       = "hmrc-currency-input__unit"

    inline val taskListCssClass              = "app-task-list"
    inline val taskListSectionCssClass       = "app-task-list__section"
    inline val taskListSectionNumberCssClass = "app-task-list__section-number"
    inline val taskListItemCssClass          = "app-task-list__item"
    inline val taskListItemsCssClass         = "app-task-list__items"
    inline val taskListTaskNameCssClass      = "app-task-list__task-name"

    inline val autocompleteWrapperCssClass = "autocomplete-wrapper"
  }
}
