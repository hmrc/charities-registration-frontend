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

package models

import uk.gov.hmrc.http.cache.client.CacheMap

class UserAnswers(cacheMap: CacheMap) extends Enumerable.Implicits {

  def eligibilityPurposes: Option[YesNoModel] = cacheMap.getEntry[YesNoModel](YesNoModel.eligibilityPurposesId)
  def eligibilityAccount: Option[YesNoModel] = cacheMap.getEntry[YesNoModel](YesNoModel.eligibilityAccountId)
  def eligibilityLocation: Option[YesNoModel] = cacheMap.getEntry[YesNoModel](YesNoModel.eligibilityLocationId)
  def eligibilityCountries: Option[YesNoModel] = cacheMap.getEntry[YesNoModel](YesNoModel.eligibilityCountriesId)

  def charityNameDetails: Option[CharityNamesModel] = cacheMap.getEntry[CharityNamesModel](CharityNamesModel.toString)
  def contactDetails: Option[ContactDetailsModel] = cacheMap.getEntry[ContactDetailsModel](ContactDetailsModel.toString)

}
