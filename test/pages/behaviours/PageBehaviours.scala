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

package pages.behaviours

import base.TestData
import models.UserAnswers
import org.scalacheck.Arbitrary.arbitrary
import org.scalacheck.{Arbitrary, Gen}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.{OptionValues, TryValues}
import org.scalatestplus.scalacheck.ScalaCheckPropertyChecks
import pages.QuestionPage
import play.api.libs.json.*
import utils.Generators

trait PageBehaviours
    extends AnyWordSpec
    with Matchers
    with ScalaCheckPropertyChecks
    with Generators
    with OptionValues
    with TryValues
    with TestData {

  class BeRetrievable[A] {
    def apply[P <: QuestionPage[A]](genP: Gen[P])(implicit ev1: Arbitrary[A], ev2: Format[A]): Unit = {

      "return None" when {

        "being retrieved from UserAnswers" when {

          val gen = for {
            page        <- genP
            userAnswers <- arbitrary[UserAnswers]
          } yield (page, userAnswers.remove(page).success.value)

          forAll(gen) { case (page, userAnswers) =>
            s"the question has not been answered $page" in {
              userAnswers.get(page) must be(empty)
            }
          }
        }
      }

      "return the saved value" when {

        "being retrieved from UserAnswers" when {
          val gen = for {
            page        <- genP
            savedValue  <- arbitrary[A]
            userAnswers <- arbitrary[UserAnswers]
          } yield (page, savedValue, userAnswers.set(page, savedValue).success.value)

          forAll(gen) { case (page, savedValue, userAnswers) =>
            s"the question has been answered $page and $savedValue" in {
              userAnswers.get(page).value mustEqual savedValue
            }
          }
        }
      }
    }
  }

  class BeSettable[A] {
    def apply[P <: QuestionPage[A]](genP: Gen[P])(implicit ev1: Arbitrary[A], ev2: Format[A]): Unit = {

      val gen = for {
        page        <- genP
        newValue    <- arbitrary[A]
        userAnswers <- arbitrary[UserAnswers]
      } yield (page, newValue, userAnswers)

      forAll(gen) { case (page, newValue, userAnswers) =>
        s"be able to be set on UserAnswers $page and $newValue" in {
          val updatedAnswers = userAnswers.set(page, newValue).success.value
          updatedAnswers.get(page).value mustEqual newValue
        }
      }
    }
  }

  class BeRemovable[A] {
    def apply[P <: QuestionPage[A]](genP: Gen[P])(implicit ev1: Arbitrary[A], ev2: Format[A]): Unit = {
      val gen = for {
        page        <- genP
        savedValue  <- arbitrary[A]
        userAnswers <- arbitrary[UserAnswers]
      } yield (page, userAnswers.set(page, savedValue).success.value)

      forAll(gen) { case (page, userAnswers) =>
        s"be able to be removed from UserAnswers $page" in {
          val updatedAnswers = userAnswers.remove(page).success.value
          updatedAnswers.get(page) must be(empty)
        }
      }
    }
  }

  def beRetrievable[A]: BeRetrievable[A] = new BeRetrievable[A]

  def beSettable[A]: BeSettable[A] = new BeSettable[A]

  def beRemovable[A]: BeRemovable[A] = new BeRemovable[A]
}
