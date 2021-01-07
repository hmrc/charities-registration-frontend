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

package models

import java.time.LocalDateTime

import pages._
import play.api.libs.json._

import scala.annotation.tailrec
import scala.util.{Failure, Success, Try}

//scalastyle:off magic.number
final case class UserAnswers(
                              id: String,
                              data: JsObject = Json.obj(),
                              lastUpdated: LocalDateTime = LocalDateTime.now,
                              expiresAt: LocalDateTime = LocalDateTime.now.plusMinutes(15)
                            ) {

  def arePagesDefined(pages: Seq[QuestionPage[_]]): Boolean = {
    if (pages.isEmpty) {
      false
    } else {
      pages.forall(page => checkDataPresent(page).nonEmpty)
    }
  }

  def checkDataPresent[A](page: QuestionPage[A], idx: Option[Int] = None): Option[JsValue] = {
    path(page, idx).readNullable[JsValue].reads(data).getOrElse(None)
  }

  def unneededPagesNotPresent(neededPages: Seq[QuestionPage[_]], allPages: Seq[QuestionPage[_]]): Boolean = {
    (allPages diff neededPages).forall(page => checkDataPresent(page).isEmpty)
  }

  private def path[A](page: QuestionPage[A], idx: Option[Int]) = idx.fold(page.path)(idx => page.path \ (idx - 1))

  def get[A](page: QuestionPage[A], idx: Option[Int] = None)(implicit rds: Reads[A]): Option[A] =
    path(page, idx).readNullable[A].reads(data).getOrElse(None)

  def set[A](page: QuestionPage[A], value: A, idx: Option[Int] = None)(implicit writes: Writes[A]): Try[UserAnswers] = {
    setData(path(page, idx), value).flatMap {
      d =>
        val updatedAnswers = copy(data = d)
        page.cleanup(Some(value), updatedAnswers)
    }
  }

  private def setData[A](path: JsPath, value: A)(implicit writes: Writes[A]): Try[JsObject] = {
    data.setObject(path, Json.toJson(value)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(errors) =>
        Failure(JsResultException(errors))
    }
  }

  def remove[A](page: QuestionPage[A], idx: Option[Int] = None): Try[UserAnswers] = {

    val updatedData = data.removeObject(path(page, idx)) match {
      case JsSuccess(jsValue, _) =>
        Success(jsValue)
      case JsError(_) =>
        Success(data)
    }

    updatedData.map { d => copy(data = d) }
  }

  def remove(pages: Seq[QuestionPage[_]]): Try[UserAnswers] = recursivelyClearQuestions(pages, this)

  @tailrec
  private def recursivelyClearQuestions(pages: Seq[QuestionPage[_]], userAnswers: UserAnswers): Try[UserAnswers] = {
    if (pages.isEmpty) Success(userAnswers) else {
      userAnswers.remove(pages.head) match {
        case Success(answers) => recursivelyClearQuestions(pages.tail, answers)
        case failure@Failure(_) => failure
      }
    }
  }
}

object UserAnswers {

  implicit lazy val reads: Reads[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").read[String] and
        (__ \ "data").read[JsObject] and
        (__ \ "lastUpdated").read(MongoDateTimeFormats.localDateTimeRead) and
        (__ \ "expiresAt").read(MongoDateTimeFormats.localDateTimeRead)
      ) (UserAnswers.apply _)
  }

  implicit lazy val writes: OWrites[UserAnswers] = {

    import play.api.libs.functional.syntax._

    (
      (__ \ "_id").write[String] and
        (__ \ "data").write[JsObject] and
        (__ \ "lastUpdated").write(MongoDateTimeFormats.localDateTimeWrite) and
        (__ \ "expiresAt").write(MongoDateTimeFormats.localDateTimeWrite)
      ) (unlift(UserAnswers.unapply))
  }
}
