import sbt._

object AppDependencies {

  private lazy val mongoHmrcVersion     = "0.74.0"
  private lazy val bootstrapPlayVersion = "7.13.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.12.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "6.4.0-play-28",
    "uk.gov.hmrc"       %% "http-caching-client"           % "10.0.0-play-28",
    "uk.gov.hmrc"       %% "play-allowlist-filter"         % "1.1.0",
    "com.typesafe.play" %% "play-json-joda"                % "2.9.4"
  )

  val test: Seq[ModuleID]    = Seq(
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28"     % mongoHmrcVersion,
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"      % bootstrapPlayVersion,
    "org.scalatest"          %% "scalatest"                   % "3.2.15",
    "org.scalatestplus.play" %% "scalatestplus-play"          % "5.1.0",
    "org.jsoup"               % "jsoup"                       % "1.15.3",
    "com.typesafe.play"      %% "play-test"                   % "2.8.19",
    "org.scalacheck"         %% "scalacheck"                  % "1.17.0",
    "org.scalamock"          %% "scalamock"                   % "5.2.0",
    "com.github.tomakehurst"  % "wiremock-standalone"         % "2.27.2",
    "org.mockito"            %% "mockito-scala-scalatest"     % "1.17.12",
    "org.scalatestplus"      %% "scalacheck-1-15"             % "3.2.11.0",
    "com.vladsch.flexmark"    % "flexmark-all"                % "0.62.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
