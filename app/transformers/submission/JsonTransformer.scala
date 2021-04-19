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

package transformers.submission

import play.api.libs.functional.syntax._
import play.api.libs.json.Reads.JsObjectReducer
import play.api.libs.json._

trait JsonTransformer {

  private def getNonUKAddressLines(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {
    (userAnswerPath \ 'postcode).readNullable[String].flatMap {
      case Some(code) =>
        (userAnswerPath \ 'lines \ 1).readNullable[String].flatMap { line1 =>
          (userAnswerPath \ 'lines \ 2).readNullable[String].flatMap { line2 =>
            (userAnswerPath \ 'lines \ 3).readNullable[String].flatMap { line3 =>
              List(line1, line2, line3).flatten.filter(_.trim.nonEmpty) match {
                case l1::Nil =>
                  ((submissionPath \ 'addressLine2).json.put(JsString(l1)) and
                    (submissionPath \ 'addressLine3).json.put(JsString(code))).reduce
                case l1::l2::Nil =>
                  ((submissionPath \ 'addressLine2).json.put(JsString(l1)) and
                    (submissionPath \ 'addressLine3).json.put(JsString(l2)) and
                    (submissionPath \ 'addressLine4).json.put(JsString(code))).reduce
                case l1::l2::l3::Nil =>
                  val lineWithPostCode = List(l3, code).filter(_.trim.nonEmpty).mkString(", ")
                  ((submissionPath \ 'addressLine2).json.put(JsString(l1)) and
                   (submissionPath \ 'addressLine3).json.put(JsString(l2)) and
                    (submissionPath \ 'addressLine4).json.put(
                      if (lineWithPostCode.length > 35) JsString(l3) else JsString(lineWithPostCode)
                    )).reduce
                case _ =>
                  (submissionPath \ 'addressLine2).json.put(JsString(code))
              }
            }
          }
        }
      case _ =>
        (((submissionPath \ 'addressLine2).json.copyFrom((userAnswerPath \ 'lines \ 1).json.pick) orElse doNothing) and
         ((submissionPath \ 'addressLine3).json.copyFrom((userAnswerPath \ 'lines \ 2).json.pick) orElse doNothing) and
          ((submissionPath \ 'addressLine4).json.copyFrom((userAnswerPath \ 'lines \ 3).json.pick) orElse doNothing)).reduce
    }
  }

  def replaceInvalidCharacters(jsonString: String): String = {
    jsonString.replaceAllLiterally("\t", " ").replaceAllLiterally("\r\n", " ")
  }

  val doNothing: Reads[JsObject] = __.json.put(Json.obj())

  def getAddress(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {

    val isNonUK = (userAnswerPath \ 'country \ 'code).read[String].map {
      code => JsBoolean(code != "GB")
    }

    isNonUK.flatMap {
      case JsBoolean(true) =>
        ((submissionPath \ 'nonUKAddress).json.copyFrom(isNonUK) and
        (submissionPath \ 'addressLine1).json.copyFrom((userAnswerPath \ 'lines \ 0).json.pick) and
        getNonUKAddressLines(submissionPath, userAnswerPath) and
        (submissionPath \ 'nonUKCountry).json.copyFrom((userAnswerPath \ 'country \ 'code).json.pick)).reduce
      case _ =>
        ((submissionPath \ 'nonUKAddress).json.copyFrom(isNonUK) and
        (submissionPath \ 'addressLine1).json.copyFrom((userAnswerPath \ 'lines \ 0).json.pick) and
        (submissionPath \ 'addressLine2).json.copyFrom((userAnswerPath \ 'lines \ 1).json.pick) and
        ((submissionPath \ 'addressLine3).json.copyFrom((userAnswerPath \ 'lines \ 2).json.pick) orElse doNothing) and
        ((submissionPath \ 'addressLine4).json.copyFrom((userAnswerPath \ 'lines \ 3).json.pick) orElse doNothing) and
        ((submissionPath \ 'postcode).json.copyFrom((userAnswerPath \ 'postcode).json.pick) orElse doNothing)).reduce
    }
  }

  def getOptionalAddress(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {
    userAnswerPath.read[JsObject].flatMap { _ =>
      getAddress(submissionPath, userAnswerPath)
    } orElse doNothing
  }

  def getPhone(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {
    submissionPath.json.copyFrom(
      userAnswerPath.read[String].map{
        phone => if(phone.startsWith("+")) {
          JsString(phone.replace("+", ""))
        } else {
          JsString(phone)
        }
      })
  }

  def getOptionalPhone(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {
    userAnswerPath.read[String].flatMap { _ =>
      getPhone(submissionPath, userAnswerPath)
    } orElse doNothing
  }

  def getName(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {

    ((submissionPath \ 'title).json.copyFrom((userAnswerPath \ 'title).json.pick) and
      (submissionPath \ 'firstName).json.copyFrom((userAnswerPath \ 'firstName).json.pick) and
      ((submissionPath \ 'middleName).json.copyFrom((userAnswerPath \ 'middleName).json.pick) orElse doNothing) and
      (submissionPath \ 'lastName).json.copyFrom((userAnswerPath \ 'lastName).json.pick)).reduce
  }

}
