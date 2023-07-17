/*
 * Copyright 2023 HM Revenue & Customs
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

///*
// * Copyright 2023 HM Revenue & Customs
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *     http://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package transformers
//
//import base.SpecBase
//import models.oldCharities._
//import models.transformers.TransformerKeeper
//import org.joda.time.LocalDate
//import org.mockito.ArgumentMatchers.{eq => meq}
//import org.mockito.Mockito.when
//import org.mockito.MockitoSugar.mock
//import play.api.libs.json.{JsResultException, Json, JsonValidationError, __}
//import uk.gov.hmrc.http.cache.client.CacheMap
//
//import java.util.NoSuchElementException
//
//class CharitiesJsObjectSpec extends SpecBase {
//
//  lazy val mockCacheMap: CacheMap                       = mock[CacheMap]
//  lazy val howMany: CharityHowManyAuthOfficials         = CharityHowManyAuthOfficials(Some(1))
////  lazy val userAnswerTransformer: UserAnswerTransformer = new UserAnswerTransformer
//
//  val identityPassport: OfficialIndividualIdentity                             = OfficialIndividualIdentity(
//    Some("false"),
//    "",
//    OfficialIndividualNationalIdentityCardDetails("PaspNum", "Country", Some(LocalDate.parse("2100-01-01")))
//  )
//  val currentAddress: CharityAddress                                           = CharityAddress("current", "address", "", "", "AA1 1AA", "")
//  val previousAddress: OptionalCharityAddress                                  =
//    OptionalCharityAddress(Some("true"), CharityAddress("previous", "address", "", "", "AA2 2AA", ""))
//  val charityAuthorisedOfficialIndividual: CharityAuthorisedOfficialIndividual = CharityAuthorisedOfficialIndividual(
//    "0008",
//    "First",
//    "Middle",
//    "Last",
//    LocalDate.parse("1990-01-01"),
//    "01",
//    "0123123123",
//    Some("0123123124"),
//    None,
//    currentAddress,
//    previousAddress,
//    identityPassport
//  )
//
//  "CharitiesJsObject" when {
//
//    "getJson" must {
//
//      "transform correct inputs correctly" in {
//        when(
//          mockCacheMap.getEntry[CharityHowManyAuthOfficials](meq("charityHowManyAuthOfficials"))(
//            meq(CharityHowManyAuthOfficials.formats)
//          )
//        ).thenReturn(Some(howMany))
//
//        val transformerKeeper: TransformerKeeper = TransformerKeeper(Json.obj(), Seq.empty)
//          .getJson(
//            cacheMap = mockCacheMap,
////            userAnswerTransformer.toUserAnswersCharityHowManyAuthOfficials,
//            key = "charityHowManyAuthOfficials"
//          )(CharityHowManyAuthOfficials.formats)
//
//        transformerKeeper.errors mustBe Seq.empty
//        transformerKeeper.accumulator mustBe Json.obj("isAddAnotherOfficial" -> false)
//
//      }
//
//      "not transform correctly when the input doesn't match the transformer" in {
//        when(
//          mockCacheMap.getEntry[CharityHowManyAuthOfficials](meq("charityHowManyAuthOfficials"))(
//            meq(CharityHowManyAuthOfficials.formats)
//          )
//        ).thenReturn(Some(howMany))
//
//        val transformerKeeper: TransformerKeeper = TransformerKeeper(Json.obj(), Seq.empty)
//          .getJson(
//            mockCacheMap,
//            userAnswerTransformer.toUserAnswersCharityHowManyOtherOfficials,
//            "charityHowManyAuthOfficials"
//          )(CharityHowManyAuthOfficials.formats)
//
//        transformerKeeper.errors mustBe Seq.empty
//        transformerKeeper.accumulator mustBe Json.obj()
//
//      }
//
//      "failed with JsResultException when the input doesn't match the transformer" in {
//        when(
//          mockCacheMap.getEntry[CharityHowManyAuthOfficials](meq("charityHowManyAuthOfficials"))(
//            meq(CharityHowManyAuthOfficials.formats)
//          )
//        ).thenThrow(
//          JsResultException(
//            List(
//              (
//                __ \ "charityHowManyAuthOfficials" \ "numberOfAuthOfficials",
//                List(JsonValidationError(List("error.path.missing")))
//              )
//            )
//          )
//        )
//
//        val transformerKeeper: TransformerKeeper = TransformerKeeper(Json.obj(), Seq.empty)
//          .getJson(
//            mockCacheMap,
//            userAnswerTransformer.toUserAnswersCharityHowManyOtherOfficials,
//            "charityHowManyAuthOfficials"
//          )(CharityHowManyAuthOfficials.formats)
//
//        transformerKeeper.errors mustBe Seq(
//          (
//            __ \ "charityHowManyAuthOfficials" \ "numberOfAuthOfficials",
//            Seq(JsonValidationError(Seq("error.path.missing")))
//          )
//        )
//        transformerKeeper.accumulator mustBe Json.obj()
//
//      }
//
//      "failed with throwable when unknown exception thrown during transformation" in {
//        when(
//          mockCacheMap.getEntry[CharityHowManyAuthOfficials](meq("charityHowManyAuthOfficials"))(
//            meq(CharityHowManyAuthOfficials.formats)
//          )
//        ).thenThrow(new RuntimeException)
//
//        intercept[RuntimeException] {
//          TransformerKeeper(Json.obj(), Seq.empty)
//            .getJson(
//              mockCacheMap,
//              userAnswerTransformer.toUserAnswersCharityHowManyOtherOfficials,
//              "charityHowManyAuthOfficials"
//            )(CharityHowManyAuthOfficials.formats)
//        }
//
//      }
//    }
//
//    "getJsonOfficials" must {
//
//      "transform correct inputs correctly" in {
//        when(
//          mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual2"))(
//            meq(CharityAuthorisedOfficialIndividual.formats)
//          )
//        ).thenReturn(Some(charityAuthorisedOfficialIndividual))
//
//        val transformerKeeper: TransformerKeeper = TransformerKeeper(
//          Json.obj("authorisedOfficials" -> Json.arr(Json.parse("""
//              |{
//              |            "isOfficialPreviousAddress": true,
//              |            "officialsPhoneNumber": {
//              |                "mobilePhone": "0123123124",
//              |                "daytimePhone": "0123123123"
//              |            },
//              |            "officialsPosition": "01",
//              |            "officialsDOB": "1990-01-01",
//              |            "isOfficialNino": true,
//              |            "officialAddress": {
//              |                "country": {
//              |                    "code": "GB",
//              |                    "name": "United Kingdom"
//              |                },
//              |                "postcode": "AA1 1AA",
//              |                "lines": [
//              |                    "current",
//              |                    "address"
//              |                ]
//              |            },
//              |            "officialPreviousAddress": {
//              |                "country": {
//              |                    "code": "GB",
//              |                    "name": "United Kingdom"
//              |                },
//              |                "postcode": "AA2 2AA",
//              |                "lines": [
//              |                    "previous",
//              |                    "address"
//              |                ]
//              |            },
//              |            "officialsName": {
//              |                "firstName": "First",
//              |                "lastName": "Last",
//              |                "middleName": "Middle",
//              |                "title": "0001"
//              |            },
//              |            "officialsNino": "AB111111A"
//              |        }""".stripMargin))),
//          Seq.empty
//        )
//          .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//            mockCacheMap,
//            userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "authorised"),
//            "authorisedOfficialIndividual2",
//            "authorisedOfficials"
//          )(CharityAuthorisedOfficialIndividual.formats)
//
//        transformerKeeper.errors mustBe Seq.empty
//        transformerKeeper.accumulator mustBe Json.obj(
//          "authorisedOfficials" -> Json.arr(
//            Json.parse("""
//            |{
//            |            "isOfficialPreviousAddress": true,
//            |            "officialsPhoneNumber": {
//            |                "mobilePhone": "0123123124",
//            |                "daytimePhone": "0123123123"
//            |            },
//            |            "officialsPosition": "01",
//            |            "officialsDOB": "1990-01-01",
//            |            "isOfficialNino": true,
//            |            "officialAddress": {
//            |                "country": {
//            |                    "code": "GB",
//            |                    "name": "United Kingdom"
//            |                },
//            |                "postcode": "AA1 1AA",
//            |                "lines": [
//            |                    "current",
//            |                    "address"
//            |                ]
//            |            },
//            |            "officialPreviousAddress": {
//            |                "country": {
//            |                    "code": "GB",
//            |                    "name": "United Kingdom"
//            |                },
//            |                "postcode": "AA2 2AA",
//            |                "lines": [
//            |                    "previous",
//            |                    "address"
//            |                ]
//            |            },
//            |            "officialsName": {
//            |                "firstName": "First",
//            |                "lastName": "Last",
//            |                "middleName": "Middle",
//            |                "title": "0001"
//            |            },
//            |            "officialsNino": "AB111111A"
//            |        }""".stripMargin),
//            Json.parse("""
//            |{
//            |            "isOfficialPreviousAddress": true,
//            |            "officialsPhoneNumber": {
//            |                "mobilePhone": "0123123124",
//            |                "daytimePhone": "0123123123"
//            |            },
//            |            "officialsPosition": "01",
//            |            "officialsDOB": "1990-01-01",
//            |            "isOfficialNino": false,
//            |            "officialAddress": {
//            |                "country": {
//            |                    "code": "GB",
//            |                    "name": "United Kingdom"
//            |                },
//            |                "postcode": "AA1 1AA",
//            |                "lines": [
//            |                    "current",
//            |                    "address"
//            |                ]
//            |            },
//            |            "officialPreviousAddress": {
//            |                "country": {
//            |                    "code": "GB",
//            |                    "name": "United Kingdom"
//            |                },
//            |                "postcode": "AA2 2AA",
//            |                "lines": [
//            |                    "previous",
//            |                    "address"
//            |                ]
//            |            },
//            |            "officialsName": {
//            |                "firstName": "First",
//            |                "lastName": "Last",
//            |                "middleName": "Middle",
//            |                "title": "unsupported"
//            |            },
//            |            "officialsPassport": {
//            |              "country": "Country",
//            |              "expiryDate": "2100-01-01",
//            |              "passportNumber": "PaspNum"
//            |            }
//            |        }""".stripMargin)
//          )
//        )
//
//      }
//
//      "not transform correctly when the input doesn't match the transformer" in {
//
//        when(
//          mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual2"))(
//            meq(CharityAuthorisedOfficialIndividual.formats)
//          )
//        ).thenThrow(new NoSuchElementException)
//
//        intercept[NoSuchElementException] {
//          TransformerKeeper(
//            Json.obj("authorisedOfficials" -> Json.arr(Json.parse("""
//                |{
//                |            "notExistent": "1"
//                |        }""".stripMargin))),
//            Seq.empty
//          )
//            .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//              mockCacheMap,
//              userAnswerTransformer.toUserAnswersCharityHowManyOtherOfficials,
//              "authorisedOfficialIndividual2",
//              "authorisedOfficials"
//            )(CharityAuthorisedOfficialIndividual.formats)
//        }
//
//      }
//
//      "failed with JsResultException when the input doesn't match the transformer" in {
//        when(
//          mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual2"))(
//            meq(CharityAuthorisedOfficialIndividual.formats)
//          )
//        ).thenThrow(
//          JsResultException(List((__ \ "authorisedOfficials", List(JsonValidationError(List("error.path.missing"))))))
//        )
//
//        val transformerKeeper: TransformerKeeper = TransformerKeeper(Json.obj(), Seq.empty)
//          .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//            mockCacheMap,
//            userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "authorised"),
//            "authorisedOfficialIndividual2",
//            "authorisedOfficials"
//          )(CharityAuthorisedOfficialIndividual.formats)
//
//        transformerKeeper.errors mustBe Seq(
//          (__ \ "authorisedOfficials", Seq(JsonValidationError(Seq("error.path.missing"))))
//        )
//        transformerKeeper.accumulator mustBe Json.obj()
//
//      }
//
//      "failed with throwable when unknown exception thrown during transformation" in {
//        when(
//          mockCacheMap.getEntry[CharityAuthorisedOfficialIndividual](meq("authorisedOfficialIndividual2"))(
//            meq(CharityAuthorisedOfficialIndividual.formats)
//          )
//        ).thenThrow(new RuntimeException)
//
//        intercept[RuntimeException] {
//          TransformerKeeper(Json.obj(), Seq.empty)
//            .getJsonOfficials[CharityAuthorisedOfficialIndividual](
//              mockCacheMap,
//              userAnswerTransformer.toUserAnswersCharityAuthorisedOfficialIndividual(1, "authorised"),
//              "authorisedOfficialIndividual2",
//              "authorisedOfficials"
//            )(CharityAuthorisedOfficialIndividual.formats)
//        }
//
//      }
//    }
//  }
//
//}
