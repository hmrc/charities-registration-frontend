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

package navigation

import config.FrontendAppConfig
import controllers.regulatorsAndDocuments.{routes => regulatorDocsRoutes}
import controllers.routes
import javax.inject.Inject
import models._
import models.regulators.CharityRegulator
import models.regulators.CharityRegulator.{EnglandWales, NorthernIreland, Scottish, Other => OtherRegulator}
import models.regulators.SelectWhyNoRegulator._
import pages.Page
import pages.regulatorsAndDocuments._
import play.api.mvc.Call


class RegulatorsAndDocumentsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call = {
    case IsCharityRegulatorPage => userAnswers: UserAnswers => userAnswers.get(IsCharityRegulatorPage) match {
      case Some(true) => regulatorDocsRoutes.CharityRegulatorController.onPageLoad(NormalMode)
      case Some(false) => regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(NormalMode)
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharityRegulatorPage => userAnswers: UserAnswers => userAnswers.get(CharityRegulatorPage) match {
      case Some(items) if items.contains(EnglandWales) =>
        regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(NormalMode)
      case result => englandWalesRegulatorNavigation(result)
    }

    case CharityCommissionRegistrationNumberPage => userAnswers: UserAnswers => userAnswers.get(CharityCommissionRegistrationNumberPage) match {
      case Some(_) => englandWalesRegulatorNavigation(userAnswers.get(CharityRegulatorPage))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case ScottishRegulatorRegNumberPage => userAnswers: UserAnswers => userAnswers.get(ScottishRegulatorRegNumberPage) match {
      case Some(_) => scottishRegulatorNavigation(userAnswers.get(CharityRegulatorPage))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case NIRegulatorRegNumberPage => userAnswers: UserAnswers => userAnswers.get(NIRegulatorRegNumberPage) match {
      case Some(_) => niRegulatorNavigation(userAnswers.get(CharityRegulatorPage))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharityOtherRegulatorDetailsPage => userAnswers: UserAnswers => userAnswers.get(CharityOtherRegulatorDetailsPage) match {
      case Some(_) => otherRegulatorNavigation(userAnswers.get(CharityRegulatorPage))
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case SelectWhyNoRegulatorPage => userAnswers: UserAnswers => userAnswers.get(SelectWhyNoRegulatorPage) match {
      case Some(Other) => regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(NormalMode)
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case WhyNotRegisteredWithCharityPage => userAnswers: UserAnswers => userAnswers.get(WhyNotRegisteredWithCharityPage) match {
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case GoverningDocumentSummaryPage => _ => routes.IndexController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {

    case CharityCommissionRegistrationNumberPage => userAnswers: UserAnswers => userAnswers.get(CharityCommissionRegistrationNumberPage) match {
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case ScottishRegulatorRegNumberPage => userAnswers : UserAnswers => userAnswers.get(ScottishRegulatorRegNumberPage) match {
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case NIRegulatorRegNumberPage => userAnswers : UserAnswers => userAnswers.get(NIRegulatorRegNumberPage) match {
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case CharityOtherRegulatorDetailsPage => userAnswers : UserAnswers => userAnswers.get(CharityOtherRegulatorDetailsPage) match {
      case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
  }

  private def englandWalesRegulatorNavigation(result: Option[Set[CharityRegulator]]): Call = result match {
    case Some(items) if items.contains(Scottish) =>
      regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(NormalMode)
    case _ => scottishRegulatorNavigation(result)
  }

  private def scottishRegulatorNavigation(result: Option[Set[CharityRegulator]]): Call = result match {
    case Some(items) if items.contains(NorthernIreland) =>
      regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(NormalMode)
    case _ => niRegulatorNavigation(result)
  }

  private def niRegulatorNavigation(result: Option[Set[CharityRegulator]]): Call = result match {
    case Some(items) if items.contains(OtherRegulator) =>
      regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(NormalMode)
    case _ => otherRegulatorNavigation(result)
  }

  private def otherRegulatorNavigation(result: Option[Set[CharityRegulator]]): Call = result match {
    case Some(_) => regulatorDocsRoutes.GoverningDocumentSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }
}
