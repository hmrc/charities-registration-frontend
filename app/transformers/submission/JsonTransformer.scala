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

  val doNothing: Reads[JsObject] = __.json.put(Json.obj())

  def getAddress(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {

    val isNonUK = (userAnswerPath \ 'country \ 'code).read[String].map {
      code => JsBoolean(code != "GB")
    }

    ((submissionPath \ 'nonUKAddress).json.copyFrom(isNonUK) and
      isNonUK.flatMap {
        case JsBoolean(true) => (submissionPath \ 'nonUKCountry).json.copyFrom((userAnswerPath \ 'country \ 'name).json.pick)
        case _ => doNothing
      } and
      (submissionPath \ 'addressLine1).json.copyFrom((userAnswerPath \ 'lines \ 0).json.pick) and
      (submissionPath \ 'addressLine2).json.copyFrom((userAnswerPath \ 'lines \ 1).json.pick) and
      ((submissionPath \ 'addressLine3).json.copyFrom((userAnswerPath \ 'lines \ 2).json.pick) orElse doNothing) and
      ((submissionPath \ 'addressLine4).json.copyFrom((userAnswerPath \ 'lines \ 3).json.pick) orElse doNothing) and
      ((submissionPath \ 'postcode).json.copyFrom((userAnswerPath \ 'postcode).json.pick) orElse doNothing)).reduce
  }

  def getOptionalAddress(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {
    userAnswerPath.read[JsObject].flatMap { _ =>
      getAddress(submissionPath, userAnswerPath)
    } orElse doNothing
  }

  def getName(submissionPath: JsPath, userAnswerPath: JsPath): Reads[JsObject] = {

    ((submissionPath \ 'title).json.copyFrom((userAnswerPath \ 'title).json.pick) and
      (submissionPath \ 'firstName).json.copyFrom((userAnswerPath \ 'firstName).json.pick) and
      ((submissionPath \ 'middleName).json.copyFrom((userAnswerPath \ 'middleName).json.pick) orElse doNothing) and
      (submissionPath \ 'lastName).json.copyFrom((userAnswerPath \ 'lastName).json.pick)).reduce
  }

}
