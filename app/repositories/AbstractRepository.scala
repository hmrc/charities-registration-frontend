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

package repositories

import java.time.LocalDateTime

import models.UserAnswers
import play.api.Logger
import play.api.libs.json.Json
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.bson.BSONDocument
import reactivemongo.play.json.ImplicitBSONHandlers.JsObjectDocumentWriter
import reactivemongo.play.json.collection.JSONCollection

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


trait AbstractRepository {

  val mongo: ReactiveMongoApi

  val collectionName: String

  val timeToLive: Int

  private def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val lastUpdatedIndex = Index(
    key = Seq("expiresAt" -> IndexType.Ascending),
    name = Some("dataExpiry"),
    background = true,
    options = BSONDocument("expireAfterSeconds" -> timeToLive)
  )

  val started: Future[Boolean] = {
    collection.flatMap {
      _.indexesManager.ensure(lastUpdatedIndex) map {
        result => {
          Logger.debug(s"set [dataExpiry] with value $timeToLive -> result : $result")
          result
        }
      } recover {
        case e => Logger.error("Failed to set TTL index", e)
          false
      }
    }
  }

  private def calculateExpiryTime = LocalDateTime.now.plusMinutes(timeToLive)

  def get(id: String): Future[Option[UserAnswers]] =
    collection.flatMap(_.find(Json.obj("_id" -> id), None).one[UserAnswers])

  def set(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> userAnswers.copy(lastUpdated  = LocalDateTime.now, expiresAt = calculateExpiryTime)
    )

    collection.flatMap {
      _.update(ordered = false)
        .one(selector, modifier, upsert = true).map {
        lastError =>
          lastError.ok
      }
    }
  }

  def delete(userAnswers: UserAnswers): Future[Boolean] = {

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    collection.flatMap(_.delete(ordered = false)
      .one(selector)).map {
      lastError =>
        lastError.ok
    }
  }
}
