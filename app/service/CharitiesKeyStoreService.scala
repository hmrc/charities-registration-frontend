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

package service

import connectors.CharitiesShortLivedCache
import javax.inject.Inject
import models.oldCharities.{CharityAddress, CharityContactDetails}
import play.api.Logger
import play.api.libs.json.{JsValue, Json}
import uk.gov.hmrc.http.HeaderCarrier

import scala.concurrent.{ExecutionContext, Future}

class CharitiesKeyStoreService @Inject()(cache: CharitiesShortLivedCache){

  private val logger = Logger(this.getClass)

  def getCacheData(internalId:String)(implicit hc: HeaderCarrier, ec: ExecutionContext): Future[Option[JsValue]] ={

    cache.fetch(internalId).map {
      case Some(cacheMap) =>
        cacheMap.getEntry[CharityContactDetails]("charityContactDetails").map { contactDetails =>
          Json.obj("charityContactDetails" -> Json.toJson(contactDetails))
        }
      case _ =>
        None
    } recover {
      case errors: Throwable =>
        logger.error(s"[CharitiesKeyStoreService][getCacheData] registration failed with error ${errors.getMessage}")
        throw errors
    }
  }
}
