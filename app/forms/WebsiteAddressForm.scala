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

package forms

import models.WebsiteAddressModel
import play.api.data.Form
import play.api.data.Forms.{optional, _}

object WebsiteAddressForm{

  val WebsiteAddressForm = Form(
    mapping(
      "website"-> optional(text.verifying("charities_err.error.websiteAddressInvalid",  model => model.matches(websiteAddressPattern))),
    )(WebsiteAddressModel.apply)(WebsiteAddressModel.unapply)
  )

  val websiteAddressPattern = """^(http:\/\/www\.|https:\/\/www\.|http:\/\/|https:\/\/)?[a-z0-9]+([\-\.]{1}[a-z0-9]+)*\.[a-z]{2,5}(:[0-9]{1,5})?(\/.*)?$"""
}
