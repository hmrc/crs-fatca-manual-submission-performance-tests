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

package uk.gov.hmrc.perftests

import uk.gov.hmrc.performance.simulation.PerformanceTestRunner
import uk.gov.hmrc.perftests.Requests._

class Simulation extends PerformanceTestRunner {

  setup("AuthLogin", "Logging in via Auth").withRequests (
    getAuthLoginPage,
    postAuthLoginCredentials,
    getCRSFATCADashboardPage
  )

  setup("VoidFatcaInformation", "Manage Your Reports").withActions(
    getManageYourFiPage,
    getSubmittedReportsForFi,
    getVoidingFatcaInformation,
    postVoidingFatcaInformation,
    getInformationVoided
  )

  setup("ReportDetails", "Submit Report Details").withActions(
    getManageYourFiPage,
    getSubmittedReportsForFi,
    getReportDetailsRegimePage,
    postReportDetailsRegimePage,
    getReportDetailsYearPage,
    postReportDetailsYearPage,
    getTypeOfReportPage,
    postTypeOfReportPage,
    getReportDetailsCheckAnswersPage,
    postReportDetailsCheckAnswersPage,
    getSendAReportPage
  )

  setup("SponsorDetails", "Add the Sponsor").withActions(
    getHaveSponsorPage,
    postHaveSponsorPage("true"),
    getSponsorNamePage,
    postSponsorNamePage,
    getGiinForSponsorPage,
    postGiinForSponsorPage,
    getWhereAreTheyBasedPage,
    postWhereAreTheyBasedPage("true"),
    getPostcodeForSponsorPage,
    getAddressUKPage,
    postAddressUKPage,
    getResidentTaxPage
    
  )

  setup("ManageYourElections", "Manage Your Elections").withRequests(
    getManageYourFiPage,
    getSubmittedReportsForFi,
    getManageElectionsForFI
  )

  setup("ManageCrsElections", "CRS - Manage Election Journey").withActions(
    getCrsContracts,
    postCrsContracts,
    getCrsDormantAccounts,
    postCrsDormantAccounts,
    getCrsThresholds,
    postCrsThresholds,
    getCrsCarfGrossProceeds,
    postCrsCarfGrossProceeds,
    getCrsGrossProceeds,
    postCrsGrossProceeds,
    //getCheckAnswers,
    postCheckAnswers,
    getElectionsSent
  )

  setup("ManageFatcaElections", "FATCA - Manage Election Journey").withActions(
    getFatcaUsTreasuryRegulations,
    postFatcaUsTreasuryRegulations,
    getFatcaThresholds,
    postFatcaThresholds,
    //getFatcaCheckAnswers,
    postFatcaCheckAnswers,
    getElectionsSent
  )


    runSimulation()
}
