import sbt.*

object AppDependencies {

  private lazy val mongoHmrcVersion     = "1.3.0"
  private lazy val bootstrapPlayVersion = "7.21.0"

  val compile: Seq[ModuleID] = Seq(
    play.sbt.PlayImport.ws,
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"            % mongoHmrcVersion,
    "uk.gov.hmrc"       %% "play-conditional-form-mapping" % "1.13.0-play-28",
    "uk.gov.hmrc"       %% "bootstrap-frontend-play-28"    % bootstrapPlayVersion,
    "uk.gov.hmrc"       %% "play-frontend-hmrc"            % "7.19.0-play-28",
    "com.typesafe.play" %% "play-json-joda"                % "2.9.4"
  )

  val test: Seq[ModuleID]    = Seq(
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-test-play-28"   % mongoHmrcVersion,
    "uk.gov.hmrc"           %% "bootstrap-test-play-28"    % bootstrapPlayVersion,
    "org.scalatest"         %% "scalatest"                 % "3.2.16",
    "org.jsoup"              % "jsoup"                     % "1.16.1",
    "com.github.tomakehurst" % "wiremock-standalone"       % "2.27.2",
    "org.mockito"           %% "mockito-scala-scalatest"   % "1.17.14",
    "org.scalatestplus"     %% "scalacheck-1-17"           % "3.2.16.0",
    "com.vladsch.flexmark"   % "flexmark-all"              % "0.64.8",
    "com.stephenn"          %% "scalatest-json-jsonassert" % "0.2.5"
  ).map(_ % "test, it")

  def apply(): Seq[ModuleID] = compile ++ test
}
