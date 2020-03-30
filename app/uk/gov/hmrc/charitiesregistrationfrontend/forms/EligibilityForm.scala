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

package uk.gov.hmrc.charitiesregistrationfrontend.forms

import javax.inject.Inject
import play.api.data.Form
import play.api.data.Forms._
import play.api.i18n.{I18nSupport, MessagesApi}
import uk.gov.hmrc.charitiesregistrationfrontend.models.EligibilityModel
import uk.gov.hmrc.charitiesregistrationfrontend.common.Validation._
import uk.gov.hmrc.charitiesregistrationfrontend.common.Transformers._

class EligibilityForm @Inject()(val messagesApi: MessagesApi)extends I18nSupport{

  val charitableForm = Form(
    mapping(
      "charitable" -> text
        .verifying("charities_elig.check_eligibility", mandatoryCheck)
        .verifying("charities_elig.check_eligibility", yesNoCheck)
        .transform(stringToBoolean, booleanToString)
    )(EligibilityModel.apply)(EligibilityModel.unapply)
  )
}
