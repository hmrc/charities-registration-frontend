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

package config

import com.google.inject.AbstractModule
import connectors.CharitiesShortLivedCache
import controllers.actions._
import repositories._
import uk.gov.hmrc.http.cache.client.ShortLivedCache

class ModuleBindings extends AbstractModule {

  override def configure(): Unit = {

    bind(classOf[SessionRepository]).to(classOf[SessionRepositoryImpl]).asEagerSingleton()
    bind(classOf[UserAnswerRepository]).to(classOf[UserAnswerRepositoryImpl]).asEagerSingleton()

    bind(classOf[DataRetrievalAction]).to(classOf[DataRetrievalActionImpl]).asEagerSingleton()

    bind(classOf[DataRequiredAction]).to(classOf[DataRequiredActionImpl]).asEagerSingleton()
    bind(classOf[UserDataRetrievalAction]).to(classOf[UserDataRetrievalActionImpl]).asEagerSingleton()
    bind(classOf[RegistrationDataRequiredAction]).to(classOf[RegistrationDataRequiredActionImpl]).asEagerSingleton()

    bind(classOf[IdentifierAction]).to(classOf[SessionIdentifierAction]).asEagerSingleton()
    bind(classOf[AuthIdentifierAction]).to(classOf[AuthenticatedIdentifierAction]).asEagerSingleton()

    bind(classOf[ShortLivedCache]).to(classOf[CharitiesShortLivedCache])
  }
}
