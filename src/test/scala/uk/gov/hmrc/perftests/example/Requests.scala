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

package uk.gov.hmrc.perftests.example

import io.gatling.core.Predef.*
import io.gatling.core.session.el.*
import io.gatling.http.Predef.*
import io.gatling.http.request.builder.HttpRequestBuilder
import uk.gov.hmrc.performance.conf.ServicesConfiguration

object Requests extends ServicesConfiguration {

  val baseUrlFi: String = baseUrlFor("crs-fatca-fi-management-frontend")
  val baseUrlManualSub: String = baseUrlFor("crs-fatca-manual-submission-frontend")
  val baseUrlAuth: String = baseUrlFor("auth-frontend")
  val FiRoute: String = "/manage-your-crs-and-fatca-financial-institutions"
  val authRoute: String = "/auth-login-stub/gg-sign-in"
  val manualSubRoute: String = "/crs-fatca-manual-submission-frontend"
  val amazonUrlPattern = """action="(.*?)""""
  val staticId = "683373339"
  val messageRefId = "GB2025GB-XZU9323406858-APIMB0666"
  val informationVoidedUrl = "/fatca-void/information-voided?originalMessageRefId=GB2025GB-XZU9323406858-APIMB0666"
  val year = "2026"
  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth login page")
      .get(baseUrlAuth + authRoute)
      .check(status.is(200))
  val postAuthLoginCredentials: HttpRequestBuilder =
    http("Enter Auth login credentials")
      .post(baseUrlAuth + authRoute)
      .formParam("authorityId", "".el[String])
      .formParam("redirectionUrl", (baseUrlFi + FiRoute).el[String])
      .formParam("credentialStrength", "strong".el[String])
      .formParam("confidenceLevel", "50".el[String])
      .formParam("affinityGroup", "Organisation".el[String])
      .formParam("enrolment[0].name", "HMRC-FATCA-ORG".el[String])
      .formParam("enrolment[0].taxIdentifier[0].name", "FATCAID".el[String])
      .formParam("enrolment[0].taxIdentifier[0].value", "XE2ATCA0009234567".el[String])
      .formParam("enrolment[0].state", "Activated".el[String])
      .formParam("enrolment[4].name", "IR-CT".el[String])
      .formParam("enrolment[4].taxIdentifier[0].name", "UTR".el[String])
      .formParam("enrolment[4].taxIdentifier[0].value", "333".el[String])
      .formParam("enrolment[4].state", "Activated".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(baseUrlFi + FiRoute).saveAs("LandingPage"))

  val getCRSFATCADashboardPage: HttpRequestBuilder =
    http("Get CRS FATCA Dashboard Page")
      .get("#{LandingPage}")
      .check(status.is(200))

  val getManageYourFiPage: HttpRequestBuilder =
    http("Get Manage your FI Page Redirect")
      .get("#{LandingPage}/your-fis")
      .check(status.is(200))

  val getSubmittedReportsForFi: HttpRequestBuilder =
    http("Get Manage Report For Fi Page")
      .get(s"$baseUrlManualSub$manualSubRoute/manage-reports-for-2025")
      .queryParam("fiId", staticId.el[String])
      .check(status.is(200))

  val getVoidingFatcaInformation: HttpRequestBuilder =
    http("Get Voiding Fatac information Page")
      .get(s"$baseUrlManualSub$manualSubRoute/fatca-void/voiding-fatca-information")
      .queryParam("originalMessageRefId", messageRefId.el[String])
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postVoidingFatcaInformation: HttpRequestBuilder =
    http("Post Fatca Voiding Information - Yes")
      .post(s"$baseUrlManualSub$manualSubRoute/fatca-void/voiding-fatca-information")
      .queryParam("originalMessageRefId", messageRefId.el[String])
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + informationVoidedUrl).saveAs("informationVoided"))

  val getInformationVoided: HttpRequestBuilder =
    http("Get Information Voided Page")
      .get(baseUrlManualSub + "#{informationVoided}")
      .check(status.is(200))

  val getManageElectionsForFI: HttpRequestBuilder =
    http("Get Manage Elections Page")
      .get(s"$baseUrlManualSub$manualSubRoute/elections/manage-elections-for-2026")
      .queryParam("fiId", staticId.el[String])
      .check(status.is(200))

  val getCrsContracts: HttpRequestBuilder =
    http("Get Crs Contracts Page")
      .get(s"$baseUrlManualSub$manualSubRoute/elections/crs/contracts")
      .queryParam("year", year.el[String])
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCrsContracts: HttpRequestBuilder =
    http("Post Crs Contracts Page")
      .post(s"$baseUrlManualSub$manualSubRoute/elections/crs/contracts")
      .queryParam("year", year.el[String])
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/crs/dormant-accounts?year=2026").saveAs("crsDormantAccounts"))

  val getCrsDormantAccounts: HttpRequestBuilder =
    http("Get CRS Dormant Accounts Page")
      .get(baseUrlManualSub + "#{crsDormantAccounts}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCrsDormantAccounts: HttpRequestBuilder =
    http("post crs dormant accounts-yes")
      .post(baseUrlManualSub + "#{crsDormantAccounts}")
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/crs/thresholds?year=2026").saveAs("crsThresholds"))

  val getCrsThresholds: HttpRequestBuilder =
    http("Get CRS Thresholds")
      .get(baseUrlManualSub + "#{crsThresholds}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCrsThresholds: HttpRequestBuilder =
    http("post crs thresholds-yes")
      .post(baseUrlManualSub + "#{crsThresholds}")
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/crs/carf-gross-proceeds?year=2026").saveAs("crsCarfGrossProceeds"))

  val getCrsCarfGrossProceeds: HttpRequestBuilder =
    http("Get CRS CARF Gross Proceeds Page")
      .get(baseUrlManualSub + "#{crsCarfGrossProceeds}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCrsCarfGrossProceeds: HttpRequestBuilder =
    http("post CRS CARF Gross Proceeds - yes")
      .post(baseUrlManualSub + "#{crsCarfGrossProceeds}")
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/crs/gross-proceeds?year=2026").saveAs("crsGrossProceeds"))

  val getCrsGrossProceeds: HttpRequestBuilder =
    http("Get CRS  Gross Proceeds Page")
      .get(baseUrlManualSub + "#{crsGrossProceeds}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCrsGrossProceeds: HttpRequestBuilder =
    http("post CRS Gross Proceeds - yes")
      .post(baseUrlManualSub + "#{crsGrossProceeds}")
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/check-answers?year=2026").saveAs("checkAnswers"))

  val getCheckAnswers: HttpRequestBuilder =
    http("Get Check Answers Page")
      .get(baseUrlManualSub + "#{checkAnswers}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postCheckAnswers: HttpRequestBuilder =
    http("Post Check Answers Page")
      .post(baseUrlManualSub + "#{checkAnswers}")
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/elections-sent").saveAs("electionsSent"))

  val getElectionsSent: HttpRequestBuilder =
    http("Get Elections Sent Page")
      .post(baseUrlManualSub + "#{electionsSent}")
      .check(status.is(200))


  val getFatcaUsTreasuryRegulations: HttpRequestBuilder =
    http("Get Fatca US Treasury Regulations Page")
      .get(s"$baseUrlManualSub$manualSubRoute/elections/fatca/us-treasury-regulations")
      .queryParam("year", year.el[String])
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postFatcaUsTreasuryRegulations: HttpRequestBuilder =
    http("Post Fatca US Treasury Regulations Page")
      .post(s"$baseUrlManualSub$manualSubRoute/elections/fatca/us-treasury-regulations")
      .queryParam("year", year.el[String])
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/fatca/thresholds?year=2026").saveAs("fatcaThresholds"))

  val getFatcaThresholds: HttpRequestBuilder =
    http("Get Fatca Thresholds Page")
      .get(baseUrlManualSub + "#{fatcaThresholds}")
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postFatcaThresholds: HttpRequestBuilder =
    http("Post Fatca Thresholds Page")
      .post(baseUrlManualSub + "#{fatcaThresholds}")
      .queryParam("year", year.el[String])
      .formParam("csrfToken", "#{csrfToken}".el[String])
      .formParam("value", "true".el[String])
      .check(status.is(303))
      .check(header("Location".el[String]).is(manualSubRoute + "/elections/check-answers?year=2026").saveAs("fatcaCheckAnswers"))


  def inputSelectorByName(name: String): String = s"input[name='$name']"


}
