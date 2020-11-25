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

import play.api.Logger
import play.api.libs.json._
import uk.gov.hmrc.http.cache.client.CacheMap

package object transformers {

  private val logger = Logger(this.getClass)

  implicit class CharitiesJsObject(val accumulator: JsObject) {

    def getJson[T](cacheMap: CacheMap, transformer: Reads[JsObject], key: String)(implicit format: OFormat[T]): JsObject = {
      cacheMap.getEntry[T](key) match {
        case Some(result) =>
          Json.obj(key -> Json.toJson(result)).transform(transformer) match {
            case JsSuccess(requestJson, _) =>
              logger.info(s"[CharitiesKeyStoreService][getCacheData] $key transformation successful")
              accumulator ++ requestJson

            case JsError(err) =>
              logger.error(s"[CharitiesKeyStoreService][getCacheData] $key transformation failed with errors : " + err)
              throw new RuntimeException("transformation failed for existing charities data migration")
          }
        case None =>
          accumulator
      }
    }
  }

}

