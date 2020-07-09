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

package models

import play.api.mvc.PathBindable

import scala.language.implicitConversions

case class Index(id: Int)

object Index {

  implicit def indexPathBindable(implicit intBinder: PathBindable[Int]): PathBindable[Index] = new PathBindable[Index] {

    override def bind(key: String, value: String): Either[String, Index] = {
      intBinder.bind(key, value) match {
        case Right(x) if x > 0 => Right(Index(x - 1))
        case _ => Left("Index binding failed")
      }
    }

    override def unbind(key: String, value: Index): String = {
      intBinder.unbind(key, value.id + 1)
    }
  }

  implicit def indexToInt(index: Index): Int =
    index.id

  implicit def intToIndex(index: Int): Index =
    Index(index)
}
