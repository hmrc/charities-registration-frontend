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

package base.data.constants

import base.SpecBase
import models.addressLookup.AddressModel

object AddressModelConstants extends SpecBase{

  val address: AddressModel =
    AddressModel(Seq("7", "Morrison street"), Some("G58AN"), gbCountryModel)

  val addressModelMax: AddressModel =
    AddressModel(
      Seq("7", "Morrison street near riverview gardens"),
      Some("G58AN"),
      gbCountryModel
    )

  val addressModelMin: AddressModel =
    AddressModel(Seq("7 Morrison street"), Some("G58AN"), gbCountryModel)

}
