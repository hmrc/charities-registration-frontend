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

package models.addressLookup

import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{Json, Writes}
import utils.MessageOption

case class AddressMessageLanguageModel(en: AddressMessagesModel, cy: AddressMessagesModel)

object AddressMessageLanguageModel {
  implicit val writes: Writes[AddressMessageLanguageModel] = Json.writes[AddressMessageLanguageModel]
}

case class AddressMessagesModel(appLevelLabels: AppLevelMessagesModel,
                                lookupPageLabels: LookupPageMessagesModel,
                                selectPageLabels: SelectPageMessagesModel,
                                editPageLabels: EditPageMessagesModel,
                                confirmPageLabels: ConfirmPageMessagesModel)

object AddressMessagesModel {
  implicit val writes: Writes[AddressMessagesModel] = Json.writes[AddressMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): AddressMessagesModel = {
    AddressMessagesModel(
      appLevelLabels = AppLevelMessagesModel.forLang(lang),
      lookupPageLabels = LookupPageMessagesModel.forLang(lang),
      selectPageLabels = SelectPageMessagesModel.forLang(lang),
      editPageLabels = EditPageMessagesModel.forLang(lang),
      confirmPageLabels = ConfirmPageMessagesModel.forLang(lang)
    )
  }
}

case class AppLevelMessagesModel(navTitle: String)

object AppLevelMessagesModel {
  implicit val writes: Writes[AppLevelMessagesModel] = Json.writes[AppLevelMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): AppLevelMessagesModel = {
    val messages = messagesApi.preferred(Seq(lang))

    AppLevelMessagesModel(navTitle = messages("service.name"))
  }
}

case class LookupPageMessagesModel(title: Option[String],
                                   heading: Option[String],
                                   filterLabel: Option[String],
                                   postcodeLabel: Option[String],
                                   submitLabel: Option[String],
                                   noResultsFoundMessage: Option[String],
                                   resultLimitExceededMessage: Option[String],
                                   manualAddressLinkText: Option[String]
                                )

object LookupPageMessagesModel {
  implicit val writes: Writes[LookupPageMessagesModel] = Json.writes[LookupPageMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): LookupPageMessagesModel = {
    LookupPageMessagesModel (
      title = MessageOption("addressLookup.lookupPage.title", lang),
      heading = MessageOption("addressLookup.lookupPage.heading", lang),
      filterLabel = MessageOption("addressLookup.lookupPage.filterLabel", lang),
      postcodeLabel = MessageOption("addressLookup.LookupPage.postcodeLabel", lang),
      submitLabel = MessageOption("addressLookup.lookupPage.submitLabel", lang),
      noResultsFoundMessage = MessageOption("addressLookup.lookupPage.noResultsFoundMessage", lang),
      resultLimitExceededMessage = MessageOption("addressLookup.lookupPage.resultLimitExceededMessage", lang),
      manualAddressLinkText = MessageOption("addressLookup.lookupPage.manualAddressLinkText", lang)
    )
  }
}

case class SelectPageMessagesModel(title: Option[String],
                                   heading: Option[String],
                                   headingWithPostcode: Option[String],
                                   proposalListLabel: Option[String],
                                   submitLabel: Option[String],
                                   searchAgainLinkText: Option[String],
                                   editAddressLinkText: Option[String]
                                  )

object SelectPageMessagesModel {
  implicit val writes: Writes[SelectPageMessagesModel] = Json.writes[SelectPageMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): SelectPageMessagesModel = {
    SelectPageMessagesModel(
      title = MessageOption("addressLookup.selectPage.title", lang),
      heading = MessageOption("addressLookup.selectPage.heading", lang),
      headingWithPostcode = MessageOption("addressLookup.selectPage.headingWithPostcode", lang),
      proposalListLabel = MessageOption("addressLookup.selectPage.proposalListLabel", lang),
      submitLabel = MessageOption("addressLookup.selectPage.submitLabel", lang),
      searchAgainLinkText = MessageOption("addressLookup.selectPage.searchAgainLinkText", lang),
      editAddressLinkText = MessageOption("addressLookup.selectPage.editAddressLinkText", lang)
    )
  }
}

case class EditPageMessagesModel(title: Option[String],
                                 heading: Option[String],
                                 line1Label: Option[String],
                                 line2Label: Option[String],
                                 townLabel: Option[String],
                                 line3Label: Option[String],
                                 postcodeLabel: Option[String],
                                 submitLabel: Option[String]
                                )

object EditPageMessagesModel {
  implicit val writes: Writes[EditPageMessagesModel] = Json.writes[EditPageMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): EditPageMessagesModel = {

    EditPageMessagesModel(
      title = MessageOption("addressLookup.editPage.title", lang),
      heading = MessageOption("addressLookup.editPage.heading", lang),
      line1Label = MessageOption("addressLookup.editPage.line1Label", lang),
      line2Label = MessageOption("addressLookup.editPage.line2Label", lang),
      line3Label = MessageOption("addressLookup.editPage.line3Label", lang),
      townLabel = MessageOption("addressLookup.editPage.townLabel", lang),
      postcodeLabel = MessageOption("addressLookup.editPage.postcodeLabel", lang),
      submitLabel = MessageOption("addressLookup.editPage.submitLabel", lang)
    )
  }
}

case class ConfirmPageMessagesModel(title: Option[String],
                                    heading: Option[String],
                                    infoSubheading: Option[String],
                                    infoMessage: Option[String],
                                    submitLabel: Option[String],
                                    searchAgainLinkText: Option[String],
                                    changeLinkText: Option[String],
                                    confirmChangeText: Option[String]
                                   )

object ConfirmPageMessagesModel {
  implicit val writes: Writes[ConfirmPageMessagesModel] = Json.writes[ConfirmPageMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): ConfirmPageMessagesModel = {
    ConfirmPageMessagesModel(
      title = MessageOption("addressLookup.confirmPage.title", lang),
      heading = MessageOption("addressLookup.confirmPage.heading", lang),
      infoMessage = Some(""),
      infoSubheading = MessageOption("addressLookup.confirmPage.infoSubheading", lang),
      submitLabel = MessageOption("addressLookup.confirmPage.submitLabel", lang),
      searchAgainLinkText = MessageOption("addressLookup.confirmPage.searchAgainLinkText", lang),
      changeLinkText = MessageOption("addressLookup.confirmPage.changeLinkText", lang),
      confirmChangeText = MessageOption("addressLookup.confirmPage.confirmChangeText", lang)
    )
  }
}
