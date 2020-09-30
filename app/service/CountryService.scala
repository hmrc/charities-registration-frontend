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

package service

import com.google.inject.ImplementedBy
import javax.inject.Singleton
import models.{Country, FcoCountry}
import play.api.i18n.Messages
import play.api.libs.json.Json

@ImplementedBy(classOf[CountryServiceImpl])
trait CountryService {

  def countries()(implicit messages: Messages): Seq[(String, String)]

  def find(code: String)(implicit messages: Messages): Option[Country]

}

@Singleton
class CountryServiceImpl extends CountryService {

  private def getCountries(resourceFile: String): Seq[Country] = {
    Json.parse( getClass.getResourceAsStream(resourceFile)).as[Map[String, FcoCountry]].map {
      country =>
        Country(country._2.country, country._2.name)
    }.toSeq.sortWith(_.name < _.name)
  }

  private lazy val countriesEN: Seq[Country] = getCountries("/countriesEN.json")

  private lazy val countriesCY: Seq[Country] = getCountries("/countriesCY.json")

  private def isWelsh(implicit messages: Messages) = messages.lang.code == "cy"

  override def countries()(implicit messages: Messages): Seq[(String, String)] = {

    val countries = if (isWelsh) countriesCY else countriesEN

    countries.map { c => c.code -> c.name }
  }

  override def find(code: String)(implicit messages: Messages): Option[Country] = {

    if (isWelsh) countriesCY.find(_.code == code) else countriesEN.find(_.code == code)

  }

}