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

package controllers

import models._
import models.addressLookup.AddressModel
import models.regulators.SelectGoverningDocument
import models.requests.DataRequest
import pages.QuestionPage
import pages.regulatorsAndDocuments.GoverningDocumentNamePage
import pages.sections._
import play.api.i18n.I18nSupport
import play.api.mvc.{AnyContent, Result}
import uk.gov.hmrc.play.bootstrap.frontend.controller.FrontendBaseController

import scala.concurrent.{ExecutionContext, Future}

trait LocalBaseController extends FrontendBaseController with I18nSupport with Enumerable.Implicits {

  implicit lazy val ec: ExecutionContext = controllerComponents.executionContext

  def getFullName(
    page: QuestionPage[Name]
  )(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(page)
      .map { name =>
        block(name.getFullName)
      }
      .getOrElse(Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad())))

  def getOrganisationName(
    page: QuestionPage[String]
  )(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(page)
      .map { name =>
        block(name)
      }
      .getOrElse(Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad())))

  def getAddress(
    addressLookupPageId: QuestionPage[AddressModel]
  )(block: (Seq[String], Country) => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers
      .get(addressLookupPageId)
      .map { charityInformationAddressLookup =>
        val addressList = charityInformationAddressLookup.lines
        val postcode    = charityInformationAddressLookup.postcode.fold(Seq[String]())(Seq(_))
        val countryName = charityInformationAddressLookup.country.name
        val countryCode = charityInformationAddressLookup.country.code
        block(Seq(addressList, postcode).flatten, Country(countryCode, countryName))

      }
      .getOrElse(Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad())))

  def getDocumentNameKey(
    page: QuestionPage[SelectGoverningDocument]
  )(block: String => Future[Result])(implicit request: DataRequest[AnyContent]): Future[Result] =
    request.userAnswers.get(page) match {
      case Some(SelectGoverningDocument.Other) =>
        request.userAnswers.get(GoverningDocumentNamePage) match {
          case Some(_) => block("7")
          case None    => Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad()))
        }
      case Some(documentNameKey)               =>
        block(s"$documentNameKey")
      case None                                => Future.successful(Redirect(controllers.routes.PageNotFoundController.onPageLoad()))
    }

  def isAllSectionsCompleted()(implicit request: DataRequest[AnyContent]): Boolean =
    Seq(
      Section1Page,
      Section2Page,
      Section3Page,
      Section4Page,
      Section5Page,
      Section6Page,
      Section7Page,
      Section8Page,
      Section9Page
    )
      .forall(page => request.userAnswers.get(page).contains(true))
}
