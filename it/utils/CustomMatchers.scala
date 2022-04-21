/*
 * Copyright 2022 HM Revenue & Customs
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

package utils

import org.scalatest.matchers._
import play.api.libs.ws.WSResponse

trait CustomMatchers {
  def httpStatus(expectedValue: Int): HavePropertyMatcher[WSResponse, Int] =
    new HavePropertyMatcher[WSResponse, Int] {
      def apply(response: WSResponse): HavePropertyMatchResult[Int] =
        HavePropertyMatchResult(
          response.status == expectedValue,
          "httpStatus",
          expectedValue,
          response.status
        )
    }

  def redirectLocation(expectedValue: String): HavePropertyMatcher[WSResponse, Option[String]] =
    new HavePropertyMatcher[WSResponse, Option[String]] {
      def apply(response: WSResponse): HavePropertyMatchResult[Option[String]] =
        HavePropertyMatchResult(
          response.header("Location").contains(expectedValue),
          "headerLocation",
          Some(expectedValue),
          response.header("Location")
        )
    }
}