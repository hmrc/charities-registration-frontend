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

package controllers.connectors

import com.google.inject.ImplementedBy
import controllers.utils.CascadeUpsert
import javax.inject.Inject
import play.api.libs.json.Format
import repositories.{SessionRepository, ShortSessionRepository}
import uk.gov.hmrc.http.cache.client.CacheMap

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DataCacheConnectorImpl @Inject()(sessionRepository: SessionRepository,
                                    val cascadeUpsert: CascadeUpsert)
  extends DataCacheConnector {

  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] = {
    sessionRepository.get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap = cascadeUpsert(key, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      sessionRepository.upsert(updatedCacheMap).map {_ => updatedCacheMap}
    }
  }

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    sessionRepository.get(cacheId)
}

@ImplementedBy(classOf[DataCacheConnectorImpl])
trait DataCacheConnector {
  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap]

  def fetch(cacheId: String): Future[Option[CacheMap]]
}

class DataShortCacheConnector @Inject()(shortSessionRepository: ShortSessionRepository,
                                       val cascadeUpsert: CascadeUpsert)
  extends DataCacheConnector {

  def save[A](cacheId: String, key: String, value: A)(implicit fmt: Format[A]): Future[CacheMap] = {
    shortSessionRepository.get(cacheId).flatMap { optionalCacheMap =>
      val updatedCacheMap = cascadeUpsert(key, value, optionalCacheMap.getOrElse(new CacheMap(cacheId, Map())))
      shortSessionRepository.upsert(updatedCacheMap).map {_ => updatedCacheMap}
    }
  }

  def fetch(cacheId: String): Future[Option[CacheMap]] =
    shortSessionRepository.get(cacheId)
}