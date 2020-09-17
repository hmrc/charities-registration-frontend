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
import models.regulators.SelectWhyNoRegulator._
import pages.{Page, QuestionPage}
import pages.regulatorsAndDocuments._
import play.api.mvc.Call

class RegulatorsAndDocumentsNavigator @Inject()(implicit frontendAppConfig: FrontendAppConfig) extends BaseNavigator {

  private val normalRoutes: Page => UserAnswers => Call =  {
    case IsCharityRegulatorPage => userAnswers: UserAnswers => isCharityRegulatorPageNav(NormalMode, userAnswers)

    case CharityRegulatorPage | CharityCommissionRegistrationNumberPage | ScottishRegulatorRegNumberPage
         | NIRegulatorRegNumberPage | CharityOtherRegulatorDetailsPage => userAnswers: UserAnswers  =>
      userAnswers.get(CharityRegulatorPage) match {
        case Some(items) => nextNav(CharityRegulator.pageMap.filter(p => items.contains(p._1)).values.toSeq, userAnswers, NormalMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }

    case SelectWhyNoRegulatorPage => userAnswers: UserAnswers => selectWhyNoRegulatorPageNav(userAnswers, NormalMode)

    case WhyNotRegisteredWithCharityPage => userAnswers: UserAnswers => userAnswers.get(WhyNotRegisteredWithCharityPage) match {
      case Some(_) => regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case RegulatorsSummaryPage => _ => routes.IndexController.onPageLoad()

    case _ => _ => routes.IndexController.onPageLoad()
  }

  private val checkRouteMap: Page => UserAnswers => Call = {
    case IsCharityRegulatorPage => userAnswers: UserAnswers => isCharityRegulatorCheckNav(CheckMode, userAnswers)

    case CharityRegulatorPage | CharityCommissionRegistrationNumberPage | ScottishRegulatorRegNumberPage
         | NIRegulatorRegNumberPage | CharityOtherRegulatorDetailsPage => userAnswers: UserAnswers  =>
      userAnswers.get(CharityRegulatorPage) match {
        case Some(items) => nextNav(CharityRegulator.pageMap.filter{ case(key, _) => items.contains(key)}.values.toSeq, userAnswers, CheckMode)
        case _ => routes.SessionExpiredController.onPageLoad()
      }

    case RegulatorsSummaryPage => _ => routes.IndexController.onPageLoad()

    case SelectWhyNoRegulatorPage => userAnswers: UserAnswers => userAnswers.get(SelectWhyNoRegulatorPage) match {
      case Some(Other) =>  userAnswers.get(WhyNotRegisteredWithCharityPage) match {
        case Some(_) => regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
        case _ => regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(CheckMode)
      }
      case Some(_) => regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case WhyNotRegisteredWithCharityPage => userAnswers: UserAnswers => userAnswers.get(WhyNotRegisteredWithCharityPage) match {
      case Some(_) => regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
      case _ => routes.SessionExpiredController.onPageLoad()
    }

    case _ => _ => routes.IndexController.onPageLoad()
  }

  override def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode =>
      normalRoutes(page)(userAnswers)
    case CheckMode =>
      checkRouteMap(page)(userAnswers)
    case PlaybackMode =>
      routes.SessionExpiredController.onPageLoad() // TODO
  }

  private def isCharityRegulatorPageNav(mode: Mode, userAnswers: UserAnswers): Call = userAnswers.get(IsCharityRegulatorPage) match {
    case Some(true) => regulatorDocsRoutes.CharityRegulatorController.onPageLoad(mode)
    case Some(false) => regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(mode)
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def isCharityRegulatorCheckNav (mode: Mode, userAnswers: UserAnswers): Call =
    (userAnswers.get(IsCharityRegulatorPage), userAnswers.get(CharityRegulatorPage), userAnswers.get(SelectWhyNoRegulatorPage)) match {
      case (Some(true), None, _) => regulatorDocsRoutes.CharityRegulatorController.onPageLoad(mode)
      case (Some(false), _, None) => regulatorDocsRoutes.SelectWhyNoRegulatorController.onPageLoad(mode)
      case (None, _, _) => routes.SessionExpiredController.onPageLoad()
      case _ => regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
    }

  private def selectWhyNoRegulatorPageNav(userAnswers: UserAnswers, mode: Mode): Call = userAnswers.get(SelectWhyNoRegulatorPage) match {
    case Some(Other) => regulatorDocsRoutes.WhyNotRegisteredWithCharityController.onPageLoad(mode)
    case Some(_) => regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
    case _ => routes.SessionExpiredController.onPageLoad()
  }

  private def nextNav(res: Seq[QuestionPage[_]], userAnswers: UserAnswers, mode: Mode): Call ={

    def checkNextNav(seqPages: Seq[QuestionPage[_]]): Call ={
      if(seqPages.isEmpty){
        regulatorDocsRoutes.RegulatorsSummaryController.onPageLoad()
      } else {
        seqPages.head match {
          case CharityCommissionRegistrationNumberPage => userAnswers.get(CharityCommissionRegistrationNumberPage).fold(
            regulatorDocsRoutes.CharityCommissionRegistrationNumberController.onPageLoad(mode)
          )(_ => checkNextNav(seqPages.tail))
          case ScottishRegulatorRegNumberPage  => userAnswers.get(ScottishRegulatorRegNumberPage).fold(
            regulatorDocsRoutes.ScottishRegulatorRegNumberController.onPageLoad(mode)
          )(_ => checkNextNav(seqPages.tail))
          case NIRegulatorRegNumberPage  => userAnswers.get(NIRegulatorRegNumberPage).fold(
            regulatorDocsRoutes.NIRegulatorRegNumberController.onPageLoad(mode)
          )(_ => checkNextNav(seqPages.tail))
          case CharityOtherRegulatorDetailsPage  => userAnswers.get(CharityOtherRegulatorDetailsPage).fold(
            regulatorDocsRoutes.CharityOtherRegulatorDetailsController.onPageLoad(mode)
          )(_ => checkNextNav(seqPages.tail))
        }
      }
    }
    checkNextNav(res)
  }
}
