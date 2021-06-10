import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"         %% "play2-reactivemongo"             % "0.20.11-play27",
    "org.reactivemongo"         %% "reactivemongo-bson-api"          % "0.20.11",
    "com.typesafe.play"         %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play"         %% "play-iteratees-reactive-streams" % "2.6.1",
    "uk.gov.hmrc"               %% "logback-json-logger"             % "5.1.0",
    "uk.gov.hmrc"               %% "play-health"                     % "3.16.0-play-27",
    "uk.gov.hmrc"               %% "play-conditional-form-mapping"   % "1.9.0-play-27",
    "uk.gov.hmrc"               %% "bootstrap-frontend-play-27"      % "5.3.0",
    "uk.gov.hmrc"               %% "play-frontend-hmrc"              % "0.69.0-play-27",
    "uk.gov.hmrc"               %% "play-language"                   % "5.1.0-play-27",
    "uk.gov.hmrc"               %% "http-caching-client"             % "9.5.0-play-27",
    "uk.gov.hmrc"               %% "play-allowlist-filter"           % "1.0.0-play-27",
    "com.typesafe.play"         %% "play-json-joda"                  % "2.7.4"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %%    "reactivemongo-test"          % "5.0.0-play-27",
    "org.scalatest"             %%    "scalatest"                   % "3.0.9",
    "org.scalatestplus.play"    %%    "scalatestplus-play"          % "4.0.3",
    "org.pegdown"               %     "pegdown"                     % "1.6.0",
    "org.jsoup"                 %     "jsoup"                       % "1.13.1",
    "com.typesafe.play"         %%    "play-test"                   % PlayVersion.current,
    "org.mockito"               %     "mockito-core"                % "3.11.0",
    "org.scalacheck"            %%    "scalacheck"                  % "1.15.4",
    "org.scalamock"             %%    "scalamock-scalatest-support" % "3.6.0",
    "com.github.tomakehurst"    %     "wiremock-standalone"         % "2.27.2",
    "org.jsoup"                 %     "jsoup"                       % "1.12.2"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
