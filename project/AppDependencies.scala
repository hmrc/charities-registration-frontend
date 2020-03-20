import play.core.PlayVersion.current
import play.sbt.PlayImport._
import sbt.Keys.libraryDependencies
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    ws,
    "uk.gov.hmrc"             %% "http-caching-client"      % "9.0.0-play-26",
    "uk.gov.hmrc"             %% "play-partials"            % "6.9.0-play-26",
    "uk.gov.hmrc"             %% "govuk-template"           % "5.52.0-play-26",
    "uk.gov.hmrc"             %% "play-language"            % "4.2.0-play-26",
    "uk.gov.hmrc"             %% "play-ui"                  % "8.8.0-play-26",
    "uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.5.0",
    "com.github.fge"          % "json-schema-validator"     % "2.2.6",
    "uk.gov.hmrc"             %% "auth-client"              % "2.35.0-play-26"
  )

  val test: Seq[ModuleID] = Seq(
		"uk.gov.hmrc"             %% "bootstrap-play-26"        % "1.5.0" ,
		"org.scalatest"           %% "scalatest"                % "3.0.8" ,
		"org.jsoup"               %  "jsoup"                    % "1.13.1",
    "com.typesafe.play"       %% "play-test"                % current ,
    "org.pegdown"             %  "pegdown"                  % "1.6.0" ,
    "org.scalatestplus.play"  %% "scalatestplus-play"       % "3.1.2" ,
    "org.mockito"             % "mockito-core"              % "3.3.3"
  ).map(_ % "test")

  val all: Seq[ModuleID] = compile ++ test
}
