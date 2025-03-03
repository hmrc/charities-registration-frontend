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

import config.FrontendAppConfig
import models.UserAnswers
import org.mongodb.scala.model._
import pages.AcknowledgementReferencePage
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository
import org.mongodb.scala.SingleObservableFuture

import java.time.{LocalDateTime, ZoneOffset}
import java.util.concurrent.TimeUnit
import javax.inject.{Inject, Singleton}
import scala.concurrent.{ExecutionContext, Future}

@Singleton
class SessionRepository @Inject() (val mongoComponent: MongoComponent, val appConfig: FrontendAppConfig)(implicit
  ec: ExecutionContext
) extends PlayMongoRepository[UserAnswers](
      collectionName = "user-eligibility-answers",
      mongoComponent = mongoComponent,
      domainFormat = UserAnswers.formats,
      replaceIndexes = true,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("expiresAt"),
          IndexOptions()
            .name("dataExpiry")
            .expireAfter(appConfig.userAnswersTimeToLive, TimeUnit.SECONDS)
        )
      )
    ) {

  private def now: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

  private def calculateExpiryTime: LocalDateTime = now.plusSeconds(appConfig.userAnswersTimeToLive)

  private def expiryDate(userAnswers: UserAnswers): LocalDateTime =
    userAnswers.get(AcknowledgementReferencePage) match {
      case Some(_) => userAnswers.expiresAt
      case _       => calculateExpiryTime
    }

  private def byId(userAnswers: UserAnswers) =
    Filters.equal("_id", userAnswers.id)

  def get(id: String): Future[Option[UserAnswers]] =
    collection.find(Filters.equal("_id", id)).headOption()

  def upsert(userAnswers: UserAnswers): Future[Boolean] = {
    val document = userAnswers.copy(lastUpdated = now, expiresAt = expiryDate(userAnswers))

    collection
      .findOneAndReplace(
        byId(userAnswers),
        document,
        FindOneAndReplaceOptions().upsert(true)
      )
      .toFuture()
      .map(_ => true)
  }

  def delete(userAnswers: UserAnswers): Future[Unit] =
    collection.deleteOne(byId(userAnswers)).toFuture().map(_ => ())
}
