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

package repositories

import models.UserAnswers
import org.scalatest.BeforeAndAfterEach
import pages.checkEligibility.IsEligiblePurposePage
import play.api.libs.json.Json
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.mongo.test.MongoSupport

class AbstractRepositorySpec extends MongoUnitSpec with BeforeAndAfterEach with MongoSupport {

  private val repository = inject[SessionRepository]

  override protected def collection: JSONCollection = await(repository.collection)

  private lazy val eligibilityUserAnswers = emptyUserAnswers
    .set(IsEligiblePurposePage, true).success.value

  "the eligibility answers session repository" must {

    "get eligibility user answer" in {
      givenAnExistingDocument(emptyUserAnswers.copy(data = Json.obj(
        "isEligiblePurpose" -> true)))

      await(repository.get(emptyUserAnswers.id)) mustBe Some(eligibilityUserAnswers)
    }

    "set eligibility user answer" in {
      await(repository.set(eligibilityUserAnswers))

      await(collection.find(Json.obj("_id" -> eligibilityUserAnswers.id), None).one[UserAnswers]).getOrElse(emptyUserAnswers).data mustBe
        Json.obj("isEligiblePurpose" -> true)
    }

    "delete eligibility user answer" in {
      await(repository.delete(eligibilityUserAnswers))

      await(collection.find(Json.obj("_id" -> eligibilityUserAnswers.id), None).one[UserAnswers]).getOrElse(emptyUserAnswers).data mustBe
        Json.obj()
    }

  }

  private def givenAnExistingDocument(userAnswers: UserAnswers): Unit = {
    await(await(repository.collection).update(ordered = false).one(Json.obj(
      "_id" -> userAnswers.id
    ), Json.obj(
      "$set" -> userAnswers
    ),
      upsert = true))
  }

}
