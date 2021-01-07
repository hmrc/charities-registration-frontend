/*
 * Copyright 2021 HM Revenue & Customs
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

import config.FrontendAppConfig
import models.UserAnswers
import pages.{AcknowledgementReferencePage, OldServiceSubmissionPage}
import play.api.libs.json.{JsObject, Json}
import play.modules.reactivemongo.ReactiveMongoApi
import reactivemongo.api.bson.BSONDocument
import reactivemongo.api.bson.collection.BSONSerializationPack
import reactivemongo.api.indexes.Index.Aux
import reactivemongo.api.indexes.{Index, IndexType}
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.crypto.{ApplicationCrypto, Crypted, PlainText}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

trait AbstractRepository {

  val mongo: ReactiveMongoApi
  val collectionName: String
  val timeToLive: Int

  val applicationCrypto: ApplicationCrypto
  val appConfig: FrontendAppConfig

  def calculateExpiryTime: LocalDateTime

  def collection: Future[JSONCollection] =
    mongo.database.map(_.collection[JSONCollection](collectionName))

  private val lastUpdatedIndex: Aux[BSONSerializationPack.type] = index(
    key = Seq("expiresAt" -> IndexType.Ascending),
    name = Some("dataExpiry"),
    options = BSONDocument("expireAfterSeconds" -> 0)
  )

  private def index(key: Seq[(String, IndexType)],
    name: Option[String],
    unique: Boolean = false,
    sparse: Boolean = false,
    background: Boolean = false,
    options: BSONDocument = BSONDocument.empty): Aux[BSONSerializationPack.type] = Index(
      key = key,
      unique = unique,
      name = name,
      background = background,
      sparse = sparse,
      expireAfterSeconds = None,
      storageEngine = None,
      weights = None,
      defaultLanguage = None,
      languageOverride = None,
      textIndexVersion = None,
      sphereIndexVersion = None,
      bits = None,
      min = None,
      max = None,
      bucketSize = None,
      collation = None,
      wildcardProjection = None,
      version = None,
      partialFilter = None,
      options = options
    )

  def ensureTtlIndex(collection: JSONCollection): Future[Unit] = {
    collection.indexesManager.ensure(lastUpdatedIndex) flatMap {
      newlyCreated =>
        if (!newlyCreated) {
          for {
            _ <- collection.indexesManager.drop("expiresAt")
            _ <- collection.indexesManager.ensure(lastUpdatedIndex)
          } yield ()
        } else {
          Future.successful(())
        }
    }
  }

  val started: Future[Unit] = collection.flatMap(ensureTtlIndex).map(_ => ())

  def get(id: String): Future[Option[UserAnswers]] = {

    if (appConfig.encryptData) {

      collection.flatMap(_.find(Json.obj("_id" -> id), None).one[UserAnswers]).map {
        _.map {
          userAnswers =>
            val decrypted = Try {
              val dataAsString = Json.stringify((userAnswers.data \ "encrypted").getOrElse(Json.obj()))
              val decryptedString = applicationCrypto.JsonCrypto.decrypt(Crypted(dataAsString)).value
              Json.parse(decryptedString).as[JsObject]
            }.toOption.getOrElse(Json.obj())

            userAnswers.copy(data = decrypted)
        }
      }

    } else {
      collection.flatMap(_.find(Json.obj("_id" -> id), None).one[UserAnswers])
    }
  }

  def set(userAnswers: UserAnswers): Future[Boolean] = {

    def expiryDate: LocalDateTime = (userAnswers.get(AcknowledgementReferencePage), userAnswers.get(OldServiceSubmissionPage)) match {
      case (Some(_), _) | (_, Some(_)) => userAnswers.expiresAt
      case _ => calculateExpiryTime
    }
    val document = if (appConfig.encryptData) {

      val userDataAsString = PlainText(Json.stringify(userAnswers.data))
      val encryptedData = applicationCrypto.JsonCrypto.encrypt(userDataAsString).value

      userAnswers.copy(data = Json.obj("encrypted" -> encryptedData), lastUpdated = LocalDateTime.now, expiresAt = expiryDate)

    } else {
      userAnswers.copy(lastUpdated = LocalDateTime.now, expiresAt = expiryDate)
    }

    val selector = Json.obj(
      "_id" -> userAnswers.id
    )

    val modifier = Json.obj(
      "$set" -> document
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
