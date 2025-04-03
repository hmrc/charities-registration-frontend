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

package service

import connectors.BarsConnector
import models.requests.{BarsBankAccount, BarsValidateRequest}
import play.api.Logging
import play.api.http.Status.*
import play.api.libs.json.*
import uk.gov.hmrc.http.{HeaderCarrier, UpstreamErrorResponse}
import utils.HttpResponseUtils.HttpResponseOps
import models.responses.*
import models.responses.ValidateResponse.*

import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class BarsService @Inject() (barsConnector: BarsConnector)(implicit ec: ExecutionContext) extends Logging {

  def validateBankDetails(
    bankAccount: BarsBankAccount
  )(implicit hc: HeaderCarrier): Future[Either[BarsError, BarsResponse]] =
    validateBankAccount(bankAccount).map {
      case validateResponse @ validateFailure() =>
        Left(handleValidateErrorResponse(validateResponse))
      case response: SortCodeOnDenyList         =>
        Left(SortCodeOnDenyListErrorResponse(response))
      case response: InvalidAccountNumber       =>
        Left(InvalidAccountNumberErrorResponse(response))
      case barsResponse @ (_: ValidateResponse) => Right(barsResponse)
    }

  private def validateBankAccount(bankAccount: BarsBankAccount)(implicit hc: HeaderCarrier): Future[BarsResponse] =
    barsConnector.validateBankDetails(BarsValidateRequest(bankAccount)).map { httpResponse =>
      httpResponse.status match {
        case OK =>
          httpResponse
            .parseJSON[BarsValidateResponse]
            .map(ValidateResponse.apply)
            .getOrElse(throw UpstreamErrorResponse(httpResponse.body, httpResponse.status))

        case BAD_REQUEST =>
          httpResponse.json.validate[BarsErrorResponse] match {
            case JsSuccess(barsErrorResponse, _) if barsErrorResponse.code == "SORT_CODE_ON_DENY_LIST" =>
              SortCodeOnDenyList(barsErrorResponse)
            case JsSuccess(barsErrorResponse, _) if barsErrorResponse.code == "INVALID_ACCOUNT_NUMBER" =>
              InvalidAccountNumber(barsErrorResponse)
            case _                                                                                     =>
              throw UpstreamErrorResponse(httpResponse.body, httpResponse.status)
          }
        case _           =>
          throw UpstreamErrorResponse(httpResponse.body, httpResponse.status)
      }
    }

  private def handleValidateErrorResponse(response: ValidateResponse): BarsError = {
    import ValidateResponse._
    response match {
      case accountNumberIsWellFormattedNo() => AccountNumberNotWellFormattedValidateResponse(response)
      case sortCodeIsPresentOnEiscdNo()     => SortCodeNotPresentOnEiscdValidateResponse(response)
      case _                                => UnknownBarsErrorResponse(response)
    }
  }
}
