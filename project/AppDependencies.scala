import sbt._

object AppDependencies {

  import play.core.PlayVersion

  val compile = Seq(
    play.sbt.PlayImport.ws,
    "org.reactivemongo"         %% "play2-reactivemongo"            % "0.17.1-play26",
    "uk.gov.hmrc"               %% "logback-json-logger"            % "4.8.0",
    "uk.gov.hmrc"               %% "play-health"                    % "3.15.0-play-26",
    "uk.gov.hmrc"               %% "play-conditional-form-mapping"  % "1.2.0-play-26",
    "uk.gov.hmrc"               %% "bootstrap-play-26"              % "1.7.0",
    "uk.gov.hmrc"               %% "play-frontend-govuk"            % "0.48.0-play-26",
    "uk.gov.hmrc"               %% "play-frontend-hmrc"             % "0.17.0-play-26",
    "uk.gov.hmrc"               %% "play-language"                  % "4.3.0-play-26"
  )

  val test = Seq(
    "org.scalatest"             %%    "scalatest"                   % "3.0.8",
    "org.scalatestplus.play"    %%    "scalatestplus-play"          % "3.1.2",
    "org.pegdown"               %     "pegdown"                     % "1.6.0",
    "org.jsoup"                 %     "jsoup"                       % "1.12.1",
    "com.typesafe.play"         %%    "play-test"                   % PlayVersion.current,
    "org.mockito"               %     "mockito-core"                % "3.3.3",
    "org.scalacheck"            %%    "scalacheck"                  % "1.14.1",
    "org.scalamock"             %%    "scalamock-scalatest-support" % "3.6.0",
    "com.github.tomakehurst"    %     "wiremock-standalone"         % "2.22.0",
    "org.jsoup"                 %     "jsoup"                       % "1.12.1"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
