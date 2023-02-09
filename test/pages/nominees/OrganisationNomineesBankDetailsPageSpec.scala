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

package pages.nominees

import models.BankDetails
import org.scalacheck.Arbitrary
import pages.behaviours.PageBehaviours

class OrganisationNomineesBankDetailsPageSpec extends PageBehaviours {

  "OrganisationNomineesBankAccountDetailsPage" must {

    implicit lazy val arbitraryBankDetails: Arbitrary[BankDetails] = Arbitrary {
      BankDetails(
        accountName = "fullName",
        sortCode = "123456",
        accountNumber = "12345678",
        rollNumber = Some("operatingName")
      )
    }

    beRetrievable[BankDetails](OrganisationNomineesBankDetailsPage)

    beSettable[BankDetails](OrganisationNomineesBankDetailsPage)

    beRemovable[BankDetails](OrganisationNomineesBankDetailsPage)
  }
}
