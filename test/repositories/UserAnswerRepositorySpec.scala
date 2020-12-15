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

import base.SpecBase
import config.FrontendAppConfig
import models.{CharityName, UserAnswers}
import org.mockito.Mockito.when
import org.scalatest.BeforeAndAfterEach
import org.scalatestplus.mockito.MockitoSugar.mock
import pages.contactDetails.CharityNamePage
import pages.sections.Section1Page
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.Json
import reactivemongo.play.json.collection.Helpers.idWrites
import reactivemongo.play.json.collection.JSONCollection
import uk.gov.hmrc.mongo.MongoSpecSupport

class UserAnswerRepositorySpec extends MongoUnitSpec with BeforeAndAfterEach with MongoSpecSupport {

  override lazy val userAnswers: Option[UserAnswers] = Some(emptyUserAnswers)

  override def applicationBuilder(): GuiceApplicationBuilder =
    new GuiceApplicationBuilder()
      .configure("mongodb.encrypted" -> "true")

  private val appConfig = mock[FrontendAppConfig]
  when(appConfig.encryptData).thenReturn(true)


  private val repository = inject[UserAnswerRepositoryImpl]
  override protected def collection: JSONCollection = await(repository.collection)

  private lazy val charityNameAnswers = emptyUserAnswers
    .set(CharityNamePage, CharityName("AAA", None))
    .flatMap(_.set(Section1Page, false)).success.value

  "the user answers repository" when {
    "getting an encrypted field" must {
      "give the correct result if encryption is right" in {
        givenAnExistingDocument(emptyUserAnswers.copy(data = Json.obj(
          "encrypted" -> "aPkmuJI8W05ScBBWbXvihJhwlqBAeHOETpYpc2yxHoDh++ChfAnqj/LtrR1xc35tDPbXnO3TRLYV240a7ZrYiw==")))

        await(repository.get(emptyUserAnswers.id)) mustBe Some(charityNameAnswers)
      }

      "give empty useranswers if encryption is incorrect" in {
        givenAnExistingDocument(emptyUserAnswers.copy(data = Json.obj("encrypted" -> "thisEncryptionIsWrong")))

        await(repository.get(emptyUserAnswers.id)) mustBe Some(emptyUserAnswers)
      }
    }

    "setting userAnswers" must {
      "set an encrypted value" in {
        await(repository.set(charityNameAnswers))

        await(collection.find(Json.obj("_id" -> charityNameAnswers.id), None).one[UserAnswers]).getOrElse(emptyUserAnswers).data mustBe
          Json.obj("encrypted" -> "aPkmuJI8W05ScBBWbXvihJhwlqBAeHOETpYpc2yxHoDh++ChfAnqj/LtrR1xc35tDPbXnO3TRLYV240a7ZrYiw==")
      }
    }
  }

  private def givenAnExistingDocument(userAnswers: UserAnswers): Unit = {
    await(await(repository.collection).update(ordered = false).one(Json.obj(
      "_id" -> userAnswers.id
    ), Json.obj(
      "$set" -> userAnswers
    ),
      upsert = true))
  }


}
