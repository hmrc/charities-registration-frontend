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

package models.addressLookup

import play.api.i18n.{Lang, MessagesApi}
import play.api.libs.json.{Json, Writes}
import utils.MessageOption

case class AddressMessageLanguageModel(en: AddressMessagesModel, cy: AddressMessagesModel)

object AddressMessageLanguageModel {
  implicit val writes: Writes[AddressMessageLanguageModel] = Json.writes[AddressMessageLanguageModel]
}

case class AddressMessagesModel(
  appLevelLabels: AppLevelMessagesModel,
  lookupPageLabels: LookupPageMessagesModel,
  selectPageLabels: SelectPageMessagesModel,
  editPageLabels: EditPageMessagesModel,
  confirmPageLabels: ConfirmPageMessagesModel
)

object AddressMessagesModel {
  implicit val writes: Writes[AddressMessagesModel] = Json.writes[AddressMessagesModel]

  def forLang(lang: Lang, messagePrefix: String, fullName: Option[String])(implicit
    messagesApi: MessagesApi
  ): AddressMessagesModel =
    AddressMessagesModel(
      appLevelLabels = AppLevelMessagesModel.forLang(lang),
      lookupPageLabels = LookupPageMessagesModel.forLang(lang, messagePrefix, fullName),
      selectPageLabels = SelectPageMessagesModel.forLang(lang, messagePrefix, fullName),
      editPageLabels = EditPageMessagesModel.forLang(lang, messagePrefix, fullName),
      confirmPageLabels = ConfirmPageMessagesModel.forLang(lang, messagePrefix, fullName)
    )
}

case class AppLevelMessagesModel(navTitle: String)

object AppLevelMessagesModel {
  implicit val writes: Writes[AppLevelMessagesModel] = Json.writes[AppLevelMessagesModel]

  def forLang(lang: Lang)(implicit messagesApi: MessagesApi): AppLevelMessagesModel = {
    val messages = messagesApi.preferred(Seq(lang))

    AppLevelMessagesModel(navTitle = messages("service.name"))
  }
}

case class LookupPageMessagesModel(
  title: Option[String],
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

  def forLang(lang: Lang, messagePrefix: String, fullName: Option[String])(implicit
    messagesApi: MessagesApi
  ): LookupPageMessagesModel =
    LookupPageMessagesModel(
      title = MessageOption(s"$messagePrefix.lookupPage.title", lang),
      heading = MessageOption(s"$messagePrefix.lookupPage.heading", lang, fullName.getOrElse("")),
      filterLabel = MessageOption(s"commonAddress.lookupPage.filterLabel", lang),
      postcodeLabel = MessageOption(s"commonAddress.LookupPage.postcodeLabel", lang),
      submitLabel = MessageOption(s"commonAddress.lookupPage.submitLabel", lang),
      noResultsFoundMessage = MessageOption(s"commonAddress.lookupPage.noResultsFoundMessage", lang),
      resultLimitExceededMessage = MessageOption(s"commonAddress.lookupPage.resultLimitExceededMessage", lang),
      manualAddressLinkText =
        MessageOption(s"$messagePrefix.lookupPage.manualAddressLinkText", lang, fullName.getOrElse(""))
    )
}

case class SelectPageMessagesModel(
  title: Option[String],
  heading: Option[String],
  headingWithPostcode: Option[String],
  proposalListLabel: Option[String],
  submitLabel: Option[String],
  searchAgainLinkText: Option[String],
  editAddressLinkText: Option[String]
)

object SelectPageMessagesModel {
  implicit val writes: Writes[SelectPageMessagesModel] = Json.writes[SelectPageMessagesModel]

  def forLang(lang: Lang, messagePrefix: String, fullName: Option[String])(implicit
    messagesApi: MessagesApi
  ): SelectPageMessagesModel =
    SelectPageMessagesModel(
      title = MessageOption(s"$messagePrefix.selectPage.title", lang),
      heading = MessageOption(s"$messagePrefix.selectPage.heading", lang, fullName.getOrElse("")),
      headingWithPostcode = MessageOption(s"commonAddress.selectPage.headingWithPostcode", lang),
      proposalListLabel = MessageOption(s"commonAddress.selectPage.proposalListLabel", lang),
      submitLabel = MessageOption(s"commonAddress.selectPage.submitLabel", lang),
      searchAgainLinkText =
        MessageOption(s"$messagePrefix.selectPage.searchAgainLinkText", lang, fullName.getOrElse("")),
      editAddressLinkText =
        MessageOption(s"$messagePrefix.selectPage.editAddressLinkText", lang, fullName.getOrElse(""))
    )
}

case class EditPageMessagesModel(
  title: Option[String],
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

  def forLang(lang: Lang, messagePrefix: String, fullName: Option[String])(implicit
    messagesApi: MessagesApi
  ): EditPageMessagesModel =
    EditPageMessagesModel(
      title = MessageOption(s"$messagePrefix.editPage.title", lang),
      heading = MessageOption(s"$messagePrefix.editPage.heading", lang, fullName.getOrElse("")),
      line1Label = MessageOption(s"commonAddress.editPage.line1Label", lang),
      line2Label = MessageOption(s"commonAddress.editPage.line2Label", lang),
      line3Label = MessageOption(s"commonAddress.editPage.line3Label", lang),
      townLabel = MessageOption(s"commonAddress.editPage.townLabel", lang),
      postcodeLabel = MessageOption(s"commonAddress.editPage.postcodeLabel", lang),
      submitLabel = MessageOption(s"commonAddress.editPage.submitLabel", lang)
    )
}

case class ConfirmPageMessagesModel(
  title: Option[String],
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

  def forLang(lang: Lang, messagePrefix: String, fullName: Option[String])(implicit
    messagesApi: MessagesApi
  ): ConfirmPageMessagesModel =
    ConfirmPageMessagesModel(
      title = MessageOption(s"$messagePrefix.confirmPage.title", lang),
      heading = MessageOption(s"$messagePrefix.confirmPage.heading", lang, fullName.getOrElse("")),
      infoMessage = Some(""),
      infoSubheading = Some(""),
      submitLabel = MessageOption(s"commonAddress.confirmPage.submitLabel", lang),
      searchAgainLinkText = MessageOption(s"commonAddress.confirmPage.searchAgainLinkText", lang),
      changeLinkText = MessageOption(s"$messagePrefix.confirmPage.changeLinkText", lang, fullName.getOrElse("")),
      confirmChangeText = MessageOption(s"commonAddress.confirmPage.confirmChangeText", lang)
    )
}
