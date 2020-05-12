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

package generators

import org.scalacheck.Arbitrary
import org.scalacheck.Arbitrary._
import pages.contact.CharityNamePage
import pages.checkEligibility._
import play.api.libs.json.{JsValue, Json}

trait UserAnswersEntryGenerators extends PageGenerators{

  implicit lazy val arbitraryCharityNameUserAnswersEntry: Arbitrary[(CharityNamePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[CharityNamePage.type]
        value <- arbitrary[String].suchThat(_.nonEmpty).map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsEligibleLocationUserAnswersEntry: Arbitrary[(IsEligibleLocationPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsEligibleLocationPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsEligibleLocationOtherUserAnswersEntry: Arbitrary[(IsEligibleLocationOtherPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsEligibleLocationOtherPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsEligibleAccountUserAnswersEntry: Arbitrary[(IsEligibleAccountPage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsEligibleAccountPage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryInEligibleUserAnswersEntry: Arbitrary[(InEligiblePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[InEligiblePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

  implicit lazy val arbitraryIsEligiblePurposeUserAnswersEntry: Arbitrary[(IsEligiblePurposePage.type, JsValue)] =
    Arbitrary {
      for {
        page  <- arbitrary[IsEligiblePurposePage.type]
        value <- arbitrary[Boolean].map(Json.toJson(_))
      } yield (page, value)
    }

}
