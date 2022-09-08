import sbt._

object AppDependencies {

  import play.core.PlayVersion

  private lazy val mongoHmrcVersion = "0.71.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "logback-json-logger"           % "5.2.0",
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.11.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % "5.25.0",
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "3.22.0-play-28",
    "uk.gov.hmrc"       %% "play-language"                 % "5.3.0-play-28",
    "uk.gov.hmrc"       %% "http-caching-client"           % "9.6.0-play-28",
    "uk.gov.hmrc"       %% "play-allowlist-filter"         % "1.1.0",
    "com.typesafe.play" %% "play-json-joda"                % "2.9.3"
  )

  val test: Seq[ModuleID]    = Seq(
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28"     % mongoHmrcVersion,
    "org.scalatest"          %% "scalatest"                   % "3.2.13",
    "org.scalatestplus.play" %% "scalatestplus-play"          % "5.1.0",
    "org.jsoup"               % "jsoup"                       % "1.15.3",
    "com.typesafe.play"      %% "play-test"                   % PlayVersion.current,
    "org.mockito"             % "mockito-core"                % "4.8.0",
    "org.scalacheck"         %% "scalacheck"                  % "1.16.0",
    "org.scalamock"          %% "scalamock-scalatest-support" % "3.6.0",
    "com.github.tomakehurst"  % "wiremock-standalone"         % "2.27.2",
    "org.scalatestplus"      %% "mockito-3-4"                 % "3.2.10.0",
    "org.scalatestplus"      %% "scalacheck-1-15"             % "3.2.11.0",
    "com.vladsch.flexmark"    % "flexmark-all"                % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
