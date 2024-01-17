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

import config.FrontendAppConfig
import forms.operationsAndFunds.CharitablePurposesFormProvider
import forms.regulatorsAndDocuments.CharityRegulatorFormProvider
import models._
import models.operations.CharitablePurposes
import models.regulators.CharityRegulator
import org.scalacheck.Arbitrary
import play.api.data.Form
import play.api.data.Forms._
import play.twirl.api.Html
import uk.gov.hmrc.govukfrontend.views.viewmodels.summarylist.SummaryListRow
import uk.gov.hmrc.scalatestaccessibilitylinter.views.AutomaticAccessibilitySpec
import utils.TestData
import viewmodels.{OfficialSummaryListRow, TaskListRow}
import views.html._
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

class FrontendAccessibilitySpec extends AutomaticAccessibilitySpec with TestData {

  // Some view template parameters can't be completely arbitrary,
  // but need to have same values for pages to render properly.
  // eg. if there is validation or conditional logic in the twirl template.
  // These can be provided by calling `fixed()` to wrap an existing concrete value.
  private val appConfig: FrontendAppConfig = app.injector.instanceOf[FrontendAppConfig]

  private val booleanForm: Form[Boolean] = Form("value" -> boolean)

  private val name = Name(SelectTitle.Mr, "Martin", middleName = None, "Odersky")

  private val countries = Seq(("GB", "United Kingdom"))

  private val section1: TaskListRow = TaskListRow(
    "index.section1.spoke1.label",
    controllers.contactDetails.routes.CharityNameController.onPageLoad(NormalMode),
    "index.section.notStarted"
  )

  private val section2: TaskListRow = TaskListRow(
    "index.section2.spoke1.label",
    controllers.regulatorsAndDocuments.routes.IsCharityRegulatorController.onPageLoad(NormalMode),
    "index.section.completed"
  )

  private val section3: TaskListRow = TaskListRow(
    "index.section2.spoke2.label",
    controllers.regulatorsAndDocuments.routes.SelectGoverningDocumentController.onPageLoad(NormalMode),
    "index.section.inProgress"
  )

  private val section4: TaskListRow = TaskListRow(
    "index.section3.spoke1.label",
    controllers.operationsAndFunds.routes.CharitableObjectivesController.onPageLoad(NormalMode),
    "index.section.notStarted"
  )

  private val section5: TaskListRow = TaskListRow(
    "index.section3.spoke2.label",
    controllers.operationsAndFunds.routes.FundRaisingController.onPageLoad(NormalMode),
    "index.section.notStarted"
  )

  private val section6: TaskListRow = TaskListRow(
    "index.section3.spoke3.label",
    controllers.operationsAndFunds.routes.BankDetailsController.onPageLoad(NormalMode),
    "index.section.completed"
  )

  private val section7: TaskListRow = TaskListRow(
    "index.section4.spoke1.label",
    controllers.authorisedOfficials.routes.CharityAuthorisedOfficialsController.onPageLoad(),
    "index.section.notStarted"
  )

  private val section8: TaskListRow = TaskListRow(
    "index.section4.spoke2.label",
    controllers.otherOfficials.routes.CharityOtherOfficialsController.onPageLoad(),
    "index.section.notStarted"
  )

  private val section9: TaskListRow = TaskListRow(
    "index.section4.spoke3.label",
    controllers.nominees.routes.CharityNomineeController.onPageLoad(),
    "index.section.inProgress"
  )

  private val section10: TaskListRow = TaskListRow(
    "index.section5.spoke1.label",
    controllers.routes.StartDeclarationController.onPageLoad,
    "index.section.inProgress"
  )

  private val taskList: List[TaskListRow] =
    List(section1, section2, section3, section4, section5, section6, section7, section8, section9, section10)

  implicit val arbConfig: Arbitrary[FrontendAppConfig] = fixed(appConfig)

  implicit val arbForm: Arbitrary[Form[Boolean]] = fixed(booleanForm)

  implicit val arbHtmlInput: Arbitrary[Html] = fixed(Html.apply(""))

  implicit val arbModeInput: Arbitrary[Mode] = fixed(NormalMode)

  implicit val arbNomineeDetails: Arbitrary[Seq[SummaryListRow]] = fixed(Seq(SummaryListRow()))

  implicit val arbSelectGoverningFormInput: Arbitrary[Form[_]] = fixed(booleanForm)

  implicit val arbCountries: Arbitrary[Seq[(String, String)]] = fixed(countries)

  implicit val arbTaskList: Arbitrary[List[TaskListRow]] = fixed(taskList)

  implicit val arbSelectGoverningDocInput: Arbitrary[(Form[_], Mode)] = fixed((booleanForm, NormalMode))

  implicit val arbOffSummaryListInput: Arbitrary[Seq[OfficialSummaryListRow]] = fixed(
    Seq(OfficialSummaryListRow(name, call, call, isCompleted = true))
  )

  implicit val arbCharityRegulatorInput: Arbitrary[Form[Set[CharityRegulator]]] = fixed(
    new CharityRegulatorFormProvider()()
  )

  implicit val arbCharitablePurposesInput: Arbitrary[Form[Set[CharitablePurposes]]] = fixed(
    new CharitablePurposesFormProvider()()
  )

  override implicit val arbAsciiString: Arbitrary[String] = fixed("testString")

  // This is the package where the page templates are located in your service
  val viewPackageName = "views.html"

  // This is the layout class or classes which are injected into all full pages in your service.
  // This might be `HmrcLayout` or some custom class(es) that your service uses as base page templates.
  val layoutClasses: Seq[Class[GovukLayoutWrapper]] = Seq(classOf[GovukLayoutWrapper])

  // this partial function wires up the generic render() functions with arbitrary instances of the correct types.
  // Important: there's a known issue with intellij incorrectly displaying warnings here, you should be able to ignore these for now.
  override def renderViewByClass: PartialFunction[Any, Html] = {
    // authorisedOfficials
    case charityAuthorisedOfficialsView: CharityAuthorisedOfficialsView                               => render(charityAuthorisedOfficialsView)
    // checkEligibility
    case checkIfCanRegisterView: CheckIfCanRegisterView                                               => render(checkIfCanRegisterView)
    case eligibleCharityView: EligibleCharityView                                                     => render(eligibleCharityView)
    case incorrectDetailsView: IncorrectDetailsView                                                   => render(incorrectDetailsView)
    case inEligibleBankView: InEligibleBankView                                                       => render(inEligibleBankView)
    case inEligibleCharitablePurposesView: InEligibleCharitablePurposesView                           => render(inEligibleCharitablePurposesView)
    case inEligibleLocationOtherView: InEligibleLocationOtherView                                     => render(inEligibleLocationOtherView)
    case isEligibleAccountView: IsEligibleAccountView                                                 => render(isEligibleAccountView)
    case isEligibleLocationOtherView: IsEligibleLocationOtherView                                     => render(isEligibleLocationOtherView)
    case isEligibleLocationView: IsEligibleLocationView                                               => render(isEligibleLocationView)
    case isEligiblePurposeView: IsEligiblePurposeView                                                 => render(isEligiblePurposeView)
    // common
    case addedOfficialsView: AddedOfficialsView                                                       => render(addedOfficialsView)
    case amendAddressView: AmendAddressView                                                           => render(amendAddressView)
    case bankAccountDetailsView: BankAccountDetailsView                                               => render(bankAccountDetailsView)
    case confirmAddressView: ConfirmAddressView                                                       => render(confirmAddressView)
    case currencyView: CurrencyView                                                                   => render(currencyView)
    case dateOfBirthView: DateOfBirthView                                                             => render(dateOfBirthView)
    case isNomineePaymentsView: IsNomineePaymentsView                                                 => render(isNomineePaymentsView)
    case isOfficialsNinoView: IsOfficialsNinoView                                                     => render(isOfficialsNinoView)
    case isPreviousAddressView: IsPreviousAddressView                                                 => render(isPreviousAddressView)
    case nameView: NameView                                                                           => render(nameView)
    case ninoView: NinoView                                                                           => render(ninoView)
    case officialsPositionView: OfficialsPositionView                                                 => render(officialsPositionView)
    case officialsSummaryView: OfficialsSummaryView                                                   => render(officialsSummaryView)
    case passportView: PassportView                                                                   => render(passportView)
    case phoneNumberView: PhoneNumberView                                                             => render(phoneNumberView)
    case yesNoView: YesNoView                                                                         => render(yesNoView)
    // contactDetails
    case canWeSendToThisAddressView: CanWeSendToThisAddressView                                       => render(canWeSendToThisAddressView)
    case charityContactDetailsView: CharityContactDetailsView                                         => render(charityContactDetailsView)
    case charityNameView: CharityNameView                                                             => render(charityNameView)
    case startInformationView: StartInformationView                                                   => render(startInformationView)
    // errors
    case pageNotFoundView: PageNotFoundView                                                           => render(pageNotFoundView)
    case signedYouOutView: SignedYouOutView                                                           => render(signedYouOutView)
    case technicalDifficultiesErrorView: TechnicalDifficultiesErrorView                               => render(technicalDifficultiesErrorView)
    case weDeletedYourAnswersView: WeDeletedYourAnswersView                                           => render(weDeletedYourAnswersView)
    case youDeletedYourAnswersView: YouDeletedYourAnswersView                                         => render(youDeletedYourAnswersView)
    // nominees
    case charityNomineeView: CharityNomineeView                                                       => render(charityNomineeView)
    case chooseNomineeView: ChooseNomineeView                                                         => render(chooseNomineeView)
    case isAuthoriseNomineeView: IsAuthoriseNomineeView                                               => render(isAuthoriseNomineeView)
    case nomineeDetailsSummaryView: NomineeDetailsSummaryView                                         => render(nomineeDetailsSummaryView)
    case organisationNomineeAuthorisedPersonView: OrganisationNomineeAuthorisedPersonView             =>
      render(organisationNomineeAuthorisedPersonView)
    case organisationNomineeContactDetailsView: OrganisationNomineeContactDetailsView                 =>
      render(organisationNomineeContactDetailsView)
    case organisationNomineeNameView: OrganisationNomineeNameView                                     => render(organisationNomineeNameView)
    // operations and funds
    case accountingPeriodEndDateView: AccountingPeriodEndDateView                                     => render(accountingPeriodEndDateView)
    case bankDetailsView: BankDetailsView                                                             => render(bankDetailsView)
    case charitableObjectivesView: CharitableObjectivesView                                           => render(charitableObjectivesView)
    case charitablePurposesView: CharitablePurposesView                                               => render(charitablePurposesView)
    case charityEstablishedInView: CharityEstablishedInView                                           => render(charityEstablishedInView)
    case fundRaisingView: FundRaisingView                                                             => render(fundRaisingView)
    case isBankStatementsView: IsBankStatementsView                                                   => render(isBankStatementsView)
    case isFinancialAccountsView: IsFinancialAccountsView                                             => render(isFinancialAccountsView)
    case operatingLocationView: OperatingLocationView                                                 => render(operatingLocationView)
    case otherFundRaisingView: OtherFundRaisingView                                                   => render(otherFundRaisingView)
    case overseasOperatingLocationSummaryView: OverseasOperatingLocationSummaryView                   =>
      render(overseasOperatingLocationSummaryView)
    case publicBenefitsView: PublicBenefitsView                                                       => render(publicBenefitsView)
    case startBankDetailsView: StartBankDetailsView                                                   => render(startBankDetailsView)
    case startCharitableObjectivesView: StartCharitableObjectivesView                                 => render(startCharitableObjectivesView)
    case startFundraisingView: StartFundraisingView                                                   => render(startFundraisingView)
    case whatCountryDoesTheCharityOperateInView: WhatCountryDoesTheCharityOperateInView               =>
      render(whatCountryDoesTheCharityOperateInView)
    case whyNoBankStatementView: WhyNoBankStatementView                                               => render(whyNoBankStatementView)
    // other officials
    case charityOtherOfficialsView: CharityOtherOfficialsView                                         => render(charityOtherOfficialsView)
    // regulators and documents
    case charityCommissionRegistrationNumberView: CharityCommissionRegistrationNumberView             =>
      render(charityCommissionRegistrationNumberView)
    case charityOtherRegulatorDetailsView: CharityOtherRegulatorDetailsView                           => render(charityOtherRegulatorDetailsView)
    case charityRegulatorView: CharityRegulatorView                                                   => render(charityRegulatorView)
    case governingDocumentNameView: GoverningDocumentNameView                                         => render(governingDocumentNameView)
    case hasCharityChangedPartsOfGoverningDocumentView: HasCharityChangedPartsOfGoverningDocumentView =>
      render(hasCharityChangedPartsOfGoverningDocumentView)
    case isApprovedGoverningDocumentView: IsApprovedGoverningDocumentView                             => render(isApprovedGoverningDocumentView)
    case isCharityRegulatorView: IsCharityRegulatorView                                               => render(isCharityRegulatorView)
    case niRegulatorRegNumberView: NIRegulatorRegNumberView                                           => render(niRegulatorRegNumberView)
    case scottishRegulatorRegNumberView: ScottishRegulatorRegNumberView                               => render(scottishRegulatorRegNumberView)
    case sectionsChangedGoverningDocumentView: SectionsChangedGoverningDocumentView                   =>
      render(sectionsChangedGoverningDocumentView)
    case selectGoverningDocumentView: SelectGoverningDocumentView                                     => render(selectGoverningDocumentView)
    case selectWhyNoRegulatorView: SelectWhyNoRegulatorView                                           => render(selectWhyNoRegulatorView)
    case startCharityRegulatorView: StartCharityRegulatorView                                         => render(startCharityRegulatorView)
    case startGoverningDocumentView: StartGoverningDocumentView                                       => render(startGoverningDocumentView)
    case whenGoverningDocumentApprovedView: WhenGoverningDocumentApprovedView                         =>
      render(whenGoverningDocumentApprovedView)
    case whyNotRegisteredWithCharityView: WhyNotRegisteredWithCharityView                             => render(whyNotRegisteredWithCharityView)
    // others
    case applicationBeingProcessedView: ApplicationBeingProcessedView                                 => render(applicationBeingProcessedView)
    case cannotFindApplicationView: CannotFindApplicationView                                         => render(cannotFindApplicationView)
    case checkYourAnswersView: CheckYourAnswersView                                                   => render(checkYourAnswersView)
    case deadEndView: DeadEndView                                                                     => render(deadEndView)
    case declarationView: DeclarationView                                                             => render(declarationView)
    case emailOrPostView: EmailOrPostView                                                             => render(emailOrPostView)
    case registrationSentView: RegistrationSentView                                                   => render(registrationSentView)
    case startDeclarationView: StartDeclarationView                                                   => render(startDeclarationView)
    case switchOverAnswersLostErrorView: SwitchOverAnswersLostErrorView                               => render(switchOverAnswersLostErrorView)
    case switchOverErrorView: SwitchOverErrorView                                                     => render(switchOverErrorView)
    case taskList: TaskList                                                                           => render(taskList)
  }

  runAccessibilityTests()
}
