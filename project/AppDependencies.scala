import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"         %% "play2-reactivemongo"             % "0.20.11-play28",
    "org.reactivemongo"         %% "reactivemongo-bson-api"          % "0.20.11",

    // https://github.com/playframework/play-iteratees
    // End of Life
    // The active Playframework contributors consider this repository has reached End of Life and archived it.
    // This repository is not being used anymore and won't get any further updates.
    "com.typesafe.play"         %% "play-iteratees"                  % "2.6.1",
    "com.typesafe.play"         %% "play-iteratees-reactive-streams" % "2.6.1",

    "uk.gov.hmrc"               %% "logback-json-logger"             % "5.1.0",
    // https://github.com/hmrc/play-health/blob/9ef63c80cd97c2297fa14a2b14425e4840ee33ae/README.md
    // Play Health is no longer maintained. The common health check functionality comes with bootstrap.
    // Please ensure you are using the latest bootstrap-play and remove the dependency on Play Health.
    // "uk.gov.hmrc"               %% "play-health"                     % "3.16.0-play-27",
    "uk.gov.hmrc"               %% "play-conditional-form-mapping"   % "1.9.0-play-28",
    "uk.gov.hmrc"               %% "bootstrap-frontend-play-28"      % "5.6.0",
    "uk.gov.hmrc"               %% "play-frontend-hmrc"              % "0.80.0-play-28",
    "uk.gov.hmrc"               %% "play-language"                   % "5.0.0-play-28",
    "uk.gov.hmrc"               %% "http-caching-client"             % "9.5.0-play-28",
    "uk.gov.hmrc"               %% "play-allowlist-filter"           % "1.0.0-play-28",

    "com.typesafe.play"         %% "play-json-joda"                  % "2.8.1"

  )

  val test: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"               %%    "reactivemongo-test"          % "5.0.0-play-28",
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
