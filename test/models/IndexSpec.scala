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

import org.scalatest.{MustMatchers, OptionValues, WordSpecLike}

class IndexSpec extends WordSpecLike with MustMatchers with OptionValues {

  "indexPathBindable" must {
    val binder = Index.indexPathBindable

    "bind a valid index" in {
      binder.bind("index", "1") mustEqual Right(Index(0))
    }

    "fail to bind an index with negative value" in {
      binder.bind("index", "-1") mustEqual Left("Index binding failed")
    }

    "unbind an index" in {
      binder.unbind("index", Index(0)) mustEqual "1"
    }
  }

  "implicit conversion from Int" must {
    "return the correct index" in {
      val x: Index = 0
      x mustEqual Index(0)
    }
  }

  "implicit conversion to index" must {
    "return the correct index" in {
      val x: Int = Index(0)
      x mustEqual 0
    }
  }
}
