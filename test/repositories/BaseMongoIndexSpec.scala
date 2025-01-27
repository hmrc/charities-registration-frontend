/*
 * Copyright 2025 HM Revenue & Customs
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
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.model.{IndexModel, IndexOptions, Indexes}
import scala.util.{Failure, Success}

import java.util.concurrent.TimeUnit
import scala.jdk.CollectionConverters._
import scala.util.Try

trait BaseMongoIndexSpec extends SpecBase {

  protected implicit val ordering: Ordering[IndexModel] = Ordering.by { i: IndexModel => i.toString }

  protected def getIndexes(collection: MongoCollection[_]): Seq[IndexModel] =
    await(
      collection
        .listIndexes()
        .toFuture()
        .map(_.map { document =>
          val indexFields              = document.get("key").map(_.asDocument().keySet().asScala).getOrElse(Set.empty[String]).toSeq
          val name                     = document.getString("name")
          val expireAfterSeconds       = document.getInteger("expireAfterSeconds")
          val indexOptions             = IndexOptions().name(name)
          val indexOptionsWithTtlCheck = Try(expireAfterSeconds.longValue()) match {
            case Success(expireAfterSeconds) => indexOptions.expireAfter(expireAfterSeconds, TimeUnit.SECONDS)
            case Failure(_)                  => indexOptions
          }
          IndexModel(
            Indexes.ascending(indexFields: _*),
            indexOptionsWithTtlCheck
          )
        })
    )

  protected def assertIndexes(expectedIndexes: Iterable[IndexModel], actualIndexes: Iterable[IndexModel]): Unit = {
    actualIndexes.size mustBe expectedIndexes.size

    expectedIndexes
      .zip(actualIndexes)
      .foreach { indexTuple =>
        val expectedIndex = indexTuple._1
        val actualIndex   = indexTuple._2

        assertIndex(expectedIndex, actualIndex)
      }
  }

  private def assertIndex(expectedIndex: IndexModel, actualIndex: IndexModel): Unit = {
    actualIndex.getKeys.toBsonDocument.keySet().asScala mustBe expectedIndex.getKeys.toBsonDocument.keySet().asScala
    actualIndex.getKeys.toBsonDocument.toString mustBe expectedIndex.getKeys.toBsonDocument.toString

    actualIndex.getOptions.toString mustBe expectedIndex.getOptions.toString
  }

}
