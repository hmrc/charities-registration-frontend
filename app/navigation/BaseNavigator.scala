/*
 * Copyright 2022 HM Revenue & Customs
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

import controllers.routes
import models.addressLookup.AddressModel
import models.{CheckMode, Mode, NormalMode, PlaybackMode, UserAnswers}
import pages.Page
import play.api.mvc.Call

trait BaseNavigator {

  val normalRoutes: Page => UserAnswers => Call

  val checkRouteMap: Page => UserAnswers => Call

  val playbackRouteMap: Page => UserAnswers => Call = _ => _ => routes.PageNotFoundController.onPageLoad()

  def nextPage(page: Page, mode: Mode, userAnswers: UserAnswers): Call = mode match {
    case NormalMode   =>
      normalRoutes(page)(userAnswers)
    case CheckMode    =>
      checkRouteMap(page)(userAnswers)
    case PlaybackMode =>
      playbackRouteMap(page)(userAnswers)
  }

  def isNotValidAddress(address: AddressModel): Boolean = {

    val validateFieldWithFullStop = "^[a-zA-Z0-9-, '.]+$"
    val postcode                  = address.postcode.getOrElse("")
    val isValidAddressLines       = address.lines.length >= 2

    !isValidAddressLines || address.lines.exists(addr =>
      addr.length > 35 || !addr.matches(validateFieldWithFullStop)
    ) ||
    (postcode.nonEmpty && !postcode.matches(validateFieldWithFullStop))

  }

}
