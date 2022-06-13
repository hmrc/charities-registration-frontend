import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"         %% "play2-reactivemongo"             % "0.20.13-play28",
    "org.reactivemongo"         %% "reactivemongo-bson-api"          % "0.20.13",
    "com.typesafe.play"         %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play"         %% "play-iteratees-reactive-streams" % "2.6.1",
    "uk.gov.hmrc"               %% "logback-json-logger"             % "5.1.0",
    "uk.gov.hmrc"               %% "play-conditional-form-mapping"   % "1.11.0-play-28",
    "uk.gov.hmrc"               %% "bootstrap-frontend-play-28"      % "5.24.0",
    "uk.gov.hmrc"               %% "play-frontend-hmrc"              % "3.21.0-play-28",
    "uk.gov.hmrc"               %% "play-language"                   % "5.3.0-play-28",
    "uk.gov.hmrc"               %% "http-caching-client"             % "9.6.0-play-28",
    "uk.gov.hmrc"               %% "play-allowlist-filter"           % "1.0.0-play-28",
    "com.typesafe.play"         %% "play-json-joda"                  % "2.8.1"
  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %%    "reactivemongo-test"          % "5.0.0-play-28",
    "org.scalatest"             %%    "scalatest"                   % "3.1.4",
    "org.scalatestplus.play"    %%    "scalatestplus-play"          % "5.1.0",
    "org.pegdown"               %     "pegdown"                     % "1.6.0",
    "org.jsoup"                 %     "jsoup"                       % "1.14.3",
    "com.typesafe.play"         %%    "play-test"                   % PlayVersion.current,
    "org.mockito"               %     "mockito-core"                % "4.5.1",
    "org.scalacheck"            %%    "scalacheck"                  % "1.16.0",
    "org.scalamock"             %%    "scalamock-scalatest-support" % "3.6.0",
    "com.github.tomakehurst"    %     "wiremock-standalone"         % "2.27.2",
    "org.jsoup"                 %     "jsoup"                       % "1.15.1",
    "org.scalatestplus"         %%    "mockito-3-4"                 % "3.2.10.0",
    "org.scalatestplus"         %%    "scalacheck-1-15"             % "3.2.11.0",
    "com.vladsch.flexmark"      %     "flexmark-all"                % "0.36.8"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
