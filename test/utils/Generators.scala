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

package utils

import java.nio.charset.StandardCharsets
import java.time._

import models.UserAnswers
import org.scalacheck.Arbitrary._
import org.scalacheck.Gen._
import org.scalacheck.{Arbitrary, Gen, Shrink}
import org.scalatest.TryValues
import org.scalatestplus.scalacheck.ScalaCheckDrivenPropertyChecks
import pages.QuestionPage
import play.api.libs.json.{JsValue, Json}

import scala.collection.immutable.NumericRange

trait Generators extends TryValues with ScalaCheckDrivenPropertyChecks {

  implicit override val generatorDrivenConfig: PropertyCheckConfiguration =
    PropertyCheckConfiguration(minSuccessful = 1)

  implicit val dontShrink: Shrink[String] = Shrink.shrinkAny

  implicit lazy val arbitraryUserData: Arbitrary[UserAnswers] = {

    import models._

    Arbitrary {
      for {
        id   <- nonEmptyString
        data <- Gen.const(Map[QuestionPage[?], JsValue]())
      } yield UserAnswers(
        id = id,
        data = data.foldLeft(Json.obj()) { case (obj, (path, value)) =>
          obj.setObject(path.path, value).get
        }
      )
    }
  }

  def genIntersperseString(gen: Gen[String], value: String, frequencyV: Int = 1, frequencyN: Int = 10): Gen[String] = {

    val genValue: Gen[Option[String]] = Gen.frequency(frequencyN -> None, frequencyV -> Gen.const(Some(value)))

    for {
      seq1 <- gen
      seq2 <- Gen.listOfN(seq1.length, genValue)
    } yield seq1.toSeq.zip(seq2).foldRight("") {
      case ((n, Some(v)), m) =>
        m + n + v
      case ((n, _), m)       =>
        m + n
    }
  }

  def intsInRangeWithCommas(min: Int, max: Int): Gen[String] = {
    val numberGen = choose[Int](min, max)
    genIntersperseString(numberGen.toString, ",")
  }

  def decimalInRangeWithCommas(min: BigDecimal, max: BigDecimal): Gen[String] = {
    val numberGen = arbitrary[BigDecimal] suchThat (x => x >= min && x <= max)
    genIntersperseString(numberGen.toString, ",")
  }

  def intsLargerThanMaxValue: Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x > Int.MaxValue)

  def intsSmallerThanMinValue: Gen[BigInt] =
    arbitrary[BigInt] suchThat (x => x < Int.MinValue)

  def nonNumerics: Gen[String] =
    alphaStr suchThat (_.nonEmpty)

  def decimals: Gen[String] =
    arbitrary[BigDecimal]
      .suchThat(_.abs < Int.MaxValue)
      .suchThat(!_.isValidInt)
      .map("%f".format(_))

  def intsBelowValue(value: Int): Gen[Int] =
    arbitrary[Int] suchThat (_ < value)

  def intsAboveValue(value: Int): Gen[Int] =
    arbitrary[Int] suchThat (_ > value)

  def intsOutsideRange(min: Int, max: Int): Gen[Int] =
    arbitrary[Int] suchThat (x => x < min || x > max)

  def nonBooleans: Gen[String] =
    nonEmptyString
      .suchThat(_.nonEmpty)
      .suchThat(_ != "true")
      .suchThat(_ != "false")

  def nonEmptyString: Gen[String] = for {
    length <- Gen.chooseNum(1, 10)
    chars  <- listOfN(length, randomChar)
  } yield chars.mkString

  def stringsWithMaxLength(maxLength: Int): Gen[String] =
    for {
      length <- choose(1, maxLength)
      chars  <- listOfN(length, randomChar)
    } yield chars.mkString

  def stringsLongerThan(minLength: Int): Gen[String] = for {
    maxLength <- (minLength * 2).max(100)
    length    <- Gen.chooseNum(minLength + 1, maxLength)
    chars     <- listOfN(length, randomChar)
  } yield chars.mkString

  def stringsExceptSpecificValues(excluded: Seq[String]): Gen[String] =
    nonEmptyString suchThat (!excluded.contains(_))

  def oneOf[T](xs: Seq[Gen[T]]): Gen[T] =
    if (xs.isEmpty) {
      throw new IllegalArgumentException("oneOf called on empty collection")
    } else {
      val vector = xs.toVector
      choose(0, vector.size - 1).flatMap(vector(_))
    }

  def datesBetween(min: LocalDate, max: LocalDate): Gen[LocalDate] = {

    def toMillis(date: LocalDate): Long =
      date.atStartOfDay.atZone(ZoneOffset.UTC).toInstant.toEpochMilli

    Gen.choose(toMillis(min), toMillis(max)).map { millis =>
      val date = Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate match {
        case leap if leap.getDayOfMonth == 29 && leap.getMonthValue == 2 => leap.plusDays(1)
        case nonLeap                                                     => nonLeap
      }
      date
    }
  }

  private val unicodeCapitalEnglish: NumericRange.Inclusive[Char] = '\u0041' to '\u005A'
  private val unicodeLowerEnglish: NumericRange.Inclusive[Char]   = '\u0061' to '\u007A'
  private val unicodeNumbers: NumericRange.Inclusive[Char]        = '\u0030' to '\u0039'
  private val specialCharacters: List[Char]                       =
    """!"#$%&'()*+,-./:;<=>?@[\]^_`{|}~""".getBytes(StandardCharsets.UTF_8).toList.map(_.toChar)
  def randomChar: Gen[Char]                                       =
    Gen.oneOf(specialCharacters ++ List(unicodeCapitalEnglish, unicodeLowerEnglish, unicodeNumbers).flatten)
}
