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

package forms.mappings

import play.api.data.FormError

private[mappings] trait GenericDateFormatter extends Formatters with Constraints {

  val fieldKeys: List[String]
  def keyWithError(id: String, error: String): String = {
    id + "." + error
  }

  val fields: (String, Map[String, String]) => Map[String, Option[String]] = (key, data) => fieldKeys.map {
    field =>
      field -> data.get(s"$key.$field").filter(_.nonEmpty).map(f => filter(f))
  }.toMap

  lazy val missingFields: (String, Map[String, String]) => List[String] = (key, data) =>
    fields(key, data)
    .withFilter(_._2.isEmpty)
    .map(_._1)
    .toList

  lazy val illegalFields: (String, Map[String, String]) => List[String] = (key, data) => fields(key, data)
    .withFilter(_._2.getOrElse("").matches("""^(.*[^\d].*)+$"""))
    .map(_._1)
    .toList

  lazy val illegalZero: (String, Map[String, String]) => List[String] = (key, data) => fields(key, data)
    .withFilter(_._2.getOrElse("").matches("""^[0]+$"""))
    .map(_._1)
    .toList

  lazy val illegalErrors: (String, Map[String, String], String, Seq[String], (String, Map[String, String]) => List[String]) => Option[FormError] =
    (key, data, invalidKey, args, validate) => validate(key, data) match {
      case emptyList if emptyList.isEmpty => None
      case foundErrors => Some(FormError(keyWithError(key, validate(key, data).head), invalidKey, foundErrors ++ args))
    }

  def leftErrors(key: String, data: Map[String, String], missingMessage: String, invalidMessage: String, args: Seq[String]): Left[Seq[FormError], Nothing] =
    Left(
      List(
        FormError(keyWithError(key, missingFields(key, data).head), missingMessage, missingFields(key, data) ++ args))
        ++ illegalErrors(key, data, invalidMessage, args, illegalFields)++ illegalErrors(key, data, invalidMessage, args, illegalZero))

}
