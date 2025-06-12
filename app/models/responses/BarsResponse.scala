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

package models.responses

import models.responses.BarsAssessmentType.No

sealed trait BarsResponse

final case class ValidateResponse(barsValidateResponse: BarsValidateResponse) extends BarsResponse

final case class SortCodeOnDenyList(error: BarsErrorResponse) extends BarsResponse

final case class InvalidAccountNumber(error: BarsErrorResponse) extends BarsResponse

object ValidateResponse {

  object validateFailure {
    def unapply(response: ValidateResponse): Boolean =
      response.barsValidateResponse.sortCodeIsPresentOnEISCD.equals(No) ||
        response.barsValidateResponse.accountNumberIsWellFormatted.equals(No) ||
        response.barsValidateResponse.sortCodeSupportsDirectCredit.contains(No)
  }

  object accountNumberIsWellFormattedNo {
    def unapply(response: ValidateResponse): Boolean =
      response.barsValidateResponse.accountNumberIsWellFormatted.equals(No)
  }

  object sortCodeIsPresentOnEiscdNo {
    def unapply(response: ValidateResponse): Boolean =
      response.barsValidateResponse.sortCodeIsPresentOnEISCD.equals(No)
  }

  object sortCodeSupportsDirectCreditNo {
    def unapply(response: ValidateResponse): Boolean =
      response.barsValidateResponse.sortCodeSupportsDirectCredit.contains(No)
  }

}
