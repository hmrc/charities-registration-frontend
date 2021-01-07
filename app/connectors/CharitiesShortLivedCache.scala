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

package connectors

import config.FrontendAppConfig
import javax.inject.Inject
import uk.gov.hmrc.crypto.{ApplicationCrypto, CompositeSymmetricCrypto}
import uk.gov.hmrc.http.cache.client.{ShortLivedCache, ShortLivedHttpCaching}
import uk.gov.hmrc.play.bootstrap.http.HttpClient

class CharitiesShortLivedHttpCaching @Inject()(val http: HttpClient,
  appConfig: FrontendAppConfig) extends ShortLivedHttpCaching {

    override lazy val defaultSource: String = "charities-frontend"
    override lazy val baseUri: String = appConfig.save4laterCacheBaseUrl
    override lazy val domain: String = appConfig.save4laterDomain
}

class CharitiesShortLivedCache @Inject()(val shortLiveCache: CharitiesShortLivedHttpCaching,
  applicationCrypto: ApplicationCrypto) extends ShortLivedCache {

    override implicit lazy val crypto: CompositeSymmetricCrypto = applicationCrypto.JsonCrypto
}
