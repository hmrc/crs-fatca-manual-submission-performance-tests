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

import io.gatling.core.Predef._
import io.gatling.core.session.Expression
import io.gatling.http.Predef._
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

  def inputSelectorByName(name: String): Expression[String] = s"input[name='$name']"

  val getAuthLoginPage: HttpRequestBuilder =
    http("Get Auth login page")
      .get(baseUrlAuth + authRoute)
      .check(status.is(200))

  val postAuthLoginCredentials: HttpRequestBuilder =
    http("Enter Auth login credentials")
      .post(baseUrlAuth + authRoute)
      .formParam("authorityId", "")
      .formParam("redirectionUrl", baseUrlFi + FiRoute)
      .formParam("credentialStrength", "strong")
      .formParam("confidenceLevel", "50")
      .formParam("affinityGroup", "Organisation")
      .formParam("enrolment[0].name", "HMRC-FATCA-ORG")
      .formParam("enrolment[0].taxIdentifier[0].name", "FATCAID")
      .formParam("enrolment[0].taxIdentifier[0].value", "XE2ATCA0009234567")
      .formParam("enrolment[0].state", "Activated")
      .formParam("enrolment[4].name", "IR-CT")
      .formParam("enrolment[4].taxIdentifier[0].name", "UTR")
      .formParam("enrolment[4].taxIdentifier[0].value", "333")
      .formParam("enrolment[4].state", "Activated")
      .check(status.is(303))
      .check(header("Location").is(baseUrlFi + FiRoute).saveAs("LandingPage"))

  val getCRSFATCADashboardPage: HttpRequestBuilder =
    http("Get CRS FATCA Dashboard Page")
      .get("#{LandingPage}")
      .check(status.is(200))

  val getManageYourFiPage: HttpRequestBuilder =
    http("Get Manage your FI Page Redirect")
      .get("#{LandingPage}/your-fis")
      .check(status.is(200))

  val getManualSubmissionRefresh: HttpRequestBuilder =
    http("Get Refresh To Manual Submission")
      .get("#{LandingPage}/refresh-session")
      .check(status.is(200))

  val getSubmittedReportsForFiRedirect:HttpRequestBuilder =
    http("Get Manage Report For Fi Page")
      .get(s"$baseUrlManualSub$manualSubRoute/read-submission-data")
      .queryParam("fiId", staticId)
      .queryParam("fiName", "Fifth FI")
      .check(status.is(303))
      .check(header("Location").saveAs("readSubmissionForFi"))

  val getSubmittedReportForFiPage:HttpRequestBuilder =
    http("Get Submitted Reports For Fi Page")
      .get(s"$baseUrlManualSub$manualSubRoute/manage-reports-for-2025")
      .queryParam("fiId", staticId)
      .queryParam("fiName", "Fifth FI")
      .check(status.is(200))

  val getVoidingFatcaInformationPage: HttpRequestBuilder =
    http("Get Voiding FATCA Information Page")
      .get(s"$baseUrlManualSub$manualSubRoute/fatca-void/voiding-fatca-information")
      .queryParam("originalMessageRefId", messageRefId)
      .check(status.is(200))
      .check(css(inputSelectorByName("csrfToken"), "value").saveAs("csrfToken"))

  val postVoidingFatcaInformationPage: HttpRequestBuilder =
    http("Post Voiding FATCA Information Page")
      .post(s"$baseUrlManualSub$manualSubRoute/fatca-void/voiding-fatca-information")
      .queryParam("originalMessageRefId", messageRefId)
      .formParam("csrfToken", "#{csrfToken}")
      .formParam("value", "true")
      .check(status.is(303))
      .check(header("Location").saveAs("informationVoided"))

  val getInformationVoidedPage: HttpRequestBuilder =
    http("Get Fatca Information Voided Page")
      .get(s"$baseUrlManualSub$manualSubRoute/fatca-void/information-voided")
      .queryParam("originalMessageRefId", messageRefId)
      .check(status.is(200))













}
