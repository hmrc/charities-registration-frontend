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

import java.time.{LocalDateTime, ZoneOffset}

import config.FrontendAppConfig
import javax.inject.{Inject, Singleton}
import play.modules.reactivemongo.ReactiveMongoApi
import uk.gov.hmrc.crypto.ApplicationCrypto

@Singleton
class SessionRepositoryImpl @Inject()(
   override val mongo: ReactiveMongoApi,
   override val appConfig: FrontendAppConfig,
   override val applicationCrypto: ApplicationCrypto) extends SessionRepository {

    override lazy val collectionName: String = "user-eligibility-answers"

    override lazy val timeToLive: Int = appConfig.servicesConfig.getInt("mongodb.user-eligibility-answers.timeToLiveInSeconds")

    override def calculateExpiryTime: LocalDateTime = LocalDateTime.now(ZoneOffset.UTC).plusSeconds(timeToLive)
}

trait SessionRepository extends AbstractRepository
