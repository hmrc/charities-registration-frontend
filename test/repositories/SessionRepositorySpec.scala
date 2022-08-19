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

import config.FrontendAppConfig
import models.UserAnswers
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.{Filters, FindOneAndReplaceOptions, IndexModel, IndexOptions, Indexes}
import org.scalatest.BeforeAndAfterEach
import org.scalatest.concurrent.Eventually.eventually
import pages.checkEligibility.IsEligiblePurposePage
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.MongoSupport

import java.util.concurrent.TimeUnit
import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

class SessionRepositorySpec extends BaseMongoIndexSpec with BeforeAndAfterEach with MongoSupport {

  private val config          = inject[FrontendAppConfig]
  private lazy val repository = new SessionRepository(mongoComponent, config)

  private lazy val collection: MongoCollection[UserAnswers] = repository.collection

  private def findById(id: String, defaultValue: UserAnswers): UserAnswers =
    await(collection.find(Filters.equal("_id", id)).headOption().map(_.getOrElse(defaultValue)))

  private lazy val eligibilityUserAnswers = emptyUserAnswers
    .set(IsEligiblePurposePage, true)
    .success
    .value

  private def givenAnExistingDocument(userAnswers: UserAnswers): Unit =
    await(
      repository.collection
        .findOneAndReplace(
          Filters.equal("_id", userAnswers.id),
          userAnswers,
          FindOneAndReplaceOptions().upsert(true)
        )
        .toFuture()
        .map(_ => Future.unit)
    )

  "the eligibility answers session repository" must {

    "get eligibility user answer" in {
      givenAnExistingDocument(emptyUserAnswers.copy(data = Json.obj("isEligiblePurpose" -> true)))

      await(repository.get(emptyUserAnswers.id)) mustBe Some(eligibilityUserAnswers)
    }

    "set eligibility user answer" in {
      await(repository.upsert(eligibilityUserAnswers))

      findById(eligibilityUserAnswers.id, emptyUserAnswers).data mustBe Json.obj("isEligiblePurpose" -> true)
    }

    "delete eligibility user answer" in {
      await(repository.delete(eligibilityUserAnswers))

      findById(eligibilityUserAnswers.id, emptyUserAnswers).data mustBe Json.obj()
    }

    "have all expected indexes" in {
      val expectedIndexes = List(
        IndexModel(
          Indexes.ascending("expiresAt"),
          IndexOptions().name("dataExpiry").expireAfter(config.userAnswersTimeToLive, TimeUnit.SECONDS)
        ),
        IndexModel(Indexes.ascending("_id"), IndexOptions().name("_id_"))
      )

      await(repository.ensureIndexes)

      eventually(timeout(5.seconds), interval(100.milliseconds)) {
        assertIndexes(expectedIndexes.sorted, getIndexes(repository.collection).sorted)
      }

      await(repository.collection.drop.toFuture())
    }
  }

}
