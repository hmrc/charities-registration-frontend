/*
 * Copyright 2023 HM Revenue & Customs
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

import config.FrontendAppConfig
import models._
import models.operations.CharitablePurposes
import models.regulators.CharityRegulator
import org.joda.time.DateTime
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.api.data.Forms._
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.scalatestaccessibilitylinter.views.AutomaticAccessibilitySpec
import viewmodels.OfficialSummaryListRow
import views.html.authorisedOfficials.CharityAuthorisedOfficialsView
import views.html.checkEligibility._
import views.html.common._
import views.html.contactDetails._
import views.html.errors._
import views.html.nominees._
import views.html.operationsAndFunds._
import views.html.otherOfficials.CharityOtherOfficialsView
import views.html.regulatorsAndDocuments._
import views.html.templates.GovukLayoutWrapper

import scala.util.Try

class FrontendAccessibilitySpec extends AutomaticAccessibilitySpec {

  // Some view template parameters can't be completely arbitrary,
  // but need to have same values for pages to render properly.
  // eg. if there is validation or conditional logic in the twirl template.
  // These can be provided by calling `fixed()` to wrap an existing concrete value.
  private val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  private val booleanForm: Form[Boolean] = Form("value" -> boolean)

  implicit val arbConfig: Arbitrary[FrontendAppConfig] = fixed(appConfig)

  implicit val arbForm: Arbitrary[Form[Boolean]] = fixed(booleanForm)

  implicit val arbHtmlInput: Arbitrary[Html] = fixed(Html.apply(""))

  implicit val arbModeInput: Arbitrary[Mode] = fixed(NormalMode)

  implicit val arbNomineeDetails: Arbitrary[Seq[SummaryListRow]] = fixed(Seq(SummaryListRow()))

  implicit val arbSelectGoverningFormInput: Arbitrary[Form[_]] = fixed(booleanForm)

  implicit val arbSelectGoverningDocInput: Arbitrary[(Form[_], Mode)] = fixed((booleanForm, NormalMode))

  private val name = Name(SelectTitle.Mr, "Martin", middleName = None, "Odersky")
  implicit val arbOffSummaryListInput: Arbitrary[Seq[OfficialSummaryListRow]] = fixed(Seq(OfficialSummaryListRow(name, call, call, isCompleted = true)))

//  implicit val arbCharityRegulatorInput: Arbitrary[Form[Set[CharityRegulator]]] = fixed(
//    Form(
//      mapping(
//        "value" -> ignored(CharityRegulator.values.toSet)
//      )(Set(CharityRegulator))(Option(Set(CharityRegulator)))
//    )
//  )

//  implicit val arbCharitablePurposesInput: Arbitrary[Form[Set[CharitablePurposes]]] = fixed(Form(
//    mapping(
//
//
//      Set(CharitablePurposes.Other))
//
//  ))

  // Another limitation of the framework is that it can generate Arbitrary[T] but not Arbitrary[T[_]],
  // so any nested types (like a Play `Form[]`) must similarly be provided by wrapping
  // a concrete value using `fixed()`.  Usually, you'll have a value you can use somewhere else
  // in your codebase - either in your production code or another test.
  // Note - these values are declared as `implicit` to simplify calls to `render()` below
  // e.g implicit val arbReportProblemPage: Arbitrary[Form[ReportProblemForm]] = fixed(reportProblemForm)

  // This is the package where the page templates are located in your service
  val viewPackageName = "views.html"

  // This is the layout class or classes which are injected into all full pages in your service.
  // This might be `HmrcLayout` or some custom class(es) that your service uses as base page templates.
  val layoutClasses: Seq[Class[GovukLayoutWrapper]] = Seq(classOf[GovukLayoutWrapper])

  // this partial function wires up the generic render() functions with arbitrary instances of the correct types.
  // Important: there's a known issue with intellij incorrectly displaying warnings here, you should be able to ignore these for now.
  override def renderViewByClass: PartialFunction[Any, Html] = {
    // authorisedOfficials
    case charityAuthorisedOfficialsView: CharityAuthorisedOfficialsView => render(charityAuthorisedOfficialsView)
    // checkEligibility
    case checkIfCanRegisterView: CheckIfCanRegisterView => render(checkIfCanRegisterView)
    case eligibleCharityView: EligibleCharityView => render(eligibleCharityView)
    case incorrectDetailsView: IncorrectDetailsView => render(incorrectDetailsView)
    case inEligibleBankView: InEligibleBankView => render(inEligibleBankView)
    case inEligibleCharitablePurposesView: InEligibleCharitablePurposesView => render(inEligibleCharitablePurposesView)
    case inEligibleLocationOtherView: InEligibleLocationOtherView => render(inEligibleLocationOtherView)
    case isEligibleAccountView: IsEligibleAccountView => render(isEligibleAccountView)
    case isEligibleLocationOtherView: IsEligibleLocationOtherView => render(isEligibleLocationOtherView)
    case isEligibleLocationView: IsEligibleLocationView => render(isEligibleLocationView)
    case isEligiblePurposeView: IsEligiblePurposeView => render(isEligiblePurposeView)
    // common
    case addedOfficialsView: AddedOfficialsView => render(addedOfficialsView)
    case amendAddressView: AmendAddressView => render(amendAddressView)
    case bankAccountDetailsView: BankAccountDetailsView => render(bankAccountDetailsView)
    case confirmAddressView: ConfirmAddressView => render(confirmAddressView)
    case currencyView: CurrencyView => render(currencyView)
    case dateOfBirthView: DateOfBirthView => render(dateOfBirthView)
    case isNomineePaymentsView: IsNomineePaymentsView => render(isNomineePaymentsView)
    case isOfficialsNinoView: IsOfficialsNinoView => render(isOfficialsNinoView)
    case isPreviousAddressView: IsPreviousAddressView => render(isPreviousAddressView)
    case nameView: NameView => render(nameView)
    case ninoView: NinoView => render(ninoView)
    case officialsPositionView: OfficialsPositionView => render(officialsPositionView)
    case officialsSummaryView: OfficialsSummaryView => render(officialsSummaryView)
    case passportView: PassportView => render(passportView)
    case phoneNumberView: PhoneNumberView => render(phoneNumberView)
    case yesNoView: YesNoView => render(yesNoView)
    // contactDetails
    case canWeSendToThisAddressView: CanWeSendToThisAddressView => render(canWeSendToThisAddressView)
    case charityContactDetailsView: CharityContactDetailsView => render(charityContactDetailsView)
    case charityNameView: CharityNameView => render(charityNameView)
    case startInformationView: StartInformationView => render(startInformationView)
      // errors
    case pageNotFoundView: PageNotFoundView => render(pageNotFoundView)
    case signedYouOutView: SignedYouOutView => render(signedYouOutView)
    case technicalDifficultiesErrorView: TechnicalDifficultiesErrorView => render(technicalDifficultiesErrorView)
    case weDeletedYourAnswersView: WeDeletedYourAnswersView => render(weDeletedYourAnswersView)
    case youDeletedYourAnswersView: YouDeletedYourAnswersView => render(youDeletedYourAnswersView)
      // nominees
    case charityNomineeView: CharityNomineeView => render(charityNomineeView)
    case chooseNomineeView: ChooseNomineeView => render(chooseNomineeView)
    case isAuthoriseNomineeView: IsAuthoriseNomineeView => render(isAuthoriseNomineeView)
    case nomineeDetailsSummaryView: NomineeDetailsSummaryView => render(nomineeDetailsSummaryView)
    case organisationNomineeAuthorisedPersonView: OrganisationNomineeAuthorisedPersonView => render(organisationNomineeAuthorisedPersonView)
    case organisationNomineeContactDetailsView: OrganisationNomineeContactDetailsView => render(organisationNomineeContactDetailsView)
    case organisationNomineeNameView: OrganisationNomineeNameView => render(organisationNomineeNameView)
      // operations and funds
    case accountingPeriodEndDateView: AccountingPeriodEndDateView => render(accountingPeriodEndDateView)
    case bankDetailsView: BankDetailsView => render(bankDetailsView)
    case charitableObjectivesView: CharitableObjectivesView => render(charitableObjectivesView)
//    case charitablePurposesView: CharitablePurposesView => render(charitablePurposesView) // todo fix implicit
    case charityEstablishedInView: CharityEstablishedInView => render(charityEstablishedInView)
    case fundRaisingView: FundRaisingView => render(fundRaisingView)
    case isBankStatementsView: IsBankStatementsView => render(isBankStatementsView)
    case isFinancialAccountsView: IsFinancialAccountsView => render(isFinancialAccountsView)
    case operatingLocationView: OperatingLocationView => render(operatingLocationView)
    case otherFundRaisingView: OtherFundRaisingView => render(otherFundRaisingView)
    case overseasOperatingLocationSummaryView: OverseasOperatingLocationSummaryView => render(overseasOperatingLocationSummaryView)
    case publicBenefitsView: PublicBenefitsView => render(publicBenefitsView)
    case startBankDetailsView: StartBankDetailsView => render(startBankDetailsView)
    case startCharitableObjectivesView: StartCharitableObjectivesView => render(startCharitableObjectivesView)
    case startFundraisingView: StartFundraisingView => render(startFundraisingView)
    case whatCountryDoesTheCharityOperateInView: WhatCountryDoesTheCharityOperateInView => render(whatCountryDoesTheCharityOperateInView)
    case whyNoBankStatementView: WhyNoBankStatementView => render(whyNoBankStatementView)
      // other officials
    case charityOtherOfficialsView: CharityOtherOfficialsView => render(charityOtherOfficialsView)
      // regulators and documents
    case charityCommissionRegistrationNumberView: CharityCommissionRegistrationNumberView => render(charityCommissionRegistrationNumberView)
    case charityOtherRegulatorDetailsView: CharityOtherRegulatorDetailsView => render(charityOtherRegulatorDetailsView)
//    case charityRegulatorView: CharityRegulatorView => render(charityRegulatorView) // todo fix implicit
    case governingDocumentNameView: GoverningDocumentNameView => render(governingDocumentNameView)
    case hasCharityChangedPartsOfGoverningDocumentView: HasCharityChangedPartsOfGoverningDocumentView => render(hasCharityChangedPartsOfGoverningDocumentView)
    case isApprovedGoverningDocumentView: IsApprovedGoverningDocumentView => render(isApprovedGoverningDocumentView)
    case isCharityRegulatorView: IsCharityRegulatorView => render(isCharityRegulatorView)
    case niRegulatorRegNumberView: NIRegulatorRegNumberView => render(niRegulatorRegNumberView)
    case scottishRegulatorRegNumberView: ScottishRegulatorRegNumberView => render(scottishRegulatorRegNumberView)
    case sectionsChangedGoverningDocumentView: SectionsChangedGoverningDocumentView => render(sectionsChangedGoverningDocumentView)
    case selectGoverningDocumentView: SelectGoverningDocumentView => render(selectGoverningDocumentView)
    case selectWhyNoRegulatorView: SelectWhyNoRegulatorView => render(selectWhyNoRegulatorView)
    case startCharityRegulatorView: StartCharityRegulatorView => render(startCharityRegulatorView)
    case startGoverningDocumentView: StartGoverningDocumentView => render(startGoverningDocumentView)
    case whenGoverningDocumentApprovedView: WhenGoverningDocumentApprovedView => render(whenGoverningDocumentApprovedView)
    case whyNotRegisteredWithCharityView: WhyNotRegisteredWithCharityView => render(whyNotRegisteredWithCharityView)
  }

  runAccessibilityTests()

  // TODO We are missing the following views as they were clashing with others, the ideal will be to
  //  separate and isolate them in different view tests instead of just running all the AccessibilityTests together,
  //  see https://github.com/hmrc/sbt-accessibility-linter#running-accessibility-checks for more info.
  //    case zero_declaration: zero_declaration => render(zero_declaration)
  //    case dashboard: dashboard => render(dashboard)

  // TODO These views are not picked up as accessible pending for missing wiring,
  //  tobacco_input is the main template for these views so they can be tested individually in their own view tests.
  //    case no_of_sticks_input: no_of_sticks_input => render(no_of_sticks_input)
  //    case no_of_sticks_weight_or_volume_input: no_of_sticks_weight_or_volume_input => render(no_of_sticks_weight_or_volume_input)
  //    case weight_or_volume_input: weight_or_volume_input => render(weight_or_volume_input)
}
