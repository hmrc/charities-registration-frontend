/*
 * Copyright 2023 HM Revenue & Customs
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

import models.transformers.TransformerKeeper
import play.api.Logger
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.util.{Failure, Success, Try}

package object transformers {

  private val logger = Logger(this.getClass)

  implicit class CharitiesJsObject(val transformerKeeper: TransformerKeeper) {

    def getJson[T](cacheMap: CacheMap, transformer: Reads[JsObject], key: String)(implicit
      format: OFormat[T]
    ): TransformerKeeper =
      Try(cacheMap.getEntry[T](key)) match {
        case Success(Some(result)) =>
          Json.obj(key -> Json.toJson(result)).transform(transformer) match {
            case JsSuccess(requestJson, _) =>
              logger.info(s"[CharitiesJsObject][getJson] $key transformation successful")
              TransformerKeeper(transformerKeeper.accumulator ++ requestJson, transformerKeeper.errors)

            case JsError(err) =>
              logger.error(s"[CharitiesJsObject][getJson] $key transformation failed with errors: " + err)
              TransformerKeeper(transformerKeeper.accumulator, transformerKeeper.errors ++ err)
          }
        case Success(None)         =>
          transformerKeeper

        case Failure(ex: JsResultException) =>
          logger.info(
            s"[CharitiesJsObject][getJson] $key transformation failed with JsResultException: " + ex.getMessage
          )
          TransformerKeeper(transformerKeeper.accumulator, transformerKeeper.errors ++ ex.errors)

        case Failure(ex) =>
          logger.error(s"[CharitiesJsObject][getJson] $key exception during transformation: " + ex.getMessage)
          throw ex
      }

    def getJsonOfficials[T](cacheMap: CacheMap, transformer: Reads[JsObject], key: String, goalKey: String)(implicit
      format: OFormat[T]
    ): TransformerKeeper =
      Try(cacheMap.getEntry[T](key)) match {
        case Success(Some(result)) =>
          Json.obj(key -> Json.toJson(result)).transform(transformer) match {
            case JsSuccess(requestJson, _) =>
              val combinedPreviousAndNewOfficial = transformerKeeper.accumulator.fields
                .find(_._1 == goalKey)
                .map(_._2.as[JsArray] ++ requestJson(goalKey).as[JsArray])

              logger.info(s"[CharitiesJsObject][getJsonOfficials] $key array transformation successful")

              TransformerKeeper(
                transformerKeeper.accumulator ++ Json.obj(goalKey -> combinedPreviousAndNewOfficial),
                transformerKeeper.errors
              )

            case JsError(err) =>
              logger.error(s"[CharitiesJsObject][getJsonOfficials] $key transformation failed with errors: " + err)
              TransformerKeeper(transformerKeeper.accumulator, transformerKeeper.errors ++ err)
          }
        case Success(None)         =>
          transformerKeeper

        case Failure(ex: JsResultException) =>
          logger.info(
            s"[CharitiesJsObject][getJsonOfficials] $key transformation failed with JsResultException: " + ex.getMessage
          )
          TransformerKeeper(transformerKeeper.accumulator, transformerKeeper.errors ++ ex.errors)

        case Failure(ex) =>
          logger.error(s"[CharitiesJsObject][getJsonOfficials] $key exception during transformation: " + ex.getMessage)
          throw ex
      }
  }

}
