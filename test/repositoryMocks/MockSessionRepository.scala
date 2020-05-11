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

package repositoryMocks

import models.UserAnswers
import org.scalamock.scalatest.MockFactory
import repositories.SessionRepository

import scala.concurrent.{ExecutionContext, Future}

trait MockSessionRepository extends MockFactory {

  val mockSessionRepository: SessionRepository = mock[SessionRepository]

//  def mockGetAnswers(result: Option[UserAnswers]): Unit = {
//    (mockSessionRepository.get(_: String)(_: ExecutionContext))
//      .expects(*,*)
//      .returns(Future.successful(result))
//  }
//
//  def mockSetAnswers(): Unit = {
//    (mockSessionRepository.set(_: UserAnswers)(_: ExecutionContext))
//      .expects(*,*)
//      .returns(Future.successful(true))
//  }
//
//  def mockDeleteAnswers(): Unit = {
//    (mockSessionRepository.delete(_: UserAnswers)(_: ExecutionContext))
//      .expects(*,*)
//      .returns(Future.successful(true))
//  }
}
