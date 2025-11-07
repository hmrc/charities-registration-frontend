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

package repositories

import base.SpecBase
import config.FrontendAppConfig
import models.UserAnswers
import org.mongodb.scala.model.*
import org.mongodb.scala.{MongoCollection, SingleObservableFuture}
import pages.AcknowledgementReferencePage
import pages.checkEligibility.IsEligiblePurposePage
import play.api.libs.json.Json
import uk.gov.hmrc.mongo.test.MongoSupport

import scala.concurrent.Future

class SessionRepositorySpec extends SpecBase with MongoSupport {

  override protected def databaseName: String = "test-charities" + this.getClass.getSimpleName

  private val config: FrontendAppConfig = inject[FrontendAppConfig]

  private lazy val repository: SessionRepository            = new SessionRepository(mongoComponent, config)
  private lazy val collection: MongoCollection[UserAnswers] = repository.collection

  private def findById(id: String, defaultValue: UserAnswers): UserAnswers =
    await(collection.find(Filters.equal("_id", id)).headOption().map(_.getOrElse(defaultValue)))

  private lazy val eligibilityUserAnswers: UserAnswers = emptyUserAnswers
    .set(IsEligiblePurposePage, true)
    .success
    .value

  private lazy val eligibilityUserAnswersWithTtl: UserAnswers = emptyUserAnswers
    .set(AcknowledgementReferencePage, "test-input")
    .success
    .value

  private def givenAnExistingDocument(userAnswers: UserAnswers): Unit = {
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
    ()
  }

  "the eligibility answers session repository" must {

    "get eligibility user answer" in {
      givenAnExistingDocument(emptyUserAnswers.copy(data = Json.obj("isEligiblePurpose" -> true)))

      await(repository.get(emptyUserAnswers.id)) mustBe Some(eligibilityUserAnswers)
    }

    "set eligibility user answer" in {
      await(repository.upsert(eligibilityUserAnswers))

      findById(eligibilityUserAnswers.id, emptyUserAnswers).data mustBe Json.obj("isEligiblePurpose" -> true)
    }

    "set eligibility user answer with expired set" in {
      await(repository.upsert(eligibilityUserAnswersWithTtl))

      findById(eligibilityUserAnswers.id, emptyUserAnswers).data mustBe Json.obj(
        "acknowledgementReference" -> "test-input"
      )
    }

    "delete eligibility user answer" in {
      await(repository.delete(eligibilityUserAnswers))

      findById(eligibilityUserAnswers.id, emptyUserAnswers).data mustBe Json.obj()
    }

  }

}
