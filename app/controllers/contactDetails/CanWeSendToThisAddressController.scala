/*
 * Copyright 2021 HM Revenue & Customs
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

package controllers.contactDetails

import config.FrontendAppConfig
import controllers.LocalBaseController
import controllers.actions._
import forms.contactDetails.CanWeSendToThisAddressFormProvider
import javax.inject.Inject
import models.Mode
import models.requests.DataRequest
import navigation.CharityInformationNavigator
import pages.addressLookup.CharityOfficialAddressLookupPage
import pages.contactDetails.CanWeSendToThisAddressPage
import pages.sections.Section1Page
import play.api.mvc._
import repositories.UserAnswerRepository
import service.CountryService
import views.html.contactDetails.CanWeSendToThisAddressView

import scala.concurrent.Future

class CanWeSendToThisAddressController @Inject()(
   sessionRepository: UserAnswerRepository,
   navigator: CharityInformationNavigator,
   identify: AuthIdentifierAction,
   getData: UserDataRetrievalAction,
   requireData: DataRequiredAction,
   val countryService: CountryService,
   formProvider: CanWeSendToThisAddressFormProvider,
   val controllerComponents: MessagesControllerComponents,
   view: CanWeSendToThisAddressView
 )(implicit appConfig: FrontendAppConfig) extends LocalBaseController {

  def onPageLoad(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val form = formProvider()

      val preparedForm = request.userAnswers.get(CanWeSendToThisAddressPage) match {
        case None => form
        case Some(value) => form.fill(value)
      }

      getAddress(CharityOfficialAddressLookupPage) { (charityAddress, country) =>
        val addressWithTranslatedCountry: Seq[String] = charityAddress :+ countryService.translatedCountryName(country)
        Future.successful(Ok(view(preparedForm, mode, addressWithTranslatedCountry)))
      }
  }

  def onSubmit(mode: Mode): Action[AnyContent] = (identify andThen getData andThen requireData).async {
    implicit request =>

      val form = formProvider()

      form.bindFromRequest().fold(
        formWithErrors =>
          getAddress(CharityOfficialAddressLookupPage) { (charityAddress, country) =>
            val addressWithTranslatedCountry: Seq[String] = charityAddress :+ countryService.translatedCountryName(country)
            Future.successful(BadRequest(view(formWithErrors, mode, addressWithTranslatedCountry)))
          },

        value =>
          for {
            updatedAnswers <- Future.fromTry(request.userAnswers.set(CanWeSendToThisAddressPage, value).flatMap(_.set(Section1Page, false)))
            _ <- sessionRepository.set(updatedAnswers)
          } yield Redirect(navigator.nextPage(CanWeSendToThisAddressPage, mode, updatedAnswers))
      )
  }
}
