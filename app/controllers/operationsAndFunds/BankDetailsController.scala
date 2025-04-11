/*
 * Copyright 2025 HM Revenue & Customs
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

package controllers.operationsAndFunds

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions.*
import forms.common.BankDetailsFormProvider
import models.requests.BarsBankAccount
import models.responses.BarsAssessmentType.Yes
import models.responses.{BarsError, BarsResponse, ValidateResponse}
import models.{BankDetails, Mode}
import navigation.BankDetailsNavigator
import pages.contactDetails.CharityNamePage
import pages.operationsAndFunds.BankDetailsPage
import pages.sections.{Section1Page, Section6Page}
import play.api.data.{Form, FormError}
import play.api.mvc.*
import service.{BarsService, UserAnswerService}
import views.html.operationsAndFunds.BankDetailsView

import javax.inject.Inject
import scala.concurrent.Future

class BankDetailsController @Inject() (
  val sessionRepository: UserAnswerService,
  val navigator: BankDetailsNavigator,
  identify: AuthIdentifierAction,
  getData: UserDataRetrievalAction,
  requireData: DataRequiredAction,
  formProvider: BankDetailsFormProvider,
  val controllerComponents: MessagesControllerComponents,
  view: BankDetailsView,
  barsService: BarsService
)(implicit appConfig: FrontendAppConfig)
    extends LocalBaseController {

  val messagePrefix: String = "bankDetails"
  val sectionName: String   = "operationsAndFunds.section"

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      if (!request.userAnswers.get(Section1Page).contains(true)) {
        Future.successful(Redirect(controllers.routes.IndexController.onPageLoad(None)))
      } else {

        request.userAnswers.get(CharityNamePage) match {
          case Some(charityName) =>
            val form: Form[BankDetails] = formProvider(messagePrefix, charityName.fullName)

            val preparedForm = request.userAnswers.get(BankDetailsPage) match {
              case None        => form
              case Some(value) => form.fill(value)
            }

            Future.successful(
              Ok(
                view(
                  preparedForm,
                  charityName.fullName,
                  controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(mode),
                  messagePrefix,
                  sectionName,
                  None
                )
              )
            )
          case _                 => Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad()))
        }
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>
      request.userAnswers.get(CharityNamePage) match {
        case Some(charityName) =>
          val form: Form[BankDetails] = formProvider(messagePrefix, charityName.fullName)
          form
            .bindFromRequest()
            .fold(
              formWithErrors =>
                Future.successful(
                  BadRequest(
                    view(
                      formWithErrors,
                      charityName.fullName,
                      controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(mode),
                      messagePrefix,
                      sectionName,
                      None
                    )
                  )
                ),
              value =>
                for {
                  barsServiceCall <-
                    barsService.validateBankDetails(BarsBankAccount(value.sortCode, value.accountNumber))
                  updatedAnswers  <-
                    Future.fromTry(
                      request.userAnswers
                        .set(
                          BankDetailsPage,
                          value.copy(barsValidationFailed = Option.when(barsServiceCall.isLeft)(true))
                        )
                        .flatMap(_.set(Section6Page, false))
                    )
                  _               <- sessionRepository.set(updatedAnswers)
                } yield barsServiceCall match
                  case Right(barsResponse: ValidateResponse)
                      if barsResponse.barsValidateResponse.nonStandardAccountDetailsRequiredForBacs == Yes =>
                    BadRequest(
                      view(
                        form
                          .fill(value)
                          .withError(FormError("rollNumber", "bankAccountDetails.notFound.buildingSociety.required")),
                        charityName.fullName,
                        controllers.operationsAndFunds.routes.BankDetailsController.onSubmit(mode),
                        messagePrefix,
                        sectionName,
                        None
                      )
                    )
                  case Right(_) => Redirect(navigator.nextPage(BankDetailsPage, mode, updatedAnswers))
                  case _        =>
                    Redirect(controllers.operationsAndFunds.routes.BankDetailsSummaryController.onPageLoad())
            )
        case _                 => Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad()))
      }
  }
}
